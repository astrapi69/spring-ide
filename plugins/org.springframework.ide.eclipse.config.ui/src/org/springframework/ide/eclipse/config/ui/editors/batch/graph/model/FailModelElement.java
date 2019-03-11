/*******************************************************************************
 *  Copyright (c) 2012 VMware, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      VMware, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.config.ui.editors.batch.graph.model;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.springframework.ide.eclipse.config.core.schemas.BatchSchemaConstants;
import org.springframework.ide.eclipse.config.graph.model.AbstractConfigGraphDiagram;
import org.springframework.ide.eclipse.config.graph.model.Activity;


/**
 * @author Leo Dos Santos
 * @author Christian Dupuis
 */
@SuppressWarnings("restriction")
public class FailModelElement extends Activity {

	public FailModelElement() {
		super();
	}

	public FailModelElement(IDOMElement input, AbstractConfigGraphDiagram diagram) {
		super(input, diagram);
	}

	@Override
	public String getInputName() {
		return BatchSchemaConstants.ELEM_FAIL;
	}

	@Override
	protected void internalSetName() {
		String localName = getInput().getLocalName();
		String onAttr = BatchSchemaConstants.ATTR_ON;
		String onVal = getInput().getAttribute(onAttr);
		if (onVal == null) {
			onVal = ""; //$NON-NLS-1$
		}
		String name = localName + " " + onAttr + "=\"" + onVal + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String exitAttr = BatchSchemaConstants.ATTR_EXIT_CODE;
		String exitVal = getInput().getAttribute(exitAttr);
		if (exitVal != null) {
			name = name.concat("\n" + exitAttr + "=\"" + exitVal + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		setName(name);
	}

}
