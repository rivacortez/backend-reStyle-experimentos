package com.metasoft.restyle.platform.project.domain.services;

import com.metasoft.restyle.platform.project.domain.model.aggregates.Project;
import com.metasoft.restyle.platform.project.domain.model.queries.GetAllProjects;
import com.metasoft.restyle.platform.project.domain.model.queries.GetAllProjectsByBusinessIdQuery;
import com.metasoft.restyle.platform.project.domain.model.queries.GetProjectByIdQuery;

import java.util.List;
import java.util.Optional;

public interface ProjectQueryService {

    List<Project> handle(GetAllProjects query);
    List<Project> handle(GetAllProjectsByBusinessIdQuery query);
    Optional<Project> handle(GetProjectByIdQuery query);
}
