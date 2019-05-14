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
/**
 * 
 * @author Lukas Balzer
 */
package xstampp.stlsa.wizards.stepData;

import messages.Messages;
import xstampp.stlsa.Activator;
import xstampp.stlsa.messages.SecMessages;
import xstampp.stlsa.ui.vulloss.VulnerabilityView;
import xstampp.stlsa.util.jobs.ICSVExportConstants;
import xstampp.stlsa.wizards.AbstractPrivacyExportWizard;
import xstampp.ui.wizards.CSVExportPage;

/**
 * 
 * @author Lukas Balzer
 * 
 */
public class VulnerabilitiesWizard extends AbstractPrivacyExportWizard {

	/**
	 * 
	 * @author Lukas Balzer
	 * 
	 */
	public VulnerabilitiesWizard() {
		super(VulnerabilityView.ID);
		String[] filters = new String[] { "*.csv" }; //$NON-NLS-1$ 
		this.setExportPage(new CSVExportPage(filters, SecMessages.Vulnerabilities + Messages.AsDataSet, Activator.PLUGIN_ID));
	}

	@Override
	public boolean performFinish() {
		return this.performCSVExport(ICSVExportConstants.HAZARD);
	}
}
