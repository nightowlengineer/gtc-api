package uk.org.gtc.api.domain;

import java.time.Year;
import java.util.List;

import javax.validation.constraints.NotNull;

public class MemberDO extends ApplicationDO
{
	@NotNull
	private Long membershipNumber;
	
	public MemberDO()
	{
		// Jackson Mapping
	}
	
	public MemberDO(final Long membershipNumber, final ApplicationDO application)
	{
		new MemberDO(membershipNumber, application.getType(), application.getStatus(), application.getSalutation(),
				application.getFirstName(), application.getLastName(), application.getEmail(), application.getPhoneNumbers(),
				application.getAddresses(), application.getCurrentPost(), application.getCareerStartDate(), application.getSponsorMembers(),
				application.getReferralSource());
	}
	
	public MemberDO(final Long membershipNumber, final MemberType type, final MemberStatus status, final Salutation salutation,
			final String firstName, final String lastName, final String email, final List<PhoneNumber> phoneNumbers,
			final List<Address> addresses, final String currentPost, final Year careerStartDate, final List<Long> sponsorMembers,
			final String referralSource)
	{
		super(type, status, salutation, firstName, lastName, email, phoneNumbers, addresses, referralSource, careerStartDate,
				sponsorMembers, referralSource);
		setMembershipNumber(membershipNumber);
	}
	
	public Long getMembershipNumber()
	{
		return membershipNumber;
	}
	
	public void setMembershipNumber(final Long membershipNumber)
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
