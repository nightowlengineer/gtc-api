package uk.org.gtc.api.domain;

import java.util.Date;

public class MailchimpInfo
{
    private final Date lastChanged;
    private final String unsubscribeReason;
    private final MailchimpStatus status;
    
    public MailchimpInfo(final Date lastChanged, final String unsubscribeReason, final MailchimpStatus status)
    {
        super();
        this.lastChanged = lastChanged;
        this.unsubscribeReason = unsubscribeReason;
        this.status = status;
    }
    
    /**
     * @return the lastChanged
     */
    public Date getLastChanged()
    {
        return lastChanged;
    }
    
    /**
     * @return the unsubscribeReason
     */
    public String getUnsubscribeReason()
    {
        return unsubscribeReason;
    }
    
    /**
     * @return the status
     */
    public MailchimpStatus getStatus()
    {
        return status;
    }
}
