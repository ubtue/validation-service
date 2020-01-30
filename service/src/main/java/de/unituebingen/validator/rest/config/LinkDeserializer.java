package de.unituebingen.validator.rest.config;

import java.io.IOException;
import javax.ws.rs.core.Link;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Deserializer for links.
 *
 */
public class LinkDeserializer extends JsonDeserializer<Link> {

	/**
	 * Dummy method since we do not consider links on put / post.
	 */
	@Override
	public Link deserialize(JsonParser parser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return null;
	}

}
