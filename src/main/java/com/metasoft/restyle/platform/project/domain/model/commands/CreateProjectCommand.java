package com.metasoft.restyle.platform.project.domain.model.commands;
import java.util.Date;

public record CreateProjectCommand(
        String name,
        String image,
        String description,
        Integer businessId,
        Integer contractorId,
        Date startDate,
        Date finishDate) {
    public CreateProjectCommand{
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description cannot be null or empty");
        }
        if (businessId == null) {
            throw new IllegalArgumentException("businessId cannot be null");
        }
        if (contractorId == null) {
            throw new IllegalArgumentException("contractorId cannot be null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("startDate cannot be null");
        }
        if (finishDate == null) {
            throw new IllegalArgumentException("finishDate cannot be null");
        }
        if (image == null || image.isBlank()) {
            throw new IllegalArgumentException("image cannot be null or empty");
        }
    }

}
