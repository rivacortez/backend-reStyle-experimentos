package com.metasoft.restyle.platform.project.application.internal.commandservices;

import com.metasoft.restyle.platform.project.domain.model.aggregates.Project;
import com.metasoft.restyle.platform.project.domain.model.commands.CreateProjectCommand;
import com.metasoft.restyle.platform.project.domain.services.ProjectCommandService;
import com.metasoft.restyle.platform.project.infrastructure.persistance.jpa.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;

import java.util.Optional;

@Service("com.metasoft.restyle.platform.project.application.internal.commandservices.ProjectCommandServiceImpl")
@Primary
public class ProjectCommandServiceImpl implements ProjectCommandService {

    private final ProjectRepository projectRepository;

    public ProjectCommandServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Optional<Project> handle(CreateProjectCommand command){
        if(projectRepository.existsByName(command.name())){
            throw new IllegalArgumentException("Project with same name already exists");
        }
        var project = new Project(command);
        var createdProject = projectRepository.save(project);
        return Optional.of(createdProject);
    }
}
