package com.metasoft.restyle.platform.projectRequest.domain.model.queries;

public record GetAllProjectRequestsByContractorIdQuery(Long contractorId) {
    public GetAllProjectRequestsByContractorIdQuery {
        if (contractorId == null) {
            throw new IllegalArgumentException("ContractorId is required");
        }
    }
}
