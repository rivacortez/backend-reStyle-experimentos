package com.metasoft.restyle.platform.information.profiles.domain.services;


import com.metasoft.restyle.platform.information.profiles.domain.model.commands.CreateRemodelerCommand;


public interface RemodelerCommandService {
    Long handle(CreateRemodelerCommand command);
}
