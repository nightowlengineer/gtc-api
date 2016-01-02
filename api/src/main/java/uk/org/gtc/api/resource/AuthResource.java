package uk.org.gtc.api.resource;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.org.gtc.api.GtcAuthenticator;
import uk.org.gtc.api.domain.AuthDO;
import uk.org.gtc.api.domain.User;
import uk.org.gtc.api.service.AuthService;
import uk.org.gtc.api.service.UserService;

@Path("/auth")
@Api("/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource extends GenericResource<AuthDO>
{
	private AuthService authService;
	private UserService userService;
	
	public AuthResource(AuthService authService, UserService userService)
	{
		super(authService);
		this.authService = authService;
		this.userService = userService;
	}
	
	@Override
	@GET
	@Timed
	@Path("all")
	@RolesAllowed("ADMIN")
	@ApiOperation(value = "Return a list of all users", response = AuthDO.class, responseContainer = "List")
	public List<AuthDO> getAll()
	{
		logger().debug("Fetching all members");
		return super.getAll();
	}
	
	@POST
	@Timed
	@Path("create")
	public User create(final AuthDO user) throws NoSuchAlgorithmException, AuthenticationException
	{
		User newUser = null;
		Boolean success = false;
		if (authService.getByUsername(user.getUsername()) == null)
		{
			final GtcAuthenticator authenticator = new GtcAuthenticator(authService, logger());
			success = authenticator.createUser(user);
			newUser = userService.getByUsername(user.getUsername());
		}
		
		if (success)
			return newUser;
		else
			throw new AuthenticationException("Something went wrong. Code 888");
	}
	
	@GET
	@Timed
	@Path("logout")
	public Response logout(BasicCredentials credentials)
	{
		if (authService.getByUsername(credentials.getUsername()) == null)
		{
			return Response.ok().build();
		}
		else
		{
			return Response.ok().status(Status.UNAUTHORIZED).build();
		}
		
	}
	
	@Override
	Logger logger()
	{
		return LoggerFactory.getLogger(AuthResource.class);
	}
}