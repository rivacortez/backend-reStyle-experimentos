package com.metasoft.restyle.platform.iam.interfaces.rest.resources;

import java.util.List;

public record UserResource(Long id, String username, List<String> roles, String email, String firstName, String paternalSurname, String maternalSurname, String description, String phone, String image) {
}
