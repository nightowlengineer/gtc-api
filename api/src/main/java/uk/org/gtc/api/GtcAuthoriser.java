package uk.org.gtc.api;

import java.util.List;

import io.dropwizard.auth.Authorizer;
import uk.org.gtc.api.domain.ApplicationRole;
import uk.org.gtc.api.domain.AuthDO;
import uk.org.gtc.api.domain.User;
import uk.org.gtc.api.service.UserService;

public class GtcAuthoriser implements Authorizer<AuthDO>
{
	final UserService userService;
	
	public GtcAuthoriser(UserService userService)
	{
		this.userService = userService;
	}
	
	@Override
	public boolean authorize(AuthDO user, String role)
	{
		final ApplicationRole appRole = ApplicationRole.valueOf(role);
		final User fetchedUser = userService.getByUsername(user.getUsername());
		final List<ApplicationRole> userRoles = fetchedUser.getRoles();
		if (userRoles != null && !userRoles.isEmpty())
		{
			return userRoles.contains(appRole);
		}
		else
		{
			return false;
		}
	}
	
}
