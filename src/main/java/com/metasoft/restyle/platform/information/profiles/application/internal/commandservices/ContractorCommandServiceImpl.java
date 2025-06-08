package com.metasoft.restyle.platform.information.profiles.application.internal.commandservices;

import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Contractor;
import com.metasoft.restyle.platform.information.profiles.domain.model.commands.CreateContractorCommand;
import com.metasoft.restyle.platform.information.profiles.domain.services.ContractorCommandService;
import com.metasoft.restyle.platform.information.profiles.infrastructure.persistence.jpa.repositories.ContractorRepository;
import org.springframework.stereotype.Service;

@Service
public class ContractorCommandServiceImpl implements ContractorCommandService {

    private final ContractorRepository contractorRepository;

    public ContractorCommandServiceImpl(ContractorRepository contractorRepository) {
        this.contractorRepository = contractorRepository;
    }


    @Override
    public Long handle(CreateContractorCommand command) {
        var contractor = new Contractor(command.description(), command.phone());
        contractorRepository.save(contractor);
        return contractor.getId();
    }
}
