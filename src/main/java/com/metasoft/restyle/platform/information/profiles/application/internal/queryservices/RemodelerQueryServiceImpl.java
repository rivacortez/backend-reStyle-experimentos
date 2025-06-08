package com.metasoft.restyle.platform.information.profiles.application.internal.queryservices;


import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Remodeler;
import com.metasoft.restyle.platform.information.profiles.domain.model.queries.GetAllRemodelerQuery;
import com.metasoft.restyle.platform.information.profiles.domain.model.queries.GetRemodelerByIdQuery;
import com.metasoft.restyle.platform.information.profiles.domain.services.RemodelerQueryService;
import com.metasoft.restyle.platform.information.profiles.infrastructure.persistence.jpa.repositories.RemodelerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RemodelerQueryServiceImpl implements RemodelerQueryService {

    private final RemodelerRepository remodelerRepository;

    public RemodelerQueryServiceImpl(RemodelerRepository remodelerRepository) {
        this.remodelerRepository = remodelerRepository;
    }

    public Optional<Remodeler> handle(GetRemodelerByIdQuery query) {
        return remodelerRepository.findById(query.getId());
    }

    @Override
    public List<Remodeler> handle(GetAllRemodelerQuery query) {
           return remodelerRepository.findAll();
    }
}
