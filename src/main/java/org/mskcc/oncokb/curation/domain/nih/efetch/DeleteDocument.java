package org.mskcc.oncokb.curation.domain.nih.efetch;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "pmid" })
@XmlRootElement(name = "DeleteDocument")
public class DeleteDocument {

    @XmlElement(name = "PMID")
    protected List<PMID> pmid;

    /**
     * Gets the value of the pmid property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pmid property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPMID().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PMID }
     *
     *
     */
    public List<PMID> getPMID() {
        if (pmid == null) {
            pmid = new ArrayList<PMID>();
        }
        return this.pmid;
    }
}
