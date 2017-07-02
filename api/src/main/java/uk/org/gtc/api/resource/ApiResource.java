package uk.org.gtc.api.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
    @Path("version")
    public String getVersion()
    {
        final Properties prop = new Properties();
        String version = null;
        try (final InputStream input = this.getClass().getClassLoader().getResourceAsStream("config.properties"))
        {
            prop.load(input);
            version = prop.getProperty("version");
        }
        catch (final IOException ioe)
        {
            logger().warn("Unable to open config.properties", ioe);
        }
        return version;
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