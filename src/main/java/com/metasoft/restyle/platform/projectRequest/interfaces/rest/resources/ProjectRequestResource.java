package com.metasoft.restyle.platform.projectRequest.interfaces.rest.resources;

import java.util.Date;

public record ProjectRequestResource(
        Long id,
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
        Integer budget){
}
