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

package xstampp.astpa.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;
import org.junit.Assert;
import org.osgi.framework.Bundle;

import messages.Messages;
import xstampp.astpa.Activator;
import xstampp.astpa.model.causalfactor.CausalFactorController;
import xstampp.astpa.model.causalfactor.ICausalController;
import xstampp.astpa.model.causalfactor.interfaces.ICausalComponent;
import xstampp.astpa.model.controlaction.ControlAction;
import xstampp.astpa.model.controlaction.ControlActionController;
import xstampp.astpa.model.controlaction.IControlActionController;
import xstampp.astpa.model.controlaction.NotProvidedValuesCombi;
import xstampp.astpa.model.controlaction.ProvidedValuesCombi;
import xstampp.astpa.model.controlaction.UCAHazLink;
import xstampp.astpa.model.controlaction.interfaces.IControlAction;
import xstampp.astpa.model.controlaction.interfaces.IUnsafeControlAction;
import xstampp.astpa.model.controlaction.interfaces.UnsafeControlActionType;
import xstampp.astpa.model.controlaction.safetyconstraint.ICorrespondingUnsafeControlAction;
import xstampp.astpa.model.controlstructure.ControlStructureController;
import xstampp.astpa.model.controlstructure.components.Anchor;
import xstampp.astpa.model.controlstructure.components.Component;
import xstampp.astpa.model.controlstructure.components.ComponentType;
import xstampp.astpa.model.controlstructure.components.ConnectionType;
import xstampp.astpa.model.controlstructure.interfaces.IConnection;
import xstampp.astpa.model.controlstructure.interfaces.IRectangleComponent;
import xstampp.astpa.model.export.ExportInformation;
import xstampp.astpa.model.extendedData.ExtendedDataController;
import xstampp.astpa.model.extendedData.interfaces.IExtendedDataController;
import xstampp.astpa.model.hazacc.Accident;
import xstampp.astpa.model.hazacc.HazAccController;
import xstampp.astpa.model.hazacc.Hazard;
import xstampp.astpa.model.hazacc.IHazAccController;
import xstampp.astpa.model.interfaces.IAccidentViewDataModel;
import xstampp.astpa.model.interfaces.ICausalFactorDataModel;
import xstampp.astpa.model.interfaces.IControlActionViewDataModel;
import xstampp.astpa.model.interfaces.IControlStructureEditorDataModel;
import xstampp.astpa.model.interfaces.ICorrespondingSafetyConstraintDataModel;
import xstampp.astpa.model.interfaces.IDesignRequirementViewDataModel;
import xstampp.astpa.model.interfaces.IExtendedDataModel;
import xstampp.astpa.model.interfaces.IHazardViewDataModel;
import xstampp.astpa.model.interfaces.ILinkingViewDataModel;
import xstampp.astpa.model.interfaces.INavigationViewDataModel;
import xstampp.astpa.model.interfaces.ISTPADataModel;
import xstampp.astpa.model.interfaces.ISafetyConstraintViewDataModel;
import xstampp.astpa.model.interfaces.IStatusLineDataModel;
import xstampp.astpa.model.interfaces.ISystemDescriptionViewDataModel;
import xstampp.astpa.model.interfaces.ISystemGoalViewDataModel;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.interfaces.IUnsafeControlActionDataModel;
import xstampp.astpa.model.interfaces.Severity;
import xstampp.astpa.model.linking.Link;
import xstampp.astpa.model.linking.LinkController;
import xstampp.astpa.model.linking.LinkingType;
import xstampp.astpa.model.projectdata.ProjectDataController;
import xstampp.astpa.model.sds.ISDSController;
import xstampp.astpa.model.sds.SDSController;
import xstampp.astpa.model.sds.SystemGoal;
import xstampp.astpa.model.service.UndoTextChange;
import xstampp.astpa.usermanagement.AstpaCollaborationSystem;
import xstampp.astpa.util.jobs.SaveJob;
import xstampp.model.AbstractDataModel;
import xstampp.model.AbstractLTLProvider;
import xstampp.model.IDataModel;
import xstampp.model.IEntryFilter;
import xstampp.model.ISafetyDataModel;
import xstampp.model.IValueCombie;
import xstampp.model.ObserverValue;
import xstampp.ui.common.ProjectManager;
import xstampp.usermanagement.api.AccessRights;
import xstampp.usermanagement.api.EmptyUserSystem;
import xstampp.usermanagement.api.ICollaborationSystem;
import xstampp.usermanagement.api.IUserProject;
import xstampp.usermanagement.api.IUserSystem;
import xstampp.usermanagement.api.UserManagement;
import xstampp.util.IUndoCallback;
import xstampp.util.service.UndoRedoService;

/**
 * Data Model controller class
 * 
 * @author Fabian Toth, Lukas Balzer
 * @since 2.0
 * 
 */
@XmlRootElement(namespace = "astpa.model")
@XmlAccessorType(XmlAccessType.NONE)
public class DataModelController extends AbstractDataModel
    implements ISafetyDataModel, ILinkingViewDataModel, INavigationViewDataModel,
    ISystemDescriptionViewDataModel, IAccidentViewDataModel, IHazardViewDataModel,
    IStatusLineDataModel, IDesignRequirementViewDataModel, ISafetyConstraintViewDataModel,
    ISystemGoalViewDataModel, IControlActionViewDataModel, IControlStructureEditorDataModel,
    IUnsafeControlActionDataModel, ICausalFactorDataModel, ICorrespondingSafetyConstraintDataModel,
    IExtendedDataModel, IUserProject, Observer, ISTPADataModel {

  private static final String HAZ = "haz";
  private static final String HAZX = "hazx";

  private String astpaVersion;

  @XmlAttribute(name = "userSystemId")
  private UUID userSystemId;

  @XmlAttribute(name = "userSystemName")
  private String userSystemName;

  @XmlAttribute(name = "exclusiveUserId")
  private UUID exclusiveUserId;

  @XmlAttribute(name = "projectId")
  private UUID projectId;

  @XmlElement(name = "exportinformation")
  private ExportInformation exportInformation;

  @XmlElement(name = "projectdata")
  private ProjectDataController projectDataManager;

  @XmlElement(name = "hazacc")
  private HazAccController hazAccController;

  @XmlElement(name = "sds")
  private SDSController sdsController;

  @XmlElement(name = "controlstructure")
  private ControlStructureController controlStructureController;

  @XmlElement(name = "ignoreLTLValue")
  private Component ignoreLtlValue;

  @XmlElement(name = "cac")
  private ControlActionController controlActionController;

  @XmlElement(name = "causalfactor")
  private CausalFactorController causalFactorController;

  @XmlElement(name = "extendedData")
  private ExtendedDataController extendedDataController;

  @XmlElement
  private LinkController linkController;

  private String projectExtension;
  private boolean refreshLock;

  /**
   * When this boolean is true than all {@link IUndoCallback}s that are pushed to the
   * {@link UndoRedoService} are recorded and added as one {@link IUndoCallback} when pushRecord is
   * set to false.
   */
  private boolean recordCallbacks;
  private List<ObserverValue> blockedUpdates;
  private IUserSystem userSystem;

  public void setVersion(String astpaVersion) {
    this.astpaVersion = astpaVersion;
  }

  @XmlAttribute(name = "version")
  public String getVersion() {
    return this.astpaVersion;
  }

  /**
   * Constructor of the DataModel Controller
   * 
   * @author Fabian Toth
   * 
   */
  public DataModelController() {
    this(false);
  }

  public DataModelController(boolean testable) {
    super(testable);
    this.linkController = new LinkController();
    this.projectDataManager = new ProjectDataController();
    this.controlStructureController = new ControlStructureController(testable);
    this.causalFactorController = new CausalFactorController(testable);
    this.extendedDataController = new ExtendedDataController();
    getIgnoreLTLValue();
    refreshLock = false;
    this.recordCallbacks = false;
    this.userSystem = new EmptyUserSystem();
    if (!testable) {
      Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
      if (bundle != null) {
        Dictionary<?, ?> dictionary = bundle.getHeaders();
        String versionWithQualifier = (String) dictionary.get(Messages.BundleVersion);
        this.setVersion(versionWithQualifier.substring(0, versionWithQualifier.lastIndexOf('.')));
      }
    }
  }

  @Override
  public void initializeProject() {
    this.controlStructureController.initializeCSS();
  }

  @Override
  public void initializeProject(IDataModel original) {
    initializeProject();
    if (original instanceof DataModelController) {
      AstpaCollaborationSystem system = new AstpaCollaborationSystem(this);
      system.syncData(((DataModelController) original));
      this.userSystemId = ((DataModelController) original).userSystemId;
      this.userSystemName = ((DataModelController) original).userSystemName;
    }

  }

  @Override
  public boolean prepareForExport() {

    this.exportInformation = null;
    this.getHazAccController().prepareForExport(getLinkController(), this.getSdsController());
    getSdsController().prepareForExport(getLinkController(), getHazAccController(),
        getControlActionController(), getCausalFactorController());
    this.extendedDataController.prepareForExport(getControlActionController(), getLinkController());
    this.getControlActionController().prepareForExport(this, ignoreLtlValue.getText());
    this.causalFactorController.prepareForExport(getHazAccController(), getRoot(),
        getExtendedDataController(), getControlActionController(), getLinkController(),
        getSdsController());
    this.projectDataManager.prepareForExport();
    this.exportInformation = new ExportInformation();
    ProjectManager.getLOGGER().debug("Project: " + getProjectName() + " prepared for export");
    return true;
  }

  @Override
  public void prepareForSave() {
    lockUpdate();
    this.extendedDataController.prepareForSave(getControlActionController(), getLinkController());
    if (!this.getControlActionController().prepareForSave(getLinkController(), hazAccController)) {
      this.controlActionController = null;
    }

    this.causalFactorController.prepareForSave(this.getHazAccController(),
        controlStructureController.getInternalComponents(),
        this.extendedDataController.getAllScenarios(true, true, true), getAllUnsafeControlActions(),
        getLinkController());
    if (!this.getHazAccController().prepareForSave(linkController)) {
      this.hazAccController = null;
    }
    this.projectDataManager.prepareForSave();
    if (!this.getSdsController().prepareForSave()) {
      this.sdsController = null;
    }
    this.exportInformation = null;
    getLinkController().prepareForSave();
    releaseLockAndUpdate();
    ProjectManager.getLOGGER().debug("Project: " + getProjectName() + " prepared for save");
  }

  @Override
  public UUID getProjectId() {
    if (this.projectId == null) {
      this.projectId = UUID.randomUUID();
    }
    return this.projectId;
  }

  @Override
  public UUID addAccident(String title, String description) {
    if (!getUserSystem().checkAccess(AccessRights.CREATE)
        || ((title == null) || (description == null))) {
      return null;
    }
    return this.getHazAccController().addAccident(title, description);
  }

  @Override
  public void addCANotProvidedVariable(UUID caID, UUID notProvidedVariable) {

    if (this.controlStructureController.getComponent(notProvidedVariable) != null) {
      this.getControlActionController().addNotProvidedVariable(caID, notProvidedVariable);
      setUnsavedAndChanged(ObserverValue.Extended_DATA);
    } else if (!isTestable()) {
      ProjectManager.getLOGGER().debug("given provided id is not related to a valid component");
    }
  }

  @Override
  public void addCAProvidedVariable(UUID caID, UUID providedVariable) {

    if (this.controlStructureController.getComponent(providedVariable) != null) {
      this.getControlActionController().addProvidedVariable(caID, providedVariable);
      setUnsavedAndChanged(ObserverValue.Extended_DATA);
    } else if (!isTestable()) {
      ProjectManager.getLOGGER().debug("given provided id is not related to a valid component");
    }
  }

  @Override
  public UUID addCausalFactor() {
    return this.getCausalFactorController().addCausalFactor();
  }

  @Override
  public UUID addCausalFactor(UUID componentId) {
    return addCausalFactor(componentId, null);
  }

  @Override
  public UUID addCausalFactor(UUID componentId, UUID ucaID) {
    IRectangleComponent component = getControlStructureController().getComponent(componentId);
    ICausalComponent causalComponent = getCausalFactorController().getCausalComponent(component);
    UUID factorId = null;
    if (causalComponent != null) {
      lockUpdate();
      factorId = addCausalFactor();
      UUID linkId = getLinkController().addLink(LinkingType.UCA_CausalFactor_LINK, ucaID, factorId);
      UUID causalEntryLink = getLinkController().addLink(LinkingType.UcaCfLink_Component_LINK,
          linkId, componentId);
      getLinkController().addLink(LinkingType.CausalEntryLink_SC2_LINK, causalEntryLink, null);
      releaseLockAndUpdate(
          new ObserverValue[] { ObserverValue.CAUSAL_FACTOR, ObserverValue.LINKING });
    }
    return factorId;

  }

  @Override
  public UUID addComponent(UUID parentId, Rectangle layout, String text, ComponentType type,
      Integer index) {
    return addComponent(null, parentId, layout, text, type, index);
  }

  @Override
  public UUID addComponent(UUID controlActionId, UUID parentId, Rectangle layout, String text,
      ComponentType type, Integer index) {
    if ((parentId == null) || (layout == null) || (text == null) || (type == null)) {
      return null;
    }
    if (!(this.getComponent(parentId) instanceof Component)) {
      return null;
    }
    UUID result;
    if (controlActionId == null && type.equals(ComponentType.CONTROLACTION)) {
      controlActionId = this.getControlActionController().addControlAction("", "");
      getControlActionController().setControlActionTitle(controlActionId, Messages.ControlAction
          + " " + getControlActionController().getControlAction(controlActionId).getNumber());
    }
    if (type.equals(ComponentType.CONTROLACTION)) {
      String caTitle = getControlAction(controlActionId).getText();
      result = this.controlStructureController.addComponent(controlActionId, parentId, layout,
          caTitle, type, index);
      this.getControlActionController().setComponentLink(result, controlActionId);
    } else {
      result = this.controlStructureController.addComponent(parentId, layout, text, type, index);
    }
    if (result != null) {
      this.setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
    }
    return result;
  }

  @Override
  public UUID addConnection(Anchor sourceAnchor, Anchor targetAnchor,
      ConnectionType connectionType) {
    if ((sourceAnchor == null) || (targetAnchor == null) || (connectionType == null)) {
      return null;
    }

    UUID result = this.controlStructureController.addConnection(sourceAnchor, targetAnchor,
        connectionType);
    if (result != null) {
      this.setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
    }
    return result;
  }

  @Override
  public UUID addDesignRequirement(String title, String description) {
    if ((title == null) || (description == null)) {
      return null;
    }
    return this.getSdsController().addDesignRequirement(title, description,
        ObserverValue.DESIGN_REQUIREMENT);
  }

  @Override
  public UUID addHazard(String title, String description) {
    if (!getUserSystem().checkAccess(AccessRights.CREATE) && (title == null)
        || (description == null)) {
      return null;
    }
    return this.getHazAccController().addHazard(title, description);
  }

  @Override
  public boolean addLink(UUID accidentId, UUID hazardId) {
    if ((accidentId == null) || (hazardId == null)) {
      return false;
    }
    if (!(this.getHazAccController().getHazard(hazardId) instanceof Hazard)) {
      return false;
    }
    if (!(this.getHazAccController().getAccident(accidentId) instanceof Accident)) {
      return false;
    }

    if (getLinkController().addLink(LinkingType.HAZ_ACC_LINK, accidentId, hazardId) != null) {
      return true;
    }
    return false;
  }

  @Override
  public UUID addSafetyConstraint(String title, String description) {
    if ((title == null) || (description == null)) {
      return null;
    }
    return this.getSdsController().addSafetyConstraint(title, description,
        getUserSystem().getCurrentUserId());
  }

  public UUID addSafetyConstraint(ITableModel model) {
    if (model == null) {
      return null;
    }
    return this.getSdsController().addSafetyConstraint(model);
  }

  @Override
  public boolean addStyleRange(StyleRange styleRange) {
    if (styleRange != null && this.projectDataManager.addStyleRange(styleRange)) {
      this.setUnsavedAndChanged(ObserverValue.PROJECT_DESCRIPTION);
      return true;
    }
    return false;
  }

  @Override
  public boolean putStyleRanges(StyleRange[] styleRanges) {
    return getProjectDataManager().putStyleRanges(styleRanges);
  }

  @Override
  public UUID addSystemGoal(String title, String description) {
    if ((title == null) || (description == null)) {
      return null;
    }
    return this.getSdsController().addSystemGoal(title, description);
  }

  @Override
  public boolean addUCAHazardLink(UUID unsafeControlActionId, UUID hazardId) {
    if ((unsafeControlActionId == null) || (hazardId == null)) {
      return false;
    }

    if (!(this.getHazAccController().getHazard(hazardId) instanceof Hazard)) {
      return false;
    }

    boolean ucaExists = false;
    for (ICorrespondingUnsafeControlAction uca : this.getControlActionController().getAllUnsafeControlActions()) {
      if (uca.getId().equals(unsafeControlActionId)) {
        ucaExists = true;
        break;
      }
    }

    if (!ucaExists) {
      return false;
    }

    if (this.getLinkController().addLink(LinkingType.UCA_HAZ_LINK, unsafeControlActionId,
        hazardId) != null) {
      return true;
    }
    return false;
  }

  @Override
  public UUID addUnsafeControlAction(UUID controlActionId, String description,
      UnsafeControlActionType unsafeControlActionType) {
    if ((controlActionId == null) || (description == null) || (unsafeControlActionType == null)) {
      return null;
    }

    UUID result = this.getControlActionController().addUnsafeControlAction(controlActionId,
        description, unsafeControlActionType);
    if (result != null) {
      this.setUnsavedAndChanged(ObserverValue.UNSAFE_CONTROL_ACTION);
    }
    return result;
  }

  public UUID addUnsafeControlAction(UUID controlActionId, String description,
      UnsafeControlActionType unsafeControlActionType, UUID ucaId) {
    if ((controlActionId == null) || (description == null) || (unsafeControlActionType == null)) {
      return null;
    }

    UUID result = this.getControlActionController().addUnsafeControlAction(controlActionId,
        description, unsafeControlActionType, ucaId);
    if (result != null) {
      this.setUnsavedAndChanged(ObserverValue.UNSAFE_CONTROL_ACTION);
    }
    return result;
  }

  @Override
  public boolean addUnsafeProcessVariable(UUID componentId, UUID variableID) {
    return this.controlStructureController.addUnsafeProcessVariable(componentId, variableID);
  }

  /**
   * adds the given values combination to the list of value combinations in which the system gets
   * into a hazardous state if the control action is not provided
   * 
   * @param caID
   *          the uuid object of the control action
   * @param valueWhenNotProvided
   *          the values combination
   * @return whether or not the operation was successful, null if the given uuid is no legal
   *         controlAction id
   */
  public boolean addValuesWhenNotProvided(UUID caID, NotProvidedValuesCombi valueWhenNotProvided) {
    if (this.getControlActionController().addValueWhenNotProvided(caID, valueWhenNotProvided)) {
      setUnsavedAndChanged(ObserverValue.Extended_DATA);
      return true;
    }
    return false;
  }

  /**
   * adds the given values combination to the list of value combinations in which the system gets
   * into a hazardous state if the control action is provided
   * 
   * @param caID
   *          the uuid object of the control action
   * @param valueWhenNotProvided
   *          the values combination
   * @return whether or not the operation was successful, null if the given uuid is no legal
   *         controlAction id
   */
  public boolean addValueWhenProvided(UUID caID, ProvidedValuesCombi valueWhenProvided) {
    if (this.getControlActionController().addValueWhenProvided(caID, valueWhenProvided)) {
      setUnsavedAndChanged(ObserverValue.Extended_DATA);
      return true;
    }
    return false;
  }

  @Override
  public boolean changeComponentLayout(UUID componentId, Rectangle layout, boolean step1) {
    if ((componentId == null) || (layout == null)) {
      return false;
    }

    if (this.controlStructureController.changeComponentLayout(componentId, layout, step1)) {
      this.setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
      return true;
    }
    return false;
  }

  @Override
  public boolean changeComponentText(UUID componentId, String text) {
    if ((componentId == null) || (text == null)) {
      return false;
    }

    if (this.controlStructureController.changeComponentText(componentId, text)) {
      this.setControlActionTitle(this.getComponent(componentId).getControlActionLink(), text);
      this.setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
      return true;
    }
    return false;
  }

  @Override
  public boolean changeConnectionSource(UUID connectionId, Anchor sourceAnchor, boolean withPm) {
    if ((connectionId == null) || (sourceAnchor == null)) {
      return false;
    }

    if (this.controlStructureController.changeConnectionSource(connectionId, sourceAnchor,
        withPm)) {
      this.setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
      return true;
    }
    return false;
  }

  @Override
  public boolean changeConnectionTarget(UUID connectionId, Anchor targetAnchor, boolean withPm) {
    if ((connectionId == null) || (targetAnchor == null)) {
      return false;
    }

    if (this.controlStructureController.changeConnectionTarget(connectionId, targetAnchor,
        withPm)) {
      this.setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
      return true;
    }
    return false;
  }

  @Override
  public boolean changeConnectionType(UUID connectionId, ConnectionType connectionType) {
    if ((connectionId == null) || (connectionType == null)) {
      return false;
    }

    if (this.controlStructureController.changeConnectionType(connectionId, connectionType)) {
      this.setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
      return true;
    }
    return false;
  }

  @Override
  public boolean deleteLink(UUID accidentId, UUID hazardId) {
    if ((accidentId == null) || (hazardId == null)) {
      return false;
    }

    if (this.getLinkController().deleteLink(LinkingType.HAZ_ACC_LINK, accidentId, hazardId)) {
      return false;
    }
    return true;
  }

  @Override
  public Job doSave(final File file, Logger log, boolean isUIcall) {
    String sLossOfData = String.format(
        "%s contains data that can only be stored in an extended STPA File (.hazx)\n"
            + "Do you want to change the file extension to store the extended data?/n"
            + "NOTE: the file is not longer compatible with older versions of XSTAMPP or A-STPA",
        file.getName());
    if (usesHAZXData() && file.getName().endsWith("haz") && MessageDialog
        .openQuestion(Display.getDefault().getActiveShell(), "Unexpected data", sLossOfData)) {
      UUID id = ProjectManager.getContainerInstance().getProjectID(this);
      ProjectManager.getContainerInstance().changeProjectExtension(id, "hazx");
      return null;
    }
    SaveJob job = new SaveJob(file, this);
    return job;
  }

  @Override
  public ITableModel getAccident(UUID accidentId) {
    if (accidentId == null) {
      return null;
    }
    ITableModel accident = this.getHazAccController().getAccident(accidentId);
    if (!(accident instanceof Accident)) {
      return null;
    }

    return accident;
  }

  @Override
  public List<ITableModel> getAllAccidents() {
    return this.getHazAccController().getAllAccidents();
  }

  @Override
  public List<IControlAction> getAllControlActions() {
    List<IControlAction> result = new ArrayList<>();
    for (IControlAction controlAction : this.getControlActionController().getAllControlActionsU()) {
      result.add(controlAction);
    }
    return result;
  }

  @Override
  public List<IControlAction> getAllControlActionsU() {
    return this.getControlActionController().getAllControlActionsU();
  }

  @Override
  public List<ITableModel> getAllDesignRequirements() {
    return this.getSdsController().getAllDesignRequirements();
  }

  public List<Link> getAllHazAccLinks() {
    return this.getLinkController().getLinksFor(LinkingType.HAZ_ACC_LINK);
  }

  @Override
  public List<ITableModel> getAllHazards() {
    return this.getHazAccController().getAllHazards();
  }

  @Override
  public List<ITableModel> getAllSafetyConstraints() {
    return this.getSdsController().getAllSafetyConstraints();
  }

  @Override
  public List<ITableModel> getAllSystemGoals() {
    return this.getSdsController().getAllSystemGoals();
  }

  @Override
  public List<UCAHazLink> getAllUCALinks() {
    List<UCAHazLink> links = new ArrayList<>();
    List<Link> linksFor = this.getLinkController().getLinksFor(LinkingType.UCA_HAZ_LINK);
    for (Link link : linksFor) {
      links.add(new UCAHazLink(link.getLinkA(), link.getLinkB()));
    }
    return links;
  }

  @Override
  public List<ICorrespondingUnsafeControlAction> getAllUnsafeControlActions() {
    return this.getControlActionController().getUCAList(new IEntryFilter<IUnsafeControlAction>() {
      @Override
      public boolean check(IUnsafeControlAction model) {
        return getLinkController().isLinked(LinkingType.UCA_HAZ_LINK, model.getId());
      }
    });
  }

  /**
   * {@link ControlAction#getNotProvidedVariables()}
   * 
   * @param caID
   *          the control action id which is used to look up the action
   * @return {@link ControlAction#getProvidedVariables()}
   */
  public List<UUID> getCANotProvidedVariables(UUID caID) {
    ArrayList<UUID> list = new ArrayList<>();
    for (UUID id : this.getControlActionController().getNotProvidedVariables(caID)) {
      if (getComponent(id) != null) {
        list.add(id);
      }
    }
    return list;
  }

  /**
   * {@link ControlAction#getProvidedVariables()}
   * 
   * @param caID
   *          the control action id which is used to look up the action
   * @return a copie of the provided variables list
   */
  public List<UUID> getCAProvidedVariables(UUID caID) {
    ArrayList<UUID> list = new ArrayList<>();
    for (UUID id : this.getControlActionController().getProvidedVariables(caID)) {
      if (getComponent(id) != null) {
        list.add(id);
      }
    }
    return list;
  }

  @Override
  public IRectangleComponent getComponent(UUID componentId) {
    if ((componentId == null)) {
      return null;
    }
    if (ignoreLtlValue != null && componentId.equals(ignoreLtlValue.getId())) {
      return ignoreLtlValue;
    }
    return this.controlStructureController.getComponent(componentId);
  }

  @Override
  public List<IRectangleComponent> getCausalComponents() {
    ArrayList<IRectangleComponent> causalComponents = new ArrayList<>();
    getControlStructureController().getRoot().getChildren().forEach((comp) -> {
      if (getCausalFactorController().validateCausalComponent(comp.getComponentType())) {
        causalComponents.add(comp);
      }
    });
    return causalComponents;
  }

  @Override
  public int getComponentTrashSize() {
    return this.controlStructureController.getComponentTrashSize();
  }

  @Override
  public IConnection getConnection(UUID connectionId) {
    if (connectionId == null) {
      return null;
    }

    return this.controlStructureController.getConnection(connectionId);
  }

  @Override
  public List<IConnection> getConnections() {
    return this.controlStructureController.getConnections();
  }

  @Override
  public int getConnectionTrashSize() {
    return this.controlStructureController.getConnectionTrashSize();
  }

  @Override
  public ITableModel getControlAction(UUID controlActionId) {
    if (controlActionId == null) {
      return null;
    }
    return this.getControlActionController().getControlAction(controlActionId);
  }

  public ITableModel getControlActionForUca(UUID ucaId) {
    return this.getControlActionController().getControlActionFor(ucaId);
  }

  @Override
  public IControlAction getControlActionU(UUID controlActionId) {
    if (controlActionId == null) {
      return null;
    }

    return this.getControlActionController().getControlActionU(controlActionId);
  }

  @Override
  public List<ITableModel> getCorrespondingSafetyConstraints() {
    return this.getControlActionController()
        .getCorrespondingSafetyConstraints(new IEntryFilter<IUnsafeControlAction>() {

          @Override
          public boolean check(IUnsafeControlAction model) {
            return !getLinksOfUCA(model.getId()).isEmpty();
          }
        });
  }

  @Override
  public ITableModel getDesignRequirement(UUID designRequirementId) {
    return this.getSdsController().getDesignRequirement(designRequirementId,
        ObserverValue.DESIGN_REQUIREMENT);
  }

  public ExportInformation getExportInfo() {
    return this.exportInformation;
  }

  @Override
  public String getFileExtension() {
    if (this.projectExtension == null) {
      return "haz";
    }
    return this.projectExtension;
  }

  @Override
  public ITableModel getHazard(UUID hazardId) {
    if (hazardId == null) {
      return null;
    }
    ITableModel hazard = this.getHazAccController().getHazard(hazardId);
    if (!(hazard instanceof Hazard)) {
      return null;
    }
    return hazard;
  }

  @Override
  public List<ITableModel> getHazards(List<UUID> ids) {
    List<ITableModel> hazards = new ArrayList<>();
    for (int i = 0; ids != null && i < ids.size(); i++) {
      ITableModel hazard = getHazard(ids.get(i));
      if (hazard != null) {
        hazards.add(hazard);
      }
    }
    Collections.sort(hazards);
    return hazards;
  }

  @Override
  public IRectangleComponent getIgnoreLTLValue() {
    if (this.ignoreLtlValue == null) {
      this.ignoreLtlValue = new Component("(don't care)", new Rectangle(),
          ComponentType.PROCESS_VALUE);
    }
    return this.ignoreLtlValue;
  }

  /**
   * @param caID
   *          the control action id which is used to look up the action
   * @return the valuesWhenNotProvided
   */
  public List<IValueCombie> getIValuesWhenCANotProvided(UUID caID) {
    ArrayList<IValueCombie> combies = new ArrayList<>();
    for (IValueCombie combie : this.getControlActionController().getValuesWhenNotProvided(caID)) {
      combies.add(combie);
    }
    return combies;
  }

  /**
   * @param caID
   *          the control action id which is used to look up the action
   * @return the IValueCombie objects when provided
   */
  public List<IValueCombie> getIvaluesWhenCAProvided(UUID caID) {
    ArrayList<IValueCombie> combies = new ArrayList<>();
    for (IValueCombie combie : this.getControlActionController().getValuesWhenProvided(caID)) {
      combies.add(combie);
    }
    return combies;
  }

  @Override
  public List<ITableModel> getLinkedAccidents(UUID hazardId) {
    if (hazardId == null) {
      return null;
    }
    List<UUID> links = this.getLinkController().getLinksFor(LinkingType.HAZ_ACC_LINK, hazardId);
    List<ITableModel> result = new ArrayList<>();
    for (UUID link : links) {
      ITableModel accident = this.getAccident(link);
      if (accident != null) {
        result.add(accident);
      }
    }
    return result;
  }

  @Override
  public List<ITableModel> getLinkedHazards(UUID accidentId) {
    if (accidentId == null) {
      return null;
    }
    List<UUID> links = this.getLinkController().getLinksFor(LinkingType.HAZ_ACC_LINK, accidentId);
    List<ITableModel> result = new ArrayList<>();
    for (UUID link : links) {
      result.add(this.getHazard(link));
    }
    return result;
  }

  @Override
  public List<ITableModel> getLinkedHazardsOfUCA(UUID unsafeControlActionId) {
    if (unsafeControlActionId == null) {
      return null;
    }
    List<UUID> links = getLinksOfUCA(unsafeControlActionId);
    List<ITableModel> result = new ArrayList<>();
    for (UUID link : links) {
      result.add(this.getHazard(link));
    }
    return result;
  }
  
  public List<String> getStringLinkedHazardsOfUCA(UUID unsafeControlActionId) {
    if (unsafeControlActionId == null) {
      return null;
    }
    List<UUID> links = getLinksOfUCA(unsafeControlActionId);
    List<String> result = new ArrayList<>();
    for (UUID link : links) {
      result.add(this.getHazard(link).getTitle());
    }
    return result;  
    }
  
  @Override
  public List<UUID> getLinksOfUCA(UUID unsafeControlActionId) {
    return this.getLinkController().getLinksFor(LinkingType.UCA_HAZ_LINK, unsafeControlActionId);
  }

  @Override
  public List<AbstractLTLProvider> getLTLPropertys() {
    return getExtendedDataController().getAllScenarios(true, false, false);
  }

  @Override
  public String getPluginID() {
    return Activator.PLUGIN_ID;
  }

  @XmlTransient
  @Override
  public String getProjectDescription() {
    return this.projectDataManager.getProjectDescription();
  }

  @Override
  public String getProjectName() {
    return this.projectDataManager.getProjectName();
  }

  public AbstractLTLProvider getRefinedScenario(UUID id) {
    return this.extendedDataController.getRefinedScenario(id);
  }

  @Override
  public Map<IRectangleComponent, Boolean> getRelatedProcessVariables(UUID componentId) {
    return this.controlStructureController.getRelatedProcessVariables(componentId);
  }

  @Override
  public ITableModel getSafetyConstraint(UUID safetyConstraintId) {
    if (safetyConstraintId == null) {
      return BadReferenceModel.getBadReference();
    }
    ITableModel constraint = this.getSdsController().getSafetyConstraint(safetyConstraintId);
    if (constraint instanceof BadReferenceModel) {
      constraint = this.getControlActionController()
          .getCorrespondingSafetyConstraint(safetyConstraintId);
    }
    if (constraint instanceof BadReferenceModel) {
      constraint = this.getCausalFactorController().getSafetyConstraint(safetyConstraintId);
    }

    return constraint;
  }

  @Override
  public List<StyleRange> getStyleRanges() {
    return this.projectDataManager.getStyleRanges();
  }

  @Override
  public StyleRange[] getStyleRangesAsArray() {
    return this.projectDataManager.getStyleRangesAsArray();
  }

  @Override
  public ITableModel getSystemGoal(UUID systemGoalId) {
    if (systemGoalId == null) {
      return null;
    }
    if (!(this.getSdsController().getSystemGoal(systemGoalId) instanceof SystemGoal)) {
      return null;
    }

    return this.getSdsController().getSystemGoal(systemGoalId);
  }

  @Override
  public List<ICorrespondingUnsafeControlAction> getUCAList(
      IEntryFilter<IUnsafeControlAction> filter) {
    return getControlActionController().getUCAList(filter);
  }

  @Override
  public int getUCANumber(UUID ucaID) {
    return this.getControlActionController().getUCANumber(ucaID);
  }

  @Override
  public IUserSystem createUserSystem() {
    if (getUserSystem() instanceof EmptyUserSystem) {
      this.userSystem = UserManagement.getInstance().createUserSystem(getProjectName());
      if (!(this.userSystem instanceof EmptyUserSystem)) {
        this.userSystemName = userSystem.getSystemName();
        this.userSystemId = userSystem.getSystemId();
        setUnsavedAndChanged(ObserverValue.UserSystem);
      }
    }
    return getUserSystem();
  }

  @Override
  public IUserSystem loadUserSystem() {
    if (getUserSystem() instanceof EmptyUserSystem) {
      this.userSystem = UserManagement.getInstance().loadExistingSystem();
      if (!(this.userSystem instanceof EmptyUserSystem)) {
        this.userSystemName = getProjectName();
        this.userSystemId = userSystem.getSystemId();
        setUnsavedAndChanged(ObserverValue.UserSystem);
      }
    }
    return getUserSystem();
  }

  @Override
  public IUserSystem getUserSystem() {
    if (userSystem instanceof EmptyUserSystem && userSystemId != null) {
      try {
        this.userSystem = UserManagement.getInstance().loadSystem(userSystemName, userSystemId,
            exclusiveUserId);
        this.userSystemName = !(userSystem instanceof EmptyUserSystem) ? userSystem.getSystemName()
            : this.userSystemName;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return this.userSystem;
  }

  @Override
  public UUID getExclusiveUserId() {
    return exclusiveUserId;
  }

  @Override
  public void setExclusiveUserId(UUID userId) {
    this.exclusiveUserId = userId;
  }

  @Override
  public Map<String, List<String>> getValuesTOVariables() {

    Map<String, List<String>> resultMap = new HashMap<>();
    List<String> valueNames;
    for (IRectangleComponent parentComponent : getRoot().getChildren()) {
      if (parentComponent.getComponentType().name().equals("CONTROLLER")) {

        // get the process models
        for (IRectangleComponent tempPM : parentComponent.getChildren()) {
          // get the variables
          for (IRectangleComponent tempPMV : tempPM.getChildren()) {
            valueNames = new ArrayList<>();
            // get the values and add the new object to the processmodel list
            for (IRectangleComponent tempPMVV : tempPMV.getChildren()) {
              valueNames.add(tempPMVV.getText());
            }
            resultMap.put(tempPMV.getText(), valueNames);
          }
        }
      }
    }
    return resultMap;
  }

  /**
   * @param caID
   *          the control action id which is used to look up the action
   * @return the NotProvidedValuesCombi objects when not provided
   */
  public List<NotProvidedValuesCombi> getValuesWhenCANotProvided(UUID caID) {
    return this.getControlActionController().getValuesWhenNotProvided(caID);
  }

  /**
   * @param caID
   *          the control action id which is used to look up the action
   * @return the ProvidedValuesCombi when provided
   */
  public List<ProvidedValuesCombi> getValuesWhenCAProvided(UUID caID) {
    return this.getControlActionController().getValuesWhenProvided(caID);
  }

  @Override
  public boolean isCASafetyCritical(UUID caID) {
    return this.getControlActionController().isSafetyCritical(caID);
  }

  @Override
  public boolean isCSComponentSafetyCritical(UUID componentId) {
    return this.controlStructureController.isSafetyCritical(componentId);
  }

  @Override
  public boolean linkControlAction(UUID caId, UUID componentId) {
    if (this.getControlActionController().setComponentLink(componentId, caId)) {
      this.setUnsavedAndChanged(ObserverValue.CONTROL_ACTION);
      return true;
    }
    return false;
  }

  @Override
  public void lockUpdate() {
    this.recordCallbacks = true;
    this.refreshLock = true;
    if (!isTestable()) {
      ProjectManager.getLOGGER().debug("set data update lock to prevent system lacks");
    }
  }

  public boolean moveEntry(boolean allWay, boolean moveUp, UUID id, ObserverValue value) {
    boolean result = false;
    switch (value) {
    case CONTROL_STRUCTURE: {
      result = this.controlStructureController.moveEntry(allWay, moveUp, id);
      break;
    }
    case HAZARD:
    case ACCIDENT:
      result = getHazAccController().moveEntry(moveUp, id, value);
      break;
    case DESIGN_REQUIREMENT:
    case SAFETY_CONSTRAINT:
    case SYSTEM_GOAL:
      result = getSdsController().moveEntry(moveUp, id, value);
      break;
    case CONTROL_ACTION:
      result = getControlActionController().moveEntry(moveUp, id, value);
      break;
    default:
      break;
    }
    if (result) {
      setUnsavedAndChanged(value);
    }
    return result;
  }

  protected void pushToUndo(IUndoCallback callback) {
    if (!isTestable()) {
      ISourceProviderService service = (ISourceProviderService) PlatformUI.getWorkbench()
          .getService(ISourceProviderService.class);
      UndoRedoService provider = (UndoRedoService) service
          .getSourceProvider(UndoRedoService.CAN_REDO);
      if (this.recordCallbacks) {
        this.recordCallbacks = false;
        provider.startRecord();
      }
      provider.push(callback);
    }
  }

  @Override
  public boolean recoverComponent(UUID parentId, UUID componentId) {
    if ((parentId == null) || (componentId == null)) {
      return false;
    }
    if (this.controlStructureController.recoverComponent(parentId, componentId)) {
      this.setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
      return true;
    }
    return false;
  }

  @Override
  public boolean recoverConnection(UUID connectionId) {
    if (connectionId == null) {
      return false;
    }
    if (this.controlStructureController.recoverConnection(connectionId)) {
      this.setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
      return true;
    }
    return false;
  }

  @Override
  public boolean recoverControlAction(UUID id) {
    return this.getControlActionController().recoverControlAction(id);

  }

  @Override
  public void releaseLockAndUpdate(ObserverValue[] values) {
    ISourceProviderService service = (ISourceProviderService) PlatformUI.getWorkbench()
        .getService(ISourceProviderService.class);
    UndoRedoService provider = (UndoRedoService) service
        .getSourceProvider(UndoRedoService.CAN_REDO);
    provider.pushRecord();
    this.refreshLock = false;
    this.recordCallbacks = false;
    blockedUpdates = blockedUpdates == null ? new ArrayList<>() : blockedUpdates;
    if (values != null) {
      for (int i = 0; i < values.length; i++) {
        if (!blockedUpdates.contains(values[i])) {
          setChanged();
          blockedUpdates.add(values[i]);
        }
      }
    }
    if (!isTestable()) {
      ProjectManager.getLOGGER().debug("released update lock");
    }
    if (hasChanged()) {
      setUnsavedAndChanged();
      for (int i = 0; i < blockedUpdates.size(); i++) {
        setUnsavedAndChanged(blockedUpdates.get(i));
      }
    }
    blockedUpdates.clear();
  }

  @Override
  public boolean removeAccident(UUID accidentId) {
    if (getUserSystem().checkAccess(accidentId, AccessRights.CREATE)) {
      return this.getHazAccController().removeAccident(accidentId);
    }
    return false;
  }

  @Override
  public boolean removeCANotProvidedVariable(UUID caID, UUID notProvidedVariable) {
    if (this.getControlActionController().removeNotProvidedVariable(caID, notProvidedVariable)) {
      setUnsavedAndChanged(ObserverValue.Extended_DATA);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeCAProvidedVariable(UUID caID, UUID providedVariable) {
    if (this.getControlActionController().removeProvidedVariable(caID, providedVariable)) {
      setUnsavedAndChanged(ObserverValue.Extended_DATA);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeComponent(UUID componentId) {
    IRectangleComponent comp = this.controlStructureController.getComponent(componentId);
    if ((comp == null)) {
      return false;
    }
    for (IRectangleComponent child : comp.getChildren()) {
      removeComponent(child.getId());
    }
    if (this.controlStructureController.removeComponent(componentId)) {
      this.setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
      return true;
    }

    return false;
  }

  @Override
  public boolean removeConnection(UUID connectionId) {
    if (connectionId == null) {
      return false;
    }

    if (this.controlStructureController.removeConnection(connectionId)) {
      this.setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeControlAction(UUID controlActionId) {
    if (controlActionId == null) {
      return false;
    }
    ITableModel caObject = this.getControlActionController().getControlAction(controlActionId);
    if (caObject == null || !(caObject instanceof ControlAction)) {
      return false;
    }
    boolean refreshCS = this.controlStructureController
        .removeComponent(((ControlAction) caObject).getComponentLink());

    if (this.getControlActionController().removeControlAction(controlActionId)) {

      this.setUnsavedAndChanged(ObserverValue.CONTROL_ACTION);
      if (refreshCS) {
        this.setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean removeDesignRequirement(UUID designRequirementId) {
    return removeDesignRequirement(designRequirementId, ObserverValue.DESIGN_REQUIREMENT);
  }

  @Override
  public boolean removeDesignRequirement(UUID designRequirementId, ObserverValue type) {
    if (designRequirementId == null) {
      return false;
    }
    return this.getSdsController().removeDesignRequirement(designRequirementId, type);
  }

  @Override
  public boolean removeHazard(UUID hazardId) {
    if (getUserSystem().checkAccess(hazardId, AccessRights.CREATE) && hazardId == null) {
      return false;
    }
    return this.getHazAccController().removeHazard(hazardId);
  }

  @Override
  public boolean removeSafetyConstraint(UUID safetyConstraintId) {
    if (safetyConstraintId == null) {
      return false;
    }
    return this.getSdsController().removeSafetyConstraint(safetyConstraintId);
  }

  @Override
  public boolean removeSystemGoal(UUID systemGoalId) {
    return this.getSdsController().removeSystemGoal(systemGoalId);
  }

  @Override
  public boolean removeUCAHazardLink(UUID unsafeControlActionId, UUID hazardId) {
    return this.getLinkController().deleteLink(LinkingType.UCA_HAZ_LINK, unsafeControlActionId,
        hazardId);
  }

  @Override
  public boolean removeAllUCAHazardLinks(UUID unsafeControlActionId) {
    boolean result = true;
    if (unsafeControlActionId == null) {
      return false;
    }
    this.getLinkController().deleteAllFor(LinkingType.UCA_HAZ_LINK, unsafeControlActionId);

    if (result) {
      this.setUnsavedAndChanged(ObserverValue.LINKING);
    }
    return result;
  }

  @Override
  public boolean removeUnsafeControlAction(UUID unsafeControlActionId) {
    if (unsafeControlActionId == null) {
      return false;
    }
    this.getControlActionController().removeAllLinks(unsafeControlActionId);
    final UUID id = unsafeControlActionId;
    List<ITableModel> constraints = getControlActionController()
        .getCorrespondingSafetyConstraints(new IEntryFilter<IUnsafeControlAction>() {

          @Override
          public boolean check(IUnsafeControlAction model) {
            return id.equals(model.getId());
          }
        });
    if (this.getControlActionController().removeUnsafeControlAction(unsafeControlActionId)) {
      getLinkController().deleteAllFor(LinkingType.UCA_HAZ_LINK, unsafeControlActionId);
      for (ITableModel model : constraints) {
        getLinkController().deleteAllFor(LinkingType.DR1_CSC_LINK, model.getId());
      }
      this.setUnsavedAndChanged(ObserverValue.UNSAFE_CONTROL_ACTION);
      this.setUnsavedAndChanged(ObserverValue.LINKING);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeUnsafeProcessVariable(UUID componentId, UUID variableID) {
    return this.controlStructureController.removeUnsafeProcessVariable(componentId, variableID);
  }

  /**
   * removes the given value combinations from the list of value combinations in which the system
   * gets into a hazardous state if the control action is not provided
   * 
   * @param caID
   *          the uuid object of the control action
   * @param combieId
   *          the uuid of the value combination
   * @return whether or not the operation was successful, null if the given uuid is no legal
   *         controlAction id
   */
  public boolean removeValueWhenNotProvided(UUID caID, UUID combieId) {
    if (this.getControlActionController().removeValueWhenNotProvided(caID, combieId)) {
      setUnsavedAndChanged(ObserverValue.Extended_DATA);
      return true;
    }
    return false;
  }

  /**
   * removes the given value combinations from the list of value combinations in which the system
   * gets into a hazardous state if the control action is provided
   * 
   * @param caID
   *          the uuid object of the control action
   * @param combieId
   *          the values combination id
   * @return whether or not the operation was successful, null if the given uuid is no legal
   *         controlAction id
   */
  public boolean removeValueWhenProvided(UUID caID, UUID combieId) {
    if (this.getControlActionController().removeValueWhenProvided(caID, combieId)) {
      setUnsavedAndChanged(ObserverValue.Extended_DATA);
      return true;
    }
    return false;
  }

  @Override
  public boolean setAccidentDescription(UUID accidentId, String description) {
    if (!getUserSystem().checkAccess(accidentId, AccessRights.WRITE)) {
      return false;
    }
    return getHazAccController().setAccidentDescription(accidentId, description);
  }

  @Override
  public boolean setAccidentTitle(UUID accidentId, String title) {
    if (!getUserSystem().checkAccess(accidentId, AccessRights.WRITE)) {
      return false;
    }
    return getHazAccController().setAccidentTitle(accidentId, title);
  }

  /**
   * @param caID
   *          the control action id which is used to look up the action
   * @param isSafetyCritical
   *          the isSafetyCritical to set
   */
  public boolean setCASafetyCritical(UUID caID, boolean isSafetyCritical) {
    if (this.getControlActionController().setSafetyCritical(caID, isSafetyCritical)) {
      setUnsavedAndChanged(ObserverValue.Extended_DATA);
      return true;
    }
    return false;
  }

  @Override
  public boolean setCausalFactorText(UUID causalFactorId, String causalFactorText) {
    if ((causalFactorId == null) || (causalFactorText == null)) {
      return false;
    }
    List<Component> components = this.controlStructureController.getInternalComponents();
    if (components == null) {
      return false;
    }
    return this.causalFactorController.setCausalFactorText(causalFactorId, causalFactorText);
  }

  @Override
  public boolean setControlActionDescription(UUID controlActionId, String description) {
    return getControlActionController().setControlActionDescription(controlActionId, description);
  }

  @Override
  public boolean setControlActionTitle(UUID controlActionId, String title) {
    boolean result = false;
    if (controlActionId != null) {
      result = getControlActionController().setControlActionTitle(controlActionId, title);
       ControlAction controlAction = (ControlAction) getControlActionController().getControlAction(controlActionId);
      // if the control action is linked with a component in the control structure than the title is
      // also synchronized with the components' text
      if (result
          && changeComponentText((controlAction).getComponentLink(), title)) {
        this.setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
        result = true;
      }
    }
    return result;
  }

  @Override
  public boolean setCorrespondingSafetyConstraint(UUID unsafeControlActionId,
      String safetyConstraintDescription) {
    return getControlActionController().setCorrespondingSafetyConstraint(unsafeControlActionId,
        safetyConstraintDescription);
  }

  @Override
  public void setCSComponentComment(UUID componentId, String comment) {
    this.controlStructureController.setComment(componentId, comment);
  }

  @Override
  public boolean setCSImagePath(String path) {
    if (this.exportInformation == null) {
      return false;
    }
    Image img = new Image(null, path);
    File imgFile = new File(path);
    this.exportInformation.setCsImageWidth(String.valueOf(img.getBounds().width));
    this.exportInformation.setCsImageHeight(String.valueOf(img.getBounds().height));
    return this.exportInformation.setCsImagePath(imgFile.toURI().toString());
  }

  @Override
  public boolean setCSPMImagePath(String path) {
    if (this.exportInformation == null) {
      return false;
    }
    Image img = new Image(null, path);
    File imgFile = new File(path);
    this.exportInformation.setCsPmImageWidth(String.valueOf(img.getBounds().width));
    this.exportInformation.setCsPmImageHeight(String.valueOf(img.getBounds().height));

    return this.exportInformation.setCspmImagePath(imgFile.toURI().toString());
  }

  @Override
  public boolean setDesignRequirementDescription(UUID designRequirementId, String description) {
    return getSdsController().setDesignRequirementDescription(ObserverValue.DESIGN_REQUIREMENT,
        designRequirementId, description);
  }

  @Override
  public boolean setDesignRequirementTitle(UUID designRequirementId, String title) {
    return getSdsController().setDesignRequirementTitle(ObserverValue.DESIGN_REQUIREMENT,
        designRequirementId, title);
  }

  @Override
  public boolean setHazardDescription(UUID hazardId, String description) {
    return getHazAccController().setHazardDescription(hazardId, description);
  }

  @Override
  public boolean setHazardTitle(UUID hazardId, String title) {
    return getHazAccController().setHazardTitle(hazardId, title);
  }

  public boolean setSeverity(Object entry, Severity severity) {
    Severity oldValue = null;
    if (entry instanceof EntryWithSeverity) {
      oldValue = ((EntryWithSeverity) entry).setSeverity(severity);
    }
    if (oldValue != null) {
      setUnsavedAndChanged(ObserverValue.SEVERITY);
      return true;
    }
    return false;
  }

  @Override
  public boolean setProjectDescription(String projectDescription) {
    if (projectDescription == null) {
      return false;
    }

    if (this.projectDataManager.setProjectDescription(projectDescription)) {
      this.setUnsavedAndChanged(ObserverValue.PROJECT_DESCRIPTION);
    }
    return true;
  }

  /**
   *
   * @author Lukas Balzer
   *
   * @param ext
   *          must be one of
   * @return whether the given extension is supported or not
   */
  public boolean setProjectExtension(String ext) {
    if (ext.equals(HAZ) || ext.equals(HAZX)) {
      this.projectExtension = ext;
      return true;
    }
    return false;
  }

  @Override
  public boolean setProjectName(String projectName) {
    if (projectName == null) {
      return false;
    }
    if (this.projectDataManager.setProjectName(projectName)) {
      this.setUnsavedAndChanged(ObserverValue.PROJECT_NAME);
    }
    return true;
  }

  @Override
  public void setRelativeOfComponent(UUID componentId, UUID relativeId) {
    this.controlStructureController.setRelativeOfComponent(componentId, relativeId);
    setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
    setUnsavedAndChanged(ObserverValue.CONTROL_ACTION);
  }

  @Override
  public UUID setRoot(Rectangle layout, String text) {
    if ((layout == null) || (text == null)) {
      return null;
    }

    UUID result = this.controlStructureController.setRoot(layout, text);
    if (result != null) {
      this.setUnsavedAndChanged(ObserverValue.PROJECT_TREE);
    }
    return result;
  }

  @Override
  public IRectangleComponent getRoot() {
    if (this.controlStructureController.getRoot() == null) {
      this.controlStructureController.setRoot(new Rectangle(), new String());
    }
    return this.controlStructureController.getRoot();
  }

  @Override
  public List<IRectangleComponent> getRoots() {
    return this.controlStructureController.getRoots();
  }

  @Override
  public void setActiveRoot(UUID rootId) {
    this.controlStructureController.setActiveRoot(rootId);
  }

  @Override
  public boolean setSafetyConstraintDescription(UUID safetyConstraintId, String description) {
    return getSdsController().setSafetyConstraintDescription(safetyConstraintId, description);
  }

  @Override
  public boolean setSafetyConstraintTitle(UUID safetyConstraintId, String title) {
    return getSdsController().setSafetyConstraintTitle(safetyConstraintId, title);
  }

  private boolean setModelText(ITableModel model, String input, ObserverValue value,
      boolean changeTitle) {
    Assert.assertNotNull(value);
    if (!(model instanceof BadReferenceModel)) {
      boolean changed;
      String oldText;
      String newText = input.replaceAll("\r", "");
      if (changeTitle) {
        changed = !newText.equals(((ATableModel) model).getTitle());
        oldText = ((ATableModel) model).setTitle(newText);
      } else {
        changed = !newText.equals(((ATableModel) model).getDescription());
        oldText = ((ATableModel) model).setDescription(newText);
      }
      if (changed) {
        UndoTextChange textChange = new UndoTextChange(oldText, newText, value);
        textChange.setConsumer((text) -> setModelText(model, text, value, changeTitle));
        setChanged();
        update(null,textChange);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean setModelTitle(ITableModel model, String newText, ObserverValue value) {
    return setModelText(model, newText, value, true);
  }

  @Override
  public boolean setModelDescription(ITableModel model, String newText, ObserverValue value) {
    return setModelText(model, newText, value, false);
  }

  @Override
  public boolean setSafetyCritical(UUID componentId, boolean isSafetyCritical) {
    boolean result = this.controlStructureController.setSafetyCritical(componentId,
        isSafetyCritical);
    if (result) {
      this.setUnsavedAndChanged(ObserverValue.CAUSAL_FACTOR);
    }
    return result;
  }

  @Override
  public boolean setSystemGoalDescription(UUID systemGoalId, String description) {
    return getSdsController().setSystemGoalDescription(systemGoalId, description);
  }

  @Override
  public boolean setSystemGoalTitle(UUID systemGoalId, String title) {
    return getSdsController().setSystemGoalTitle(systemGoalId, title);
  }

  @Override
  public boolean setUcaDescription(UUID unsafeControlActionId, String description) {
    return getControlActionController().setUcaDescription(unsafeControlActionId, description);
  }

  @Override
  public void setUseScenarios(boolean useScenarios) {
    if (isUseScenarios() != useScenarios) {
      this.causalFactorController.setUseScenarios(useScenarios);
      this.setUnsavedAndChanged(ObserverValue.CAUSAL_FACTOR);
    }

  }

  @Override
  public boolean isUseScenarios() {
    return this.getCausalFactorController().isUseScenarios();
  }

  @Override
  public void setValuesWhenCANotProvided(UUID caID,
      List<NotProvidedValuesCombi> valuesWhenNotProvided) {
    this.getControlActionController().setValuesWhenNotProvided(caID, valuesWhenNotProvided);
    setUnsavedAndChanged(ObserverValue.Extended_DATA);
  }

  @Override
  public void setValuesWhenCAProvided(UUID caID, List<ProvidedValuesCombi> valuesWhenProvided) {
    this.getControlActionController().setValuesWhenProvided(caID, valuesWhenProvided);
    setUnsavedAndChanged(ObserverValue.Extended_DATA);

  }

  @Override
  public boolean synchronizeLayouts() {
    if (this.getRoot() == null) {
      return false;
    }
    if (this.controlStructureController.sychronizeLayout()) {
      setUnsavedAndChanged(ObserverValue.CONTROL_STRUCTURE);
      return true;
    }
    return false;
  }

  @Override
  public void updateValue(ObserverValue value) {
    this.setChanged();
    if (!refreshLock) {
      if (!isTestable()) {
        ProjectManager.getLOGGER().debug("Trigger update for " + value.name()); //$NON-NLS-1$
      }
      switch (value) {
      case LINKING:
      case SEVERITY: {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

          @Override
          public void run() {
            if (getControlActionController() != null) {
              controlActionController.fetchUCASeverity(linkController, hazAccController);
            }
          }
        });
        break;
      }
      default:
        break;
      }
      super.updateValue(value);
    } else if (!value.equals(ObserverValue.UNSAVED_CHANGES)) {
      if (blockedUpdates == null) {
        blockedUpdates = new ArrayList<>();
        blockedUpdates.add(value);
      } else if (!blockedUpdates.contains(value)) {
        blockedUpdates.add(value);
      }
    }
  }

  public boolean usesHAZXData() {
    if (this.getControlActionController().usesHAZXData())
      return true;
    if (this.controlStructureController.usesHAZXData())
      return true;

    if (this.ignoreLtlValue != null)
      return true;
    return false;
  }

  @Override
  public boolean isUseSeverity() {
    return this.getHazAccController().isUseSeverity();
  }

  @Override
  public boolean setUseSeverity(boolean useSeverity) {
    if (this.getUserSystem().checkAccess(AccessRights.ADMIN)
        && this.getHazAccController().setUseSeverity(useSeverity)) {
      setUnsavedAndChanged(ObserverValue.HAZARD);
      return true;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getAdapter(Class<T> clazz) {
    if (clazz == ICollaborationSystem.class) {
      return (T) new AstpaCollaborationSystem(this);
    }
    if (clazz == ControlActionController.class) {
      return (T) this.controlActionController;
    }
    if (clazz == SDSController.class) {
      return (T) this.getSdsController();
    }
    if (clazz == HazAccController.class) {
      return (T) this.getHazAccController();
    }
    return null;
  }

  public LinkController getLinkController() {
    linkController.addObserver(this);
    return linkController;
  }

  @Override
  public ControlStructureController getControlStructureController() {
    if (this.controlStructureController == null) {
      this.controlStructureController = new ControlStructureController(isTestable());
    }
    this.controlStructureController.addObserver(this);
    return controlStructureController;
  }

  @Override
  public IControlActionController getControlActionController() {
    if (this.controlActionController == null) {
      this.controlActionController = new ControlActionController();
    }
    this.controlActionController.addObserver(this);
    return controlActionController;

  }

  @Override
  public IHazAccController getHazAccController() {
    if (this.hazAccController == null) {
      this.hazAccController = new HazAccController();
    }
    this.hazAccController.setLinkController(getLinkController());
    this.hazAccController.addObserver(this);
    return hazAccController;
  }

  @Override
  public ISDSController getSdsController() {
    if (this.sdsController == null) {
      this.sdsController = new SDSController();
    }
    this.sdsController.setLinkController(getLinkController());
    this.sdsController.setUserSystem(getUserSystem());
    this.sdsController.setExclusiveUserFile(this.exclusiveUserId != null);
    this.sdsController.addObserver(this);
    return sdsController;
  }

  @Override
  public ICausalController getCausalFactorController() {
    if (this.causalFactorController == null) {
      this.causalFactorController = new CausalFactorController();
    }
    this.causalFactorController.addObserver(this);
    this.causalFactorController.setLinkController(getLinkController());
    return this.causalFactorController;
  }

  @Override
  public IExtendedDataController getExtendedDataController() {
    if (this.extendedDataController == null) {
      this.extendedDataController = new ExtendedDataController();
    }
    this.extendedDataController.addObserver(this);
    return this.extendedDataController;
  }

  @Override
  public ProjectDataController getProjectDataManager() {
    if (this.projectDataManager == null) {
      this.projectDataManager = new ProjectDataController();
    }
    this.projectDataManager.addObserver(this);
    return projectDataManager;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getProperty(String key, Class<T> clazz) {
    if (clazz == Boolean.class && "astpa.use.multics".equals(key)) {
      return (T) this.controlStructureController.useMultiRoots();
    }
    if (clazz == Boolean.class && "astpa.use.singlecs".equals(key)) {
      return (T) Boolean.valueOf(!this.controlStructureController.useMultiRoots());
    }
    return super.getProperty(key, clazz);
  }

  @Override
  public void update(Observable o, Object arg) {
    if (arg == null) {
      setUnsavedAndChanged();
    } else if (arg instanceof IUndoCallback) {
      pushToUndo((IUndoCallback) arg);
      setUnsavedAndChanged(((IUndoCallback) arg).getChangeConstant());
    } else if (arg instanceof ObserverValue) {
      setUnsavedAndChanged((ObserverValue) arg);
    }
  }
}
