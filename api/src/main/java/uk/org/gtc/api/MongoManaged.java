package uk.org.gtc.api;

import com.mongodb.MongoClient;

import io.dropwizard.lifecycle.Managed;

public class MongoManaged implements Managed
{
	private final MongoClient mongo;
	
	public MongoManaged(final MongoClient mongo)
	{
		this.mongo = mongo;
	}
	
	@Override
	public void start() throws Exception
	{
		// stub method
	}
	
	@Override
	public void stop() throws Exception
	{
		mongo.close();
	}
	
}
