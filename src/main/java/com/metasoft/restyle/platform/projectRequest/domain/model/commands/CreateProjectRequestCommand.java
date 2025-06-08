package com.metasoft.restyle.platform.projectRequest.domain.model.commands;

import java.util.Date;

public record CreateProjectRequestCommand(
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
    public CreateProjectRequestCommand{
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (surname == null || surname.isBlank()) {
            throw new IllegalArgumentException("surname cannot be null or empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email cannot be null or empty");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("phone cannot be null or empty");
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("address cannot be null or empty");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("city cannot be null or empty");
        }
        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException("summary cannot be null or empty");
        }
        if (businessId == null) {
            throw new IllegalArgumentException("businessId cannot be null");
        }
        if (contractorId == null) {
            throw new IllegalArgumentException("contractorId cannot be null");
        }
        if (deadlineDate == null) {
            throw new IllegalArgumentException("deadlineDate cannot be null");
        }
        if (rooms == null) {
            throw new IllegalArgumentException("rooms cannot be null");
        }
        if (budget == null) {
            throw new IllegalArgumentException("budget cannot be null");
        }
    }
}
