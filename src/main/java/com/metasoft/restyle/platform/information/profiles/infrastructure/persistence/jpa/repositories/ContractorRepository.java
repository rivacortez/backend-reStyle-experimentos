package com.metasoft.restyle.platform.information.profiles.infrastructure.persistence.jpa.repositories;

import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Contractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractorRepository extends JpaRepository<Contractor, Long> {
    Optional<Contractor> findByPhone(String phone);
    //Boolean existsByPhone(String phone);

}
