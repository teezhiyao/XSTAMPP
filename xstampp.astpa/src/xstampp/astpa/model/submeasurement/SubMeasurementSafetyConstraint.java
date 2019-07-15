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

package xstampp.astpa.model.submeasurement;

import xstampp.astpa.model.ATableModel;
import xstampp.astpa.model.interfaces.ITableModel;

/**
 * A corresponding safety constraint
 * 
 * @author Fabian Toth
 * 
 */
class SubMeasurementSafetyConstraint extends ATableModel {

  private static final String ID = "SC2.";

  /**
   * Constructor of a causal factor safety constraint
   * 
   * @param title
   *          the description of the new safety constraint
   * 
   * @author Fabian Toth
   */
  public SubMeasurementSafetyConstraint(String title) {
    super(title, "", -1);
  }

  public SubMeasurementSafetyConstraint(ITableModel model) {
    super(model, -1);
  }

  /**
   * Empty constructor used for JAXB. Do not use it!
   * 
   * @author Fabian Toth
   */
  public SubMeasurementSafetyConstraint() {
    // empty constructor for JAXB
  }

  @Override
  public String getDescription() {
    return super.getDescription();
  }

  @Override
  public String getIdString() {
    return ID + super.getIdString();
  }
}
