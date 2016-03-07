package uk.org.gtc.api;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.servlet.ServletRequest;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.Auth0User;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64;
import com.google.common.base.Optional;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

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
		JSONObject userJson = null;
		Auth0User user = null;
		try
		{
			jwtVerifier.verify(token);
			userJson = new JSONObject(Unirest.post("https://gtc.eu.auth0.com/tokeninfo").field("id_token", token).asString().getBody());
			user = new Auth0User(userJson);
		}
		catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException | SignatureException | IOException
				| JWTVerifyException | UnirestException | JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AuthenticationException("Could not authenticate");
		}
		return Optional.of(user);
	}
}
