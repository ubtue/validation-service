package de.unituebingen.validator.rest.resources;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.unituebingen.validator.beans.SetupBean;
import de.unituebingen.validator.persistence.model.configuration.GlobalSettings;
import de.unituebingen.validator.persistence.model.user.Role;
import de.unituebingen.validator.rest.authentication.JWTTokenNeeded;
import de.unituebingen.validator.rest.converters.GlobalSettingsConverter;
import de.unituebingen.validator.rest.representations.SetupRepresentation;

@JWTTokenNeeded(Permission = Role.ADMIN)
@Path(SetupResource.PATH)
@Transactional(rollbackOn = Exception.class)
public class SetupResource {

	public static final String PATH = "setup";

	@Inject
	SetupBean setup;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional(value = TxType.SUPPORTS)
	public Response getSettings() {
		GlobalSettings globalSettings = setup.getGlobalSettings();
		return Response.ok(GlobalSettingsConverter.toRepresentation(globalSettings)).build();
	}

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateSettings(SetupRepresentation representation) {
		GlobalSettings newSettings = GlobalSettingsConverter.toEntity(representation);
		GlobalSettings globalSettings = setup.getGlobalSettings();

		if (newSettings.getId() != globalSettings.getId())
			throw new BadRequestException("id mismatch");

		newSettings.setVersion(globalSettings.getVersion());
		setup.updateGlobalSettings(newSettings);
		return Response.noContent().build();
	}

}
