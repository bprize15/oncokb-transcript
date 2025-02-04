package org.mskcc.oncokb.curation.repository;

import java.util.Optional;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.mskcc.oncokb.curation.domain.SpecimenType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SpecimenType entity.
 */
@JaversSpringDataAuditable
@Repository
public interface SpecimenTypeRepository extends JpaRepository<SpecimenType, Long> {
    Optional<SpecimenType> findOneByType(String type);
    Optional<SpecimenType> findOneByName(String name);
}
