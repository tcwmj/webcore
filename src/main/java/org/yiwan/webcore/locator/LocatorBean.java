//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.01.21 at 02:07:01 PM CST 
//


package org.yiwan.webcore.locator;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="page" type="{}page" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "page"
})
@XmlRootElement(name = "locatorBean")
public class LocatorBean {

    protected List<Page> page;

    /**
     * Gets the value of the page property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the page property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Page }
     * 
     * 
     */
    public List<Page> getPage() {
        if (page == null) {
            page = new ArrayList<Page>();
        }
        return this.page;
    }
    
	public Page page(String id) {
		return (Page) org.yiwan.webcore.util.Helper.filterListById(this.getPage(), id);
	}

	public Locator locator(String id, String... replacements) {
		int index = id.indexOf(".");
		String pageid = id.substring(0, index);
		return page(pageid).locator(id, replacements);
	}
			
}
