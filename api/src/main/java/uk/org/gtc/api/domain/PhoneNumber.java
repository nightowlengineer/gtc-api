package uk.org.gtc.api.domain;

public class PhoneNumber
{
	private LocationType phoneType;
	
	private String number;
	
	public PhoneNumber()
	{
		// Jackson Mapping
	}
	
	public PhoneNumber(LocationType phoneType, String number)
	{
		setPhoneType(phoneType);
		setNumber(number);
	}
	
	public LocationType getPhoneType()
	{
		return phoneType;
	}
	
	public void setPhoneType(LocationType phoneType)
	{
		this.phoneType = phoneType;
	}
	
	public String getNumber()
	{
		return number;
	}
	
	public void setNumber(String number)
	{
		this.number = number;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((phoneType == null) ? 0 : phoneType.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhoneNumber other = (PhoneNumber) obj;
		if (number == null)
		{
			if (other.number != null)
				return false;
		}
		else if (!number.equals(other.number))
			return false;
		if (phoneType != other.phoneType)
			return false;
		return true;
	}
	
}
