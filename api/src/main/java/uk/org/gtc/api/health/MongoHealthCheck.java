package uk.org.gtc.api.health;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.MongoClient;

public class MongoHealthCheck extends HealthCheck
{
	private MongoClient mongo;
	
	public MongoHealthCheck(MongoClient mongo)
	{
		this.mongo = mongo;
	}
	
	@Override
	protected Result check() throws Exception
	{
		mongo.getDatabaseNames();
		return Result.healthy();
	}
	
}
