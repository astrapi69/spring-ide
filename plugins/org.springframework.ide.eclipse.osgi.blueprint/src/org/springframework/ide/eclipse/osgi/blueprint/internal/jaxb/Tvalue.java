//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="https://java.sun.com/xml/jaxb">https://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 05:47:27 PM CET 
//

package org.springframework.ide.eclipse.osgi.blueprint.internal.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 
 * 
 * Tvalue is the type used for <value> elements. The <value> element is used for
 * types that can be created from a single string value. The string value is the
 * data value for the element. The optional type attribute allows a target
 * conversion value to be explicitly specified.
 * 
 * 
 * 
 * <p>
 * Java class for Tvalue complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="Tvalue">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="type" type="{http://www.osgi.org/xmlns/blueprint/v1.0.0}Ttype" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Tvalue", propOrder = { "content" })
public class Tvalue {

	@XmlValue
	protected String content;
	@XmlAttribute(name = "type")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String type;

	/**
	 * 
	 * 
	 * Tvalue is the type used for <value> elements. The <value> element is used
	 * for types that can be created from a single string value. The string
	 * value is the data value for the element. The optional type attribute
	 * allows a target conversion value to be explicitly specified.
	 * 
	 * 
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the value of the content property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setContent(String value) {
		this.content = value;
	}

	/**
	 * Gets the value of the type property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the value of the type property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setType(String value) {
		this.type = value;
	}

}
