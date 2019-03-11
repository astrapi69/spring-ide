/*******************************************************************************
 * Copyright (c) 2009 Spring IDE Developers
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Spring IDE Developers - initial API and implementation
 *******************************************************************************/

package org.springframework.oxm.v1.config;

import org.w3c.dom.Element;

import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;

/**
 * Parser for the <code>&lt;oxm:jaxb1-marshaller/&gt; element.
 * @author Christian Dupuis
 * @author Arjen Poutsma
 * @since 1.5.0
 */
class Jaxb1MarshallerBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    public static final String JAXB1_MARSHALLER_CLASS_NAME = "org.springframework.oxm.jaxb.Jaxb1Marshaller";

    protected String getBeanClassName(Element element) {
        return JAXB1_MARSHALLER_CLASS_NAME;
    }
}
