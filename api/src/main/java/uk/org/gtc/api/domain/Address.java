package uk.org.gtc.api.domain;

import java.util.List;

public class Address
{
	private List<String> lines;
	
	private String country;
	
	private String postcode;
	
	private LocationType addressType;
	
	public Address()
	{
		// Jackson Mapping
	}
	
	public Address(final List<String> lines, final String country, final String postcode, final LocationType addressType)
	{
		setLines(lines);
		setCountry(country);
		setPostcode(postcode);
		setAddressType(addressType);
	}
	
	public LocationType getAddressType()
	{
		return addressType;
	}
	
	public void setAddressType(final LocationType addressType)
	{
		this.addressType = addressType;
	}
	
	public List<String> getLines()
	{
		return lines;
	}
	
	public void setLines(final List<String> lines)
	{
		this.lines = lines;
	}
	
	public String getCountry()
	{
		return country;
	}
	
	public void setCountry(final String country)
	{
		this.country = country;
	}
	
	public String getPostcode()
	{
		return postcode;
	}
	
	public void setPostcode(final String postcode)
	{
		this.postcode = postcode;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((lines == null) ? 0 : lines.hashCode());
		result = prime * result + ((postcode == null) ? 0 : postcode.hashCode());
		result = prime * result + ((addressType == null) ? 0 : addressType.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Address other = (Address) obj;
		if (country == null)
		{
			if (other.country != null)
				return false;
		}
		else if (!country.equals(other.country))
			return false;
		if (lines == null)
		{
			if (other.lines != null)
				return false;
		}
		else if (!lines.equals(other.lines))
			return false;
		if (postcode == null)
		{
			if (other.postcode != null)
				return false;
		}
		else if (!postcode.equals(other.postcode))
			return false;
		if (addressType != other.addressType)
			return false;
		return true;
	}
	
}
