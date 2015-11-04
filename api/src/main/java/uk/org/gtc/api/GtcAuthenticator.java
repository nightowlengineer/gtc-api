package uk.org.gtc.api;

import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import uk.org.gtc.api.domain.User;

public class GtcAuthenticator implements Authenticator<BasicCredentials, User>
{
	
	@Override
	public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException
	{
		if ("secret".equals(credentials.getPassword()))
		{
			return Optional.of(new User(credentials.getUsername()));
		}
		
		return Optional.absent();
	}
	
}
