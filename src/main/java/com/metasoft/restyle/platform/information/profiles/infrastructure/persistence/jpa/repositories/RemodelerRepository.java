package com.metasoft.restyle.platform.information.profiles.infrastructure.persistence.jpa.repositories;

import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Remodeler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RemodelerRepository extends JpaRepository<Remodeler, Long> {
    Optional<Remodeler> findByPhone(String phone);
    //boolean existsByPhone(String phone);

}

