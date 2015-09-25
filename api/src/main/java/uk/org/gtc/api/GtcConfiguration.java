package uk.org.gtc.api;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

public class GtcConfiguration extends Configuration {

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
}