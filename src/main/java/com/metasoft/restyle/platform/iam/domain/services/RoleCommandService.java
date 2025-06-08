package com.metasoft.restyle.platform.iam.domain.services;

import com.metasoft.restyle.platform.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {
    void handle(SeedRolesCommand command);
}
