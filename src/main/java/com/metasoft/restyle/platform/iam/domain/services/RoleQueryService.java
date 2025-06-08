package com.metasoft.restyle.platform.iam.domain.services;

import com.metasoft.restyle.platform.iam.domain.model.entities.Role;
import com.metasoft.restyle.platform.iam.domain.model.queries.GetAllRolesQuery;
import com.metasoft.restyle.platform.iam.domain.model.queries.GetRoleByNameQuery;

import java.util.List;
import java.util.Optional;

public interface RoleQueryService {
    List<Role> handle(GetAllRolesQuery query);
    Optional<Role> handle(GetRoleByNameQuery query);
}
