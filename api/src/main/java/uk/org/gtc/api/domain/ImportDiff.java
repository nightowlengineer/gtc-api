package uk.org.gtc.api.domain;

import java.util.Set;

public class ImportDiff
{
	Set<Long> createdSet;
	Set<Long> updatedSet;
	Set<Long> deletedSet;
	Set<Long> errorSet;
	Set<Long> importedSet;
	Set<Long> existingSet;
	
	public ImportDiff()
	{
		// Jackson mapping
	}
	
	/**
	 * @return the createdSet
	 */
	public Set<Long> getCreatedSet()
	{
		return createdSet;
	}
	
	/**
	 * @return the updatedSet
	 */
	public Set<Long> getUpdatedSet()
	{
		return updatedSet;
	}
	
	/**
	 * @param createdSet
	 *            the createdSet to set
	 */
	public void setCreatedSet(final Set<Long> createdSet)
	{
		this.createdSet = createdSet;
	}
	
	/**
	 * @param updatedSet
	 *            the updatedSet to set
	 */
	public void setUpdatedSet(final Set<Long> updatedSet)
	{
		this.updatedSet = updatedSet;
	}
	
	/**
	 * @return the deletedSet
	 */
	public Set<Long> getDeletedSet()
	{
		return deletedSet;
	}
	
	/**
	 * @param deletedSet
	 *            the deletedSet to set
	 */
	public void setDeletedSet(final Set<Long> deletedSet)
	{
		this.deletedSet = deletedSet;
	}
	
	/**
	 * @return the errorSet
	 */
	public Set<Long> getErrorSet()
	{
		return errorSet;
	}
	
	/**
	 * @param errorSet
	 *            the errorSet to set
	 */
	public void setErrorSet(final Set<Long> errorSet)
	{
		this.errorSet = errorSet;
	}
	
	/**
	 * @return the importedSet
	 */
	public Set<Long> getImportedSet()
	{
		return importedSet;
	}
	
	/**
	 * @param importedSet
	 *            the importedSet to set
	 */
	public void setImportedSet(final Set<Long> importedSet)
	{
		this.importedSet = importedSet;
	}
	
	/**
	 * @return the existingSet
	 */
	public Set<Long> getExistingSet()
	{
		return existingSet;
	}
	
	/**
	 * @param existingSet
	 *            the existingSet to set
	 */
	public void setExistingSet(final Set<Long> existingSet)
	{
		this.existingSet = existingSet;
	}
}
