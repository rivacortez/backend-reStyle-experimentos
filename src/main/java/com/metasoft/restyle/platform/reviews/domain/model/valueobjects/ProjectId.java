package com.metasoft.restyle.platform.reviews.domain.model.valueobjects;

public record ProjectId(Integer projectId) {

    //validate that the projectId is equal to or greater than 1
    public ProjectId {
        if (projectId < 1) {
            throw new IllegalArgumentException("ProjectId must be greater than or equal to 1");
        }
    }
}
