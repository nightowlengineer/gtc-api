package uk.org.gtc.api.domain;

import java.util.List;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

public class Person extends BaseDomainObject
{
	private Salutation salutation;
	
	@Length(max = 50)
	private String firstName;
	
	@Length(max = 50)
	private String lastName;
	
	@Email
	private String email;
	
	private List<PhoneNumber> phoneNumbers;
	
	private List<Address> addresses;
	
	private String company;
	
	public Person()
	{
		// Jackson Mapping
	}
	
	public Person(Salutation salutation, String firstName, String lastName, String email, List<PhoneNumber> phoneNumbers,
			List<Address> addresses, String company)
	{
		setSalutation(salutation);
		setFirstName(firstName);
		setLastName(lastName);
		setEmail(email);
		setPhoneNumbers(phoneNumbers);
		setAddresses(addresses);
		setCompany(company);
	}
	
	public List<Address> getAddresses()
	{
		return addresses;
	}
	
	public void setAddresses(List<Address> addresses)
	{
		this.addresses = addresses;
	}
	
	public String getCompany()
	{
		return company;
	}
	
	public void setCompany(String company)
	{
		this.company = company;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public String getFirstName()
	{
		return firstName;
	}
	
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	public String getLastName()
	{
		return lastName;
	}
	
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	
	public List<PhoneNumber> getPhoneNumbers()
	{
		return phoneNumbers;
	}
	
	public void setPhoneNumbers(List<PhoneNumber> phoneNumbers)
	{
		this.phoneNumbers = phoneNumbers;
	}
	
	public Salutation getSalutation()
	{
		return salutation;
	}
	
	public void setSalutation(Salutation salutation)
	{
		this.salutation = salutation;
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
		Person other = (Person) obj;
		if (addresses == null)
		{
			if (other.addresses != null)
				return false;
		}
		else if (!addresses.equals(other.addresses))
			return false;
		if (company == null)
		{
			if (other.company != null)
				return false;
		}
		else if (!company.equals(other.company))
			return false;
		if (email == null)
		{
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		if (firstName == null)
		{
			if (other.firstName != null)
				return false;
		}
		else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null)
		{
			if (other.lastName != null)
				return false;
		}
		else if (!lastName.equals(other.lastName))
			return false;
		if (phoneNumbers == null)
		{
			if (other.phoneNumbers != null)
				return false;
		}
		else if (!phoneNumbers.equals(other.phoneNumbers))
			return false;
		if (salutation != other.salutation)
			return false;
		return true;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((addresses == null) ? 0 : addresses.hashCode());
		result = prime * result + ((company == null) ? 0 : company.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((phoneNumbers == null) ? 0 : phoneNumbers.hashCode());
		result = prime * result + ((salutation == null) ? 0 : salutation.hashCode());
		return result;
	}
	
}
