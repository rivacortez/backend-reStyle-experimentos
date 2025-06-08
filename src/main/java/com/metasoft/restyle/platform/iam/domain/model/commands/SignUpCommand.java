package com.metasoft.restyle.platform.iam.domain.model.commands;

import com.metasoft.restyle.platform.iam.domain.model.entities.Role;

import java.util.List;

public record SignUpCommand(String username, String password, List<Role> roles, String email, String firstName, String paternalSurname, String maternalSurname, String description, String phone, String image) {
}
