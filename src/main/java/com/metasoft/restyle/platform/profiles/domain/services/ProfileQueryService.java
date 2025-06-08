package com.metasoft.restyle.platform.profiles.domain.services;

import com.metasoft.restyle.platform.profiles.domain.model.aggregates.Profile;
import com.metasoft.restyle.platform.profiles.domain.model.queries.GetAllProfilesQuery;
import com.metasoft.restyle.platform.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.metasoft.restyle.platform.profiles.domain.model.queries.GetProfileByIdQuery;

import java.util.List;
import java.util.Optional;

public interface ProfileQueryService {
    Optional<Profile> handle(GetProfileByEmailQuery query);
    Optional<Profile> handle(GetProfileByIdQuery query);
    List<Profile> handle(GetAllProfilesQuery query);
}