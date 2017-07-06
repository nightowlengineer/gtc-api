package uk.org.gtc.api;

import org.mongojack.JacksonDBCollection;

import com.mongodb.DB;

import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.service.MemberService;

public class MemberServiceFactory
{
    private static MemberService instance;
    
    public static MemberService getInstance()
    {
        if (instance == null)
        {
            final DB db = DBFactory.getInstance();
            instance = new MemberService(JacksonDBCollection.wrap(db.getCollection("members"), MemberDO.class,
                    String.class));
        }
        
        return instance;
    }
}
