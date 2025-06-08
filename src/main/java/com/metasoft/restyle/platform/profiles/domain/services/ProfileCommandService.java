package com.metasoft.restyle.platform.profiles.domain.services;

import com.metasoft.restyle.platform.profiles.domain.model.commands.CreateProfileCommand;

public interface ProfileCommandService {
    Long handle(CreateProfileCommand command);
}
