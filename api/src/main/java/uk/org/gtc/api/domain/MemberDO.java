package uk.org.gtc.api.domain;

import java.time.Year;
import java.util.List;

import javax.validation.constraints.NotNull;

public class MemberDO extends Person
{
	@NotNull
	private Long membershipNumber;
	private String currentPost;
	private Year careerStartDate;
	private List<Long> sponsorMembers;
	private String referralSource;
	
	public MemberDO()
	{
		// Jackson Mapping
	}
	
	public MemberDO(final Long membershipNumber, final MemberType type, final MemberStatus status, final Salutation salutation,
			final String firstName, final String lastName, final String email, final List<PhoneNumber> phoneNumbers,
			final List<Address> addresses, final String currentPost, final Year careerStartDate, final List<Long> sponsorMembers,
			final String referralSource)
	{
		super(type, status, salutation, firstName, lastName, email, phoneNumbers, addresses);
		setMembershipNumber(membershipNumber);
		setCurrentPost(currentPost);
		setCareerStartDate(careerStartDate);
		setSponsorMembers(sponsorMembers);
		setReferralSource(referralSource);
	}
	
	public Long getMembershipNumber()
	{
		return membershipNumber;
	}
	
	public void setMembershipNumber(final Long membershipNumber)
	{
		this.membershipNumber = membershipNumber;
	}
	
	public String getCurrentPost()
	{
		return currentPost;
	}
	
	public void setCurrentPost(String currentPost)
	{
		this.currentPost = currentPost;
	}
	
	public Year getCareerStartDate()
	{
		return careerStartDate;
	}
	
	public void setCareerStartDate(Year careerStartDate)
	{
		this.careerStartDate = careerStartDate;
	}
	
	public List<Long> getSponsorMembers()
	{
		return sponsorMembers;
	}
	
	public void setSponsorMembers(List<Long> sponsorMembers)
	{
		this.sponsorMembers = sponsorMembers;
	}
	
	public String getReferralSource()
	{
		return referralSource;
	}
	
	public void setReferralSource(String referralSource)
	{
		this.referralSource = referralSource;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((careerStartDate == null) ? 0 : careerStartDate.hashCode());
		result = prime * result + ((currentPost == null) ? 0 : currentPost.hashCode());
		result = prime * result + ((membershipNumber == null) ? 0 : membershipNumber.hashCode());
		result = prime * result + ((referralSource == null) ? 0 : referralSource.hashCode());
		result = prime * result + ((sponsorMembers == null) ? 0 : sponsorMembers.hashCode());
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
		if (careerStartDate == null)
		{
			if (other.careerStartDate != null)
				return false;
		}
		else if (!careerStartDate.equals(other.careerStartDate))
			return false;
		if (currentPost == null)
		{
			if (other.currentPost != null)
				return false;
		}
		else if (!currentPost.equals(other.currentPost))
			return false;
		if (membershipNumber == null)
		{
			if (other.membershipNumber != null)
				return false;
		}
		else if (!membershipNumber.equals(other.membershipNumber))
			return false;
		if (referralSource == null)
		{
			if (other.referralSource != null)
				return false;
		}
		else if (!referralSource.equals(other.referralSource))
			return false;
		if (sponsorMembers == null)
		{
			if (other.sponsorMembers != null)
				return false;
		}
		else if (!sponsorMembers.equals(other.sponsorMembers))
			return false;
		return true;
	}
	
}
