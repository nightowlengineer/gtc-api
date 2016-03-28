package uk.org.gtc.api.service;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.ws.rs.WebApplicationException;

import org.bson.types.ObjectId;
import org.eclipse.jetty.server.Response;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import uk.org.gtc.api.UtilityHelper;
import uk.org.gtc.api.domain.BaseDomainObject;

public class GenericService<T extends BaseDomainObject>
{
	private JacksonDBCollection<T, String> collection;
	
	public GenericService(final JacksonDBCollection<T, String> collection)
	{
		this.collection = collection;
	}
	
	public T getById(final String id) throws WebApplicationException
	{
		if (!ObjectId.isValid(id))
		{
			throw new WebApplicationException(Response.SC_BAD_REQUEST);
		}
		
		final T item = collection.findOneById(id);
		
		if (item == null)
		{
			throw new WebApplicationException(Response.SC_NOT_FOUND);
		}
		return item;
	}
	
	public T create(final T item)
	{
		final WriteResult<T, String> result = collection.insert(item);
		return result.getSavedObject();
	}
	
	/**
	 * Update an existing item
	 * 
	 * @param oldItem
	 *            - the item that already exists in the system. This will be
	 *            used in the future to generate a difference object.
	 * @param newItem
	 *            - the item that contains one or more updates to the oldItem.
	 * @return the updated item from the database
	 */
	public T update(final T oldItem, final T newItem)
	{
		if (UtilityHelper.isNull(oldItem.getCreatedDate()))
		{
			newItem.setCreatedDate(new Date());
		}
		newItem.setLastUpdatedDate(new Date());
		collection.updateById(newItem.getId(), newItem);
		return collection.findOneById(newItem.getId());
	}
	
	public Boolean delete(final T item)
	{
		return collection.removeById(item.getId()).getWriteResult().wasAcknowledged();
	}
	
	public List<T> query(final Query query)
	{
		return collection.find(query).toArray();
	}
	
	public List<T> getAll()
	{
		return collection.find().toArray();
	}
	
	public DBCursor<T> getAllSorted(final DBObject sort)
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
	public List<T> getAllLightweightSorted(final DBObject sort, final DBObject projection, final Integer limit)
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
	public T getLastBy(final DBObject sort)
	{
		return getAllLightweightSorted(sort, new BasicDBObject(), 1).get(0);
	}
	
	/**
	 * Find a list of items by named field
	 * 
	 * @param field
	 *            - what field to search over
	 * @param text
	 *            - what text to search for
	 * @return a list of matching items
	 */
	protected List<T> searchByField(final String field, final String text)
	{
		final String regexPattern = "/.*" + text + ".*/i";
		final Pattern regex = Pattern.compile(regexPattern);
		return collection.find(DBQuery.regex(field, regex)).toArray();
	}
}
