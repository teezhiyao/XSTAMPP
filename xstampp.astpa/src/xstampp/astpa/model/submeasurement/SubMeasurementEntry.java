/*******************************************************************************
 * Copyright (c) 2013, 2017 Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner Institute of Software
 * Technology, Software Engineering Group University of Stuttgart, Germany
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package xstampp.astpa.model.submeasurement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import xstampp.astpa.model.ATableModel;
import xstampp.astpa.model.controlaction.IControlActionController;
import xstampp.astpa.model.controlaction.interfaces.IUnsafeControlAction;
import xstampp.astpa.model.extendedData.interfaces.IExtendedDataController;
import xstampp.astpa.model.hazacc.IHazAccController;
import xstampp.astpa.model.linking.Link;
import xstampp.astpa.model.linking.LinkController;
import xstampp.astpa.model.linking.LinkingType;
import xstampp.astpa.model.sds.ISDSController;
import xstampp.model.AbstractLTLProvider;

@XmlAccessorType(XmlAccessType.NONE)
public class SubMeasurementEntry {

  @XmlAttribute(name = "id", required = true)
  private UUID id;

  @XmlElement(name = "hazardLinks")
  private String hazardLinks;

  @XmlElement(name = "constraintText")
  private String constraintText;

  @XmlElement
  private UUID constraintId;

  @XmlElementWrapper(name = "hazardIds")
  @XmlElement(name = "id")
  private List<UUID> hazardIds;

  @XmlElement(name = "note")
  private String note;

  @XmlElement(name = "ucaDescription")
  private String ucaDescription;

  @XmlElement(name = "ucaLink")
  private UUID ucaLink;

  @XmlElementWrapper(name = "scenarioEntries")
  @XmlElement(name = "scenarioEntry")
  private List<SubMeasurementScenarioEntry> scenarioEntries;

  @XmlElementWrapper(name = "scenarioLinks")
  @XmlElement(name = "id")
  private List<UUID> scenarioLinks;

  @XmlElementWrapper(name = "hazardEntries")
  @XmlElement(name = "entry")
  private List<SubMeasurementHazardEntry> hazardEntries;

  public SubMeasurementEntry() {
    id = UUID.randomUUID();
  }

  boolean prepareForSave(UUID componentId, SubMeasurement causalFactor, LinkController linkController,
      List<SubMeasurementSafetyConstraint> list) {
    // get rid of redundant entries which should not be stored
    if (constraintText != null) {
      SubMeasurementSafetyConstraint safetyConstraint = new SubMeasurementSafetyConstraint(constraintText);
      this.constraintId = safetyConstraint.getId();
      list.add(safetyConstraint);
    }
    if (ucaLink != null) {
      UUID ucaCfLink = linkController.addLink(LinkingType.UCA_CausalFactor_LINK, ucaLink,
          causalFactor.getId());
      UUID UcaCfCompLink = linkController.addLink(LinkingType.UcaCfLink_Component_LINK, ucaCfLink,
          componentId);
      // if a constraint is already linked to this entry than the link is re-added as
      // UcaHazLink_SC2_LINK to the LinkController

      linkController.addLink(LinkingType.CausalEntryLink_SC2_LINK, UcaCfCompLink,
          this.constraintId);
      linkController.changeLinkNote(UcaCfCompLink, LinkingType.UcaCfLink_Component_LINK, note);
    }
    constraintText = null;
    ucaLink = null;
    scenarioEntries = null;
    ucaDescription = null;
    hazardEntries = null;
    hazardIds = null;
    hazardLinks = null;
    return true;
  }

  public void prepareForExport(IHazAccController hazAccController,
      IExtendedDataController extendedDataController, IControlActionController caController,
      SubMeasurementController controller, Link causalEntryLink, LinkController linkController,
      ISDSController sdsController) {
    Link ucaCfLink = linkController.getLinkObjectFor(LinkingType.UCA_CausalFactor_LINK,
        causalEntryLink.getLinkA());
    IUnsafeControlAction uca = ucaCfLink.isLinkAPresent() ? caController.getUnsafeControlAction(ucaCfLink.getLinkA())
        : null;
    hazardEntries = new ArrayList<>();
    hazardLinks = "";
    scenarioEntries = new ArrayList<>();
    if (uca != null) {
      ucaDescription = uca.getIdString() + "\n" + uca.getDescription();
      if (!controller.isUseScenarios()) {
        // If scenarios are not used than either the CausalEntryLink_HAZ_LINK
        Optional<Link> singleConstraintLink = linkController.getRawLinksFor(LinkingType.CausalEntryLink_SC2_LINK,
            causalEntryLink.getId()).stream().findFirst();
        if (singleConstraintLink.isPresent()) {
          
          Optional<UUID> sCoption = Optional.ofNullable(singleConstraintLink.get().getLinkB());
          List<UUID> hazIds = linkController.getLinksFor(LinkingType.UCA_HAZ_LINK, uca.getId());
          SubMeasurementHazardEntry hazEntry = new SubMeasurementHazardEntry(controller, linkController, sdsController, caController,
              hazIds, sCoption, hazAccController);
          hazEntry.setNote(causalEntryLink.getNote());
          hazEntry.setDesignHint(singleConstraintLink.get().getNote());
          hazardEntries.add(hazEntry);
        } else {
          for (Link causalHazLink : linkController.getRawLinksFor(LinkingType.CausalEntryLink_HAZ_LINK,
              causalEntryLink.getId())) {
            ATableModel hazard = hazAccController.getHazard(causalHazLink.getLinkB());
            Optional<UUID> causalSCoption = linkController
                .getLinksFor(LinkingType.CausalHazLink_SC2_LINK, causalHazLink.getId()).stream()
                .findFirst();
            SubMeasurementHazardEntry hazEntry = new SubMeasurementHazardEntry(controller, linkController, sdsController, caController,
                Arrays.asList(hazard.getId()), causalSCoption, hazAccController);

            Link scLink = linkController
                .getLinkObjectFor(LinkingType.CausalHazLink_SC2_LINK, causalHazLink.getId(),
                    causalSCoption.isPresent() ? causalSCoption.get() : null);
            hazEntry.setDesignHint(scLink == null ? "" : scLink.getNote());
            hazEntry.setNote(causalHazLink.getNote());
            hazardEntries.add(hazEntry);
          }
        }
      } else {
        //if scenarios are used 
        for (Link causalHazLink : linkController.getRawLinksFor(LinkingType.CausalEntryLink_HAZ_LINK,
            causalEntryLink.getId())) {
          ATableModel hazard = hazAccController.getHazard(causalHazLink.getLinkB());
          hazardLinks += hazardLinks.isEmpty() ? "" : ",";
          hazardLinks += hazard.getIdString();
        }
        for (Link causalScenarioLink : linkController.getRawLinksFor(
            LinkingType.CausalEntryLink_Scenario_LINK,
            causalEntryLink.getId())) {
          AbstractLTLProvider refinedScenario = extendedDataController
              .getRefinedScenario(causalScenarioLink.getLinkB());

          String constraintText = refinedScenario.getRefinedSafetyConstraint();
          SubMeasurementScenarioEntry entry = new SubMeasurementScenarioEntry(refinedScenario.getDescription(), constraintText);
          entry.setNote(causalScenarioLink.getNote());
          scenarioEntries.add(entry);
        }
      }
    }
  }

  public String getHazardLinks() {
    return hazardLinks;
  }

  public String getConstraintText() {
    return constraintText;
  }

  public String getNote() {
    return note;
  }

  public String getUcaDescription() {
    return ucaDescription;
  }

  public List<SubMeasurementScenarioEntry> getScenarioEntries() {
    return scenarioEntries;
  }

  public List<SubMeasurementHazardEntry> getHazardEntries() {
    return hazardEntries;
  }

}