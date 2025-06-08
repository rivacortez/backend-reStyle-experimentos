package com.metasoft.restyle.platform.profiles.interfaces.rest.transform;

import com.metasoft.restyle.platform.profiles.domain.model.aggregates.Profile;
import com.metasoft.restyle.platform.profiles.interfaces.rest.resources.ProfileResource;

public class ProfileResourceFromEntityAssembler {
    public static ProfileResource toResourceFromEntity(Profile entity){
        return new ProfileResource(
                entity.getId(),
                entity.getEmailAddress(),
                entity.getPassword(),
                entity.getType(),
                entity.getFullName()
        );
    }
}
