package de.unituebingen.validator.rest.config;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 * Resource configuration
 *
 */
@ApplicationPath("/rest/*")
public class ResourceConfig extends org.glassfish.jersey.server.ResourceConfig {

	public ResourceConfig() {
		super();
		register(MultiPartFeature.class);
		register(JacksonJaxbJsonProvider.class);
		register(JacksonFeature.class);
		packages("de.unituebingen.validator").register(DeclarativeLinkingFeature.class);
	}

}
