package uk.org.gtc.api.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;

import org.mongojack.DBQuery;
import org.mongojack.DBSort;
import org.mongojack.JacksonDBCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoException;

import uk.org.gtc.api.UtilityHelper;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.domain.MemberStatus;
import uk.org.gtc.api.exception.MemberNotFoundException;

public class MemberService extends GenericService<MemberDO>
{
    private final JacksonDBCollection<MemberDO, String> collection;

    public MemberService(final JacksonDBCollection<MemberDO, String> members)
    {
        super(members);
        this.collection = members;
    }

    public List<MemberDO> findByMemberNumber(final Long memberNumber)
    {
        final List<MemberDO> members = new ArrayList<>();
        try
        {
            members.addAll(collection.find(DBQuery.is("membershipNumber", memberNumber)).toArray());
        }
        catch (final MongoException me)
        {
            logger().error("Encountered error when looking up member number", me);
            throw new MemberNotFoundException("Member " + memberNumber + "could not be found");
        }
        return members;
    }

    public MemberDO getByMemberNumber(final Long memberNumber)
    {
        final List<MemberDO> members = findByMemberNumber(memberNumber);
        if (members.size() == 1)
        {
            return members.get(0);
        }
        else if (members.isEmpty())
        {
            return null;
        }
        else
        {
            throw new WebApplicationException(members.size() + " members were found with the membership number '" + memberNumber + "'",
                    HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public List<MemberDO> getByStatus(final MemberStatus... status)
    {
        return query(DBQuery.in("status", (Object[]) status));
    }

    public Long getNextMemberNumber()
    {
        final MemberDO lastMember = getLastBy(DBSort.desc("membershipNumber"));
        Long nextMembershipNumber = UtilityHelper.isNull(lastMember) ? 0 : lastMember.getMembershipNumber();
        nextMembershipNumber++;
        return nextMembershipNumber;
    }

    @Override
    Logger logger()
    {
        return LoggerFactory.getLogger(MemberService.class);
    }

}
