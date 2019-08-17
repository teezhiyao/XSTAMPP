/*******************************************************************************
 * Copyright (c) 2013, 2017 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam Grahovac, Jarkko
 * Heidenwag, Benedikt Markt, Jaqueline Patzek, Sebastian Sieber, Fabian Toth, Patrick
 * Wickenhäuser, Aliaksei Babkovich, Aleksander Zotov).
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/

package xstampp.astpa.model.submeasurement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.ATableModel;
import xstampp.astpa.model.controlaction.IControlActionController;
import xstampp.astpa.model.controlaction.safetyconstraint.ICorrespondingUnsafeControlAction;
import xstampp.astpa.model.extendedData.interfaces.IExtendedDataController;
import xstampp.astpa.model.hazacc.IHazAccController;
import xstampp.astpa.model.interfaces.IEntryWithNameId;
import xstampp.astpa.model.linking.Link;
import xstampp.astpa.model.linking.LinkController;
import xstampp.astpa.model.linking.LinkingType;
import xstampp.astpa.model.sds.ISDSController;
import xstampp.model.AbstractLTLProvider;
import xstampp.model.AbstractNumberedEntry;

/**
 * A causal factor
 * 
 * @author Fabian
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "submeasurement")
public class SubMeasurement extends ATableModel implements ITableModel, IEntryWithNameId {

  @XmlElement(name = "smid")
  private UUID id;

  @XmlElement(name = "smtext")
  private String text = "";

  /**
   * This is only for representing the data in the export,
   * for reference of the causal factor model during runtime refere to
   * xstampp.astpa/docs/architecture
   */
  @XmlElementWrapper(name = "causalEntries")
  @XmlElement(name = "causalEntry")
  private List<SubMeasurementEntry> entries;

//  @XmlElement
//  private UUID constraintId;
//  
  @XmlElement(name = "smdescription")
  private String description = "Description not set";

  private String modId;
  
  @XmlElement(name = "severityLikelihood")
  private String severityLikelihood;

  public String getSeverityLikelihood() {
    System.out.println("In get SeverityLikelihood: " + this.severityLikelihood);
    return this.severityLikelihood;
  }

  public void setSeverityLikelihood(String severityLikelihood) {
    this.severityLikelihood = severityLikelihood;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSubMeasurement() {
    return subMeasurement;
  }

  public void setSubMeasurement(String subMeasurement) {
    this.subMeasurement = subMeasurement;
  }

  public int getScale() {
    return scale;
  }

  public void setScale(int scale) {
    this.scale = scale;
  }

  private String type;
  @XmlElement(name = "smtitle")
  private String subMeasurement;

  private int scale;
  
  private ArrayList<String> details;
  
  public ArrayList<String> getDetails() {
    return details;
  }
  
  public void addDetails(String newDetail) {
    this.details.add(newDetail);
  }
  
  public void modifyDetail(int x, String newDetail) {
    this.details.set(x, newDetail);
  }
  
  private int row;
//  public UUID getParentUUID() {
//    return parentUUID;
//  }
//
//  public void setParentUUID(UUID parentUUID) {
//    this.parentUUID = parentUUID;
//  }

  /**
   * Constructor of a causal factor
   * 
   * @author Fabian Toth
   * 
   * @param text
   *          the text of the new causal factor
   */
  public SubMeasurement(String title) {
    this.id = UUID.randomUUID();
  }

  public SubMeasurement(String severityLikelihood, String type, String subMeasurement, int scale,int row) {
    this.id = UUID.randomUUID();
    this.severityLikelihood = severityLikelihood;
    this.type = type;
    this.subMeasurement = subMeasurement;
    this.scale = scale;
    this.row = row;
    this.details = new ArrayList<String>();
  }
  

  public int getRow() {
    return row;
  }

  /**
   * Empty constructor used for JAXB. Do not use it!
   * 
   * @author Fabian Toth
   */
  public SubMeasurement() {
    this.text = "";
  }

  @Override
  public UUID getId() {
    return this.id;
  }

  @Override
  public String getText() {
    return this.text;
  }

  /**
   * @param text
   *          the text to set
   */
  boolean setText(String text) {
    if (!this.text.equals(text)) {
      this.text = text;
      return true;
    }
    return false;
  }

  public void prepareForExport(IHazAccController hazAccController,
      IExtendedDataController extendedDataController,
      IControlActionController caController,
      SubMeasurementController controller, List<Link> causalEntryList,
      LinkController linkController, ISDSController sdsController) {
    this.entries = new ArrayList<>();
    for (Link link : causalEntryList) {
      SubMeasurementEntry entry = new SubMeasurementEntry();
      entry.prepareForExport(hazAccController, extendedDataController, caController,
          controller, link, linkController, sdsController);
      this.entries.add(entry);
    }
  }

  public void prepareForSave(UUID componentId, IHazAccController hazAccController,
      List<AbstractLTLProvider> allRefinedRules,
      List<ICorrespondingUnsafeControlAction> allUnsafeControlActions,
      List<SubMeasurementSafetyConstraint> safetyConstraints, LinkController linkController) {

    UUID link = linkController.addLink(LinkingType.UCA_CausalFactor_LINK, null, id);
    linkController.addLink(LinkingType.UcaCfLink_Component_LINK, link, componentId);

    getEntries().forEach(entry -> {
      entry.prepareForSave(componentId, this, linkController, safetyConstraints);
    });
    prepareForSave();
  }

  public void prepareForSave() {
    this.entries = null;
  }

  public List<SubMeasurementEntry> getEntries() {
    if (this.entries == null) {
      this.entries = new ArrayList<>();
    }
    return this.entries;
  }

  @Override
  public String getDescription() {
    return this.description;
  }
  
  public String setDescription(String description) {
    this.description = description;
    return this.description;
  }
  
  @Override
  public String getIdString() {
    return "CF-" + getNumber();
  }

  public String getIdString(boolean modified) {
    return this.modId;
  }
  
  public String setIdString(String x) {
    String xsub = x.substring(3);
    int y = getNumber() - 30;
    this.modId = "CF" + xsub + "."+ y;
    return "CF" + xsub + "."+ y;
  }
}
