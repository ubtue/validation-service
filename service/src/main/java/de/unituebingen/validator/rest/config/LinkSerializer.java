package de.unituebingen.validator.rest.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import javax.ws.rs.core.Link;

/**
 * Custom json serializer for links.
 *
 */
public class LinkSerializer extends JsonSerializer<Link> {

	@Override
	public void serialize(Link link, JsonGenerator jg, SerializerProvider sp)
			throws IOException, JsonProcessingException {
		jg.writeStartObject();
		jg.writeStringField("rel", link.getRel());
		jg.writeStringField("href", link.getUri().toString().replace("%3F", "?"));
		jg.writeEndObject();
	}
}
