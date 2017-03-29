
package org.biosphere.tissue.DNA;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for celltype complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="celltype"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cellname" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cellpublickey" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cellinterface" type="{http://www.biosphere.org/tissue/1.0/cell/interface}cellinterfacetype"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "celltype", propOrder =
  {
    "cellname", "cellpublickey", "cellinterface"
  })
public class Celltype
{

  @XmlElement(namespace = "http://www.biosphere.org/tissue/1.0/cell", required = true)
  protected String cellname;
  @XmlElement(namespace = "http://www.biosphere.org/tissue/1.0/cell", required = true)
  protected String cellpublickey;
  @XmlElement(namespace = "http://www.biosphere.org/tissue/1.0/cell", required = true)
  protected Cellinterfacetype cellinterface;

  /**
   * Gets the value of the cellname property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getCellname()
  {
    return cellname;
  }

  /**
   * Sets the value of the cellname property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setCellname(String value)
  {
    this.cellname = value;
  }

  /**
   * Gets the value of the cellpublickey property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getCellpublickey()
  {
    return cellpublickey;
  }

  /**
   * Sets the value of the cellpublickey property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setCellpublickey(String value)
  {
    this.cellpublickey = value;
  }

  /**
   * Gets the value of the cellinterface property.
   *
   * @return
   *     possible object is
   *     {@link Cellinterfacetype }
   *
   */
  public Cellinterfacetype getCellinterface()
  {
    return cellinterface;
  }

  /**
   * Sets the value of the cellinterface property.
   *
   * @param value
   *     allowed object is
   *     {@link Cellinterfacetype }
   *
   */
  public void setCellinterface(Cellinterfacetype value)
  {
    this.cellinterface = value;
  }

}
