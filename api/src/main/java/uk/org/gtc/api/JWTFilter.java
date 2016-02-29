package uk.org.gtc.api;

import java.io.IOException;
import java.util.Map;
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
	
	@Override
	public void init(FilterConfig filterConfig) throws WebApplicationException
	{
		jwtVerifier = new JWTVerifier(new Base64(true).decode("4Svpbso0g4QhQcD8rxdOVVuG67OynKRIqRdjBqnPSFa7xDvK99H2DwRYgKzOz7YB"),
				"y8T1angMINFrNKwwiSec1DDhQaZB7zTq");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		if (req.getMethod().equals("OPTIONS"))
		{
			return;
		}
		String token = getToken((HttpServletRequest) request);
		try
		{
			Map<String, Object> decoded = jwtVerifier.verify(token);
			// Do something with decoded information like UserId
			chain.doFilter(request, response);
		}
		catch (Exception e)
		{
			throw new ServletException("Unauthorized: Token validation failed", e);
		}
	}
	
	private String getToken(HttpServletRequest httpRequest) throws ServletException
	{
		String token = null;
		final String authorizationHeader = httpRequest.getHeader("authorization");
		if (authorizationHeader == null)
		{
			throw new ServletException("Unauthorized: No Authorization header was found");
		}
		
		String[] parts = authorizationHeader.split(" ");
		if (parts.length != 2)
		{
			throw new ServletException("Unauthorized: Format is Authorization: Bearer [token]");
		}
		
		String scheme = parts[0];
		String credentials = parts[1];
		
		Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
		if (pattern.matcher(scheme).matches())
		{
			token = credentials;
		}
		return token;
	}
	
	@Override
	public void destroy()
	{
	
	}
	
}