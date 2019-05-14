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
package xstampp.stlsa.wizards.pdf;

import messages.Messages;
import xstampp.stlsa.Activator;
import xstampp.stlsa.messages.SecMessages;
import xstampp.stlsa.ui.unsecurecontrolaction.UnsecureControlActionsView;
import xstampp.stlsa.wizards.AbstractPrivacyExportWizard;
import xstampp.ui.wizards.TableExportPage;

/**
 * 
 * @author Lukas Balzer
 * 
 */
public class UCATablePDFWizard extends AbstractPrivacyExportWizard {
	TableExportPage exportPage;

	/**
	 * 
	 * @author Lukas Balzer
	 * 
	 */
	public UCATablePDFWizard() {
		super(UnsecureControlActionsView.ID);
		String[] filters = new String[] { "*.pdf" }; //$NON-NLS-1$
		this.exportPage = new TableExportPage(filters,
				SecMessages.UnsecureControlActionsTable + Messages.AsPDF, Activator.PLUGIN_ID);
		this.setExportPage(this.exportPage);
	}

	@Override
	public void addPages() {
		this.addPage(this.exportPage);
	}

	@Override
	public boolean performFinish() {
		return this.performXSLExport(
				"/fopuca.xsl", false, SecMessages.UnsecureControlActionsTable, false); //$NON-NLS-1$
	}
}
