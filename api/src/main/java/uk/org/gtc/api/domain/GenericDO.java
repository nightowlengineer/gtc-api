package uk.org.gtc.api.domain;

import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenericDO {

	@ObjectId
	@JsonProperty("_id")
	private String id;

}
