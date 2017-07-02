package uk.org.gtc.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MemberType
{
    FULL, ASSOCIATE, STUDENT, ACADEMIC, SPONSOR, AFFILIATE, RETIRED, FELLOW, HONORARY, IAWF, IAWF_ASSOCIATE;

    @JsonCreator
    public static MemberType fromString(final String key)
    {
        for (final MemberType type : MemberType.values())
        {

            if (type.name().equalsIgnoreCase(key.replace(" ", "_")))
            {
                return type;
            }
        }
        return null;
    }

    public Boolean canUseLogo()
    {
        switch (this)
        {
            case FULL:
            case SPONSOR:
            case FELLOW:
            case HONORARY:
            case IAWF:
                return true;
            case ASSOCIATE:
            case STUDENT:
            case ACADEMIC:
            case AFFILIATE:
            case RETIRED:
                return false;
            default:
                throw new RuntimeException("No value was set for this MemberType");
        }
    }
}
