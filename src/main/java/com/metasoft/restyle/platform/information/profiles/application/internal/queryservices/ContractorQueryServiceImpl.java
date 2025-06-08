package com.metasoft.restyle.platform.information.profiles.application.internal.queryservices;

import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Contractor;
import com.metasoft.restyle.platform.information.profiles.domain.model.queries.GetAllContractorQuery;
import com.metasoft.restyle.platform.information.profiles.domain.model.queries.GetContractorByIdQuery;
import com.metasoft.restyle.platform.information.profiles.domain.services.ContractorQueryService;
import com.metasoft.restyle.platform.information.profiles.infrastructure.persistence.jpa.repositories.ContractorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractorQueryServiceImpl implements ContractorQueryService {

    private final ContractorRepository contractorRepository;

    public ContractorQueryServiceImpl(ContractorRepository contractorRepository) {
        this.contractorRepository = contractorRepository;
    }

    @Override
    public List<Contractor> handle(GetAllContractorQuery query) {
        return contractorRepository.findAll();
    }

    @Override
    public Optional<Contractor> handle(GetContractorByIdQuery query) {
        return contractorRepository.findById(query.getId());
    }
}
