package uk.org.gtc.api.health;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.MongoClient;

public class MongoHealthCheck extends HealthCheck
{
    private final MongoClient mongo;

    public MongoHealthCheck(final MongoClient mongo)
    {
        this.mongo = mongo;
    }

    @Override
    protected Result check() throws Exception
    {
        mongo.listDatabaseNames();
        return Result.healthy();
    }

}
