package de.unituebingen.validator.rest.converters;

public interface Converter<ENTITY, REPRESENTATION> {

	public REPRESENTATION toRepresentation(ENTITY e);

}
