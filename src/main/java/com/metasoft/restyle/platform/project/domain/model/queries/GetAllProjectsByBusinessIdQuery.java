package com.metasoft.restyle.platform.project.domain.model.queries;

public record GetAllProjectsByBusinessIdQuery(Long businessId){
    public GetAllProjectsByBusinessIdQuery {
        if (businessId == null) {
            throw new IllegalArgumentException("BusinessId is required");
        }
    }
}
