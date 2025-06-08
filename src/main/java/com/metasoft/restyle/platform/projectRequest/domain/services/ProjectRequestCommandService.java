package com.metasoft.restyle.platform.projectRequest.domain.services;

import com.metasoft.restyle.platform.projectRequest.domain.model.aggregates.ProjectRequest;
import com.metasoft.restyle.platform.projectRequest.domain.model.commands.CreateProjectRequestCommand;

import java.util.Optional;

public interface ProjectRequestCommandService {
    Optional<ProjectRequest> handle(CreateProjectRequestCommand command);
}
