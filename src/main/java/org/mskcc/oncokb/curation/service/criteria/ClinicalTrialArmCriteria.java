package org.mskcc.oncokb.curation.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link org.mskcc.oncokb.curation.domain.ClinicalTrialArm} entity. This class is used
 * in {@link org.mskcc.oncokb.curation.web.rest.ClinicalTrialArmResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /clinical-trial-arms?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ClinicalTrialArmCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LongFilter associationId;

    private LongFilter clinicalTrialId;

    private Boolean distinct;

    public ClinicalTrialArmCriteria() {}

    public ClinicalTrialArmCriteria(ClinicalTrialArmCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.associationId = other.associationId == null ? null : other.associationId.copy();
        this.clinicalTrialId = other.clinicalTrialId == null ? null : other.clinicalTrialId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ClinicalTrialArmCriteria copy() {
        return new ClinicalTrialArmCriteria(this);
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

    public LongFilter getAssociationId() {
        return associationId;
    }

    public LongFilter associationId() {
        if (associationId == null) {
            associationId = new LongFilter();
        }
        return associationId;
    }

    public void setAssociationId(LongFilter associationId) {
        this.associationId = associationId;
    }

    public LongFilter getClinicalTrialId() {
        return clinicalTrialId;
    }

    public LongFilter clinicalTrialId() {
        if (clinicalTrialId == null) {
            clinicalTrialId = new LongFilter();
        }
        return clinicalTrialId;
    }

    public void setClinicalTrialId(LongFilter clinicalTrialId) {
        this.clinicalTrialId = clinicalTrialId;
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
        final ClinicalTrialArmCriteria that = (ClinicalTrialArmCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(associationId, that.associationId) &&
            Objects.equals(clinicalTrialId, that.clinicalTrialId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, associationId, clinicalTrialId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClinicalTrialArmCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (associationId != null ? "associationId=" + associationId + ", " : "") +
            (clinicalTrialId != null ? "clinicalTrialId=" + clinicalTrialId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
