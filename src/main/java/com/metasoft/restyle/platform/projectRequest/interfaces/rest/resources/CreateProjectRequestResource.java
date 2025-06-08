package com.metasoft.restyle.platform.projectRequest.interfaces.rest.resources;

import java.util.Date;

public record CreateProjectRequestResource(
        String name,
        String surname,
        String email,
        String phone,
        String address,
        String city,
        String summary,
        Integer businessId,
        Integer contractorId,
        Date deadlineDate,
        Integer rooms,
        Integer budget)
    {
        public CreateProjectRequestResource{
        if(name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if(surname == null) {
            throw new IllegalArgumentException("surname cannot be null");
        }
        if(email == null) {
            throw new IllegalArgumentException("email cannot be null");
        }
        if(phone == null) {
            throw new IllegalArgumentException("phone cannot be null");
        }
        if(address == null) {
            throw new IllegalArgumentException("address cannot be null");
        }
        if(city == null) {
            throw new IllegalArgumentException("city cannot be null");
        }
        if(summary == null) {
            throw new IllegalArgumentException("summary cannot be null");
        }
        if(businessId == null) {
            throw new IllegalArgumentException("businessId cannot be null");
        }
        if(contractorId == null) {
            throw new IllegalArgumentException("contractorId cannot be null");
        }
        if(deadlineDate == null) {
            throw new IllegalArgumentException("deadlineDate cannot be null");
        }
        if(rooms == null) {
            throw new IllegalArgumentException("rooms cannot be null");
        }
        if(budget == null) {
            throw new IllegalArgumentException("budget cannot be null");
        }
    }
}
