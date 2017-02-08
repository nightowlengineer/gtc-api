package uk.org.gtc.api.service;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.eclipse.jetty.server.Response;
import org.mongojack.DBQuery;
import org.mongojack.DBSort;
import org.mongojack.JacksonDBCollection;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.mongodb.MongoException;

import uk.org.gtc.api.EmailHelper;
import uk.org.gtc.api.UtilityHelper;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.domain.MemberStatus;

public class MemberService extends GenericService<MemberDO>
{
	private final JacksonDBCollection<MemberDO, String> collection;
	private final MandrillApi mandrill;
	
	public MemberService(final JacksonDBCollection<MemberDO, String> members, final MandrillApi mandrill)
	{
		super(members);
		this.collection = members;
		this.mandrill = mandrill;
	}
	
	public List<MemberDO> findByMemberNumber(final Long memberNumber) throws MongoException
	{
		return collection.find(DBQuery.is("membershipNumber", memberNumber)).toArray();
	}
	
	public MemberDO getByMemberNumber(final Long memberNumber) throws MongoException
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
					Response.SC_NOT_FOUND);
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
	
	public Object sendEmail(final MemberDO member) throws MandrillApiError, IOException
	{
		return EmailHelper.singleRecipient(mandrill, "welcome", member, Boolean.TRUE);
	}
	
}
