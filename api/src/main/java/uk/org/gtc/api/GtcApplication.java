package uk.org.gtc.api;

import java.net.UnknownHostException;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import domain.MemberDO;
import health.BasicHealthCheck;
import health.MongoHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import resource.MemberResource;
import service.MemberService;

public class GtcApplication extends Application<GtcConfiguration> {
	public static void main(String[] args) throws Exception {
		new GtcApplication().run(args);
	}

	@Override
	public String getName() {
		return "gtc-api";
	}

	@Override
	public void initialize(Bootstrap<GtcConfiguration> bootstrap) {
		// nothing to do yet
	}

	@Override
	public void run(GtcConfiguration configuration, Environment environment) throws UnknownHostException {
		MongoClient mongo = new MongoClient(configuration.mongoHost, configuration.mongoPort);
		MongoManaged mongoManaged = new MongoManaged(mongo);
		environment.lifecycle().manage(mongoManaged);

		DB db = mongo.getDB("gtc-dev");

		JacksonDBCollection<MemberDO, String> members = JacksonDBCollection.wrap(db.getCollection("members"),
				MemberDO.class, String.class);

		// Health checks
		BasicHealthCheck basicHealthCheck = new BasicHealthCheck();
		MongoHealthCheck mongoHealthCheck = new MongoHealthCheck(mongo);

		// Services
		MemberService memberService = new MemberService(members);

		// Resources
		final MemberResource memberResource = new MemberResource(memberService);

		environment.healthChecks().register("basic", basicHealthCheck);
		environment.healthChecks().register("mongo", mongoHealthCheck);
		environment.jersey().register(memberResource);

	}

}