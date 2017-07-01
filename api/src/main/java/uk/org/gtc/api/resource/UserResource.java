package uk.org.gtc.api.resource;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.Auth0User;
import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.org.gtc.api.GtcConfiguration;
import uk.org.gtc.api.domain.ApplicationRole;
import uk.org.gtc.api.domain.UserAppMetadata;

@SuppressWarnings("rawtypes")
@Path("user")
@Api("user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource extends GenericResource
{
	private GtcConfiguration configuration;
	
	private String accessToken;
	private Date accessTokenExpiresAt;
	
	public UserResource(final GtcConfiguration configuration) throws Auth0Exception
	{
		this.configuration = configuration;
		this.accessToken = getAccessToken();
	}
	
	@GET
	@ApiOperation("Returns the current user's metadata")
	@Path("metadata/app")
	@PermitAll
	public String getUserAppMetadata(final @Context SecurityContext context)
	{
		final Auth0User prin = (Auth0User) context.getUserPrincipal();
		return prin.getAppMetadata().toString();
	}
	
	@GET
	@ApiOperation("Returns the current user's roleset")
	@Path("roles")
	@PermitAll
	public List<ApplicationRole> getUserRoles(final @Context SecurityContext context)
	{
		final Auth0User prin = (Auth0User) context.getUserPrincipal();
		final String userAppMetadataString = prin.getAppMetadata().toString();
		final ObjectMapper om = new ObjectMapper();
		try
		{
			return om.readValue(userAppMetadataString, UserAppMetadata.class).getRoles();
		}
		catch (final IOException e)
		{
			throw new WebApplicationException("Invalid roleset");
		}
	}
	
	@GET
	@Timed
	@Path("auth0/getActiveUserCount")
	@ApiOperation("Get count of Auth0 users active within the last 30 days")
	@RolesAllowed("MEMBERSHIP_MANAGE")
	public Integer getActiveAuth0Users() throws Auth0Exception
	{
		final ManagementAPI mgmt = new ManagementAPI(configuration.auth0Domain, getAccessToken());
		
		return mgmt.stats().getActiveUsersCount().execute();
	}
	
	@GET
	@Timed
	@Path("auth0/syncAuth0Users")
	@ApiOperation("Syncs Auth0 users with membership records")
	@RolesAllowed("MEMBERSHIP_MANAGE")
	public Response syncAuth0Users()
	{
		return Response.noContent().status(Status.NOT_IMPLEMENTED).build();
	}
	
	/**
	 * Gets the current access token, or requests a new token if the current one has expired.
	 * 
	 * Note that management API tokens cannot be refreshed/renewed:
	 * <a href="https://auth0.com/docs/api/management/v2/tokens#frequently-asked-questions">'Can I refresh my token?'</a>
	 * 
	 * @return a valid management token
	 * @throws Auth0Exception
	 */
	private String getAccessToken() throws Auth0Exception
	{
		// No access token or the expiry time has been reached
		if (this.accessToken == null || this.accessTokenExpiresAt == null || Calendar.getInstance().getTime().after(this.accessTokenExpiresAt))
		{
			logger().info("Getting new management token");
			final String audience = configuration.auth0Domain + "/api/v2/";
			final AuthAPI auth = new AuthAPI(configuration.auth0Domain, configuration.auth0MgmtApiId, configuration.auth0MgmtApiKey);
			
			final TokenHolder tokens = auth.requestToken(audience).execute();
			
			// Store the expiry time (defaults to 86400 seconds/24 hours)
			final Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, Math.toIntExact(tokens.getExpiresIn()));
			this.accessTokenExpiresAt = cal.getTime();
			
			this.accessToken = tokens.getAccessToken();
			return this.accessToken;
		}
		logger().debug("Using existing access token");
		return this.accessToken;
	}

	@Override
	Logger logger()
	{
		return LoggerFactory.getLogger(UserResource.class);
	}
}