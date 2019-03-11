//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="https://java.sun.com/xml/jaxb">https://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 05:47:27 PM CET 
//

package org.springframework.ide.eclipse.osgi.blueprint.internal.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.w3c.dom.Element;

/**
 * 
 * 
 * A property that will be injected into a created <bean> component. The
 * <property> elements correspond to named JavaBean setting methods for a
 * created bean object.
 * 
 * The value and ref attributes are convenience shortcuts to make the
 * <argument> tag easier to code. A fuller set of injected values and types can
 * be specified using one of the "value" type elements.
 * 
 * 
 * 
 * <p>
 * Java class for Tproperty complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="Tproperty">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.osgi.org/xmlns/blueprint/v1.0.0}Tdescription" minOccurs="0"/>
 *         &lt;group ref="{http://www.osgi.org/xmlns/blueprint/v1.0.0}Gvalue" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.osgi.org/xmlns/blueprint/v1.0.0}Tmethod" />
 *       &lt;attribute name="ref" type="{http://www.osgi.org/xmlns/blueprint/v1.0.0}Tidref" />
 *       &lt;attribute name="value" type="{http://www.osgi.org/xmlns/blueprint/v1.0.0}TstringValue" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Tproperty", propOrder = { "description", "service", "referenceList", "bean", "reference", "targetRef",
		"any", "idref", "value", "list", "set", "map", "array", "props", "_null" })
public class Tproperty {

	protected Tdescription description;
	protected TinlinedService service;
	@XmlElement(name = "reference-list")
	protected TinlinedReferenceList referenceList;
	protected TinlinedBean bean;
	protected TinlinedReference reference;
	@XmlElement(name = "ref")
	protected Tref targetRef;
	@XmlAnyElement(lax = true)
	protected Object any;
	protected Tref idref;
	protected Tvalue value;
	protected Tcollection list;
	protected Tcollection set;
	protected Tmap map;
	protected Tcollection array;
	protected Tprops props;
	@XmlElement(name = "null")
	protected Tnull _null;
	@XmlAttribute(name = "name", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String name;
	@XmlAttribute(name = "ref")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String ref;
	@XmlAttribute(name = "value")
	@XmlJavaTypeAdapter(NormalizedStringAdapter.class)
	protected String propertyValue;

	/**
	 * Gets the value of the description property.
	 * 
	 * @return possible object is {@link Tdescription }
	 * 
	 */
	public Tdescription getDescription() {
		return description;
	}

	/**
	 * Sets the value of the description property.
	 * 
	 * @param value
	 *            allowed object is {@link Tdescription }
	 * 
	 */
	public void setDescription(Tdescription value) {
		this.description = value;
	}

	/**
	 * Gets the value of the service property.
	 * 
	 * @return possible object is {@link TinlinedService }
	 * 
	 */
	public TinlinedService getService() {
		return service;
	}

	/**
	 * Sets the value of the service property.
	 * 
	 * @param value
	 *            allowed object is {@link TinlinedService }
	 * 
	 */
	public void setService(TinlinedService value) {
		this.service = value;
	}

	/**
	 * Gets the value of the referenceList property.
	 * 
	 * @return possible object is {@link TinlinedReferenceList }
	 * 
	 */
	public TinlinedReferenceList getReferenceList() {
		return referenceList;
	}

	/**
	 * Sets the value of the referenceList property.
	 * 
	 * @param value
	 *            allowed object is {@link TinlinedReferenceList }
	 * 
	 */
	public void setReferenceList(TinlinedReferenceList value) {
		this.referenceList = value;
	}

	/**
	 * Gets the value of the bean property.
	 * 
	 * @return possible object is {@link TinlinedBean }
	 * 
	 */
	public TinlinedBean getBean() {
		return bean;
	}

	/**
	 * Sets the value of the bean property.
	 * 
	 * @param value
	 *            allowed object is {@link TinlinedBean }
	 * 
	 */
	public void setBean(TinlinedBean value) {
		this.bean = value;
	}

	/**
	 * Gets the value of the reference property.
	 * 
	 * @return possible object is {@link TinlinedReference }
	 * 
	 */
	public TinlinedReference getReference() {
		return reference;
	}

	/**
	 * Sets the value of the reference property.
	 * 
	 * @param value
	 *            allowed object is {@link TinlinedReference }
	 * 
	 */
	public void setReference(TinlinedReference value) {
		this.reference = value;
	}

	/**
	 * Gets the value of the targetRef property.
	 * 
	 * @return possible object is {@link Tref }
	 * 
	 */
	public Tref getTargetRef() {
		return targetRef;
	}

	/**
	 * Sets the value of the targetRef property.
	 * 
	 * @param value
	 *            allowed object is {@link Tref }
	 * 
	 */
	public void setTargetRef(Tref value) {
		this.targetRef = value;
	}

	/**
	 * Gets the value of the any property.
	 * 
	 * @return possible object is {@link Element } {@link Object }
	 * 
	 */
	public Object getAny() {
		return any;
	}

	/**
	 * Sets the value of the any property.
	 * 
	 * @param value
	 *            allowed object is {@link Element } {@link Object }
	 * 
	 */
	public void setAny(Object value) {
		this.any = value;
	}

	/**
	 * Gets the value of the idref property.
	 * 
	 * @return possible object is {@link Tref }
	 * 
	 */
	public Tref getIdref() {
		return idref;
	}

	/**
	 * Sets the value of the idref property.
	 * 
	 * @param value
	 *            allowed object is {@link Tref }
	 * 
	 */
	public void setIdref(Tref value) {
		this.idref = value;
	}

	/**
	 * Gets the value of the value property.
	 * 
	 * @return possible object is {@link Tvalue }
	 * 
	 */
	public Tvalue getValue() {
		return value;
	}

	/**
	 * Sets the value of the value property.
	 * 
	 * @param value
	 *            allowed object is {@link Tvalue }
	 * 
	 */
	public void setValue(Tvalue value) {
		this.value = value;
	}

	/**
	 * Gets the value of the list property.
	 * 
	 * @return possible object is {@link Tcollection }
	 * 
	 */
	public Tcollection getList() {
		return list;
	}

	/**
	 * Sets the value of the list property.
	 * 
	 * @param value
	 *            allowed object is {@link Tcollection }
	 * 
	 */
	public void setList(Tcollection value) {
		this.list = value;
	}

	/**
	 * Gets the value of the set property.
	 * 
	 * @return possible object is {@link Tcollection }
	 * 
	 */
	public Tcollection getSet() {
		return set;
	}

	/**
	 * Sets the value of the set property.
	 * 
	 * @param value
	 *            allowed object is {@link Tcollection }
	 * 
	 */
	public void setSet(Tcollection value) {
		this.set = value;
	}

	/**
	 * Gets the value of the map property.
	 * 
	 * @return possible object is {@link Tmap }
	 * 
	 */
	public Tmap getMap() {
		return map;
	}

	/**
	 * Sets the value of the map property.
	 * 
	 * @param value
	 *            allowed object is {@link Tmap }
	 * 
	 */
	public void setMap(Tmap value) {
		this.map = value;
	}

	/**
	 * Gets the value of the array property.
	 * 
	 * @return possible object is {@link Tcollection }
	 * 
	 */
	public Tcollection getArray() {
		return array;
	}

	/**
	 * Sets the value of the array property.
	 * 
	 * @param value
	 *            allowed object is {@link Tcollection }
	 * 
	 */
	public void setArray(Tcollection value) {
		this.array = value;
	}

	/**
	 * Gets the value of the props property.
	 * 
	 * @return possible object is {@link Tprops }
	 * 
	 */
	public Tprops getProps() {
		return props;
	}

	/**
	 * Sets the value of the props property.
	 * 
	 * @param value
	 *            allowed object is {@link Tprops }
	 * 
	 */
	public void setProps(Tprops value) {
		this.props = value;
	}

	/**
	 * Gets the value of the null property.
	 * 
	 * @return possible object is {@link Tnull }
	 * 
	 */
	public Tnull getNull() {
		return _null;
	}

	/**
	 * Sets the value of the null property.
	 * 
	 * @param value
	 *            allowed object is {@link Tnull }
	 * 
	 */
	public void setNull(Tnull value) {
		this._null = value;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * Gets the value of the ref property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * Sets the value of the ref property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRef(String value) {
		this.ref = value;
	}

	/**
	 * Gets the value of the propertyValue property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPropertyValue() {
		return propertyValue;
	}

	/**
	 * Sets the value of the propertyValue property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPropertyValue(String value) {
		this.propertyValue = value;
	}

}
