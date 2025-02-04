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
@XmlType(name = "", propOrder = { "accessionNumber" })
@XmlRootElement(name = "AccessionNumberList")
public class AccessionNumberList {

    @XmlElement(name = "AccessionNumber", required = true)
    protected List<AccessionNumber> accessionNumber;

    /**
     * Gets the value of the accessionNumber property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the accessionNumber property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccessionNumber().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccessionNumber }
     *
     *
     */
    public List<AccessionNumber> getAccessionNumber() {
        if (accessionNumber == null) {
            accessionNumber = new ArrayList<AccessionNumber>();
        }
        return this.accessionNumber;
    }
}
