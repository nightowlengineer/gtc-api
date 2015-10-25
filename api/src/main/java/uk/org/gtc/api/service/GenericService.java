package uk.org.gtc.api.service;

import java.util.List;

import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import uk.org.gtc.api.domain.GenericDO;

public class GenericService<T extends GenericDO>
{
	private JacksonDBCollection<T, String> collection;
	
	public GenericService(JacksonDBCollection<T, String> collection)
	{
		this.collection = collection;
	}
	
	public T getById(String id)
	{
		return collection.findOneById(id);
	}
	
	public T create(T item)
	{
		WriteResult<T, String> result = collection.insert(item);
		return result.getSavedObject();
	}
	
	public List<T> getAll()
	{
		return collection.find().toArray();
	}
}
