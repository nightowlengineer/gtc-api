package uk.org.gtc.api.service;

import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;

import uk.org.gtc.api.domain.AuthDO;

public class AuthService extends GenericService<AuthDO>
{
	private JacksonDBCollection<AuthDO, String> collection;
	
	public AuthService(JacksonDBCollection<AuthDO, String> users)
	{
		super(users);
		this.collection = users;
	}
	
	@Override
	public List<AuthDO> getAll()
	{
		return collection.find().toArray();
	}
	
	public AuthDO getByUsername(final String username)
	{
		final Query userQuery = DBQuery.is("username", username);
		
		final List<AuthDO> userResults = collection.find(userQuery).toArray();
		
		if (userResults != null && !userResults.isEmpty() && userResults.size() == 1)
		{
			return userResults.get(0);
		}
		else
		{
			return null;
		}
	}
	
}
