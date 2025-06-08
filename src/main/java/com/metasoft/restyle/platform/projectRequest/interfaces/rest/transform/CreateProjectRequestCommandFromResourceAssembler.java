package com.metasoft.restyle.platform.projectRequest.interfaces.rest.transform;

import com.metasoft.restyle.platform.projectRequest.domain.model.commands.CreateProjectRequestCommand;
import com.metasoft.restyle.platform.projectRequest.interfaces.rest.resources.CreateProjectRequestResource;

public class CreateProjectRequestCommandFromResourceAssembler {

    public static CreateProjectRequestCommand toCommandFromResource(CreateProjectRequestResource resource){
        return new CreateProjectRequestCommand(
                resource.name(),
                resource.surname(),
                resource.email(),
                resource.phone(),
                resource.address(),
                resource.city(),
                resource.summary(),
                resource.businessId(),
                resource.contractorId(),
                resource.deadlineDate(),
                resource.rooms(),
                resource.budget()
        );
    }
}
