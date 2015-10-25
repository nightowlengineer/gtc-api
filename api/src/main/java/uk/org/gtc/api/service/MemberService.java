package uk.org.gtc.api.service;

import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;

import uk.org.gtc.api.domain.MemberDO;

public class MemberService extends GenericService<MemberDO>
{
	private JacksonDBCollection<MemberDO, String> collection;
	
	public MemberService(JacksonDBCollection<MemberDO, String> members)
	{
		super(members);
		this.collection = members;
	}
	
	public MemberDO getByMemberNumber(Long memberNumber) throws Exception
	{
		final List<MemberDO> members = findByMemberNumber(memberNumber);
		if (members.size() == 1)
		{
			return members.get(0);
		}
		else
		{
			throw new Exception(members.size() + " members were found with the membership number '" + memberNumber + "'");
		}
	}
	
	public List<MemberDO> findByMemberNumber(Long memberNumber) throws Exception
	{
		return collection.find(DBQuery.is("membershipNumber", memberNumber)).toArray();
	}
	
}
