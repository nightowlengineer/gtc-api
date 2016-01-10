package uk.org.gtc.api.domain;

import java.util.List;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseDomainObject
{
	@Email
	@NotEmpty
	private String email;
	
	private List<ApplicationRole> roles;
	
	// @JsonIgnoreProperties(ignoreUnknown = true)
	public User()
	{
		// Jackson Mapping
	}
	
	public User(final String email)
	{
		setEmail(email);
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public List<ApplicationRole> getRoles()
	{
		return roles;
	}
	
	public void setRoles(List<ApplicationRole> roles)
	{
		this.roles = roles;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		User other = (User) obj;
		if (email == null)
		{
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		return true;
	}
	
}
