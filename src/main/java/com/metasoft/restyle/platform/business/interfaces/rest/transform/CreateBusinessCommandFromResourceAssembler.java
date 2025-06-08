package com.metasoft.restyle.platform.business.interfaces.rest.transform;

import com.metasoft.restyle.platform.business.domain.model.commands.CreateBusinessCommand;
import com.metasoft.restyle.platform.business.interfaces.rest.resources.CreateBusinessResource;

public class CreateBusinessCommandFromResourceAssembler {

    public static CreateBusinessCommand toCommandFromResource(CreateBusinessResource resource){
        return new CreateBusinessCommand(
                resource.name(),
                resource.image(),
                resource.expertise(),
                resource.address(),
                resource.city(),
                resource.description(),
                resource.remodelerId()
        );
    }
}
