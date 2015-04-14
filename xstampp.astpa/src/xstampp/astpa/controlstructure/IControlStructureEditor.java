/*******************************************************************************
 * Copyright (c) 2013 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam
 * Grahovac, Jarkko Heidenwag, Benedikt Markt, Jaqueline Patzek, Sebastian
 * Sieber, Fabian Toth, Patrick Wickenhäuser, Aliaksei Babkovich, Aleksander
 * Zotov).
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/

package xstampp.astpa.controlstructure;

import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.Viewport;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.palette.PaletteListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.ISelectionListener;

import xstampp.ui.common.IViewBase;
import xstampp.util.export.IExportView;

/**
 * 
 * @author Lukas Balzer
 * 
 */
public interface IControlStructureEditor extends CommandStackListener,
		ISelectionListener, MouseListener, PaletteListener, IViewBase,
		SelectionListener, ZoomListener, PropertyChangeListener,
		ISelectionChangedListener, IExportView {

	
	/**
	 * this property stores the active step editor the value is always expectedf
	 * to be the id of the Editor
	 * 
	 * @author Lukas Balzer
	 */
	public static final String STEP_EDITOR = "step"; //$NON-NLS-1$
	
	
	/**
	 * makes sure that the both layouts of step 1 and 3 are only synchronized on
	 * their initial call
	 * 
	 * @author Lukas Balzer
	 * @param step1
	 *            whether Part 1 has been initialized or not
	 * @param step3
	 *            whether Part 3 has been initialized or not
	 */
	void initialSync(boolean step1, boolean step3);

	/**
	 * 
	 * @author Lukas Balzer
	 * 
	 * @return the zoom level as double
	 */
	double getZoomLevel();

	/**
	 * 
	 * @author Lukas Balzer
	 * 
	 * @param zoom
	 */
	void setZoomLevel(double zoom);

	/**
	 * 
	 * @author Lukas Balzer
	 * 
	 * @return the zoom Port
	 */
	Viewport getViewport();

	/**
	 * 
	 * @author Lukas Balzer
	 * 
	 * @param view
	 */
	void setViewport(Viewport view);

}