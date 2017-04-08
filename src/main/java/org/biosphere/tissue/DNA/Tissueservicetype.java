
package org.biosphere.tissue.DNA;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for tissueservicetype complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="tissueservicetype"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="servicename" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="serviceversion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="servicetype" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="servicedaemon" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="serviceclass" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="serviceparameters"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="serviceparameter" maxOccurs="unbounded"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="serviceparametername" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                             &lt;element name="serviceparametervalue" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
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
@XmlType(name = "tissueservicetype", propOrder = { "servicename", "serviceversion", "servicetype", "servicedaemon",
		"serviceclass", "serviceparameters" })
public class Tissueservicetype {

	@XmlElement(required = true)
	protected String servicename;
	@XmlElement(required = true)
	protected String serviceversion;
	@XmlElement(required = true)
	protected String servicetype;
	protected boolean servicedaemon;
	@XmlElement(required = true)
	protected String serviceclass;
	@XmlElement(required = true)
	protected Tissueservicetype.Serviceparameters serviceparameters;

	/**
	 * Gets the value of the servicename property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getServicename() {
		return servicename;
	}

	/**
	 * Sets the value of the servicename property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setServicename(String value) {
		this.servicename = value;
	}

	/**
	 * Gets the value of the serviceversion property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getServiceversion() {
		return serviceversion;
	}

	/**
	 * Sets the value of the serviceversion property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setServiceversion(String value) {
		this.serviceversion = value;
	}

	/**
	 * Gets the value of the servicetype property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getServicetype() {
		return servicetype;
	}

	/**
	 * Sets the value of the servicetype property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setServicetype(String value) {
		this.servicetype = value;
	}

	/**
	 * Gets the value of the servicedaemon property.
	 *
	 */
	public boolean isServicedaemon() {
		return servicedaemon;
	}

	/**
	 * Sets the value of the servicedaemon property.
	 *
	 */
	public void setServicedaemon(boolean value) {
		this.servicedaemon = value;
	}

	/**
	 * Gets the value of the serviceclass property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getServiceclass() {
		return serviceclass;
	}

	/**
	 * Sets the value of the serviceclass property.
	 *
	 * @param value
	 *            allowed object is {@link String }
	 *
	 */
	public void setServiceclass(String value) {
		this.serviceclass = value;
	}

	/**
	 * Gets the value of the serviceparameters property.
	 *
	 * @return possible object is {@link Tissueservicetype.Serviceparameters }
	 *
	 */
	public Tissueservicetype.Serviceparameters getServiceparameters() {
		return serviceparameters;
	}

	/**
	 * Sets the value of the serviceparameters property.
	 *
	 * @param value
	 *            allowed object is {@link Tissueservicetype.Serviceparameters }
	 *
	 */
	public void setServiceparameters(Tissueservicetype.Serviceparameters value) {
		this.serviceparameters = value;
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
	 *         &lt;element name="serviceparameter" maxOccurs="unbounded"&gt;
	 *           &lt;complexType&gt;
	 *             &lt;complexContent&gt;
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
	 *                 &lt;sequence&gt;
	 *                   &lt;element name="serviceparametername" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
	 *                   &lt;element name="serviceparametervalue" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
	@XmlType(name = "", propOrder = { "serviceparameter" })
	public static class Serviceparameters {

		@XmlElement(required = true)
		protected List<Tissueservicetype.Serviceparameters.Serviceparameter> serviceparameter;

		/**
		 * Gets the value of the serviceparameter property.
		 *
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the serviceparameter property.
		 *
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getServiceparameter().add(newItem);
		 * </pre>
		 *
		 *
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link Tissueservicetype.Serviceparameters.Serviceparameter }
		 *
		 *
		 */
		public List<Tissueservicetype.Serviceparameters.Serviceparameter> getServiceparameter() {
			if (serviceparameter == null) {
				serviceparameter = new ArrayList<Tissueservicetype.Serviceparameters.Serviceparameter>();
			}
			return this.serviceparameter;
		}

		/**
		 * <p>
		 * Java class for anonymous complex type.
		 *
		 * <p>
		 * The following schema fragment specifies the expected content
		 * contained within this class.
		 *
		 * <pre>
		 * &lt;complexType&gt;
		 *   &lt;complexContent&gt;
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
		 *       &lt;sequence&gt;
		 *         &lt;element name="serviceparametername" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
		 *         &lt;element name="serviceparametervalue" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
		 *       &lt;/sequence&gt;
		 *     &lt;/restriction&gt;
		 *   &lt;/complexContent&gt;
		 * &lt;/complexType&gt;
		 * </pre>
		 *
		 *
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "serviceparametername", "serviceparametervalue" })
		public static class Serviceparameter {

			@XmlElement(required = true)
			protected String serviceparametername;
			@XmlElement(required = true)
			protected String serviceparametervalue;

			/**
			 * Gets the value of the serviceparametername property.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getServiceparametername() {
				return serviceparametername;
			}

			/**
			 * Sets the value of the serviceparametername property.
			 *
			 * @param value
			 *            allowed object is {@link String }
			 *
			 */
			public void setServiceparametername(String value) {
				this.serviceparametername = value;
			}

			/**
			 * Gets the value of the serviceparametervalue property.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getServiceparametervalue() {
				return serviceparametervalue;
			}

			/**
			 * Sets the value of the serviceparametervalue property.
			 *
			 * @param value
			 *            allowed object is {@link String }
			 *
			 */
			public void setServiceparametervalue(String value) {
				this.serviceparametervalue = value;
			}

		}

	}

}
