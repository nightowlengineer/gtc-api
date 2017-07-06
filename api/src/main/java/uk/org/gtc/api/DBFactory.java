package uk.org.gtc.api;

import com.mongodb.DB;

public class DBFactory
{
    private static DB instance;
    
    public static DB getInstance()
    {
        if (instance == null)
        {
            instance = MongoFactory.getInstance().getDB(GtcConfiguration.getInstance().mongoDatabase);
        }
        return instance;
    }
}
