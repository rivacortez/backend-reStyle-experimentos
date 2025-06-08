package com.metasoft.restyle.platform.profiles.domain.model.valueobjects;

public record PersonName(String firstName, String paternalLastName, String maternalLastName) {

    public PersonName(){
        this(null, null, null);
    }

    //get Fullname
    public String getFullName() {
        return String.format("%s %s %s", firstName, paternalLastName, maternalLastName);
    }

    //validate null or blank
    public PersonName {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("Invalid first name");
        }
        if (paternalLastName == null || paternalLastName.isBlank()) {
            throw new IllegalArgumentException("Invalid paternal last name");
        }
        if (maternalLastName == null || maternalLastName.isBlank()) {
            throw new IllegalArgumentException("Invalid maternal last name");
        }
    }
}
