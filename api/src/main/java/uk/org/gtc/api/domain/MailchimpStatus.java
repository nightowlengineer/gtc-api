package uk.org.gtc.api.domain;

public enum MailchimpStatus
{
    SUBSCRIBED("subscribed"), UNSUBSCRIBED("unsubscribed"), CLEANED("cleaned"), TRANSACTIONAL("transactional"), PENDING("pending");
    
    private final String status;
    
    private MailchimpStatus(final String status)
    {
        this.status = status;
    }
    
    public String getMailchimpValue()
    {
        return status;
    }
}
