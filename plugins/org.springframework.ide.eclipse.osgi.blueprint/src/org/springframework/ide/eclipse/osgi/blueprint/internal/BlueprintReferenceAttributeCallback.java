/******************************************************************************
 * Copyright (c) 2006, 2010 VMware Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution. 
 * The Eclipse Public License is available at 
 * https://www.eclipse.org/legal/epl-v10.html and the Apache License v2.0
 * is available at https://www.opensource.org/licenses/apache2.0.php.
 * You may elect to redistribute this code under either of these licenses. 
 * 
 * Contributors:
 *   VMware Inc.
 *   Spring IDE Developers
 *****************************************************************************/

package org.springframework.ide.eclipse.osgi.blueprint.internal;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.ide.eclipse.osgi.blueprint.internal.jaxb.Tavailability;
import org.springframework.ide.eclipse.osgi.blueprint.internal.util.AttributeCallback;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * RFC124/Blueprint specific attributes that need to be converted to Spring DM.
 * 
 * @author Costin Leau
 * @author Arnaud Mergey
 * 
 * @since 3.7.2
 */
public class BlueprintReferenceAttributeCallback implements AttributeCallback {

	private static final String AVAILABILITY = "availability";

	private static final String SERVICE_BEAN_NAME_PROP = "serviceBeanName";

	private static final String COMPONENT_NAME = "component-name";

	public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
		String name = attribute.getLocalName();
		String value = attribute.getValue();

		if (AVAILABILITY.equals(name)) {
			builder.addPropertyValue(AVAILABILITY, Tavailability.valueOf(value));
			return false;
		}

		else if (COMPONENT_NAME.equals(name)) {
			builder.addPropertyValue(SERVICE_BEAN_NAME_PROP, value);
			return false;
		}

		return true;
	}
}