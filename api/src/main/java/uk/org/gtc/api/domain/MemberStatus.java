package uk.org.gtc.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MemberStatus
{
    APPLIED, DECLINED, APPROVED, INVOICED, PAID, CURRENT, LAPSED, REMOVED;

    @JsonCreator
    public static MemberStatus fromString(final String key)
    {
        for (final MemberStatus type : MemberStatus.values())
        {
            if (type.name().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

    public Boolean requiresMemberNumber()
    {
        switch (this)
        {
            case APPLIED:
            case DECLINED:
            case APPROVED:
            case INVOICED:
            case PAID:
                return false;
            case CURRENT:
            case LAPSED:
            case REMOVED:
                return true;
            default:
                throw new RuntimeException("No requirement was set for this MemberStatus");
        }
    }
}
