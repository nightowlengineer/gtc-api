package uk.org.gtc.api.domain;

import java.time.Year;
import java.util.List;

import javax.xml.bind.ValidationException;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.dropwizard.validation.ValidationMethod;
import uk.org.gtc.api.UtilityHelper;

public class MemberDO extends Person
{
	private Long membershipNumber;
	private String currentPost;
	private Year careerStartDate;
	private List<Long> sponsorMembers;
	private String referralSource;
	private MemberType type;
	private MemberStatus status;
	
	public MemberDO()
	{
		// Jackson Mapping
	}
	
	public MemberDO(final MemberType type, final MemberStatus status, final Long membershipNumber, final Salutation salutation,
			final String firstName, final String lastName, final String email, final List<PhoneNumber> phoneNumbers,
			final List<Address> addresses, final String currentPost, final Year careerStartDate, final List<Long> sponsorMembers,
			final String referralSource, final String company)
	{
		super(salutation, firstName, lastName, email, phoneNumbers, addresses, company);
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
	
	public MemberStatus getStatus()
	{
		return status;
	}
	
	public void setStatus(MemberStatus status)
	{
		this.status = status;
	}
	
	public MemberType getType()
	{
		return type;
	}
	
	public void setType(MemberType type)
	{
		this.type = type;
	}
	
	@ValidationMethod(message = "A membership number is required when the memebr is in this status")
	@JsonIgnore
	public Boolean isMemberNumberRequired() throws ValidationException
	{
		return getStatus().requiresMemberNumber() && UtilityHelper.isNullOrEmpty(getMembershipNumber().toString());
	}
	
	@ValidationMethod(message = "A member can not be a sponsor without a company")
	@JsonIgnore
	public Boolean isSponsorAndCompanyValid()
	{
		return getType().equals(MemberType.SPONSOR) && UtilityHelper.isNullOrEmpty(super.getCompany());
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
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (status != other.status)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
