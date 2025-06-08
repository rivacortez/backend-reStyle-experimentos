package com.metasoft.restyle.platform.profiles.domain.model.queries;

import com.metasoft.restyle.platform.profiles.domain.model.valueobjects.EmailAddress;

public record GetProfileByEmailQuery(EmailAddress emailAddress) {
}