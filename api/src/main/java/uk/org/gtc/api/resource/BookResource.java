package uk.org.gtc.api.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.org.gtc.api.domain.BookDO;
import uk.org.gtc.api.service.BookService;

@Path("book")
@Api("book")
@Produces(MediaType.APPLICATION_JSON)
public class BookResource extends GenericResource<BookDO>
{
	private BookService bookService;
	
	public BookResource(BookService bookService)
	{
		super(bookService);
		this.bookService = bookService;
	}
	
	@Override
	@GET
	@Timed
	@Path("all")
	@ApiOperation("Get a list of all books")
	public List<BookDO> getAll()
	{
		return super.getAll();
	}
	
	@Override
	@GET
	@Timed
	@Path("id/{id}")
	@ApiOperation("Get a book by GUID")
	public BookDO getItemById(@PathParam("id") String id) throws WebApplicationException
	{
		return super.getItemById(id);
	}
	
	@GET
	@Timed
	@Path("isbn/{isbn}")
	@ApiOperation("Get a book by ISBN")
	public List<BookDO> getItemByIsbn(@PathParam("isbn") String isbn) throws Exception
	{
		return bookService.findByIsbn(isbn);
	}
	
	@GET
	@Timed
	@Path("title/{title}")
	@ApiOperation("Get a list of books by title")
	public List<BookDO> findByTitle(@PathParam("title") String title) throws Exception
	{
		return bookService.findByTitle(title);
	}
	
	@GET
	@Timed
	@Path("author/{author}")
	@ApiOperation("Get a list of books by author")
	public List<BookDO> findByAuthor(@PathParam("author") String author) throws Exception
	{
		return bookService.findByAuthor(author);
	}
	
	@POST
	@Timed
	@ApiOperation("Create a new book")
	public BookDO createBook(BookDO book) throws Exception
	{
		return super.createItem(book);
	}
	
	@Override
	Logger logger()
	{
		return LoggerFactory.getLogger(BookResource.class);
	}
}