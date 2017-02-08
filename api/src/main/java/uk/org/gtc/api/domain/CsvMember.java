package uk.org.gtc.api.domain;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "salutation", "firstName", "lastName", "email", "membershipNumber", "type", "status" })
public class CsvMember
{
	private Salutation salutation;
	
	@Length(max = 50)
	@NotBlank
	private String firstName;
	
	@Length(max = 50)
	@NotBlank
	private String lastName;
	
	@Email(regexp = ".*@.*")
	@NotBlank
	private String email;
	
	private Long membershipNumber;
	private MemberType type;
	private MemberStatus status;
	
	public CsvMember()
	{
		// Jackson mapping
	}
	
	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}
	
	/**
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}
	
	/**
	 * @return the lastName
	 */
	public String getLastName()
	{
		return lastName;
	}
	
	/**
	 * @return the membershipNumber
	 */
	public Long getMembershipNumber()
	{
		return membershipNumber;
	}
	
	/**
	 * @return the salutation
	 */
	public Salutation getSalutation()
	{
		return salutation;
	}
	
	/**
	 * @return the status
	 */
	public MemberStatus getStatus()
	{
		return status;
	}
	
	/**
	 * @return the type
	 */
	public MemberType getType()
	{
		return type;
	}
	
	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(final String email)
	{
		this.email = email;
	}
	
	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}
	
	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}
	
	/**
	 * @param membershipNumber
	 *            the membershipNumber to set
	 */
	public void setMembershipNumber(final Long membershipNumber)
	{
		this.membershipNumber = membershipNumber;
	}
	
	/**
	 * @param salutation
	 *            the salutation to set
	 */
	public void setSalutation(final Salutation salutation)
	{
		this.salutation = salutation;
	}
	
	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(final MemberStatus status)
	{
		this.status = status;
	}
	
	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final MemberType type)
	{
		this.type = type;
	}
}
