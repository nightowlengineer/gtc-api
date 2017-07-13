package uk.org.gtc.api;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoFactory
{
    private static MongoClient instance;
    
    public static void setInstance(final MongoClient instance)
    {
        MongoFactory.instance = instance;
    }
    
    public static MongoClient getInstance()
    {
        if (instance == null)
        {
            final GtcConfiguration configuration = GtcConfiguration.getInstance();
            final ServerAddress mongoHost = new ServerAddress(configuration.mongoHost);
            final List<MongoCredential> mongoCredentials = new ArrayList<>();
            final MongoCredential credential = MongoCredential.createScramSha1Credential(configuration.mongoUser,
                    configuration.mongoDatabase,
                    configuration.mongoPassword);
            mongoCredentials.add(credential);
            instance = new MongoClient(mongoHost, mongoCredentials);
        }
        return instance;
    }
}
