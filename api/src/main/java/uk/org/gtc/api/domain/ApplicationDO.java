package uk.org.gtc.api.domain;

import java.time.Year;
import java.util.List;

public class ApplicationDO extends MemberDO
{
	private String currentPost;
	
	private Year careerStartDate;
	
	private List<Long> sponsorMembers;
	
	private String referralSource;
	
	public ApplicationDO()
	{
		// Jackson Mapping
	}
	
	public ApplicationDO(
	
	String currentPost, Year careerStartDate, List<Long> sponsorMembers, String referralSource)
	{
		// super(membershipNumber, type, status, salutation, firstName,
		// lastName, email, phoneNumbers, addresses)
		setCurrentPost(currentPost);
		setCareerStartDate(careerStartDate);
		setSponsorMembers(sponsorMembers);
		setReferralSource(referralSource);
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
		ApplicationDO other = (ApplicationDO) obj;
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
