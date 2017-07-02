package uk.org.gtc.api.domain;

public enum EmailTemplate
{
    OFFICE_MEMBER_UPDATE, MEMBER_MEMBER_WELCOME;

    public String getDefaultRecipient()
    {
        final String defaultRecipient = "services@gtc.org.uk";
        switch (this)
        {
            case OFFICE_MEMBER_UPDATE:
                return "notify.memberupdates@gtc.org.uk";
            default:
                return defaultRecipient;
        }
    }

    public String getDefaultSubject()
    {
        final String defaultPrefix = "[GTC]";
        switch (this)
        {
            case OFFICE_MEMBER_UPDATE:
                return defaultPrefix + " Notification of member updates";
            case MEMBER_MEMBER_WELCOME:
                return defaultPrefix + " Welcome to the GTC!";
            default:
                return defaultPrefix + " Office Notification";
        }
    }

    public String getSendGridTemplateId()
    {
        switch (this)
        {
            case MEMBER_MEMBER_WELCOME:
                return "efeb7ff2-75ef-4f2a-af50-a1fb863177d1";
            case OFFICE_MEMBER_UPDATE:
                return "bfa33b0c-bc78-48da-89d4-623cef65abc4";
            default:
                throw new RuntimeException("No ID was set for this EmailTemplate");
        }
    }
}
