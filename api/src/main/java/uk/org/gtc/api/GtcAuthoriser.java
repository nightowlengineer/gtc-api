package uk.org.gtc.api;

import com.auth0.Auth0User;

import io.dropwizard.auth.Authorizer;
import uk.org.gtc.api.domain.ApplicationRole;
import us.monoid.json.JSONArray;

public class GtcAuthoriser implements Authorizer<Auth0User>
{
	
	public GtcAuthoriser()
	{
	}
	
	@Override
	public boolean authorize(Auth0User user, String role)
	{
		final String appRole = ApplicationRole.valueOf(role).toString();
		try
		{
			JSONArray roles = user.getAppMetadata().getJSONArray("roles");
			if (roles != null && roles.length() > 0)
			{
				for (int i = 0; i < roles.length(); i++)
				{
					String fetchedRole = roles.getString(i);
					if (fetchedRole.equalsIgnoreCase(appRole))
						return true;
				}
			}
			return false;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
}
