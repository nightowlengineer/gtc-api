package uk.org.gtc.api.domain;

import java.security.Principal;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AuthDO extends User implements Principal
{
	@NotEmpty
	private String password;
	
	private String salt;
	
	public AuthDO()
	{
		// Jackson Mapping
	}
	
	@Override
	@JsonIgnore
	public String getName()
	{
		return getUsername();
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public String getSalt()
	{
		return salt;
	}
	
	public void setSalt(String salt)
	{
		this.salt = salt;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((salt == null) ? 0 : salt.hashCode());
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
		AuthDO other = (AuthDO) obj;
		if (password == null)
		{
			if (other.password != null)
				return false;
		}
		else if (!password.equals(other.password))
			return false;
		if (salt == null)
		{
			if (other.salt != null)
				return false;
		}
		else if (!salt.equals(other.salt))
			return false;
		return true;
	}
	
}
