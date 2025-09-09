package com.metasoft.restyle.platform.projectRequest.infrastructure.persistance.jpa;

import com.metasoft.restyle.platform.projectRequest.domain.model.aggregates.ProjectRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRequestRepository extends JpaRepository<ProjectRequest, Long> {

    List<ProjectRequest> findAllByBusinessId(Long businessId);

    List<ProjectRequest> findAllByContractorId(Long contractorId);

    boolean existsByName(String name);

    Optional<ProjectRequest> findById(Long id);
}
