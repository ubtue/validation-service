package de.unituebingen.validator.rest.resources.enums;

import javax.ws.rs.BadRequestException;

public enum BatchesSortOrder {
	DATE_DESC, DATE_ASC;

	public static BatchesSortOrder fromString(String param) {
		String toUpper = param.toUpperCase();
		try {
			return valueOf(toUpper);
		} catch (Exception e) {
			throw new BadRequestException("Unknown sort order: " + param);
		}
	}

}
