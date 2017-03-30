package uk.org.gtc.api.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * A HTTP 404 exception that produces an application/JSON response.
 */
public class MemberNotFoundException extends WebApplicationException
{
	private static final long serialVersionUID = -1971438440136687046L;
	
	/**
	 * Create a HTTP 404 exception.
	 */
	public MemberNotFoundException()
	{
		super("Member not found", Status.NOT_FOUND);
		
	}
	
	/**
	 * Create a HTTP 404 exception.
	 * 
	 * @param message
	 *            the String that is the exception message of the 409 response.
	 */
	public MemberNotFoundException(final String message)
	{
		super(Response.status(Status.NOT_FOUND).entity(new JSONExceptionMessageContainer(message)).type(MediaType.APPLICATION_JSON)
				.build());
	}
}