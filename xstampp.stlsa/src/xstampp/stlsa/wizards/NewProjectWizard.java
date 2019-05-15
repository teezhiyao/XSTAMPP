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
package xstampp.stlsa.wizards;

import java.util.HashMap;
import java.util.Map;

import messages.Messages;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import xstampp.stlsa.Activator;
import xstampp.ui.wizards.NewProjectPage;
import xstampp.util.STPAPluginUtils;

/**
 * 
 * @author Lukas Balzer
 * 
 */
public class NewProjectWizard extends Wizard implements INewWizard {

	private final NewProjectPage page;

	/**
	 * 
	 * @author Lukas Balzer
	 * 
	 */
	public NewProjectWizard() {
		ImageDescriptor desc = Activator
				.getImageDescriptor("icons/branding/i64.png"); //$NON-NLS-1$
		this.page = new NewProjectPage(Messages.CreateNewProject,
				Messages.NewProject, desc,"stlsa"); //$NON-NLS-1$ //$NON-NLS-2$
				
		this.addPage(this.page);
	}

	@Override
	public boolean performFinish() {

		Map<String, String> values = new HashMap<>();
		values.put("stlsa.new.name", this.page.getNewProjectName()); //$NON-NLS-1$
		values.put("stlsa.new.path", this.page.getNewProjectPath()); //$NON-NLS-1$
		STPAPluginUtils.executeParaCommand("stlsa.commands.new", values); //$NON-NLS-1$
		return true;
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		// initially empty

	}
}
