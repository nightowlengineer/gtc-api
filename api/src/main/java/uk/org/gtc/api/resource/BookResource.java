package uk.org.gtc.api.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import uk.org.gtc.api.domain.BookDO;
import uk.org.gtc.api.service.BookService;
import uk.org.gtc.api.service.MemberService;

@Path("book")
@Produces(MediaType.APPLICATION_JSON)
public class BookResource extends GenericResource<BookDO>
{
	private BookService bookService;
	private MemberService memberService;
	
	public BookResource(BookService bookService, MemberService memberService)
	{
		super(bookService);
		this.bookService = bookService;
		this.memberService = memberService;
	}
	
	@Override
	@GET
	@Timed
	public List<BookDO> getAll()
	{
		return super.getAll();
	}
	
	@Override
	@GET
	@Timed
	@Path("id/{id}")
	public BookDO getItemById(@PathParam("id") String id)
	{
		return super.getItemById(id);
	}
	
	@GET
	@Timed
	@Path("title/{title}")
	public List<BookDO> findByTitle(@PathParam("title") String title) throws Exception
	{
		return bookService.findByTitle(title);
	}
	
	@GET
	@Timed
	@Path("author/{author}")
	public List<BookDO> findByAuthor(@PathParam("author") String author) throws Exception
	{
		return bookService.findByAuthor(author);
	}
	
	@POST
	@Timed
	public BookDO createBook(BookDO book) throws Exception
	{
		if (true) // book doesn't exist
		{
			return super.createItem(book);
		}
		else
			throw new Exception("A book already exists with this title");
	}
}