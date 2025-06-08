package com.metasoft.restyle.platform.business.interfaces.rest.transform;

import com.metasoft.restyle.platform.business.domain.model.aggregates.Business;
import com.metasoft.restyle.platform.business.interfaces.rest.resources.BusinessResource;

public class BusinessResourceFromEntityAssembler {

    public static BusinessResource toResourceFromEntity(Business entity){
        return new BusinessResource(
                entity.getId(),
                entity.getName(),
                entity.getImage(),
                entity.getExpertise(),
                entity.getAddress(),
                entity.getCity(),
                entity.getDescription(),
                entity.getRemodelerId()
        );
    }
}
