package com.metasoft.restyle.platform.information.profiles.interfaces.rest.transform;

import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Remodeler;
import com.metasoft.restyle.platform.information.profiles.interfaces.rest.resources.RemodelerResource;

public class RemodelerResourceFromEntityAssembler {

        public static RemodelerResource toResourceFromEntity(Remodeler remodeler) {
            return new RemodelerResource(
                    remodeler.getId(),
                    remodeler.getDescription(),
                    remodeler.getPhone(),
                    remodeler.getSubscription()
            );
        }
}
