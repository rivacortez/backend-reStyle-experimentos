package com.metasoft.restyle.platform.profiles.interfaces.rest.transform;

import com.metasoft.restyle.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.metasoft.restyle.platform.profiles.interfaces.rest.resources.CreateProfileResource;

public class CreateProfileCommandFromResourceAssembler {
    public static CreateProfileCommand toCommandFromResource(CreateProfileResource resource) {
        return new CreateProfileCommand(
                resource.email(),
                resource.password(),
                resource.typeUser(),
                resource.firstName(),
                resource.paternalSurname(),
                resource.maternalSurname()
        );
    }
}
