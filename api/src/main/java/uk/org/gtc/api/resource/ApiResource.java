package uk.org.gtc.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}