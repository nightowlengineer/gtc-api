package uk.org.gtc.api;

import java.net.UnknownHostException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.mongojack.JacksonDBCollection;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uk.org.gtc.api.domain.ApplicationDO;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.health.BasicHealthCheck;
import uk.org.gtc.api.health.MongoHealthCheck;
import uk.org.gtc.api.resource.ApplicationResource;
import uk.org.gtc.api.resource.MemberResource;
import uk.org.gtc.api.service.ApplicationService;
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
	
	@Override
	public void initialize(Bootstrap<GtcConfiguration> bootstrap)
	{
		// nothing to do yet
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
				
		// Health checks
		final BasicHealthCheck basicHealthCheck = new BasicHealthCheck();
		final MongoHealthCheck mongoHealthCheck = new MongoHealthCheck(mongo);
		
		// Services
		final MemberService memberService = new MemberService(members);
		final ApplicationService applicationService = new ApplicationService(applications);
		
		// Resources
		final MemberResource memberResource = new MemberResource(memberService);
		final ApplicationResource applicationResource = new ApplicationResource(applicationService, memberService);
		
		environment.healthChecks().register("basic", basicHealthCheck);
		environment.healthChecks().register("mongo", mongoHealthCheck);
		environment.jersey().register(memberResource);
		environment.jersey().register(applicationResource);
	}
}