package uk.org.gtc.api;

import java.io.IOException;
import java.util.List;

import com.auth0.Auth0User;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dropwizard.auth.Authorizer;
import uk.org.gtc.api.domain.ApplicationRole;
import uk.org.gtc.api.domain.UserAppMetadata;

public class GtcAuthoriser implements Authorizer<Auth0User>
{
	
	public GtcAuthoriser()
	{
	}
	
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
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final List<ApplicationRole> userRoles = uam.getRoles();
		
		if (userRoles.contains(appRole) || userRoles.contains(ApplicationRole.ADMIN))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
}
