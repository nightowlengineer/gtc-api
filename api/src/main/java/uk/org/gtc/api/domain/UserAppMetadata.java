package uk.org.gtc.api.domain;

import java.util.List;

public class UserAppMetadata
{
	private List<ApplicationRole> roles;
	
	public UserAppMetadata()
	{
		// Jackson Mapping
	}
	
	public UserAppMetadata(List<ApplicationRole> roles)
	{
		setRoles(roles);
	}
	
	public List<ApplicationRole> getRoles()
	{
		return roles;
	}
	
	public void setRoles(List<ApplicationRole> roles)
	{
		this.roles = roles;
	}
}
