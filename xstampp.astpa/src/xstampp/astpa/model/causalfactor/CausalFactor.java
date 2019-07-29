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

package xstampp.astpa.model.causalfactor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

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
public class CausalFactor extends ATableModel implements ITableModel, IEntryWithNameId {

  @XmlElement(name = "id")
  private UUID id;

  @XmlElement(name = "text")
  private String text;

  
  /**
   * This is only for representing the data in the export,
   * for reference of the causal factor model during runtime refere to
   * xstampp.astpa/docs/architecture
   */
  @XmlElementWrapper(name = "causalEntries")
  @XmlElement(name = "causalEntry")
  private List<CausalFactorEntry> entries;

  @XmlElement
  private UUID constraintId;
  
  @XmlElement(name = "intention")
  private String intention;
  
  @XmlElement(name = "title")
  private String title;
  
  @XmlElement(name = "description")
  private String description = "Description not set";

  @XmlElement(name = "number")
  private int number;
  
  private String modId;
  private UUID parentUUID;
  private HashMap<String,Integer> subMeasurements;
  
  
  public HashMap<String, Integer> getSubMeasurements() {
    return subMeasurements;
  }

  public Integer getSubMeasurements(String subMeasurementTitle) {
    if(subMeasurements.containsKey(subMeasurementTitle)) {
      return subMeasurements.get(subMeasurementTitle);
      }
    else {
      return -999;
    }
  }
  
  public void setSubMeasurements(String subMeasurementTitle, Integer scale) {
    this.subMeasurements.put(subMeasurementTitle, scale);
  }

  public UUID getParentUUID() {
    return parentUUID;
  }

  public void setParentUUID(UUID parentUUID) {
    this.parentUUID = parentUUID;
  }

  /**
   * Constructor of a causal factor
   * 
   * @author Fabian Toth
   * 
   * @param text
   *          the text of the new causal factor
   */
  public CausalFactor(String title) {
    this(title, "Not set");
  }

  public CausalFactor(String title, String intention) {
    this.id = UUID.randomUUID();
    this.title = title;
    this.intention = intention;
    this.subMeasurements = new HashMap<String, Integer>();
    this.number = 0;
  }
  
  public String getIntention() {
    return intention;
  }

  public void setIntention(String intention) {
    this.intention = intention;
  }

  /**
   * Empty constructor used for JAXB. Do not use it!
   * 
   * @author Fabian Toth
   */
  public CausalFactor() {
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
      CausalFactorController controller, List<Link> causalEntryList,
      LinkController linkController, ISDSController sdsController) {
    this.entries = new ArrayList<>();
    for (Link link : causalEntryList) {
      CausalFactorEntry entry = new CausalFactorEntry();
      entry.prepareForExport(hazAccController, extendedDataController, caController,
          controller, link, linkController, sdsController);
      this.entries.add(entry);
    }
  }

  public void prepareForSave(UUID componentId, IHazAccController hazAccController,
      List<AbstractLTLProvider> allRefinedRules,
      List<ICorrespondingUnsafeControlAction> allUnsafeControlActions,
      List<CausalSafetyConstraint> safetyConstraints, LinkController linkController) {

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

  public List<CausalFactorEntry> getEntries() {
    if (this.entries == null) {
      this.entries = new ArrayList<>();
    }
    return this.entries;
  }

  @Override
  public String getTitle() {
    return this.title;
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
