package service;

import java.util.List;
import java.util.ListIterator;

import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import domain.MemberDO;

public class MemberService {
	
	private JacksonDBCollection<MemberDO, String> collection;
	
	public MemberService(JacksonDBCollection<MemberDO, String> members) {
		this.collection = members;
	}

	public MemberDO getById(String id)
	{
		return collection.findOneById(id);
	}
	
	public MemberDO getByMemberNumber(Long memberNumber) throws Exception
	{
		List<MemberDO> members = findByMemberNumber(memberNumber);
		if (members.size() == 1) {
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
	
	public MemberDO create(MemberDO member)
	{
		WriteResult<MemberDO, String> result = collection.insert(member);
		return result.getSavedObject();
	}

	public List<MemberDO> getAll() {
		return collection.find().toArray();
	}
	
}
