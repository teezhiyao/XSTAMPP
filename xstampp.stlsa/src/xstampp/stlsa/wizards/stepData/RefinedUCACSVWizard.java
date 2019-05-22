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

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;

import messages.Messages;
import xstampp.model.IDataModel;
import xstampp.stlsa.Activator;
import xstampp.stlsa.messages.StlsaMessages;
import xstampp.stlsa.util.jobs.XCSVExportJob;
import xstampp.stlsa.wizards.AbstractPrivacyExportWizard;
import xstampp.ui.common.ProjectManager;
import xstampp.ui.wizards.CSVExportPage;

/**
 * 
 * @author Lukas Balzer
 * 
 */
public class RefinedUCACSVWizard extends AbstractPrivacyExportWizard {

	/**
	 * 
	 * @author Lukas Balzer
	 * 
	 */
	public RefinedUCACSVWizard() {
		super("");
		String[] filters = new String[] { "*.csv" }; //$NON-NLS-1$
		this.setExportPage(new CSVExportPage(filters, StlsaMessages.RefinedUnsecureControlActions + Messages.AsDataSet, Activator.PLUGIN_ID));
	}

	@Override
	public boolean performFinish() {
		String filePath = getExportPage().getExportPath();
		try {
			if (this.checkError(this.checkPath(filePath))) {
				IDataModel model = ProjectManager.getContainerInstance()
						.getDataModel(this.getExportPage().getProjectID());
				XCSVExportJob export = new XCSVExportJob("Export CSV",	filePath,
						((CSVExportPage) this.getExportPage()).getSeperator(),
						model, XCSVExportJob.REFINED_UCA);
				export.schedule();
			} else {
				return false;
			}
		} catch (IOException e) {
			MessageDialog.openWarning(this.getShell(), Messages.Warning,
					Messages.ChooseTheDestination);
			return false;
		}
		return true;
	}

}
