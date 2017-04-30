package uk.org.gtc.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Salutation
{
	MR, MRS, MISS, MS, MX, DR, SIR;
	
	@JsonCreator
	public static Salutation fromString(final String key)
	{
		for (final Salutation sal : Salutation.values())
		{
			if (sal.name().equalsIgnoreCase(key))
			{
				return sal;
			}
		}
		return null;
	}
}
