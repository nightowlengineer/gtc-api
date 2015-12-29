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
	public String mongoHost = "localhost";
	
	@JsonProperty
	@Min(1)
	@Max(65535)
	public int mongoPort = 27017;
	
	@JsonProperty
	@NotEmpty
	public String mongoDatabase = "gtc-dev";
	
	@JsonProperty
	@NotEmpty
	public String corsOrigins = "*";
	
	@JsonProperty
	@NotEmpty
	public String mandrillApiKey;
	
	@JsonProperty("swagger")
	public SwaggerBundleConfiguration swaggerBundleConfiguration;
}