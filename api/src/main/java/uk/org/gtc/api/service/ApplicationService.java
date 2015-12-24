package uk.org.gtc.api.service;

import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;

import uk.org.gtc.api.domain.ApplicationDO;
import uk.org.gtc.api.domain.MemberStatus;

public class ApplicationService extends GenericService<ApplicationDO>
{
	private JacksonDBCollection<ApplicationDO, String> collection;
	
	public ApplicationService(JacksonDBCollection<ApplicationDO, String> applications)
	{
		super(applications);
		this.collection = applications;
	}
	
	public List<ApplicationDO> getAll()
	{
		final Query applicationQuery = DBQuery.notIn("memberStatus", MemberStatus.CURRENT, MemberStatus.LAPSED, MemberStatus.REMOVED);
		return collection.find(applicationQuery).toArray();
	}
	
}
