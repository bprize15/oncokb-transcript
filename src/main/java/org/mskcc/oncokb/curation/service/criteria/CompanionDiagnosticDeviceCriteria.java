package org.mskcc.oncokb.curation.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link org.mskcc.oncokb.curation.domain.CompanionDiagnosticDevice} entity. This class is used
 * in {@link org.mskcc.oncokb.curation.web.rest.CompanionDiagnosticDeviceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /companion-diagnostic-devices?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CompanionDiagnosticDeviceCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter manufacturer;

    private StringFilter indicationDetails;

    private StringFilter platformType;

    private InstantFilter lastUpdated;

    private LongFilter fdaSubmissionId;

    private LongFilter specimenTypeId;

    private Boolean distinct;

    public CompanionDiagnosticDeviceCriteria() {}

    public CompanionDiagnosticDeviceCriteria(CompanionDiagnosticDeviceCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.manufacturer = other.manufacturer == null ? null : other.manufacturer.copy();
        this.indicationDetails = other.indicationDetails == null ? null : other.indicationDetails.copy();
        this.platformType = other.platformType == null ? null : other.platformType.copy();
        this.lastUpdated = other.lastUpdated == null ? null : other.lastUpdated.copy();
        this.fdaSubmissionId = other.fdaSubmissionId == null ? null : other.fdaSubmissionId.copy();
        this.specimenTypeId = other.specimenTypeId == null ? null : other.specimenTypeId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CompanionDiagnosticDeviceCriteria copy() {
        return new CompanionDiagnosticDeviceCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getManufacturer() {
        return manufacturer;
    }

    public StringFilter manufacturer() {
        if (manufacturer == null) {
            manufacturer = new StringFilter();
        }
        return manufacturer;
    }

    public void setManufacturer(StringFilter manufacturer) {
        this.manufacturer = manufacturer;
    }

    public StringFilter getIndicationDetails() {
        return indicationDetails;
    }

    public StringFilter indicationDetails() {
        if (indicationDetails == null) {
            indicationDetails = new StringFilter();
        }
        return indicationDetails;
    }

    public void setIndicationDetails(StringFilter indicationDetails) {
        this.indicationDetails = indicationDetails;
    }

    public StringFilter getPlatformType() {
        return platformType;
    }

    public StringFilter platformType() {
        if (platformType == null) {
            platformType = new StringFilter();
        }
        return platformType;
    }

    public void setPlatformType(StringFilter platformType) {
        this.platformType = platformType;
    }

    public InstantFilter getLastUpdated() {
        return lastUpdated;
    }

    public InstantFilter lastUpdated() {
        if (lastUpdated == null) {
            lastUpdated = new InstantFilter();
        }
        return lastUpdated;
    }

    public void setLastUpdated(InstantFilter lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LongFilter getFdaSubmissionId() {
        return fdaSubmissionId;
    }

    public LongFilter fdaSubmissionId() {
        if (fdaSubmissionId == null) {
            fdaSubmissionId = new LongFilter();
        }
        return fdaSubmissionId;
    }

    public void setFdaSubmissionId(LongFilter fdaSubmissionId) {
        this.fdaSubmissionId = fdaSubmissionId;
    }

    public LongFilter getSpecimenTypeId() {
        return specimenTypeId;
    }

    public LongFilter specimenTypeId() {
        if (specimenTypeId == null) {
            specimenTypeId = new LongFilter();
        }
        return specimenTypeId;
    }

    public void setSpecimenTypeId(LongFilter specimenTypeId) {
        this.specimenTypeId = specimenTypeId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CompanionDiagnosticDeviceCriteria that = (CompanionDiagnosticDeviceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(manufacturer, that.manufacturer) &&
            Objects.equals(indicationDetails, that.indicationDetails) &&
            Objects.equals(platformType, that.platformType) &&
            Objects.equals(lastUpdated, that.lastUpdated) &&
            Objects.equals(fdaSubmissionId, that.fdaSubmissionId) &&
            Objects.equals(specimenTypeId, that.specimenTypeId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            manufacturer,
            indicationDetails,
            platformType,
            lastUpdated,
            fdaSubmissionId,
            specimenTypeId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompanionDiagnosticDeviceCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (manufacturer != null ? "manufacturer=" + manufacturer + ", " : "") +
            (indicationDetails != null ? "indicationDetails=" + indicationDetails + ", " : "") +
            (platformType != null ? "platformType=" + platformType + ", " : "") +
            (lastUpdated != null ? "lastUpdated=" + lastUpdated + ", " : "") +
            (fdaSubmissionId != null ? "fdaSubmissionId=" + fdaSubmissionId + ", " : "") +
            (specimenTypeId != null ? "specimenTypeId=" + specimenTypeId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
