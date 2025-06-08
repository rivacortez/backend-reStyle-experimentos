package com.metasoft.restyle.platform.business.infrastructure.persistance.jpa;

import com.metasoft.restyle.platform.business.domain.model.aggregates.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import java.util.Optional;

@NonNullApi
@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    boolean existsByName(@NonNull String name);

    Optional<Business> findById(@NonNull Long id);
}
