/*******************************************************************************
 * Copyright (c) 2013, 2017 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam Grahovac, Jarkko
 * Heidenwag, Benedikt Markt, Jaqueline Patzek, Sebastian Sieber, Fabian Toth, Patrick Wickenhäuser,
 * Aliaksei Babkovich, Aleksander Zotov).
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/

package xstampp.stlsa.ui.linking;

import xstampp.astpa.ui.linking.LinkingView;
import xstampp.stlsa.messages.StlsaMessages;

/**
 * Editor to create and delete links between hazards and accidents. There are two table modes:
 * accident mode and hazard mode, which identifies whether accidents are linked to hazards or
 * hazards to accidents respectively.
 * 
 * @author Patrick Wickenhaeuser
 * @author Jarkko Heidenwag
 */
public class SecLinkingView extends LinkingView {

  /**
   * Identifier of the view
   */
  public static final String ID = "stlsa.steps.step1_4";

  /**
   * Updates the text of the mode button.
   * 
   * @author Patrick Wickenhaeuser
   */
  public void updateMode() {
    if (isInAtoB_Mode()) {
      this.setUpdateSelected(StlsaMessages.Losses);
      this.setUpdateAvailableForLinking(StlsaMessages.VulnerabilitiesAvailableForLinking);
      this.setUpdateCurrentlyLinked(StlsaMessages.CurrentlyLinkedVulnerabilities);
      this.setUpdateModeButton(StlsaMessages.SwitchToVulnerabilities);
    } else {
      this.setUpdateSelected(StlsaMessages.Vulnerabilities);
      this.setUpdateAvailableForLinking(StlsaMessages.LossesAvailableForLinking);
      this.setUpdateCurrentlyLinked(StlsaMessages.CurrentlyLinkedLosses);
      this.setUpdateModeButton(StlsaMessages.SwitchToLosses);
    }

    updateSelection();
  }

  public String getTitle() {
    return StlsaMessages.LinkingOfAccidentsAndHazards;
  }

}
