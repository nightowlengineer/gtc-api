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
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.domain.MemberStatus;

public class MemberService extends GenericService<MemberDO>
{
	private JacksonDBCollection<MemberDO, String> collection;
	private MandrillApi mandrill;
	
	public MemberService(JacksonDBCollection<MemberDO, String> members, MandrillApi mandrill)
	{
		super(members);
		this.collection = members;
		this.mandrill = mandrill;
	}
	
	public MemberDO getByMemberNumber(Long memberNumber) throws MongoException
	{
		final List<MemberDO> members = findByMemberNumber(memberNumber);
		if (members.size() == 1)
		{
			return members.get(0);
		}
		else
		{
			throw new WebApplicationException(members.size() + " members were found with the membership number '" + memberNumber + "'",
					Response.SC_NOT_FOUND);
		}
	}
	
	public List<MemberDO> findByMemberNumber(Long memberNumber) throws MongoException
	{
		return collection.find(DBQuery.is("membershipNumber", memberNumber)).toArray();
	}
	
	public List<MemberDO> getByStatus(final MemberStatus... status)
	{
		return query(DBQuery.in("status", (Object[]) status));
	}
	
	public Long getNextMemberNumber()
	{
		MemberDO lastMember = getLastBy(DBSort.desc("membershipNumber"));
		Long nextMembershipNumber = lastMember.getMembershipNumber();
		nextMembershipNumber++;
		return nextMembershipNumber;
	}
	
	public Object sendEmail(MemberDO member) throws MandrillApiError, IOException
	{
		return EmailHelper.singleRecipient(mandrill, "welcome", member, Boolean.TRUE);
	}
	
}
