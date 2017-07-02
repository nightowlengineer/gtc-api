package uk.org.gtc.api.exception;

/**
 * Simple container for mapping an exception message to a JSON object
 */
public final class JSONExceptionMessageContainer
{
    private String message;

    public JSONExceptionMessageContainer(final String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(final String message)
    {
        this.message = message;
    }
}