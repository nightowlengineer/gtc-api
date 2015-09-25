package uk.org.gtc.api;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import resource.MemberResource;

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
    public void run(GtcConfiguration configuration,
                    Environment environment) {
        final MemberResource memberResource = new MemberResource();
        
        environment.jersey().register(memberResource);
    }

}