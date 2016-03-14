package uk.org.gtc.api;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.mongojack.JacksonDBCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.Auth0User;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import uk.org.gtc.api.domain.BookDO;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.health.BasicHealthCheck;
import uk.org.gtc.api.health.MandrillHealthCheck;
import uk.org.gtc.api.health.MongoHealthCheck;
import uk.org.gtc.api.resource.ApiResource;
import uk.org.gtc.api.resource.BookResource;
import uk.org.gtc.api.resource.MemberResource;
import uk.org.gtc.api.service.BookService;
import uk.org.gtc.api.service.MemberService;

public class GtcApplication extends Application<GtcConfiguration>
{
	
	public static void main(final String[] args) throws Exception
	{
		new GtcApplication().run(args);
	}
	
	@Override
	public String getName()
	{
		return "gtc-api";
	}
	
	@Override
	public void initialize(final Bootstrap<GtcConfiguration> bootstrap)
	{
		bootstrap.addBundle(new SwaggerBundle<GtcConfiguration>()
		{
			@Override
			protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(GtcConfiguration configuration)
			{
				return configuration.swaggerBundleConfiguration;
			}
		});
	}
	
	Logger logger()
	{
		return LoggerFactory.getLogger(GtcApplication.class);
	}
	
	@Override
	public void run(final GtcConfiguration configuration, final Environment environment) throws UnknownHostException
	{
		// Managed resources
		final ServerAddress mongoHost = new ServerAddress(configuration.mongoHost);
		final List<MongoCredential> mongoCredentials = new ArrayList<MongoCredential>();
		final MongoCredential credential = MongoCredential.createScramSha1Credential(configuration.mongoUser, "admin",
				configuration.mongoPassword);
		mongoCredentials.add(credential);
		final MongoClient mongo = new MongoClient(mongoHost, mongoCredentials);
		environment.lifecycle().manage(new MongoManaged(mongo));
		
		// CORS configuration
		final FilterRegistration.Dynamic corsFilter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
		corsFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
				"Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
		corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, configuration.corsOrigins);
		corsFilter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		
		// Authentication configuration
		final List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/member/*");
		urlPatterns.add("/user/*");
		
		final JWTFilter jwtFilterImpl = new JWTFilter(configuration);
		final FilterRegistration.Dynamic jwtFilter = environment.servlets().addFilter("jwt-filter", jwtFilterImpl);
		for (String urlPattern : urlPatterns)
		{
			jwtFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, urlPattern);
		}
		jwtFilter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
				"Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
		jwtFilter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
		
		// Integrations
		final MandrillApi mandrill = new MandrillApi(configuration.mandrillApiKey);
		
		// Health checks
		environment.healthChecks().register("basic", new BasicHealthCheck());
		environment.healthChecks().register("mongo", new MongoHealthCheck(mongo));
		environment.healthChecks().register("mandrill", new MandrillHealthCheck(mandrill));
		
		// Database and Jackson mappings
		@SuppressWarnings("deprecation")
		final DB db = mongo.getDB(configuration.mongoDatabase);
		
		final JacksonDBCollection<MemberDO, String> members = JacksonDBCollection.wrap(db.getCollection("members"), MemberDO.class,
				String.class);
		final JacksonDBCollection<BookDO, String> books = JacksonDBCollection.wrap(db.getCollection("books"), BookDO.class, String.class);
		
		// Services
		final MemberService memberService = new MemberService(members, mandrill);
		final BookService bookService = new BookService(books);
		
		// Resource registration
		environment.jersey().register(new ApiResource());
		environment.jersey().register(new MemberResource(memberService));
		environment.jersey().register(new BookResource(bookService));
		
		// Authentication
		final OAuthCredentialAuthFilter.Builder<Auth0User> authFilter = new OAuthCredentialAuthFilter.Builder<Auth0User>();
		final GtcAuthenticator gtcAuthenticator = new GtcAuthenticator(logger(), configuration);
		final GtcAuthoriser gtcAuthoriser = new GtcAuthoriser();
		final String tokenPrefix = "Bearer";
		final AuthDynamicFeature gtcAuth = new AuthDynamicFeature(
				authFilter.setAuthenticator(gtcAuthenticator).setPrefix(tokenPrefix).setAuthorizer(gtcAuthoriser).buildAuthFilter());
		
		environment.jersey().register(gtcAuth);
		environment.jersey().register(RolesAllowedDynamicFeature.class);
		environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Auth0User.class));
	}
}