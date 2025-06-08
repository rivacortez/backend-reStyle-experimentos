package com.metasoft.restyle.platform.business.interfaces.rest.resources;

public record CreateBusinessResource(String name, String description, String address, String city, String image, String expertise, Integer remodelerId) {

    public CreateBusinessResource{
        if(name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if(description == null) {
            throw new IllegalArgumentException("description cannot be null");
        }
        if(address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        if(city == null) {
            throw new IllegalArgumentException("city cannot be null");
        }
        if(expertise == null) {
            throw new IllegalArgumentException("expertise cannot be null");
        }
        if(remodelerId == null) {
            throw new IllegalArgumentException("remodelerId cannot be null");
        }
    }
}
