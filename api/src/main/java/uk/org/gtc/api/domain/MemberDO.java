package uk.org.gtc.api.domain;

import java.util.List;

import javax.validation.constraints.NotNull;

public class MemberDO extends Person
{
	@NotNull
	private Long membershipNumber;
	
	public MemberDO()
	{
		// Jackson Mapping
	}
	
	public MemberDO(Long membershipNumber, MemberType type, MemberStatus status, Salutation salutation, String firstName, String lastName,
			String email, List<PhoneNumber> phoneNumbers, List<Address> addresses)
	{
		super(type, status, salutation, firstName, lastName, email, phoneNumbers, addresses);
		setMembershipNumber(membershipNumber);
	}
	
	public Long getMembershipNumber()
	{
		return membershipNumber;
	}
	
	public void setMembershipNumber(Long membershipNumber)
	{
		this.membershipNumber = membershipNumber;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((membershipNumber == null) ? 0 : membershipNumber.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemberDO other = (MemberDO) obj;
		if (membershipNumber == null)
		{
			if (other.membershipNumber != null)
				return false;
		}
		else if (!membershipNumber.equals(other.membershipNumber))
			return false;
		return true;
	}
	
}
