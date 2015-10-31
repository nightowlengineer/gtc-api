package uk.org.gtc.api.resource;

import java.util.List;

import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;

import uk.org.gtc.api.domain.BaseDomainObject;
import uk.org.gtc.api.service.GenericService;

abstract class GenericResource<T extends BaseDomainObject>
{
	abstract Logger logger();
	
	private GenericService<T> genericService;
	
	public GenericResource()
	{
		// Empty constructor
	}
	
	public GenericResource(GenericService<T> genericService)
	{
		this.genericService = genericService;
	}
	
	protected List<T> getAll()
	{
		return genericService.getAll();
	}
	
	protected T getItemById(@PathParam("id") String id) throws WebApplicationException
	{
		return genericService.getById(id);
	}
	
	protected T createItem(T item) throws Exception
	{
		return genericService.create(item);
	}
}
