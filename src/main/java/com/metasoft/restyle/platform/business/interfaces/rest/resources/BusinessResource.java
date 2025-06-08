package com.metasoft.restyle.platform.business.interfaces.rest.resources;

public record BusinessResource(
        Long id,
        String name,
        String image,
        String expertise,
        String address,
        String city,
        String description,
        Integer remodelerId) {

}
