package org.mskcc.oncokb.curation.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.mskcc.oncokb.curation.domain.FdaSubmission;
import org.mskcc.oncokb.curation.repository.FdaSubmissionRepository;
import org.mskcc.oncokb.curation.util.CdxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link FdaSubmission}.
 */
@Service
@Transactional
public class FdaSubmissionService {

    private final Logger log = LoggerFactory.getLogger(FdaSubmissionService.class);

    private final FdaSubmissionRepository fdaSubmissionRepository;

    private final CdxUtils cdxUtils;

    public FdaSubmissionService(FdaSubmissionRepository fdaSubmissionRepository, CdxUtils cdxUtils) {
        this.fdaSubmissionRepository = fdaSubmissionRepository;
        this.cdxUtils = cdxUtils;
    }

    /**
     * Save a fdaSubmission.
     *
     * @param fdaSubmission the entity to save.
     * @return the persisted entity.
     */
    public FdaSubmission save(FdaSubmission fdaSubmission) {
        log.debug("Request to save FdaSubmission : {}", fdaSubmission);
        return fdaSubmissionRepository.save(fdaSubmission);
    }

    /**
     * Partially update a fdaSubmission.
     *
     * @param fdaSubmission the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FdaSubmission> partialUpdate(FdaSubmission fdaSubmission) {
        log.debug("Request to partially update FdaSubmission : {}", fdaSubmission);

        return fdaSubmissionRepository
            .findById(fdaSubmission.getId())
            .map(existingFdaSubmission -> {
                if (fdaSubmission.getNumber() != null) {
                    existingFdaSubmission.setNumber(fdaSubmission.getNumber());
                }
                if (fdaSubmission.getSupplementNumber() != null) {
                    existingFdaSubmission.setSupplementNumber(fdaSubmission.getSupplementNumber());
                }
                if (fdaSubmission.getDeviceName() != null) {
                    existingFdaSubmission.setDeviceName(fdaSubmission.getDeviceName());
                }
                if (fdaSubmission.getGenericName() != null) {
                    existingFdaSubmission.setGenericName(fdaSubmission.getGenericName());
                }
                if (fdaSubmission.getDateReceived() != null) {
                    existingFdaSubmission.setDateReceived(fdaSubmission.getDateReceived());
                }
                if (fdaSubmission.getDecisionDate() != null) {
                    existingFdaSubmission.setDecisionDate(fdaSubmission.getDecisionDate());
                }
                if (fdaSubmission.getDescription() != null) {
                    existingFdaSubmission.setDescription(fdaSubmission.getDescription());
                }
                if (fdaSubmission.getCurated() != null) {
                    existingFdaSubmission.setCurated(fdaSubmission.getCurated());
                }
                if (fdaSubmission.getGenetic() != null) {
                    existingFdaSubmission.setGenetic(fdaSubmission.getGenetic());
                }
                if (fdaSubmission.getNote() != null) {
                    existingFdaSubmission.setNote(fdaSubmission.getNote());
                }
                if (fdaSubmission.getAssociations() != null) {
                    existingFdaSubmission.setAssociations(fdaSubmission.getAssociations());
                }

                return existingFdaSubmission;
            })
            .map(fdaSubmissionRepository::save);
    }

    /**
     * Get all the fdaSubmissions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FdaSubmission> findAll(Pageable pageable) {
        log.debug("Request to get all FdaSubmissions");
        return fdaSubmissionRepository.findAll(pageable);
    }

    /**
     * Get all the fdaSubmissions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<FdaSubmission> findAllWithEagerRelationships(Pageable pageable) {
        return fdaSubmissionRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one fdaSubmission by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FdaSubmission> findOne(Long id) {
        log.debug("Request to get FdaSubmission : {}", id);
        return fdaSubmissionRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Get one fdaSubmission by number and supplement number
     * @param number the primary number of the fda submission
     * @param supplementNumber the supplement number
     * @return the entity
     */
    public Optional<FdaSubmission> findByNumberAndSupplementNumber(String number, String supplementNumber) {
        return fdaSubmissionRepository.findByNumberAndSupplementNumber(number, supplementNumber).stream().findAny();
    }

    /**
     * If the fda submission does not exist in db, then fetch and parse the information from fda website
     * @param number the primary fda submission number
     * @param supplementNumber the supplement number
     * @return Optional with the parsed fda submission or the one already existing in db, otherwise empty optional
     */
    public Optional<FdaSubmission> findOrFetchFdaSubmissionByNumber(String number, String supplementNumber, Boolean getAllSupplements) {
        Optional<FdaSubmission> fdaSubmission = this.findByNumberAndSupplementNumber(number, supplementNumber);
        if (fdaSubmission.isEmpty()) { // Fetch from FDA website if not present in database
            if (StringUtils.isEmpty(number)) {
                return Optional.empty();
            }
            String submissionNumber = number;
            if (!StringUtils.isEmpty(supplementNumber)) {
                submissionNumber += "/" + supplementNumber;
            }
            Set<FdaSubmission> newFdaSubmissions = cdxUtils.getFDASubmissionFromHTML(
                Set.of(submissionNumber),
                supplementNumber != null,
                getAllSupplements
            );
            return newFdaSubmissions
                .stream()
                .filter(sub -> {
                    return number.equals(sub.getNumber()) && StringUtils.equals(supplementNumber, sub.getSupplementNumber());
                })
                .findFirst();
        }
        return fdaSubmission;
    }

    public List<FdaSubmission> findByCompanionDiagnosticDevice(Long id) {
        return fdaSubmissionRepository.findByCompanionDiagnosticDeviceId(id);
    }

    /**
     * Checks whether another fda submission exists with the same number, supplement number and cdx association
     * @param fdaSubmission the fda submision entity
     */
    public Boolean isUnique(FdaSubmission fdaSubmission) {
        return fdaSubmissionRepository
            .findByNumberAndSupplementNumberAndCompanionDiagnosticDevice(
                fdaSubmission.getNumber(),
                fdaSubmission.getSupplementNumber(),
                fdaSubmission.getCompanionDiagnosticDevice()
            )
            .isEmpty();
    }

    /**
     * Delete the fdaSubmission by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete FdaSubmission : {}", id);
        fdaSubmissionRepository.deleteById(id);
    }
}
