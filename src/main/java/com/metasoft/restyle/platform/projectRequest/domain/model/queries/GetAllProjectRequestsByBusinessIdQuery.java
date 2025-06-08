package com.metasoft.restyle.platform.projectRequest.domain.model.queries;

public record GetAllProjectRequestsByBusinessIdQuery(Long businessId) {
    public GetAllProjectRequestsByBusinessIdQuery {
        if (businessId == null) {
            throw new IllegalArgumentException("BusinessId is required");
        }
    }
}
