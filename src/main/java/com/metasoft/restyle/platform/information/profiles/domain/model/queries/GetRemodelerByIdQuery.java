package com.metasoft.restyle.platform.information.profiles.domain.model.queries;

public record GetRemodelerByIdQuery(Long ItemId) {

    public GetRemodelerByIdQuery {
        if (ItemId == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
    }


    public Long getId() {
        return ItemId;
    }
}
