package com.metasoft.restyle.platform.projectRequest.domain.services;

import com.metasoft.restyle.platform.projectRequest.domain.model.aggregates.ProjectRequest;
import com.metasoft.restyle.platform.projectRequest.domain.model.queries.GetAllProjectRequestsByBusinessIdQuery;
import com.metasoft.restyle.platform.projectRequest.domain.model.queries.GetAllProjectRequestsByContractorIdQuery;
import com.metasoft.restyle.platform.projectRequest.domain.model.queries.GetProjectRequestByIdQuery;

import java.util.List;
import java.util.Optional;

public interface ProjectRequestQueryService {

    List<ProjectRequest> handle(GetAllProjectRequestsByBusinessIdQuery query);

    List<ProjectRequest> handle(GetAllProjectRequestsByContractorIdQuery query);

    Optional<ProjectRequest> handle(GetProjectRequestByIdQuery query);
}
