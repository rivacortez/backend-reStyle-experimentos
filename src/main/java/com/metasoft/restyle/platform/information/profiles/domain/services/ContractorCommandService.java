package com.metasoft.restyle.platform.information.profiles.domain.services;

import com.metasoft.restyle.platform.information.profiles.domain.model.commands.CreateContractorCommand;


public interface ContractorCommandService {
    Long handle(CreateContractorCommand command);

}
