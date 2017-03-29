
package org.biosphere.tissue.DNA;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="tissuename" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DNAversion" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
 *         &lt;element name="tissuepublickey" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="tissueannounce"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="tissuemulticastgroup" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;element name="tissuemulticastport" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="tissuenervoussystem"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="tissuedefaultport" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="tissuecells" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="cell" type="{http://www.biosphere.org/tissue/1.0/cell}celltype" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="tissueservices" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="tissueservice" type="{http://www.biosphere.org/tissue/1.0/service}tissueservicetype" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "", propOrder =
  {
    "tissuename", "dnAversion", "tissuepublickey", "tissueannounce", "tissuenervoussystem", "tissuecells",
    "tissueservices"
  })
@XmlRootElement(name = "tissue", namespace = "http://www.biosphere.org/tissue/1.0/DNA")
public class Tissue
{

  @XmlElement(required = true)
  protected String tissuename;
  @XmlElement(name = "DNAversion")
  protected float dnAversion;
  @XmlElement(required = true)
  protected String tissuepublickey;
  @XmlElement(required = true)
  protected Tissue.Tissueannounce tissueannounce;
  @XmlElement(required = true)
  protected Tissue.Tissuenervoussystem tissuenervoussystem;
  protected Tissue.Tissuecells tissuecells;
  protected Tissue.Tissueservices tissueservices;

  /**
   * Gets the value of the tissuename property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getTissuename()
  {
    return tissuename;
  }

  /**
   * Sets the value of the tissuename property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setTissuename(String value)
  {
    this.tissuename = value;
  }

  /**
   * Gets the value of the dnAversion property.
   *
   */
  public float getDNAversion()
  {
    return dnAversion;
  }

  /**
   * Sets the value of the dnAversion property.
   *
   */
  public void setDNAversion(float value)
  {
    this.dnAversion = value;
  }

  /**
   * Gets the value of the tissuepublickey property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getTissuepublickey()
  {
    return tissuepublickey;
  }

  /**
   * Sets the value of the tissuepublickey property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setTissuepublickey(String value)
  {
    this.tissuepublickey = value;
  }

  /**
   * Gets the value of the tissueannounce property.
   *
   * @return
   *     possible object is
   *     {@link Tissue.Tissueannounce }
   *
   */
  public Tissue.Tissueannounce getTissueannounce()
  {
    return tissueannounce;
  }

  /**
   * Sets the value of the tissueannounce property.
   *
   * @param value
   *     allowed object is
   *     {@link Tissue.Tissueannounce }
   *
   */
  public void setTissueannounce(Tissue.Tissueannounce value)
  {
    this.tissueannounce = value;
  }

  /**
   * Gets the value of the tissuenervoussystem property.
   *
   * @return
   *     possible object is
   *     {@link Tissue.Tissuenervoussystem }
   *
   */
  public Tissue.Tissuenervoussystem getTissuenervoussystem()
  {
    return tissuenervoussystem;
  }

  /**
   * Sets the value of the tissuenervoussystem property.
   *
   * @param value
   *     allowed object is
   *     {@link Tissue.Tissuenervoussystem }
   *
   */
  public void setTissuenervoussystem(Tissue.Tissuenervoussystem value)
  {
    this.tissuenervoussystem = value;
  }

  /**
   * Gets the value of the tissuecells property.
   *
   * @return
   *     possible object is
   *     {@link Tissue.Tissuecells }
   *
   */
  public Tissue.Tissuecells getTissuecells()
  {
    return tissuecells;
  }

  /**
   * Sets the value of the tissuecells property.
   *
   * @param value
   *     allowed object is
   *     {@link Tissue.Tissuecells }
   *
   */
  public void setTissuecells(Tissue.Tissuecells value)
  {
    this.tissuecells = value;
  }

  /**
   * Gets the value of the tissueservices property.
   *
   * @return
   *     possible object is
   *     {@link Tissue.Tissueservices }
   *
   */
  public Tissue.Tissueservices getTissueservices()
  {
    return tissueservices;
  }

  /**
   * Sets the value of the tissueservices property.
   *
   * @param value
   *     allowed object is
   *     {@link Tissue.Tissueservices }
   *
   */
  public void setTissueservices(Tissue.Tissueservices value)
  {
    this.tissueservices = value;
  }


  /**
   * <p>Java class for anonymous complex type.
   *
   * <p>The following schema fragment specifies the expected content contained within this class.
   *
   * <pre>
   * &lt;complexType&gt;
   *   &lt;complexContent&gt;
   *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
   *       &lt;sequence&gt;
   *         &lt;element name="tissuemulticastgroup" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
   *         &lt;element name="tissuemulticastport" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
   *       &lt;/sequence&gt;
   *     &lt;/restriction&gt;
   *   &lt;/complexContent&gt;
   * &lt;/complexType&gt;
   * </pre>
   *
   *
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder =
    {
      "tissuemulticastgroup", "tissuemulticastport"
    })
  public static class Tissueannounce
  {

    @XmlElement(required = true)
    protected String tissuemulticastgroup;
    @XmlElement(required = true)
    protected BigInteger tissuemulticastport;

    /**
     * Gets the value of the tissuemulticastgroup property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTissuemulticastgroup()
    {
      return tissuemulticastgroup;
    }

    /**
     * Sets the value of the tissuemulticastgroup property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTissuemulticastgroup(String value)
    {
      this.tissuemulticastgroup = value;
    }

    /**
     * Gets the value of the tissuemulticastport property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getTissuemulticastport()
    {
      return tissuemulticastport;
    }

    /**
     * Sets the value of the tissuemulticastport property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setTissuemulticastport(BigInteger value)
    {
      this.tissuemulticastport = value;
    }

  }


  /**
   * <p>Java class for anonymous complex type.
   *
   * <p>The following schema fragment specifies the expected content contained within this class.
   *
   * <pre>
   * &lt;complexType&gt;
   *   &lt;complexContent&gt;
   *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
   *       &lt;sequence&gt;
   *         &lt;element name="cell" type="{http://www.biosphere.org/tissue/1.0/cell}celltype" maxOccurs="unbounded" minOccurs="0"/&gt;
   *       &lt;/sequence&gt;
   *     &lt;/restriction&gt;
   *   &lt;/complexContent&gt;
   * &lt;/complexType&gt;
   * </pre>
   *
   *
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder =
    {
      "cell"
    })
  public static class Tissuecells
  {

    protected List<Celltype> cell;

    /**
     * Gets the value of the cell property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cell property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCell().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Celltype }
     *
     *
     */
    public List<Celltype> getCell()
    {
      if (cell == null)
      {
        cell = new ArrayList<Celltype>();
      }
      return this.cell;
    }

  }


  /**
   * <p>Java class for anonymous complex type.
   *
   * <p>The following schema fragment specifies the expected content contained within this class.
   *
   * <pre>
   * &lt;complexType&gt;
   *   &lt;complexContent&gt;
   *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
   *       &lt;sequence&gt;
   *         &lt;element name="tissuedefaultport" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
   *       &lt;/sequence&gt;
   *     &lt;/restriction&gt;
   *   &lt;/complexContent&gt;
   * &lt;/complexType&gt;
   * </pre>
   *
   *
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder =
    {
      "tissuedefaultport"
    })
  public static class Tissuenervoussystem
  {

    @XmlElement(required = true)
    protected BigInteger tissuedefaultport;

    /**
     * Gets the value of the tissuedefaultport property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getTissuedefaultport()
    {
      return tissuedefaultport;
    }

    /**
     * Sets the value of the tissuedefaultport property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setTissuedefaultport(BigInteger value)
    {
      this.tissuedefaultport = value;
    }

  }


  /**
   * <p>Java class for anonymous complex type.
   *
   * <p>The following schema fragment specifies the expected content contained within this class.
   *
   * <pre>
   * &lt;complexType&gt;
   *   &lt;complexContent&gt;
   *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
   *       &lt;sequence&gt;
   *         &lt;element name="tissueservice" type="{http://www.biosphere.org/tissue/1.0/service}tissueservicetype" maxOccurs="unbounded" minOccurs="0"/&gt;
   *       &lt;/sequence&gt;
   *     &lt;/restriction&gt;
   *   &lt;/complexContent&gt;
   * &lt;/complexType&gt;
   * </pre>
   *
   *
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name = "", propOrder =
    {
      "tissueservice"
    })
  public static class Tissueservices
  {

    protected List<Tissueservicetype> tissueservice;

    /**
     * Gets the value of the tissueservice property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tissueservice property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTissueservice().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tissueservicetype }
     *
     *
     */
    public List<Tissueservicetype> getTissueservice()
    {
      if (tissueservice == null)
      {
        tissueservice = new ArrayList<Tissueservicetype>();
      }
      return this.tissueservice;
    }

  }

}
