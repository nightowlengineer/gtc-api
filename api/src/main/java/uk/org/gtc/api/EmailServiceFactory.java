package uk.org.gtc.api;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;
import com.sendgrid.SendGrid;

import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.service.MemberService;

public class EmailServiceFactory
{
    private static EmailService instance;
    
    public static void setInstance(final EmailService instance)
    {
        EmailServiceFactory.instance = instance;
    }
    
    public static EmailService getInstance()
    {
        if (instance == null)
        {
            final DB db = DBFactory.getInstance();
            final MemberService memberService = new MemberService(JacksonDBCollection.wrap(db.getCollection("members"), MemberDO.class,
                    String.class));
            final SendGrid sendgrid = new SendGrid(GtcConfiguration.getInstance().sendgridApiKey);
            instance = new EmailService(sendgrid, memberService);
        }
        
        return instance;
    }
}
