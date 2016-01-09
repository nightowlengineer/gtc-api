package uk.org.gtc.api.domain;

public enum MemberType
{
	FULL, ASSOCIATE, STUDENT, ACADEMIC, SPONSOR, AFFILIATE, RETIRED, FELLOW, HONORARY;
	
	public Boolean canUseLogo()
	{
		switch (this)
		{
		case FULL:
		case SPONSOR:
		case FELLOW:
		case HONORARY:
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
