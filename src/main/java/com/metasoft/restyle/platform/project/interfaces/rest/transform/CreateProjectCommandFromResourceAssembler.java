package com.metasoft.restyle.platform.project.interfaces.rest.transform;

import com.metasoft.restyle.platform.project.domain.model.commands.CreateProjectCommand;
import com.metasoft.restyle.platform.project.interfaces.rest.resources.CreateProjectResource;

public class CreateProjectCommandFromResourceAssembler {

    public static CreateProjectCommand toCommandFromResource(CreateProjectResource resource){
        return new CreateProjectCommand(
                resource.name(),
                resource.image(),
                resource.description(),
                resource.businessId(),
                resource.contractorId(),
                resource.startDate(),
                resource.finishDate()
        );
    }

}
