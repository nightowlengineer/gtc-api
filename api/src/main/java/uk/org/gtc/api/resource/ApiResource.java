package uk.org.gtc.api.resource;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.Auth0User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@SuppressWarnings("rawtypes")
@Path("/")
@Api("/")
@Produces(MediaType.APPLICATION_JSON)
public class ApiResource extends GenericResource
{
	@GET
	@ApiOperation("Redirect to API documentation")
	public Response swagger()
	{
		return Response.temporaryRedirect(UriBuilder.fromPath("{arg1}").build("swagger")).build();
	}
	
	@Override
	Logger logger()
	{
		return LoggerFactory.getLogger(ApiResource.class);
	}
	
	@GET
	@ApiOperation("Returns the current user's metadata")
	@Path("user/metadata/app")
	@PermitAll
	public String getUserAppMetadata(@Context SecurityContext context)
	{
		Auth0User prin = (Auth0User) context.getUserPrincipal();
		return prin.getAppMetadata().toString();
		
	}
}