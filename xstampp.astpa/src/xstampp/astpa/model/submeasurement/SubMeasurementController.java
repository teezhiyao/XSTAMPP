/*******************************************************************************
 * Copyright (c) 2013-2017 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam Grahovac, Jarkko
 * Heidenwag, Benedikt Markt, Jaqueline Patzek, Sebastian Sieber, Fabian Toth, Patrick Wickenhäuser,
 * Aliaksei Babkovich, Aleksander Zotov).
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/

package xstampp.astpa.model.submeasurement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import xstampp.astpa.model.ATableModel;
import xstampp.astpa.model.ATableModelController;
import xstampp.astpa.model.BadReferenceModel;
import xstampp.astpa.model.causalfactor.interfaces.ICausalComponent;
import xstampp.astpa.model.controlaction.IControlActionController;
import xstampp.astpa.model.controlaction.interfaces.IUnsafeControlAction;
import xstampp.astpa.model.controlaction.safetyconstraint.ICorrespondingUnsafeControlAction;
import xstampp.astpa.model.controlstructure.components.Component;
import xstampp.astpa.model.controlstructure.components.ComponentType;
import xstampp.astpa.model.controlstructure.interfaces.IRectangleComponent;
import xstampp.astpa.model.extendedData.interfaces.IExtendedDataController;
import xstampp.astpa.model.hazacc.IHazAccController;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.linking.Link;
import xstampp.astpa.model.linking.LinkController;
import xstampp.astpa.model.linking.LinkingType;
import xstampp.astpa.model.sds.ISDSController;
import xstampp.astpa.model.service.UndoTextChange;
import xstampp.astpa.preferences.ASTPADefaultConfig;
import xstampp.model.AbstractLTLProvider;
import xstampp.model.NumberedArrayList;
import xstampp.model.ObserverValue;

/**
 * Manager class for the causal factors
 * 
 * for reference of the causal factor model during runtime refere to xstampp.astpa/docs/architecture
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SubMeasurementController extends ATableModelController implements ISubMeasurementController {

  @XmlElementWrapper(name = "causalComponents")
  @XmlElement(name = "causalComponent")
  private Map<UUID, SubMeasurementCSComponent> causalComponents;

  @XmlAttribute(name = "useScenarios")
  private boolean useScenarios;

  @XmlAttribute(name = "switchUCAsPerFactorToFactorsPerUCA")
  private boolean switchUCAsPerFactorToFactorsPerUCA;

  @XmlElementWrapper(name = "causalSafetyConstraints")
  @XmlElement(name = "causalSafetyConstraint")
  private NumberedArrayList<SubMeasurementSafetyConstraint> causalSafetyConstraints;

  @XmlElementWrapper(name = "causalFactors")
  @XmlElement(name = "causalFactor")
  private NumberedArrayList<SubMeasurement> causalFactors;
  private Map<String, List<String>> typeCount;  
//      new HashMap<String, Map<String, String>>();
  private LinkController linkController;

  @XmlElementWrapper(name = "componentsList")
  @XmlElement(name = "component")
  private List<SubMeasurementCSComponent> componentsList;
  private boolean addedCF = false;
  @Override
  public boolean isAddedCF() {
    return addedCF;
  }
  @Override
  public void setAddedCF(boolean addedCF) {
    this.addedCF = addedCF;
  }

  /**
   * Constructor of the causal factor controller
   * 
   * @author Fabian Toth
   * 
   */
  public SubMeasurementController() {
    this(false);
  }

  public SubMeasurementController(boolean testable) {
    this.causalSafetyConstraints = new NumberedArrayList<>();
    this.causalFactors = new NumberedArrayList<>();
    this.typeCount = new HashMap<String,String>();

    if (!testable) {
      this.setUseScenarios(ASTPADefaultConfig.getInstance().USE_CAUSAL_SCENARIO_ANALYSIS);
      this.setAnalyseFactorsPerUCA(ASTPADefaultConfig.getInstance().USE_FACTORS_PER_UCA);
    } else {
      this.setUseScenarios(false);
      this.setAnalyseFactorsPerUCA(true);
    }
  }

  @Override
  public UUID addSubMeasurement() {
    return this.addSubMeasurement(new SubMeasurement(""));
  }
  @Override
  public UUID addSubMeasurement(SubMeasurement factor) {
    String factorKey = factor.getSeverityLikelihood();
    String factorType = factor.getType();
    if(!this.typeCount.containsKey(factorKey)) {
      List<String> typeList = new ArrayList<String>();
      typeList.add(factorType);
      this.typeCount.put(factorKey, typeList);
      }
    else{
      List<String> typeList = this.typeCount.get(factorKey);
      if(!typeList.contains(factorType)) {
        typeList.add(factorType);
      }
    }
      
   
//      this.typeCount.put(factor.getSeverityLikelihood(), factor.getType());
//      }
//      else if(this.typeCount.get(factor.getSeverityLikelihood()) != factor.getType()) {
//        
    
    if (this.causalFactors.add(factor)) {
      setChanged();
      notifyObservers(new UndoAddSubMeasurement(this, factor, linkController));
      return factor.getId();
    }
    return null;
  }

  public ITableModel getSubMeasurement(UUID causalFactorId) {
    Optional<SubMeasurement> first = this.causalFactors.stream().filter((factor) -> factor.getId().equals(causalFactorId))
        .findFirst();
    if(first.isPresent()) {
      return first.get();
    } else {
      return BadReferenceModel.getBadReference();
    }
  }

  public List<ITableModel> getSubMeasurement() {
    return new ArrayList<>(causalFactors);
  }

//  public List<ATableModel> getModCausalFactors() {
//    ArrayList<ATableModel> result = new ArrayList<ATableModel>();
//    for(ITableModel CF : getCausalFactors()) {
//      result.add((ATableModel) CF);
//    }
//    return result;
////    return new ArrayList<>(causalFactors);
//  }
  
  @Override
  public boolean setSubMeasurementText(UUID causalFactorId, String causalFactorText) {
    SubMeasurement causalFactor = this.causalFactors.get(causalFactorId);
    if (causalFactor != null) {

      String oldText = causalFactor.getText();
      if (causalFactor.setText(causalFactorText)) {
        UndoTextChange textChange = new UndoTextChange(oldText, causalFactorText,
            ObserverValue.CAUSAL_FACTOR);
        textChange.setConsumer((text) -> setSubMeasurementText(causalFactorId, text));
        setChanged();
        notifyObservers(textChange);
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean removeSubMeasurement(UUID causalFactor) {
    Optional<SubMeasurement> removeOptional = this.causalFactors.stream()
        .filter(factor -> factor.getId().equals(causalFactor)).findFirst();
    if (removeOptional.isPresent() && this.causalFactors.remove(removeOptional.get())) {
      setChanged();
      notifyObservers(new UndoRemoveSubMeasurement(this, removeOptional.get(), this.linkController));
      return true;
    }
    return false;
  }

  @Override
  public boolean removeSafetyConstraint(UUID constraintId) {
    Optional<SubMeasurementSafetyConstraint> removeOptional = this.causalSafetyConstraints.stream()
        .filter(factor -> factor.getId().equals(constraintId)).findFirst();
    if (removeOptional.isPresent() && this.causalSafetyConstraints.remove(removeOptional.get())) {
      setChanged();
      notifyObservers(ObserverValue.CAUSAL_FACTOR);
      return true;
    }
    return false;
  }

  public ICausalComponent getCausalComponent(IRectangleComponent csComp) {
    ICausalComponent component = null;
    if (csComp != null && validateCausalComponent(csComp.getComponentType())) {
      component = csComp;
    }
    return component;
  }

  @Override
  public boolean validateCausalComponent(ComponentType type) {
    switch (type) {
    case ACTUATOR:
    case CONTROLLED_PROCESS:
    case CONTROLLER:
    case UNDEFINED:
    case SENSOR:
      return true;
    default:
      return false;

    }
  }

  @Override
  public void prepareForExport(IHazAccController hazAccController, IRectangleComponent root,
      IExtendedDataController extendedDataController, IControlActionController caController,
      LinkController linkController, ISDSController sdsController) {
    this.componentsList = new ArrayList<>();
    for (IRectangleComponent child : root.getChildren()) {
      if (linkController.isLinked(LinkingType.UcaCfLink_Component_LINK, child.getId())) {
        SubMeasurementCSComponent comp = new SubMeasurementCSComponent();
        comp.prepareForExport(this, hazAccController, child, extendedDataController, caController,
            linkController, sdsController);
        this.componentsList.add(comp);
      }
    }
  }

  @Override
  public SortedMap<ITableModel, List<Link>> getCausalFactorBasedMap(ICausalComponent component,
      LinkController linkController) {
    TreeMap<ITableModel, List<Link>> ucaCfLink_Component_ToCFmap = new TreeMap<>();
    for (Link link : linkController.getRawLinksFor(LinkingType.UcaCfLink_Component_LINK,
        component.getId())) {
      Link ucaCFLink = linkController.getLinkObjectFor(LinkingType.UCA_CausalFactor_LINK,
          link.getLinkA());
      try {
        ITableModel factor = getSubMeasurement(ucaCFLink.getLinkB());
        if (!ucaCfLink_Component_ToCFmap.containsKey(factor)) {
          ucaCfLink_Component_ToCFmap.put(factor, new ArrayList<>());
        }
        ucaCfLink_Component_ToCFmap.get(factor).add(link);
      } catch (NullPointerException exc) {

      }
    }
    return ucaCfLink_Component_ToCFmap;
  }

  @Override
  public SortedMap<IUnsafeControlAction, List<Link>> getUCABasedMap(ICausalComponent component,
      LinkController linkController, IControlActionController caController) {
    TreeMap<IUnsafeControlAction, List<Link>> ucaCfLink_Component_To_UCA_map = new TreeMap<>();
    for (Link link : linkController.getRawLinksFor(LinkingType.UcaCfLink_Component_LINK,
        component.getId())) {
      Link ucaCFLink = linkController.getLinkObjectFor(LinkingType.UCA_CausalFactor_LINK,
          link.getLinkA());
      try {
        IUnsafeControlAction uca = caController.getUnsafeControlAction(ucaCFLink.getLinkA());
        if (!ucaCfLink_Component_To_UCA_map.containsKey(uca)) {
          ucaCfLink_Component_To_UCA_map.put(uca, new ArrayList<>());
        }
        ucaCfLink_Component_To_UCA_map.get(uca).add(link);
      } catch (NullPointerException exc) {

      }
    }
    return ucaCfLink_Component_To_UCA_map;
  }

  @Override
  public SortedMap<IUnsafeControlAction, List<Link>> getHazardBasedMap(ITableModel hazModel,
      LinkController linkController, IControlActionController caController) {
    SortedMap<IUnsafeControlAction, List<Link>> ucaCfLink_Component_ToCFmap = new TreeMap<>();
    linkController.getRawLinksFor(LinkingType.CausalEntryLink_HAZ_LINK, hazModel.getId())
        .forEach((link) -> {
          Link entryLink = linkController.getLinkObjectFor(LinkingType.UcaCfLink_Component_LINK,
              link.getLinkA());
          Link ucaCfLink = linkController.getLinkObjectFor(LinkingType.UCA_CausalFactor_LINK,
              entryLink.getLinkA());
          Optional<Link> sc2Option = linkController
              .getRawLinksFor(LinkingType.CausalEntryLink_SC2_LINK, entryLink.getId()).stream()
              .findFirst();
          IUnsafeControlAction uca = caController.getUnsafeControlAction(ucaCfLink.getLinkA());
          if (sc2Option.isPresent() && uca != null) {
            ucaCfLink_Component_ToCFmap.put(uca, new ArrayList<>());
            ucaCfLink_Component_ToCFmap.get(uca).add(sc2Option.get());
          } else if (uca != null) {
            ucaCfLink_Component_ToCFmap.putIfAbsent(uca, new ArrayList<>());
            ucaCfLink_Component_ToCFmap.get(uca).add(link);
          }
        });
    return ucaCfLink_Component_ToCFmap;
  }

  @Override
  public void prepareForSave(IHazAccController hazAccController, List<Component> list,
      List<AbstractLTLProvider> allRefinedRules,
      List<ICorrespondingUnsafeControlAction> allUnsafeControlActions,
      LinkController linkController) {
    ArrayList<UUID> removeList = new ArrayList<>();
    if (this.causalComponents != null) {
      removeList.addAll(causalComponents.keySet());
      this.causalComponents.entrySet().forEach((comp) -> {
        this.causalFactors
            .addAll(comp.getValue().prepareForSave(comp.getKey(), hazAccController, allRefinedRules,
                allUnsafeControlActions, getCausalSafetyConstraints(), linkController));
      });
    }
    causalFactors.forEach(factor -> factor.prepareForSave());
    this.causalComponents = null;
    this.componentsList = null;
  }

  @Override
  public UUID addSafetyConstraint(String text) {
    SubMeasurementSafetyConstraint constraint = new SubMeasurementSafetyConstraint(text);
    if (this.causalSafetyConstraints.add(constraint)) {
      return constraint.getId();
    }
    return null;
  }

  UUID addSafetyConstraint(ITableModel model) {
    SubMeasurementSafetyConstraint constraint = new SubMeasurementSafetyConstraint(model);
    if (this.causalSafetyConstraints.add(constraint)) {
      return constraint.getId();
    }
    return null;
  }

  @Override
  public List<ITableModel> getSafetyConstraints() {
    List<ITableModel> list = new ArrayList<>();
    if (causalSafetyConstraints != null) {
      list.addAll(causalSafetyConstraints);
    }
    return list;
  }

  public List<SubMeasurementCSComponent> getCausalComponents() {
    if (this.componentsList == null) {
      return new ArrayList<>();
    }
    Collections.sort(componentsList);
    return new ArrayList<>(componentsList);
  }

  @Override
  public ITableModel getSafetyConstraint(UUID id) {
    SubMeasurementSafetyConstraint scObject = getCausalSafetyConstraints().stream().filter((constraint) -> {
      return constraint.getId().equals(id);
    }).findFirst().orElse(null);
    if (scObject == null) {
      return BadReferenceModel.getBadReference();
    }
    return scObject;
  }

  @Override
  public String getConstraintTextFor(UUID id) {
    return getSafetyConstraint(id).getTitle();
  }

  @Override
  public boolean setSafetyConstraintText(UUID constraintId, String newText) {
    return setModelTitle(getSafetyConstraint(constraintId), newText, ObserverValue.CAUSAL_FACTOR);
  }

  @Override
  public boolean setSafetyConstraintDescription(UUID constraintId, String newText) {
    return setModelDescription(getSafetyConstraint(constraintId), newText,
        ObserverValue.CAUSAL_FACTOR);
  }

  private NumberedArrayList<SubMeasurementSafetyConstraint> getCausalSafetyConstraints() {
    if (causalSafetyConstraints == null) {
      this.causalSafetyConstraints = new NumberedArrayList<>();
    }
    return causalSafetyConstraints;
  }

  @Override
  public boolean isUseScenarios() {
    return useScenarios;
  }

  @Override
  public boolean analyseFactorsPerUCA() {
    return switchUCAsPerFactorToFactorsPerUCA;
  }

  @Override
  public void setAnalyseFactorsPerUCA(boolean analyseFactorsPerUCA) {
    this.switchUCAsPerFactorToFactorsPerUCA = analyseFactorsPerUCA;
  }

  @Override
  public void setUseScenarios(boolean useScenarios) {
    this.useScenarios = useScenarios;
  }

  public void setLinkController(LinkController linkController) {
    this.linkController = linkController;
  }

  void setCausalFactors(ArrayList<SubMeasurement> causalFactors) {
    this.causalFactors.clear();
    this.causalFactors.addAll(causalFactors);
  }

  void setCausalSafetyConstraints(ArrayList<SubMeasurementSafetyConstraint> causalSafetyConstraints) {
    this.causalSafetyConstraints.clear();
    this.causalSafetyConstraints.addAll(causalSafetyConstraints);
  }

  public void syncContent(SubMeasurementController controller) {
    this.useScenarios = controller.useScenarios;
    this.switchUCAsPerFactorToFactorsPerUCA = controller.switchUCAsPerFactorToFactorsPerUCA;
    for (SubMeasurement other : controller.causalFactors) {
      ITableModel own = getSubMeasurement(other.getId());
      if (own instanceof BadReferenceModel) {
        addSubMeasurement(other);
      } else {
        setSubMeasurementText(other.getId(), other.getText());
      }
    }
    for (SubMeasurementSafetyConstraint otherReq : controller.causalSafetyConstraints) {
      ITableModel ownReq = getSafetyConstraint(otherReq.getId());
      if (ownReq instanceof BadReferenceModel) {
        addSafetyConstraint(otherReq);
      } else {
        setSafetyConstraintText(otherReq.getId(), otherReq.getTitle());
        setSafetyConstraintDescription(otherReq.getId(), otherReq.getDescription());
      }
    }
  }
}
