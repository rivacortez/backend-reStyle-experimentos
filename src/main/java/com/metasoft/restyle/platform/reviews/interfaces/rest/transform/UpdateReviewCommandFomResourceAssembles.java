package com.metasoft.restyle.platform.reviews.interfaces.rest.transform;

import com.metasoft.restyle.platform.reviews.domain.model.commands.UpdateReviewCommand;
import com.metasoft.restyle.platform.reviews.interfaces.rest.resources.UpdateReviewResource;

public class UpdateReviewCommandFomResourceAssembles {
    public static UpdateReviewCommand toCommandFromResource(Long reviewId, UpdateReviewResource resource){
        return new UpdateReviewCommand(
            reviewId,
            resource.duration(),
            resource.comment(),
            resource.image());
    }
}
