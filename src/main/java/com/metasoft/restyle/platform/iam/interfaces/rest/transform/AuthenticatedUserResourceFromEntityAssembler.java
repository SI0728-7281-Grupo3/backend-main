package com.metasoft.restyle.platform.iam.interfaces.rest.transform;

import com.metasoft.restyle.platform.iam.domain.model.aggregates.User;
import com.metasoft.restyle.platform.iam.domain.model.entities.Role;
import com.metasoft.restyle.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

import java.util.UUID;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        String primaryRole = user.getRoles() == null || user.getRoles().isEmpty()
                ? Role.getDefaultRole().getStringName()
                : user.getRoles().iterator().next().getStringName();

        String sessionId = UUID.randomUUID().toString();
        String firstName = user.getFirstName() != null && !user.getFirstName().isBlank()
                ? user.getFirstName()
                : user.getUsername();

        return new AuthenticatedUserResource(
                user.getId(),
                firstName,
                token,
                primaryRole,
                sessionId
        );
    }
}
