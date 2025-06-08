package com.metasoft.restyle.platform.business.interfaces.rest;

import com.metasoft.restyle.platform.business.domain.model.aggregates.Business;
//import com.metasoft.restyle.platform.business.domain.model.queries.GetAllBusinessesQuery;
import com.metasoft.restyle.platform.business.domain.model.queries.GetAllBusinessesQuery;
import com.metasoft.restyle.platform.business.domain.model.queries.GetBusinessByIdQuery;
import com.metasoft.restyle.platform.business.domain.services.BusinessCommandService;
import com.metasoft.restyle.platform.business.domain.services.BusinessQueryService;
import com.metasoft.restyle.platform.business.interfaces.rest.resources.BusinessResource;
import com.metasoft.restyle.platform.business.interfaces.rest.resources.CreateBusinessResource;
import com.metasoft.restyle.platform.business.interfaces.rest.transform.BusinessResourceFromEntityAssembler;
import com.metasoft.restyle.platform.business.interfaces.rest.transform.CreateBusinessCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/businesses")
@Tag(name = "Businesses", description = "Business Management Endpoints")
public class BusinessesController {

    private final BusinessCommandService businessCommandService;

    private final BusinessQueryService businessQueryService;

    public BusinessesController(BusinessCommandService businessCommandService, BusinessQueryService businessQueryService) {
        this.businessCommandService = businessCommandService;
        this.businessQueryService = businessQueryService;
    }

    @GetMapping
    public ResponseEntity<List<BusinessResource>> getAllBusinesses(){
        var getAllBusinessesQuery = new GetAllBusinessesQuery();
        var businesses = businessQueryService.handle(getAllBusinessesQuery);
        var businessResources = businesses.stream().map(BusinessResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(businessResources);
    }

    @PostMapping
    public ResponseEntity<BusinessResource> createBusiness(@RequestBody CreateBusinessResource resource) {
        Optional<Business> business = businessCommandService.handle(CreateBusinessCommandFromResourceAssembler.toCommandFromResource(resource));
        return business.map(b -> new ResponseEntity<>(BusinessResourceFromEntityAssembler.toResourceFromEntity(b), CREATED)).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("{id}")
    public ResponseEntity<BusinessResource> getBusinessById(@PathVariable Long id) {
        Optional<Business> business = businessQueryService.handle(new GetBusinessByIdQuery(id));
        return business.map(b -> ResponseEntity.ok(BusinessResourceFromEntityAssembler.toResourceFromEntity(b))).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
