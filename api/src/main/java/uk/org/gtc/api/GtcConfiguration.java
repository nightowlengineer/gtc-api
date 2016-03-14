package uk.org.gtc.api;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class GtcConfiguration extends Configuration
{
	@JsonProperty
	@NotEmpty
	public String mongoHost;
	
	@JsonProperty
	@Min(1)
	@Max(65535)
	public int mongoPort;
	
	@JsonProperty
	public String mongoUser;
	
	@JsonProperty
	public char[] mongoPassword;
	
	@JsonProperty
	@NotEmpty
	public String mongoDatabase;
	
	@JsonProperty
	@NotEmpty
	public String corsOrigins;
	
	@JsonProperty
	@NotEmpty
	public String mandrillApiKey;
	
	@JsonProperty
	@NotEmpty
	public String auth0ApiId;
	
	@JsonProperty
	@NotEmpty
	public String auth0ApiKey;
	
	@JsonProperty
	@NotEmpty
	public String auth0TokenUrl;
	
	@JsonProperty("swagger")
	public SwaggerBundleConfiguration swaggerBundleConfiguration;
}