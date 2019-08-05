/*******************************************************************************
 * Copyright (c) 2013, 2017 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam
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

package xstampp.stlsa.model.vulloss;

import javax.xml.bind.annotation.XmlRootElement;

import xstampp.astpa.model.hazacc.Accident;
import xstampp.astpa.model.interfaces.Severity;

/**
 * Class for accidents
 *
 * @author Fabian Toth
 *
 */
@XmlRootElement(name = "loss")
public class Loss extends Accident {

	/**
	 * Constructor of an accident
	 *
	 * @param title
	 *            the title of the new accident
	 * @param description
	 *            the description of the new accident
	 * @param number
	 *            the number of the new accident
	 *
	 * @author Fabian Toth
	 */
	public Loss(String title, String description, int number) {
		super(title, description, number);
		  setSeverity(Severity.S0);
	}

	/**
	 * Empty constructor for JAXB. Do not use it!
	 *
	 * @author Fabian Toth
	 */
	public Loss() {
		setSeverity(Severity.S0);
		// empty constructor for JAXB
	}

	public Loss(String title, String description) {
		super(title, description);
	}

	@Override
	public String getIdString() {
	  return "A-" +this.getNumber();
	}
}
