package uk.org.gtc.api;

public class UtilityHelper
{
    public static Boolean isNull(final Object obj)
    {
        return obj == null;
    }

    public static Boolean isNullOrEmpty(final String string)
    {
        return string == null || string.isEmpty();
    }

    private UtilityHelper()
    {

    }
}