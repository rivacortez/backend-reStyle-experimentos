package com.metasoft.restyle.platform.project.infrastructure.persistance.jpa;

import com.metasoft.restyle.platform.project.domain.model.aggregates.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import java.util.List;
import java.util.Optional;

@NonNullApi
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByBusinessId(@NonNull Integer businessId);

    boolean existsByName(@NonNull String name);

    Optional<Project> findById(@NonNull Long id);
}
