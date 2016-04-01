package uk.org.gtc.api.service;

import java.util.List;

import org.mongojack.JacksonDBCollection;

import uk.org.gtc.api.domain.BookDO;

public class BookService extends GenericService<BookDO>
{
	
	public BookService(final JacksonDBCollection<BookDO, String> books)
	{
		super(books);
	}
	
	public List<BookDO> findByTitle(final String title) throws Exception
	{
		return searchByField("title", title);
	}
	
	public List<BookDO> findByIsbn(final String isbn) throws Exception
	{
		return searchByField("isbn", isbn);
	}
	
	public List<BookDO> findByAuthor(final String author) throws Exception
	{
		return searchByField("author", author);
	}
	
}
