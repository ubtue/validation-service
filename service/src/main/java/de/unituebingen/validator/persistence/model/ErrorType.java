package de.unituebingen.validator.persistence.model;

public enum ErrorType {
	NOT_FOUND("not_found"),
	BAD_REQUEST("bad_request"),
	METHOD_NOT_ALLOWED("method_not_allowed"),
	SERVICE_UNAVAILABLE("service_unavailable"),
	SERVER_ERROR("internal_server_error"),
	DELETED_ENTITY("entity_gone"),
	CONFLICT("conflict");
	
	String description;

	private ErrorType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
