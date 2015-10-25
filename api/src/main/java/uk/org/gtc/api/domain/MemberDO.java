package uk.org.gtc.api.domain;

import org.hibernate.validator.constraints.Length;

public class MemberDO extends GenericDO {

	private Long membershipNumber;

	private MemberType type;

	private MemberStatus status;

	private Salutation salutation;

	@Length(max = 20)
	private String firstName;

	@Length(max = 20)
	private String lastName;

	private String email;

	public MemberDO() {
		// Jackson Mapping
	}

	public MemberDO(Long membershipNumber, MemberType type, MemberStatus status, Salutation salutation,
			String firstName, String lastName, String email) {
		setMembershipNumber(membershipNumber);
		setType(type);
		setStatus(status);
		setSalutation(salutation);
		setFirstName(firstName);
		setLastName(lastName);
		setEmail(email);
	}

	public MemberType getType() {
		return type;
	}

	public void setType(MemberType type) {
		this.type = type;
	}

	public MemberStatus getStatus() {
		return status;
	}

	public void setStatus(MemberStatus status) {
		this.status = status;
	}

	public Salutation getSalutation() {
		return salutation;
	}

	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getMembershipNumber() {
		return membershipNumber;
	}

	public void setMembershipNumber(Long membershipNumber) {
		this.membershipNumber = membershipNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + (int) (membershipNumber ^ (membershipNumber >>> 32));
		result = prime * result + ((salutation == null) ? 0 : salutation.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemberDO other = (MemberDO) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (membershipNumber != other.membershipNumber)
			return false;
		if (salutation != other.salutation)
			return false;
		if (status != other.status)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
