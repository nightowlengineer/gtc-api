package uk.org.gtc.api.domain;

import java.util.List;

public class BookDO extends BaseDomainObject
{
	private String title;
	
	private String author;
	
	private String language;
	
	private List<String> owners;
	
	private String isbn;
	
	public BookDO()
	{
		// Jackson Mapping
	}
	
	public BookDO(final String title, final String author, final String language, final List<String> owners, final String isbn)
	{
		setTitle(title);
		setAuthor(author);
		setLanguage(language);
		setOwners(owners);
		setIsbn(isbn);
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(final String title)
	{
		this.title = title;
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	public void setAuthor(final String author)
	{
		this.author = author;
	}
	
	public String getLanguage()
	{
		return language;
	}
	
	public void setLanguage(final String language)
	{
		this.language = language;
	}
	
	public List<String> getOwners()
	{
		return owners;
	}
	
	public void setOwners(final List<String> owners)
	{
		this.owners = owners;
	}
	
	public String getIsbn()
	{
		return isbn;
	}
	
	public void setIsbn(final String isbn)
	{
		this.isbn = isbn;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((owners == null) ? 0 : owners.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BookDO other = (BookDO) obj;
		if (author == null)
		{
			if (other.author != null)
				return false;
		}
		else if (!author.equals(other.author))
			return false;
		if (owners == null)
		{
			if (other.owners != null)
				return false;
		}
		else if (!owners.equals(other.owners))
			return false;
		if (language == null)
		{
			if (other.language != null)
				return false;
		}
		else if (!language.equals(other.language))
			return false;
		if (title == null)
		{
			if (other.title != null)
				return false;
		}
		else if (!title.equals(other.title))
			return false;
		if (isbn == null)
		{
			if (other.isbn != null)
				return false;
		}
		else if (!isbn.equals(other.isbn))
			return false;
		return true;
	}
	
}
