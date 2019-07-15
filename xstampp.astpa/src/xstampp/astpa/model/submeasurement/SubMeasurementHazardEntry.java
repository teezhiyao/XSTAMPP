/*******************************************************************************
 * Copyright (C) 2018 Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner Institute of SoftwareTechnology, Software Engineering Group University of Stuttgart, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner Institute of SoftwareTechnology, Software Engineering Group University of Stuttgart, Germany - initial API and implementation
 ******************************************************************************/
package xstampp.astpa.model.submeasurement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import xstampp.astpa.model.controlaction.IControlActionController;
import xstampp.astpa.model.hazacc.IHazAccController;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.linking.LinkController;
import xstampp.astpa.model.linking.LinkingType;
import xstampp.astpa.model.sds.ISDSController;
import xstampp.model.ObserverValue;

@XmlAccessorType(XmlAccessType.NONE)
public class SubMeasurementHazardEntry {

  @XmlElement
  private String text;

  @XmlElement
  private String constraint;

  @XmlElement
  private String designHint;

  @XmlElement
  private String designRequirementLink;

  @XmlElement
  private String cscReference;

  @XmlElement
  private String links;

  @XmlElement
  private String note;

  public SubMeasurementHazardEntry(SubMeasurementController controller, LinkController linkController,
      ISDSController sdsController, IControlActionController caController, List<UUID> hazIds,
      Optional<UUID> causalSCoption, IHazAccController hazAccController) {
    String constraintText = controller.getConstraintTextFor(causalSCoption.orElse(null));
    Optional<UUID> sc1Option = linkController
        .getLinksFor(LinkingType.SC2_SC1_LINK, causalSCoption.orElse(null)).stream()
        .findFirst();
    Optional<ITableModel> sc1Model = Optional
        .ofNullable(caController.getCorrespondingSafetyConstraint(sc1Option.orElse(null)));
    Optional<UUID> designReqOption = linkController
        .getLinksFor(LinkingType.DR2_CausalSC_LINK, causalSCoption.orElse(null)).stream().findFirst();
    Optional<ITableModel> reqModelOption = Optional
        .ofNullable(
            sdsController.getDesignRequirement(designReqOption.orElse(null), ObserverValue.DESIGN_REQUIREMENT_STEP2));

    constraint = constraintText;
    text = "";
    for (UUID hazId : hazIds) {
      text += text.isEmpty() ? "" : ", ";
      text += hazAccController.getHazard(hazId).getIdString();
    }
    links = "";
    cscReference = sc1Model.isPresent() ? sc1Model.get().getIdString() : "";
    links += cscReference;
    designRequirementLink = reqModelOption.isPresent() ? reqModelOption.get().getIdString() : "";
    links += !links.isEmpty() && !designRequirementLink.isEmpty() ? ", " : "";
    links += designRequirementLink;
  }

  public String getDesignHint() {
    return designHint;
  }

  public void setDesignHint(String designHint) {
    this.designHint = designHint;
  }

  public String getText() {
    return text;
  }

  public String getConstraint() {
    return constraint;
  }

  public String getDesignRequirementLink() {
    return designRequirementLink;
  }

  public String getCscReference() {
    return cscReference;
  }

  public String getLinks() {
    return links;
  }

  public void setLinks(String links) {
    this.links = links;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

}
