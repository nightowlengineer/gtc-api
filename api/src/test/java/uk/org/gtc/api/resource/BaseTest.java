package uk.org.gtc.api.resource;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import uk.org.gtc.api.DBFactory;
import uk.org.gtc.api.EmailService;
import uk.org.gtc.api.EmailServiceFactory;
import uk.org.gtc.api.MongoFactory;

/**
 * Unit test for simple App.
 */
public class BaseTest
{
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    final static MongoClient mongoClient = Mockito.mock(MongoClient.class);
    final static DB mongoDB = Mockito.mock(DB.class);
    final static EmailService emailService = Mockito.mock(EmailService.class);
    
    public BaseTest()
    {
        MongoFactory.setInstance(mongoClient);
        DBFactory.setInstance(mongoDB);
        EmailServiceFactory.setInstance(emailService);
    }
}
