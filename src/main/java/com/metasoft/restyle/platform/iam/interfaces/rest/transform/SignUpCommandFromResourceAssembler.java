package com.metasoft.restyle.platform.iam.interfaces.rest.transform;

import com.metasoft.restyle.platform.iam.domain.model.commands.SignUpCommand;
import com.metasoft.restyle.platform.iam.domain.model.entities.Role;
import com.metasoft.restyle.platform.iam.interfaces.rest.resources.SignUpResource;

import java.util.ArrayList;

public class SignUpCommandFromResourceAssembler {
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        var roles = resource.roles() != null
                ? resource.roles().stream().map(name -> Role.toRoleFromName(name)).toList()
                : new ArrayList<Role>();
        System.out.print("Roles:");
        System.out.println(roles.getFirst().getName().name());
        return new SignUpCommand(resource.username(), resource.password(), roles, resource.email(), resource.firstName(), resource.paternalSurname(), resource.maternalSurname(), resource.description(), resource.phone(), resource.image());
    }
}
