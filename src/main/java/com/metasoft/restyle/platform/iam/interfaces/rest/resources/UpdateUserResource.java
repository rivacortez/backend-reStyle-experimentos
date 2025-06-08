package com.metasoft.restyle.platform.iam.interfaces.rest.resources;

public record UpdateUserResource(String email, String description, String phone, String image) {
}
