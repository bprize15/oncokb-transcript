package org.mskcc.oncokb.curation.service;

import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.mskcc.oncokb.curation.domain.*; // for static metamodels
import org.mskcc.oncokb.curation.domain.Drug;
import org.mskcc.oncokb.curation.repository.DrugRepository;
import org.mskcc.oncokb.curation.service.criteria.DrugCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.StringFilter;

/**
 * Service for executing complex queries for {@link Drug} entities in the database.
 * The main input is a {@link DrugCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Drug} or a {@link Page} of {@link Drug} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DrugQueryService extends QueryService<Drug> {

    private final Logger log = LoggerFactory.getLogger(DrugQueryService.class);

    private final DrugRepository drugRepository;

    public DrugQueryService(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    @Transactional(readOnly = true)
    public Page<Drug> findBySearchQuery(String query, Pageable page) {
        DrugCriteria criteria = new DrugCriteria();
        StringFilter stringFilter = new StringFilter();
        stringFilter.setContains(query);
        criteria.setNcitCode(stringFilter);
        criteria.setName(stringFilter);
        return findByCriteria(criteria, page);
    }

    /**
     * Return a {@link List} of {@link Drug} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Drug> findByCriteria(DrugCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Drug> specification = createSpecification(criteria);
        return drugRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Drug} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Drug> findByCriteria(DrugCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Drug> specification = createSpecification(criteria);
        return drugRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DrugCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Drug> specification = createSpecification(criteria);
        return drugRepository.count(specification);
    }

    /**
     * Function to convert {@link DrugCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Drug> createSpecification(DrugCriteria criteria) {
        Specification<Drug> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.or(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.or(buildRangeSpecification(criteria.getId(), Drug_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.or(buildStringSpecification(criteria.getName(), Drug_.name));
            }
            if (criteria.getUuid() != null) {
                specification = specification.or(buildStringSpecification(criteria.getUuid(), Drug_.uuid));
            }
            if (criteria.getNcitCode() != null) {
                specification = specification.or(
                    buildSpecification(criteria.getNcitCode(), root -> root.join(Drug_.nciThesaurus, JoinType.LEFT).get(NciThesaurus_.code))
                );
            }
            if (criteria.getFlagId() != null) {
                specification = specification.or(
                    buildSpecification(criteria.getFlagId(), root -> root.join(Drug_.flags, JoinType.LEFT).get(Flag_.id))
                );
            }
            if (criteria.getAssociationId() != null) {
                specification = specification.or(
                    buildSpecification(
                        criteria.getAssociationId(),
                        root -> root.join(Drug_.associations, JoinType.LEFT).get(Association_.id)
                    )
                );
            }
        }
        return specification;
    }
}
