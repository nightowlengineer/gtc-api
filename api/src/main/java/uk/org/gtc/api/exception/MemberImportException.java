package uk.org.gtc.api.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * This exception can be thrown during the import of a list of membership
 * information.
 */
public class MemberImportException extends WebApplicationException
{
	private static final long serialVersionUID = -1971438440136687046L;
	
	/**
	 * Create a HTTP 500 exception.
	 */
	public MemberImportException()
	{
		super("Member could not be imported", Status.INTERNAL_SERVER_ERROR);
		
	}
	
	/**
	 * Create a HTTP 500 exception.
	 * 
	 * @param message
	 *            the String that is the exception message of the 500 response.
	 */
	public MemberImportException(final String message)
	{
		super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(new JSONExceptionMessageContainer(message))
				.type(MediaType.APPLICATION_JSON).build());
	}
}