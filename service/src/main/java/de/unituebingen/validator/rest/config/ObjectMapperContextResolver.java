package de.unituebingen.validator.rest.config;

import javax.ws.rs.core.Link;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Custom mapper context resolver for LinkSerializer
 *
 */
@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

	private final ObjectMapper mapper;

	public ObjectMapperContextResolver() {
		mapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Link.class, new LinkSerializer());
		simpleModule.addDeserializer(Link.class, new LinkDeserializer());
		mapper.registerModule(simpleModule);
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}
}