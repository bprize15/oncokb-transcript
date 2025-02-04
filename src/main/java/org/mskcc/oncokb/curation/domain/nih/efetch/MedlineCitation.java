package org.mskcc.oncokb.curation.domain.nih.efetch;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {
        "pmid",
        "dateCreated",
        "dateCompleted",
        "dateRevised",
        "article",
        "medlineJournalInfo",
        "chemicalList",
        "supplMeshList",
        "citationSubset",
        "commentsCorrectionsList",
        "geneSymbolList",
        "meshHeadingList",
        "numberOfReferences",
        "personalNameSubjectList",
        "otherID",
        "otherAbstract",
        "keywordList",
        "spaceFlightMission",
        "investigatorList",
        "generalNote",
    }
)
@XmlRootElement(name = "MedlineCitation")
public class MedlineCitation {

    @XmlAttribute(name = "Owner")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String owner;

    @XmlAttribute(name = "Status", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String status;

    @XmlAttribute(name = "VersionID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String versionID;

    @XmlAttribute(name = "VersionDate")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String versionDate;

    @XmlElement(name = "PMID", required = true)
    protected PMID pmid;

    @XmlElement(name = "DateCreated", required = true)
    protected DateCreated dateCreated;

    @XmlElement(name = "DateCompleted")
    protected DateCompleted dateCompleted;

    @XmlElement(name = "DateRevised")
    protected DateRevised dateRevised;

    @XmlElement(name = "Article", required = true)
    protected Article article;

    @XmlElement(name = "MedlineJournalInfo", required = true)
    protected MedlineJournalInfo medlineJournalInfo;

    @XmlElement(name = "ChemicalList")
    protected ChemicalList chemicalList;

    @XmlElement(name = "SupplMeshList")
    protected SupplMeshList supplMeshList;

    @XmlElement(name = "CitationSubset")
    protected List<CitationSubset> citationSubset;

    @XmlElement(name = "CommentsCorrectionsList")
    protected CommentsCorrectionsList commentsCorrectionsList;

    @XmlElement(name = "GeneSymbolList")
    protected GeneSymbolList geneSymbolList;

    @XmlElement(name = "MeshHeadingList")
    protected MeshHeadingList meshHeadingList;

    @XmlElement(name = "NumberOfReferences")
    protected String numberOfReferences;

    @XmlElement(name = "PersonalNameSubjectList")
    protected PersonalNameSubjectList personalNameSubjectList;

    @XmlElement(name = "OtherID")
    protected List<OtherID> otherID;

    @XmlElement(name = "OtherAbstract")
    protected List<OtherAbstract> otherAbstract;

    @XmlElement(name = "KeywordList")
    protected List<KeywordList> keywordList;

    @XmlElement(name = "SpaceFlightMission")
    protected List<SpaceFlightMission> spaceFlightMission;

    @XmlElement(name = "InvestigatorList")
    protected InvestigatorList investigatorList;

    @XmlElement(name = "GeneralNote")
    protected List<GeneralNote> generalNote;

    /**
     * Gets the value of the owner property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOwner() {
        if (owner == null) {
            return "NLM";
        } else {
            return owner;
        }
    }

    /**
     * Sets the value of the owner property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOwner(String value) {
        this.owner = value;
    }

    /**
     * Gets the value of the status property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the versionID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersionID() {
        return versionID;
    }

    /**
     * Sets the value of the versionID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersionID(String value) {
        this.versionID = value;
    }

    /**
     * Gets the value of the versionDate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersionDate() {
        return versionDate;
    }

    /**
     * Sets the value of the versionDate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersionDate(String value) {
        this.versionDate = value;
    }

    /**
     * Gets the value of the pmid property.
     *
     * @return
     *     possible object is
     *     {@link PMID }
     *
     */
    public PMID getPMID() {
        return pmid;
    }

    /**
     * Sets the value of the pmid property.
     *
     * @param value
     *     allowed object is
     *     {@link PMID }
     *
     */
    public void setPMID(PMID value) {
        this.pmid = value;
    }

    /**
     * Gets the value of the dateCreated property.
     *
     * @return
     *     possible object is
     *     {@link DateCreated }
     *
     */
    public DateCreated getDateCreated() {
        return dateCreated;
    }

    /**
     * Sets the value of the dateCreated property.
     *
     * @param value
     *     allowed object is
     *     {@link DateCreated }
     *
     */
    public void setDateCreated(DateCreated value) {
        this.dateCreated = value;
    }

    /**
     * Gets the value of the dateCompleted property.
     *
     * @return
     *     possible object is
     *     {@link DateCompleted }
     *
     */
    public DateCompleted getDateCompleted() {
        return dateCompleted;
    }

    /**
     * Sets the value of the dateCompleted property.
     *
     * @param value
     *     allowed object is
     *     {@link DateCompleted }
     *
     */
    public void setDateCompleted(DateCompleted value) {
        this.dateCompleted = value;
    }

    /**
     * Gets the value of the dateRevised property.
     *
     * @return
     *     possible object is
     *     {@link DateRevised }
     *
     */
    public DateRevised getDateRevised() {
        return dateRevised;
    }

    /**
     * Sets the value of the dateRevised property.
     *
     * @param value
     *     allowed object is
     *     {@link DateRevised }
     *
     */
    public void setDateRevised(DateRevised value) {
        this.dateRevised = value;
    }

    /**
     * Gets the value of the article property.
     *
     * @return
     *     possible object is
     *     {@link Article }
     *
     */
    public Article getArticle() {
        return article;
    }

    /**
     * Sets the value of the article property.
     *
     * @param value
     *     allowed object is
     *     {@link Article }
     *
     */
    public void setArticle(Article value) {
        this.article = value;
    }

    /**
     * Gets the value of the medlineJournalInfo property.
     *
     * @return
     *     possible object is
     *     {@link MedlineJournalInfo }
     *
     */
    public MedlineJournalInfo getMedlineJournalInfo() {
        return medlineJournalInfo;
    }

    /**
     * Sets the value of the medlineJournalInfo property.
     *
     * @param value
     *     allowed object is
     *     {@link MedlineJournalInfo }
     *
     */
    public void setMedlineJournalInfo(MedlineJournalInfo value) {
        this.medlineJournalInfo = value;
    }

    /**
     * Gets the value of the chemicalList property.
     *
     * @return
     *     possible object is
     *     {@link ChemicalList }
     *
     */
    public ChemicalList getChemicalList() {
        return chemicalList;
    }

    /**
     * Sets the value of the chemicalList property.
     *
     * @param value
     *     allowed object is
     *     {@link ChemicalList }
     *
     */
    public void setChemicalList(ChemicalList value) {
        this.chemicalList = value;
    }

    /**
     * Gets the value of the supplMeshList property.
     *
     * @return
     *     possible object is
     *     {@link SupplMeshList }
     *
     */
    public SupplMeshList getSupplMeshList() {
        return supplMeshList;
    }

    /**
     * Sets the value of the supplMeshList property.
     *
     * @param value
     *     allowed object is
     *     {@link SupplMeshList }
     *
     */
    public void setSupplMeshList(SupplMeshList value) {
        this.supplMeshList = value;
    }

    /**
     * Gets the value of the citationSubset property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the citationSubset property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCitationSubset().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CitationSubset }
     *
     *
     */
    public List<CitationSubset> getCitationSubset() {
        if (citationSubset == null) {
            citationSubset = new ArrayList<CitationSubset>();
        }
        return this.citationSubset;
    }

    /**
     * Gets the value of the commentsCorrectionsList property.
     *
     * @return
     *     possible object is
     *     {@link CommentsCorrectionsList }
     *
     */
    public CommentsCorrectionsList getCommentsCorrectionsList() {
        return commentsCorrectionsList;
    }

    /**
     * Sets the value of the commentsCorrectionsList property.
     *
     * @param value
     *     allowed object is
     *     {@link CommentsCorrectionsList }
     *
     */
    public void setCommentsCorrectionsList(CommentsCorrectionsList value) {
        this.commentsCorrectionsList = value;
    }

    /**
     * Gets the value of the geneSymbolList property.
     *
     * @return
     *     possible object is
     *     {@link GeneSymbolList }
     *
     */
    public GeneSymbolList getGeneSymbolList() {
        return geneSymbolList;
    }

    /**
     * Sets the value of the geneSymbolList property.
     *
     * @param value
     *     allowed object is
     *     {@link GeneSymbolList }
     *
     */
    public void setGeneSymbolList(GeneSymbolList value) {
        this.geneSymbolList = value;
    }

    /**
     * Gets the value of the meshHeadingList property.
     *
     * @return
     *     possible object is
     *     {@link MeshHeadingList }
     *
     */
    public MeshHeadingList getMeshHeadingList() {
        return meshHeadingList;
    }

    /**
     * Sets the value of the meshHeadingList property.
     *
     * @param value
     *     allowed object is
     *     {@link MeshHeadingList }
     *
     */
    public void setMeshHeadingList(MeshHeadingList value) {
        this.meshHeadingList = value;
    }

    /**
     * Gets the value of the numberOfReferences property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNumberOfReferences() {
        return numberOfReferences;
    }

    /**
     * Sets the value of the numberOfReferences property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNumberOfReferences(String value) {
        this.numberOfReferences = value;
    }

    /**
     * Gets the value of the personalNameSubjectList property.
     *
     * @return
     *     possible object is
     *     {@link PersonalNameSubjectList }
     *
     */
    public PersonalNameSubjectList getPersonalNameSubjectList() {
        return personalNameSubjectList;
    }

    /**
     * Sets the value of the personalNameSubjectList property.
     *
     * @param value
     *     allowed object is
     *     {@link PersonalNameSubjectList }
     *
     */
    public void setPersonalNameSubjectList(PersonalNameSubjectList value) {
        this.personalNameSubjectList = value;
    }

    /**
     * Gets the value of the otherID property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherID property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherID().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OtherID }
     *
     *
     */
    public List<OtherID> getOtherID() {
        if (otherID == null) {
            otherID = new ArrayList<OtherID>();
        }
        return this.otherID;
    }

    /**
     * Gets the value of the otherAbstract property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherAbstract property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherAbstract().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OtherAbstract }
     *
     *
     */
    public List<OtherAbstract> getOtherAbstract() {
        if (otherAbstract == null) {
            otherAbstract = new ArrayList<OtherAbstract>();
        }
        return this.otherAbstract;
    }

    /**
     * Gets the value of the keywordList property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the keywordList property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKeywordList().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KeywordList }
     *
     *
     */
    public List<KeywordList> getKeywordList() {
        if (keywordList == null) {
            keywordList = new ArrayList<KeywordList>();
        }
        return this.keywordList;
    }

    /**
     * Gets the value of the spaceFlightMission property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spaceFlightMission property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpaceFlightMission().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpaceFlightMission }
     *
     *
     */
    public List<SpaceFlightMission> getSpaceFlightMission() {
        if (spaceFlightMission == null) {
            spaceFlightMission = new ArrayList<SpaceFlightMission>();
        }
        return this.spaceFlightMission;
    }

    /**
     * Gets the value of the investigatorList property.
     *
     * @return
     *     possible object is
     *     {@link InvestigatorList }
     *
     */
    public InvestigatorList getInvestigatorList() {
        return investigatorList;
    }

    /**
     * Sets the value of the investigatorList property.
     *
     * @param value
     *     allowed object is
     *     {@link InvestigatorList }
     *
     */
    public void setInvestigatorList(InvestigatorList value) {
        this.investigatorList = value;
    }

    /**
     * Gets the value of the generalNote property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the generalNote property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGeneralNote().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GeneralNote }
     *
     *
     */
    public List<GeneralNote> getGeneralNote() {
        if (generalNote == null) {
            generalNote = new ArrayList<GeneralNote>();
        }
        return this.generalNote;
    }
}
