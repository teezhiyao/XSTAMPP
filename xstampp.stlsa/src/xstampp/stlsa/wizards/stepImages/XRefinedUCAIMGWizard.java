/*******************************************************************************
 * Copyright (c) 2013, 2017 Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner
 * Institute of Software Technology, Software Engineering Group
 * University of Stuttgart, Germany
 *  
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package xstampp.stlsa.wizards.stepImages;

import messages.Messages;
import xstampp.stlsa.Activator;
import xstampp.stlsa.messages.StlsaMessages;
import xstampp.stlsa.wizards.AbstractPrivacyExportWizard;
import xstampp.ui.wizards.TableExportPage;

public class XRefinedUCAIMGWizard extends AbstractPrivacyExportWizard {

	public XRefinedUCAIMGWizard() {
		super("");
		String[] filters = new String[] {"*.png" ,"*.bmp"}; //$NON-NLS-1$ 
		this.setExportPage(new TableExportPage(filters,
				StlsaMessages.RefinedUnsecureControlActions + Messages.AsImage, Activator.PLUGIN_ID)); //$NON-NLS-1$
		
	}

	@Override
	public boolean performFinish() {
		return this.performXSLExport(				
				"/fopRefinedUnsafeControlActions.xsl", false,StlsaMessages.RefinedUnsecureControlActions, false); ////$NON-NLS-1$
	}
}

