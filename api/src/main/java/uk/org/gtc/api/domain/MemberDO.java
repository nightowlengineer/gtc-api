package uk.org.gtc.api.domain;

import java.time.Year;
import java.util.Date;
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
    private Date applicationDate;
    private MemberType type;
    private MemberStatus status;

    public MemberDO()
    {
        // Jackson Mapping
    }

    public MemberDO(final MemberType type, final MemberStatus status, final Long membershipNumber, final Salutation salutation,
            final String firstName, final String lastName, final String email, final List<PhoneNumber> phoneNumbers,
            final List<Address> addresses, final String currentPost, final Year careerStartDate, final List<Long> sponsorMembers,
            final String referralSource, final Date applicationDate, final String company)
    {
        super(salutation, firstName, lastName, email, phoneNumbers, addresses, company);
        setType(type);
        setStatus(status);
        setMembershipNumber(membershipNumber);
        setCurrentPost(currentPost);
        setCareerStartDate(careerStartDate);
        setSponsorMembers(sponsorMembers);
        setReferralSource(referralSource);
        setApplicationDate(applicationDate);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final MemberDO other = (MemberDO) obj;
        if (applicationDate == null)
        {
            if (other.applicationDate != null)
            {
                return false;
            }
        }
        else if (!applicationDate.equals(other.applicationDate))
        {
            return false;
        }
        if (careerStartDate == null)
        {
            if (other.careerStartDate != null)
            {
                return false;
            }
        }
        else if (!careerStartDate.equals(other.careerStartDate))
        {
            return false;
        }
        if (currentPost == null)
        {
            if (other.currentPost != null)
            {
                return false;
            }
        }
        else if (!currentPost.equals(other.currentPost))
        {
            return false;
        }
        if (membershipNumber == null)
        {
            if (other.membershipNumber != null)
            {
                return false;
            }
        }
        else if (!membershipNumber.equals(other.membershipNumber))
        {
            return false;
        }
        if (referralSource == null)
        {
            if (other.referralSource != null)
            {
                return false;
            }
        }
        else if (!referralSource.equals(other.referralSource))
        {
            return false;
        }
        if (sponsorMembers == null)
        {
            if (other.sponsorMembers != null)
            {
                return false;
            }
        }
        else if (!sponsorMembers.equals(other.sponsorMembers))
        {
            return false;
        }
        if (status != other.status)
        {
            return false;
        }
        if (type != other.type)
        {
            return false;
        }
        return true;
    }

    /**
     * @return the applicationDate
     */
    public Date getApplicationDate()
    {
        return applicationDate;
    }

    public Year getCareerStartDate()
    {
        return careerStartDate;
    }

    public String getCurrentPost()
    {
        return currentPost;
    }

    public Long getMembershipNumber()
    {
        return membershipNumber;
    }

    public String getReferralSource()
    {
        return referralSource;
    }

    public List<Long> getSponsorMembers()
    {
        return sponsorMembers;
    }

    public MemberStatus getStatus()
    {
        return status;
    }

    public MemberType getType()
    {
        return type;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((applicationDate == null) ? 0 : applicationDate.hashCode());
        result = prime * result + ((careerStartDate == null) ? 0 : careerStartDate.hashCode());
        result = prime * result + ((currentPost == null) ? 0 : currentPost.hashCode());
        result = prime * result + ((membershipNumber == null) ? 0 : membershipNumber.hashCode());
        result = prime * result + ((referralSource == null) ? 0 : referralSource.hashCode());
        result = prime * result + ((sponsorMembers == null) ? 0 : sponsorMembers.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
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

    /**
     * @param applicationDate
     *            the applicationDate to set
     */
    public void setApplicationDate(final Date applicationDate)
    {
        this.applicationDate = applicationDate;
    }

    public void setCareerStartDate(final Year careerStartDate)
    {
        this.careerStartDate = careerStartDate;
    }

    public void setCurrentPost(final String currentPost)
    {
        this.currentPost = currentPost;
    }

    public void setMembershipNumber(final Long membershipNumber)
    {
        this.membershipNumber = membershipNumber;
    }

    public void setReferralSource(final String referralSource)
    {
        this.referralSource = referralSource;
    }

    public void setSponsorMembers(final List<Long> sponsorMembers)
    {
        this.sponsorMembers = sponsorMembers;
    }

    public void setStatus(final MemberStatus status)
    {
        this.status = status;
    }

    public void setType(final MemberType type)
    {
        this.type = type;
    }

    /**
     * Update this {@link MemberDO} with fields from a {@link CsvMember}.
     *
     * @param csvMember
     * @return an updated {@link MemberDO}
     */
    public MemberDO updateFromCsvMember(final CsvMember csvMember)
    {
        setEmail(csvMember.getEmail());
        setFirstName(csvMember.getFirstName());
        setLastName(csvMember.getLastName());
        setMembershipNumber(csvMember.getMembershipNumber());
        setSalutation(csvMember.getSalutation());
        setStatus(csvMember.getStatus());
        setType(csvMember.getType());
        return this;
    }

}
