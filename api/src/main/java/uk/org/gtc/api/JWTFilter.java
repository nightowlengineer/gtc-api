package uk.org.gtc.api;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64;

@WebFilter(filterName = "jwt-filter")
public class JWTFilter implements Filter
{
	private JWTVerifier jwtVerifier;
	
	private final GtcConfiguration configuration;
	
	public JWTFilter(final GtcConfiguration configuration)
	{
		this.configuration = configuration;
	}
	
	@Override
	public void init(final FilterConfig filterConfig) throws WebApplicationException
	{
		jwtVerifier = new JWTVerifier(new Base64(true).decode(configuration.auth0ApiKey), configuration.auth0ApiId);
	}
	
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException
	{
		final HttpServletRequest req = (HttpServletRequest) request;
		if (req.getMethod().equals("OPTIONS"))
		{
			return;
		}
		final String token = getToken((HttpServletRequest) request);
		try
		{
			jwtVerifier.verify(token);
			chain.doFilter(request, response);
		}
		catch (final Exception e)
		{
			throw new ServletException("Unauthorized: Token validation failed", e);
		}
	}
	
	private String getToken(final HttpServletRequest httpRequest) throws ServletException
	{
		final String authorizationHeader = httpRequest.getHeader("authorization");
		if (authorizationHeader == null)
		{
			throw new ServletException("Unauthorized: No Authorization header was found");
		}
		
		final String[] parts = authorizationHeader.split(" ");
		if (parts.length != 2)
		{
			throw new ServletException("Unauthorized: Format is Authorization: Bearer [token]");
		}
		
		final String scheme = parts[0];
		final String credentials = parts[1];
		
		final Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
		
		return pattern.matcher(scheme).matches() ? credentials : null;
	}
	
	@Override
	public void destroy()
	{
		
	}
	
}