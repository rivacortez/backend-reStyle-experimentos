package com.metasoft.restyle.platform.business.domain.model.commands;

public record CreateBusinessCommand(String name, String image, String expertise, String address, String city, String description, Integer remodelerId) {
    public CreateBusinessCommand{
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (expertise == null || expertise.isBlank()) {
            throw new IllegalArgumentException("expertise cannot be null or empty");
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("address cannot be null or empty");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("city cannot be null or empty");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description cannot be null or empty");
        }
        if (remodelerId == null) {
            throw new IllegalArgumentException("remodelerId cannot be null");
        }
    }

}
