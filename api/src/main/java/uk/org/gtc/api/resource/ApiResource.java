package uk.org.gtc.api.resource;

import java.io.IOException;
import java.util.List;

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
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.org.gtc.api.domain.ApplicationRole;
import uk.org.gtc.api.domain.UserAppMetadata;

@SuppressWarnings("rawtypes")
@Path("/")
@Api("/")
@Produces(MediaType.APPLICATION_JSON)
public class ApiResource extends GenericResource
{
	@GET
	@ApiOperation("Returns the current user's metadata")
	@Path("user/metadata/app")
	@PermitAll
	public String getUserAppMetadata(@Context SecurityContext context)
	{
		Auth0User prin = (Auth0User) context.getUserPrincipal();
		return prin.getAppMetadata().toString();
		
	}
	
	@GET
	@ApiOperation("Returns the current user's roleset")
	@Path("user/roles")
	@PermitAll
	public List<ApplicationRole> getUserRoles(@Context SecurityContext context)
	{
		final Auth0User prin = (Auth0User) context.getUserPrincipal();
		final String userAppMetadataString = prin.getAppMetadata().toString();
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
		
		return uam.getRoles();
	}
	
	@Override
	Logger logger()
	{
		return LoggerFactory.getLogger(ApiResource.class);
	}
	
	@GET
	@ApiOperation("Redirect to API documentation")
	public Response swagger()
	{
		return Response.temporaryRedirect(UriBuilder.fromPath("{arg1}").build("swagger")).build();
	}
}