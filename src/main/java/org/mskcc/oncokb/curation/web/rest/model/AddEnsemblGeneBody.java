package org.mskcc.oncokb.curation.web.rest.model;

import jakarta.validation.constraints.NotNull;

public class AddEnsemblGeneBody {

    @NotNull
    String referenceGenome;

    @NotNull
    Integer entrezGeneId;

    @NotNull
    String ensemblGeneId;

    @NotNull
    Boolean isCanonical = false;

    public String getReferenceGenome() {
        return referenceGenome;
    }

    public void setReferenceGenome(String referenceGenome) {
        this.referenceGenome = referenceGenome;
    }

    public Integer getEntrezGeneId() {
        return entrezGeneId;
    }

    public void setEntrezGeneId(Integer entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }

    public String getEnsemblGeneId() {
        return ensemblGeneId;
    }

    public void setEnsemblGeneId(String ensemblGeneId) {
        this.ensemblGeneId = ensemblGeneId;
    }

    public Boolean getCanonical() {
        return isCanonical;
    }

    public void setCanonical(Boolean canonical) {
        isCanonical = canonical;
    }
}
