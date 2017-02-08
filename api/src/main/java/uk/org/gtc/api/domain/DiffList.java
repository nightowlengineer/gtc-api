package uk.org.gtc.api.domain;

import java.util.List;

public class DiffList
{
	List<Long> createdList;
	List<Long> updatedList;
	
	public DiffList()
	{
		// Jackson mapping
	}
	
	/**
	 * @return the createdList
	 */
	public List<Long> getCreatedList()
	{
		return createdList;
	}
	
	/**
	 * @return the updatedList
	 */
	public List<Long> getUpdatedList()
	{
		return updatedList;
	}
	
	/**
	 * @param createdList
	 *            the createdList to set
	 */
	public void setCreatedList(final List<Long> createdList)
	{
		this.createdList = createdList;
	}
	
	/**
	 * @param updatedList
	 *            the updatedList to set
	 */
	public void setUpdatedList(final List<Long> updatedList)
	{
		this.updatedList = updatedList;
	}
}
