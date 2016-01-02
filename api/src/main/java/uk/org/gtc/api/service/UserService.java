package uk.org.gtc.api.service;

import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;

import uk.org.gtc.api.domain.User;

public class UserService extends GenericService<User>
{
	private JacksonDBCollection<User, String> collection;
	
	public UserService(JacksonDBCollection<User, String> users)
	{
		super(users);
		this.collection = users;
	}
	
	@Override
	public List<User> getAll()
	{
		return collection.find().toArray();
	}
	
	public User getByUsername(final String username)
	{
		final Query userQuery = DBQuery.is("username", username);
		
		final List<User> userResults = collection.find(userQuery).toArray();
		
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
