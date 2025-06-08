package com.metasoft.restyle.platform.project.domain.services;

import com.metasoft.restyle.platform.project.domain.model.aggregates.Project;
import com.metasoft.restyle.platform.project.domain.model.commands.CreateProjectCommand;

import java.util.Optional;

public interface ProjectCommandService {

    Optional<Project> handle(CreateProjectCommand command);
}
