package uk.org.gtc.api.service;

import java.util.List;

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
	
	public List<BookDO> findByIsbn(String isbn) throws Exception
	{
		return searchByField("isbn", isbn);
	}
	
	public List<BookDO> findByAuthor(String author) throws Exception
	{
		return searchByField("author", author);
	}
	
}
