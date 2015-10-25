package uk.org.gtc.api.service;

import java.util.List;
import java.util.regex.Pattern;

import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;

import uk.org.gtc.api.domain.BookDO;

public class BookService extends GenericService<BookDO>
{
	private JacksonDBCollection<BookDO, String> collection;
	
	public BookService(JacksonDBCollection<BookDO, String> books)
	{
		super(books);
		this.collection = books;
	}
	
	public List<BookDO> findByTitle(String title) throws Exception
	{
		return searchByField("title", title);
	}
	
	public List<BookDO> findByAuthor(String author) throws Exception
	{
		return searchByField("author", author);
	}
	
	private List<BookDO> searchByField(final String field, final String text)
	{
		final String regexPattern = "/.*" + text + ".*/i";
		final Pattern regex = Pattern.compile(regexPattern);
		return collection.find(DBQuery.regex(field, regex)).toArray();
	}
	
}
