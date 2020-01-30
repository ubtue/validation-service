package de.unituebingen.validator.persistence.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Role {
	@JsonProperty("noRights")
	NO_RIGHTS,
	@JsonProperty("user")
	USER,
	@JsonProperty("admin")
	ADMIN
}
