package uk.org.gtc.api.domain;

import java.util.List;

public class UserAppMetadata
{
	private List<ApplicationRole> roles;
	private Long membershipNumber;
	
	public UserAppMetadata()
	{
		// Jackson Mapping
	}
	
	public UserAppMetadata(final List<ApplicationRole> roles)
	{
		setRoles(roles);
	}
	
	/**
	 * @return the membershipNumber
	 */
	public Long getMembershipNumber()
	{
		return membershipNumber;
	}
	
	public List<ApplicationRole> getRoles()
	{
		return roles;
	}
	
	/**
	 * @param membershipNumber
	 *            the membershipNumber to set
	 */
	public void setMembershipNumber(final Long membershipNumber)
	{
		this.membershipNumber = membershipNumber;
	}
	
	public void setRoles(final List<ApplicationRole> roles)
	{
		this.roles = roles;
	}
}
