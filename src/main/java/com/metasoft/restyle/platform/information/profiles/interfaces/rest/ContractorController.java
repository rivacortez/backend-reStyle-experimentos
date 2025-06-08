package com.metasoft.restyle.platform.information.profiles.interfaces.rest;

import com.metasoft.restyle.platform.information.profiles.domain.model.queries.GetAllContractorQuery;
import com.metasoft.restyle.platform.information.profiles.domain.model.queries.GetContractorByIdQuery;
import com.metasoft.restyle.platform.information.profiles.domain.services.ContractorCommandService;
import com.metasoft.restyle.platform.information.profiles.domain.services.ContractorQueryService;
import com.metasoft.restyle.platform.information.profiles.interfaces.rest.resources.ContractorResource;
import com.metasoft.restyle.platform.information.profiles.interfaces.rest.resources.CreateContractorResource;
import com.metasoft.restyle.platform.information.profiles.interfaces.rest.transform.ContractorResourceFromEntityAssembler;
import com.metasoft.restyle.platform.information.profiles.interfaces.rest.transform.CreateContractorCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/Contractors", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Contractors", description = "Contractor Management Endpoints")
public class ContractorController {

    private final ContractorQueryService contractorQueryService;
    private final ContractorCommandService contractorCommandService;

    public ContractorController(ContractorQueryService contractorQueryService, ContractorCommandService contractorCommandService) {
        this.contractorQueryService = contractorQueryService;
        this.contractorCommandService = contractorCommandService;
    }

    @PostMapping
    public ResponseEntity<ContractorResource> createContractor(@RequestBody CreateContractorResource resource) {
        var createContractorCommand = CreateContractorCommandFromResourceAssembler.toCommandFromResource(resource);
        var contractorId = contractorCommandService.handle(createContractorCommand);

        if (contractorId == 0L) {
            return ResponseEntity.badRequest().build();
        }
        var getContractorByIdQuery = new GetContractorByIdQuery(contractorId);
        var contractor = contractorQueryService.handle(getContractorByIdQuery);

        if (contractor.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var contractorResource = ContractorResourceFromEntityAssembler.toResourceFromEntity(contractor.get());
        return new ResponseEntity<>(contractorResource, HttpStatus.CREATED);
    }

    @GetMapping("/{contractorId}")
    public ResponseEntity<ContractorResource> getContractorById(@PathVariable Long contractorId) {
        var getContractorByIdQuery = new GetContractorByIdQuery(contractorId);
        var contractor = contractorQueryService.handle(getContractorByIdQuery);
        if (contractor.isEmpty()) return ResponseEntity.badRequest().build();
        var contractorResource = ContractorResourceFromEntityAssembler.toResourceFromEntity(contractor.get());
        return ResponseEntity.ok(contractorResource);
    }

    @GetMapping
    public ResponseEntity<List<ContractorResource>> getAllContractors() {
        var getAllContractorQuery = new GetAllContractorQuery();
        var contractors = contractorQueryService.handle(getAllContractorQuery);
        var contractorResources = contractors.stream().map(ContractorResourceFromEntityAssembler::toResourceFromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(contractorResources);
    }
}
