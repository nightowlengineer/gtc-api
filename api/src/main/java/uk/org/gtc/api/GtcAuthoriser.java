package uk.org.gtc.api;

import io.dropwizard.auth.Authorizer;
import uk.org.gtc.api.domain.User;

public class GtcAuthoriser implements Authorizer<User>
{
	
	@Override
	public boolean authorize(User user, String role)
	{
		return user.getName().equals("good-guy") && role.equals("ADMIN");
	}
	
}
