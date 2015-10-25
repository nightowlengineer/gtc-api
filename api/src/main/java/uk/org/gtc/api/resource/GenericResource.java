package uk.org.gtc.api.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.codahale.metrics.annotation.Timed;

import uk.org.gtc.api.domain.GenericDO;
import uk.org.gtc.api.service.GenericService;

public class GenericResource<T extends GenericDO>
{
	private GenericService<T> genericService;
	
	public GenericResource(GenericService<T> genericService)
	{
		this.genericService = genericService;
	}
	
	@GET
	@Timed
	public List<T> getAll()
	{
		return genericService.getAll();
	}
	
	@GET
	@Timed
	@Path("id/{id}")
	public T getItemById(@PathParam("id") String id)
	{
		return genericService.getById(id);
	}
	
	public T createItem(T item) throws Exception
	{
		return genericService.create(item);
	}
}
