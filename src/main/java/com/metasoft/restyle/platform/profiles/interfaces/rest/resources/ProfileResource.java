package com.metasoft.restyle.platform.profiles.interfaces.rest.resources;

public record ProfileResource(
        Long id,
        String email,
        String password,
        String typeUser,
        String fullName
) {
}
