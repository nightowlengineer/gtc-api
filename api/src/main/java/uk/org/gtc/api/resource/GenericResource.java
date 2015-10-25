package uk.org.gtc.api.resource;

import java.util.List;

import javax.ws.rs.PathParam;

import uk.org.gtc.api.domain.GenericDO;
import uk.org.gtc.api.service.GenericService;

public class GenericResource<T extends GenericDO>
{
	private GenericService<T> genericService;
	
	public GenericResource(GenericService<T> genericService)
	{
		this.genericService = genericService;
	}
	
	protected List<T> getAll()
	{
		return genericService.getAll();
	}
	
	protected T getItemById(@PathParam("id") String id)
	{
		return genericService.getById(id);
	}
	
	protected T createItem(T item) throws Exception
	{
		return genericService.create(item);
	}
}
