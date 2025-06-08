package com.metasoft.restyle.platform.projectRequest.infrastructure.persistance.jpa;

import com.metasoft.restyle.platform.projectRequest.domain.model.aggregates.ProjectRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import java.util.List;
import java.util.Optional;

@NonNullApi
@Repository
public interface ProjectRequestRepository extends JpaRepository<ProjectRequest, Long> {

    List<ProjectRequest> findAllByBusinessId(@NonNull Integer businessId);

    List<ProjectRequest> findAllByContractorId(@NonNull Integer contractorId);

    boolean existsByName(@NonNull String name);

    Optional<ProjectRequest> findById(@NonNull Long id);
}
