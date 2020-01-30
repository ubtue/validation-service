package de.unituebingen.validator.rest.converters;

import java.util.ArrayList;
import java.util.List;

import de.unituebingen.validator.persistence.model.user.User;
import de.unituebingen.validator.rest.authentication.PasswordUtils;
import de.unituebingen.validator.rest.representations.LinkRelations;
import de.unituebingen.validator.rest.representations.UserRepresentation;
import de.unituebingen.validator.rest.representations.UsersRepresentation;

public class UserConverter {

	public static UserRepresentation toRepresentation(User user) {
		UserRepresentation userep = new UserRepresentation();
		userep.setId(user.getId());
		userep.setRole(user.getRole());
		userep.setUsername(user.getLogin());
		userep.setCreated(user.getCreated().getTime());
		userep.setLastModified(user.getLastModified().getTime());
		return userep;
	}

	public static UsersRepresentation toCollectionRepresentation(long totalCount, int pageIndex, int pageSize,
			List<User> userList) {
		UsersRepresentation usersrep = new UsersRepresentation(totalCount, userList.size(), pageSize, pageIndex);

		List<UserRepresentation> userReps = new ArrayList<>();
		for (User user : userList) {
			userReps.add(UserConverter.toRepresentation(user));
		}

		usersrep.getEmbedded().put(LinkRelations.USERS, userReps);
		return usersrep;
	}

	public static void updateEntity(UserRepresentation representation, User entity) {
		entity.setLogin(representation.getUsername());
		entity.setRole(representation.getRole());
		String password = representation.getPassword();
		if (password != null && password.length() != 0) {
			entity.setPasswordDigest(PasswordUtils.digestPassword(password));
		}
	}

}
