
package org.biosphere.tissue.DNA.XML;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for cellinterfacetype complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="cellinterfacetype"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cellport" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="celladdresses"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="celladdress" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cellinterfacetype", namespace = "http://www.biosphere.org/tissue/1.0/cell/interface", propOrder = {
		"cellport", "celladdresses" })
public class Cellinterfacetype {

	@XmlElement(namespace = "http://www.biosphere.org/tissue/1.0/cell/interface", required = true)
	protected BigInteger cellport;
	@XmlElement(namespace = "http://www.biosphere.org/tissue/1.0/cell/interface", required = true)
	protected Cellinterfacetype.Celladdresses celladdresses;

	/**
	 * Gets the value of the cellport property.
	 *
	 * @return possible object is {@link BigInteger }
	 *
	 */
	public BigInteger getCellport() {
		return cellport;
	}

	/**
	 * Sets the value of the cellport property.
	 *
	 * @param value
	 *            allowed object is {@link BigInteger }
	 *
	 */
	public void setCellport(BigInteger value) {
		this.cellport = value;
	}

	/**
	 * Gets the value of the celladdresses property.
	 *
	 * @return possible object is {@link Cellinterfacetype.Celladdresses }
	 *
	 */
	public Cellinterfacetype.Celladdresses getCelladdresses() {
		return celladdresses;
	}

	/**
	 * Sets the value of the celladdresses property.
	 *
	 * @param value
	 *            allowed object is {@link Cellinterfacetype.Celladdresses }
	 *
	 */
	public void setCelladdresses(Cellinterfacetype.Celladdresses value) {
		this.celladdresses = value;
	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 *
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 *
	 * <pre>
	 * &lt;complexType&gt;
	 *   &lt;complexContent&gt;
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
	 *       &lt;sequence&gt;
	 *         &lt;element name="celladdress" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
	 *       &lt;/sequence&gt;
	 *     &lt;/restriction&gt;
	 *   &lt;/complexContent&gt;
	 * &lt;/complexType&gt;
	 * </pre>
	 *
	 *
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "celladdress" })
	public static class Celladdresses {

		@XmlElement(namespace = "http://www.biosphere.org/tissue/1.0/cell/interface", required = true)
		protected List<String> celladdress;

		/**
		 * Gets the value of the celladdress property.
		 *
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the celladdress property.
		 *
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getCelladdress().add(newItem);
		 * </pre>
		 *
		 *
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link String }
		 *
		 *
		 */
		public List<String> getCelladdress() {
			if (celladdress == null) {
				celladdress = new ArrayList<String>();
			}
			return this.celladdress;
		}

	}

}
