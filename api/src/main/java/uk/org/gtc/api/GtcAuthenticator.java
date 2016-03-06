package uk.org.gtc.api;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.Auth0User;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64;
import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class GtcAuthenticator implements Authenticator<String, Auth0User>
{
	Logger logger;
	
	@Context
	ServletRequest req;
	
	private JWTVerifier jwtVerifier = new JWTVerifier(
			new Base64(true).decode("4Svpbso0g4QhQcD8rxdOVVuG67OynKRIqRdjBqnPSFa7xDvK99H2DwRYgKzOz7YB"),
			"y8T1angMINFrNKwwiSec1DDhQaZB7zTq");
			
	public GtcAuthenticator(Logger logger)
	{
		this.logger = logger;
	}
	
	Logger logger()
	{
		return LoggerFactory.getLogger(GtcAuthenticator.class);
	}
	
	@Override
	public Optional<Auth0User> authenticate(String token) throws AuthenticationException
	{
		try
		{
			Map<String, Object> decoded = jwtVerifier.verify(token);
		}
		catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException | SignatureException | IOException
				| JWTVerifyException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AuthenticationException("Could not authenticate");
		}
		return Optional.of(Auth0User.get((HttpServletRequest) req));
	}
	
}
