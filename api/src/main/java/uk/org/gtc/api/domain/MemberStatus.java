package uk.org.gtc.api.domain;

public enum MemberStatus
{
	APPLIED, DECLINED, APPROVED, INVOICED, PAID, CURRENT, LAPSED, REMOVED;
	
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
