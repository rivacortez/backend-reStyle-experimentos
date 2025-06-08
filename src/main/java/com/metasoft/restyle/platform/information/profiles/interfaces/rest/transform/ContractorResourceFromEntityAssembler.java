package com.metasoft.restyle.platform.information.profiles.interfaces.rest.transform;


import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Contractor;
import com.metasoft.restyle.platform.information.profiles.interfaces.rest.resources.ContractorResource;


public class ContractorResourceFromEntityAssembler {

    public static ContractorResource toResourceFromEntity(Contractor entity){
        return new ContractorResource(
                entity.getId(),
                entity.getDescription(),
                entity.getPhone()
        );
    }



}
