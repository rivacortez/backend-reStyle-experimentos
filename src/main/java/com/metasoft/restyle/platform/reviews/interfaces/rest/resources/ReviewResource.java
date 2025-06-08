package com.metasoft.restyle.platform.reviews.interfaces.rest.resources;

public record ReviewResource(Long id, Integer contractorId, Integer projectId, String duration, Integer rating, String comment, String image) {
}
