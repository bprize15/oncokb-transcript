package org.mskcc.oncokb.curation.domain.nih.efetch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "year", "month", "day" })
@XmlRootElement(name = "ArticleDate")
public class ArticleDate {

    @XmlAttribute(name = "DateType")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String dateType;

    @XmlElement(name = "Year", required = true)
    protected Year year;

    @XmlElement(name = "Month", required = true)
    protected Month month;

    @XmlElement(name = "Day", required = true)
    protected Day day;

    /**
     * Gets the value of the dateType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDateType() {
        if (dateType == null) {
            return "Electronic";
        } else {
            return dateType;
        }
    }

    /**
     * Sets the value of the dateType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDateType(String value) {
        this.dateType = value;
    }

    /**
     * Gets the value of the year property.
     *
     * @return
     *     possible object is
     *     {@link Year }
     *
     */
    public Year getYear() {
        return year;
    }

    /**
     * Sets the value of the year property.
     *
     * @param value
     *     allowed object is
     *     {@link Year }
     *
     */
    public void setYear(Year value) {
        this.year = value;
    }

    /**
     * Gets the value of the month property.
     *
     * @return
     *     possible object is
     *     {@link Month }
     *
     */
    public Month getMonth() {
        return month;
    }

    /**
     * Sets the value of the month property.
     *
     * @param value
     *     allowed object is
     *     {@link Month }
     *
     */
    public void setMonth(Month value) {
        this.month = value;
    }

    /**
     * Gets the value of the day property.
     *
     * @return
     *     possible object is
     *     {@link Day }
     *
     */
    public Day getDay() {
        return day;
    }

    /**
     * Sets the value of the day property.
     *
     * @param value
     *     allowed object is
     *     {@link Day }
     *
     */
    public void setDay(Day value) {
        this.day = value;
    }
}
