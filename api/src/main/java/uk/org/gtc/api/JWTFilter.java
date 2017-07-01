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
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64;

import io.dropwizard.auth.AuthenticationException;

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
		jwtVerifier = new JWTVerifier(new Base64(true).decode(configuration.auth0OfficeApiKey), configuration.auth0OfficeApiId);
	}
	
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException
	{
		final HttpServletRequest req = (HttpServletRequest) request;
		final HttpServletResponse resp = (HttpServletResponse) response;
		
		// Allow CORS preflights
		if (req.getMethod().equals("OPTIONS"))
		{
			return;
		}
		
		// Try to get the token from the request
		String token = null;
		try
		{
			token = getToken(req);
		}
		catch (final AuthenticationException ae)
		{
			resp.sendError(Status.UNAUTHORIZED.getStatusCode(), ae.getMessage());
		}
		
		// Valid token has been found, now carry out verification
		try
		{
			jwtVerifier.verify(token);
		}
		catch (final Exception e)
		{
			resp.sendError(Status.UNAUTHORIZED.getStatusCode(),
					"Token verification failed (" + e.getClass().getSimpleName() + "): " + e.getMessage());
		}
		
		chain.doFilter(request, response);
	}
	
	private String getToken(final HttpServletRequest httpRequest) throws AuthenticationException
	{
		final String authorizationHeader = httpRequest.getHeader("authorization");
		if (authorizationHeader == null)
		{
			throw new AuthenticationException("Unauthorized: No Authorization header was found");
		}
		
		final String[] parts = authorizationHeader.split(" ");
		if (parts.length != 2)
		{
			throw new AuthenticationException("Unauthorized: Format is Authorization: Bearer [token]");
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