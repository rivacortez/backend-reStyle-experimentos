package com.metasoft.restyle.platform.project.interfaces.rest.resources;

import java.util.Date;

public record CreateProjectResource(
        String name,
        String description,
        Integer businessId,
        Integer contractorId,
        Date startDate,
        Date finishDate,
        String image
) {
    public CreateProjectResource{
        if(name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if(description == null) {
            throw new IllegalArgumentException("description cannot be null");
        }
        if(businessId == null) {
            throw new IllegalArgumentException("businessId cannot be null");
        }
        if(contractorId == null) {
            throw new IllegalArgumentException("contractorId cannot be null");
        }
        if(startDate == null) {
            throw new IllegalArgumentException("startDate cannot be null");
        }
        if(finishDate == null) {
            throw new IllegalArgumentException("finishDate cannot be null");
        }
        if(image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
    }
}
