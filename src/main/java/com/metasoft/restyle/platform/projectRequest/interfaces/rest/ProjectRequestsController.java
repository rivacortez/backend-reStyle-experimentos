package com.metasoft.restyle.platform.projectRequest.interfaces.rest;


import com.metasoft.restyle.platform.projectRequest.domain.model.aggregates.ProjectRequest;
import com.metasoft.restyle.platform.projectRequest.domain.model.queries.GetAllProjectRequestsByBusinessIdQuery;
import com.metasoft.restyle.platform.projectRequest.domain.model.queries.GetAllProjectRequestsByContractorIdQuery;
import com.metasoft.restyle.platform.projectRequest.domain.model.queries.GetProjectRequestByIdQuery;
import com.metasoft.restyle.platform.projectRequest.domain.services.ProjectRequestCommandService;
import com.metasoft.restyle.platform.projectRequest.domain.services.ProjectRequestQueryService;
import com.metasoft.restyle.platform.projectRequest.interfaces.rest.resources.CreateProjectRequestResource;
import com.metasoft.restyle.platform.projectRequest.interfaces.rest.resources.ProjectRequestResource;
import com.metasoft.restyle.platform.projectRequest.interfaces.rest.transform.CreateProjectRequestCommandFromResourceAssembler;
import com.metasoft.restyle.platform.projectRequest.interfaces.rest.transform.ProjectRequestResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/project-requests")
@Tag(name = "Project Requests", description = "Project Request Management Endpoints")
public class ProjectRequestsController {

    private final ProjectRequestCommandService projectRequestCommandService;
    private final ProjectRequestQueryService projectRequestQueryService;

    public ProjectRequestsController(ProjectRequestCommandService projectRequestCommandService, ProjectRequestQueryService projectRequestQueryService) {
        this.projectRequestCommandService = projectRequestCommandService;
        this.projectRequestQueryService = projectRequestQueryService;
    }

    @PostMapping
    public ResponseEntity<ProjectRequestResource> createProjectRequest(@RequestBody CreateProjectRequestResource resource){
        Optional<ProjectRequest> projectRequest = projectRequestCommandService.handle(CreateProjectRequestCommandFromResourceAssembler.toCommandFromResource(resource));
        return projectRequest.map(p-> new ResponseEntity<>(ProjectRequestResourceFromEntityAssembler.toResourceFromEntity(p), CREATED)).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("{id}")
    public ResponseEntity<ProjectRequestResource> getProjectRequestById(@PathVariable Long id) {
        Optional<ProjectRequest> projectRequest = projectRequestQueryService.handle(new GetProjectRequestByIdQuery(id));
        return projectRequest.map(p -> ResponseEntity.ok(ProjectRequestResourceFromEntityAssembler.toResourceFromEntity(p))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<List<ProjectRequestResource>> getAllProjectRequestsByBusinessId(Long businessId){
        var getAllProjectRequestsByBusinessIdQuery = new GetAllProjectRequestsByBusinessIdQuery(businessId);
        var projectRequests = projectRequestQueryService.handle(getAllProjectRequestsByBusinessIdQuery);
        if(projectRequests.isEmpty()) return ResponseEntity.notFound().build();
        var projectRequestResources = projectRequests.stream().map(ProjectRequestResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(projectRequestResources);
    }

    private ResponseEntity<List<ProjectRequestResource>> getAllProjectRequestsByContractorId(Long contractorId){
        var getAllProjectRequestsByContractorId = new GetAllProjectRequestsByContractorIdQuery(contractorId);
        var projectRequests = projectRequestQueryService.handle(getAllProjectRequestsByContractorId);
        if(projectRequests.isEmpty()) return ResponseEntity.notFound().build();
        var projectRequestResources = projectRequests.stream().map(ProjectRequestResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(projectRequestResources);
    }

    @GetMapping
    private ResponseEntity<?> getProjectRequestsWithParameters(@RequestParam Map<String, String> parameters){
        if(parameters.containsKey("businessId")){
            return getAllProjectRequestsByBusinessId(Long.parseLong(parameters.get("businessId")));
        }
        if(parameters.containsKey("contractorId")){
            return getAllProjectRequestsByContractorId(Long.parseLong(parameters.get("contractorId")));
        }
        return ResponseEntity.badRequest().build();
    }
}
