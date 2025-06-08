package com.metasoft.restyle.platform.information.profiles.interfaces.rest.transform;

import com.metasoft.restyle.platform.information.profiles.domain.model.commands.CreateRemodelerCommand;
import com.metasoft.restyle.platform.information.profiles.interfaces.rest.resources.CreateRemodelerResource;

public class CreateRemodelerCommandFromResourceAssembler {
    public static CreateRemodelerCommand toCommandFromResource(CreateRemodelerResource resource){
        return new CreateRemodelerCommand(
                resource.description(),
                resource.phone(),
                resource.subscription()
        );
    }
}
