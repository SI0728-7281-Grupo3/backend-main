package com.metasoft.restyle.platform.iam.interfaces.rest.resources;

public record AuthenticatedUserResource(Long id, String firstName, String token, String userRole, String sessionId) {
}
