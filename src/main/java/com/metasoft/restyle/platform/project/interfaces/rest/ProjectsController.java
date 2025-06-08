package com.metasoft.restyle.platform.project.interfaces.rest;

import com.metasoft.restyle.platform.project.domain.model.aggregates.Project;
import com.metasoft.restyle.platform.project.domain.model.queries.GetAllProjects;
import com.metasoft.restyle.platform.project.domain.model.queries.GetAllProjectsByBusinessIdQuery;
import com.metasoft.restyle.platform.project.domain.model.queries.GetProjectByIdQuery;
import com.metasoft.restyle.platform.project.domain.services.ProjectCommandService;
import com.metasoft.restyle.platform.project.domain.services.ProjectQueryService;
import com.metasoft.restyle.platform.project.interfaces.rest.resources.CreateProjectResource;
import com.metasoft.restyle.platform.project.interfaces.rest.resources.ProjectResource;
import com.metasoft.restyle.platform.project.interfaces.rest.transform.CreateProjectCommandFromResourceAssembler;
import com.metasoft.restyle.platform.project.interfaces.rest.transform.ProjectResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController("com.metasoft.restyle.platform.project.interfaces.rest.ProjectsController")
@RequestMapping("/api/v1/projects")
@Primary
@Tag(name = "Projects", description = "Project Management Endpoints")
public class ProjectsController {

    private final ProjectCommandService projectCommandService;
    private final ProjectQueryService projectQueryService;

    public ProjectsController(ProjectCommandService projectCommandService, ProjectQueryService projectQueryService) {
        this.projectCommandService = projectCommandService;
        this.projectQueryService = projectQueryService;
    }

    @PostMapping
    public ResponseEntity<ProjectResource> createProject(@RequestBody CreateProjectResource resource){
        Optional<Project> project = projectCommandService.handle(CreateProjectCommandFromResourceAssembler.toCommandFromResource(resource));
        return project.map(p-> new ResponseEntity<>(ProjectResourceFromEntityAssembler.toResourceFromEntity(p), CREATED)).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<ProjectResource>> getAllProjects(){
        var projects = projectQueryService.handle(new GetAllProjects());
        if(projects.isEmpty()) return ResponseEntity.notFound().build();
        var projectResources = projects.stream().map(ProjectResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(projectResources);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProjectResource> getProjectById(@PathVariable Long id){
        Optional<Project> project = projectQueryService.handle(new GetProjectByIdQuery(id));
        return project.map(p -> ResponseEntity.ok(ProjectResourceFromEntityAssembler.toResourceFromEntity(p))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<List<ProjectResource>> getAllProjectsByBusinessId(Long businessId){
        var getAllProjectsByBusinessIdQuery = new GetAllProjectsByBusinessIdQuery(businessId);
        var projects = projectQueryService.handle(getAllProjectsByBusinessIdQuery);
        if(projects.isEmpty()) return ResponseEntity.notFound().build();
        var projectResources = projects.stream().map(ProjectResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(projectResources);
    }

    @GetMapping({"/search"})
    public ResponseEntity<?> getProjectsWithParameters(@RequestParam Map<String, String> parameters){
        if(parameters.containsKey("businessId")){
            return getAllProjectsByBusinessId(Long.parseLong(parameters.get("businessId")));
        }
        return ResponseEntity.badRequest().build();
    }

}
