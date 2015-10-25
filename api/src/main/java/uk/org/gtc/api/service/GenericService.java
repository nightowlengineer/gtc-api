package uk.org.gtc.api.service;

import java.util.List;
import java.util.regex.Pattern;

import javax.ws.rs.WebApplicationException;

import org.bson.types.ObjectId;
import org.eclipse.jetty.server.Response;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import uk.org.gtc.api.domain.BaseDomainObject;

public class GenericService<T extends BaseDomainObject>
{
	private JacksonDBCollection<T, String> collection;
	
	public GenericService(JacksonDBCollection<T, String> collection)
	{
		this.collection = collection;
	}
	
	public T getById(String id) throws WebApplicationException
	{
		if (!ObjectId.isValid(id))
		{
			throw new WebApplicationException(Response.SC_BAD_REQUEST);
		}
		
		T item = collection.findOneById(id);
		
		if (item == null)
		{
			throw new WebApplicationException(Response.SC_NOT_FOUND);
		}
		return item;
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
	
	public DBCursor<T> getAllSorted(DBObject sort)
	{
		return collection.find().sort(sort);
	}
	
	/**
	 * Find a list of sorted, lightweight items
	 * 
	 * @param sort
	 *            - how to sort the returned list
	 * @param projection
	 *            - what to return out of the retrieved objects
	 * @return a sorted, lightweight list of T
	 */
	public List<T> getAllLightweightSorted(DBObject sort, DBObject projection, Integer limit)
	{
		return collection.find(new BasicDBObject(), projection).sort(sort).limit(limit).toArray();
	}
	
	/**
	 * Find a list of sorted, lightweight items
	 * 
	 * @param sort
	 *            - how to sort the returned list
	 * @param projection
	 *            - what to return out of the retrieved objects
	 * @return a sorted, lightweight list of T
	 */
	public T getLastBy(DBObject sort)
	{
		return getAllLightweightSorted(sort, new BasicDBObject(), 1).get(0);
	}
	
	protected List<T> searchByField(final String field, final String text)
	{
		final String regexPattern = "/.*" + text + ".*/i";
		final Pattern regex = Pattern.compile(regexPattern);
		return collection.find(DBQuery.regex(field, regex)).toArray();
	}
}
