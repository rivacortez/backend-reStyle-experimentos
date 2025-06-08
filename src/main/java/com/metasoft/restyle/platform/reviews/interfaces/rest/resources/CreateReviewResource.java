package com.metasoft.restyle.platform.reviews.interfaces.rest.resources;

public record CreateReviewResource(Integer contractorId, Integer projectId, String duration, Integer rating, String comment, String image) {
}
