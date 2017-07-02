package uk.org.gtc.api.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * A HTTP 404 exception that produces an application/JSON response.
 */
public class ItemNotFoundException extends WebApplicationException
{
    private static final long serialVersionUID = -1971438440136687046L;

    /**
     * Create a HTTP 404 exception.
     */
    public ItemNotFoundException()
    {
        super("Item not found", Status.NOT_FOUND);

    }

    public ItemNotFoundException(final Class<?> clazz)
    {
        super(clazz.getName() + " not found", Status.NOT_FOUND);
    }

    public ItemNotFoundException(final Class<?> clazz, final String message)
    {
        super(Response.status(Status.NOT_FOUND).entity(new JSONExceptionMessageContainer(clazz.getName() + " not found. " + message))
                .type(MediaType.APPLICATION_JSON).build());
    }

    /**
     * Create a HTTP 404 exception.
     *
     * @param message
     *            the String that is the exception message of the 409 response.
     */
    public ItemNotFoundException(final String message)
    {
        super(Response.status(Status.NOT_FOUND).entity(new JSONExceptionMessageContainer(message)).type(MediaType.APPLICATION_JSON)
                .build());
    }
}