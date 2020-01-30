package de.unituebingen.validator.rest.authentication;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import de.unituebingen.validator.persistence.model.user.Role;

@javax.ws.rs.NameBinding
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface JWTTokenNeeded {
	Role Permission() default Role.NO_RIGHTS;
}
