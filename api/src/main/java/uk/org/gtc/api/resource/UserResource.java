package uk.org.gtc.api.resource;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.org.gtc.api.domain.User;
import uk.org.gtc.api.service.UserService;

@Path("/user")
@Api("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource extends GenericResource<User>
{
	private UserService userService;
	
	public UserResource(UserService userService)
	{
		super(userService);
		this.userService = userService;
	}
	
	@Override
	@GET
	@Timed
	@Path("all")
	@RolesAllowed("ADMIN")
	@ApiOperation(value = "Return a list of all users", response = User.class, responseContainer = "List")
	public List<User> getAll()
	{
		logger().debug("Fetching all members");
		return super.getAll();
	}
	
	@GET
	@Timed
	@Path("{username}")
	public User getByUsername(@PathParam("username") String username)
	{
		return userService.getByUsername(username);
	}
	
	@Override
	Logger logger()
	{
		return LoggerFactory.getLogger(UserResource.class);
	}
}