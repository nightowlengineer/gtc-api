package uk.org.gtc.api;

import java.net.UnknownHostException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.mongojack.JacksonDBCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import uk.org.gtc.api.domain.ApplicationDO;
import uk.org.gtc.api.domain.BookDO;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.health.BasicHealthCheck;
import uk.org.gtc.api.health.MongoHealthCheck;
import uk.org.gtc.api.resource.ApiResource;
import uk.org.gtc.api.resource.ApplicationResource;
import uk.org.gtc.api.resource.BookResource;
import uk.org.gtc.api.resource.MemberResource;
import uk.org.gtc.api.service.ApplicationService;
import uk.org.gtc.api.service.BookService;
import uk.org.gtc.api.service.MemberService;

public class GtcApplication extends Application<GtcConfiguration>
{
	
	public static void main(String[] args) throws Exception
	{
		new GtcApplication().run(args);
	}
	
	@Override
	public String getName()
	{
		return "gtc-api";
	}
	
	Logger logger()
	{
		return LoggerFactory.getLogger(GtcApplication.class);
	}
	
	@Override
	public void initialize(Bootstrap<GtcConfiguration> bootstrap)
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
	
	@Override
	public void run(GtcConfiguration configuration, Environment environment) throws UnknownHostException
	{
		FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
		filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
				"Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		
		final MongoClient mongo = new MongoClient(configuration.mongoHost, configuration.mongoPort);
		final MongoManaged mongoManaged = new MongoManaged(mongo);
		environment.lifecycle().manage(mongoManaged);
		
		final DB db = mongo.getDB("gtc-dev");
		
		final MandrillApi mandrill = new MandrillApi(configuration.mandrillApiKey);
		
		final JacksonDBCollection<MemberDO, String> members = JacksonDBCollection.wrap(db.getCollection("members"), MemberDO.class,
				String.class);
		final JacksonDBCollection<ApplicationDO, String> applications = JacksonDBCollection.wrap(db.getCollection("applications"),
				ApplicationDO.class, String.class);
		final JacksonDBCollection<BookDO, String> books = JacksonDBCollection.wrap(db.getCollection("books"), BookDO.class, String.class);
		
		// Health checks
		final BasicHealthCheck basicHealthCheck = new BasicHealthCheck();
		final MongoHealthCheck mongoHealthCheck = new MongoHealthCheck(mongo);
		
		// Services
		final MemberService memberService = new MemberService(members);
		final ApplicationService applicationService = new ApplicationService(applications);
		final BookService bookService = new BookService(books);
		
		// Resources
		final ApiResource apiResource = new ApiResource();
		final MemberResource memberResource = new MemberResource(memberService);
		final ApplicationResource applicationResource = new ApplicationResource(applicationService, memberService);
		final BookResource bookResource = new BookResource(bookService);
		
		environment.healthChecks().register("basic", basicHealthCheck);
		environment.healthChecks().register("mongo", mongoHealthCheck);
		environment.jersey().register(apiResource);
		environment.jersey().register(memberResource);
		environment.jersey().register(applicationResource);
		environment.jersey().register(bookResource);
	}
}