package uk.org.gtc.api;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.Auth0User;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.auth.Authorizer;
import uk.org.gtc.api.domain.ApplicationRole;
import uk.org.gtc.api.domain.UserAppMetadata;

public class GtcAuthoriser implements Authorizer<Auth0User>
{
	
	@Override
	public boolean authorize(final Auth0User user, final String role)
	{
		final ApplicationRole appRole = ApplicationRole.valueOf(role);
		final String userAppMetadataString = user.getAppMetadata().toString();
		final ObjectMapper om = new ObjectMapper();
		
		UserAppMetadata uam = null;
		try
		{
			uam = om.readValue(userAppMetadataString, UserAppMetadata.class);
		}
		catch (final IOException ioe)
		{
			logger().debug("Couldn't read user's app metadata", ioe);
			return false;
		}
		
		final List<ApplicationRole> userRoles = uam.getRoles();
		if (userRoles != null)
		{
			final String userRolesString = userRoles.toString();
			logger().debug("User's roles: %s", userRolesString);
			return userRoles.contains(appRole) || userRoles.contains(ApplicationRole.ADMIN);
		}
		else
		{
			return false;
		}
	}
	
	Logger logger()
	{
		return LoggerFactory.getLogger(GtcAuthoriser.class);
	}
	
}
