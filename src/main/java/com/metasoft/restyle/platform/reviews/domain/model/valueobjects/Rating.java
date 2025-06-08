package com.metasoft.restyle.platform.reviews.domain.model.valueobjects;

public record Rating(Integer rating) {

    //validate quantity between 1 and 5
    public Rating {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
}
