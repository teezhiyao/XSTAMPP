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

package xstampp.stlsa.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import xstampp.astpa.model.DataModelController;
import xstampp.astpa.model.causalfactor.CausalFactor;
import xstampp.astpa.model.controlaction.IControlActionController;
import xstampp.astpa.model.controlaction.safetyconstraint.ICorrespondingUnsafeControlAction;
import xstampp.astpa.model.extendedData.RefinedSafetyRule;
import xstampp.astpa.model.hazacc.Hazard;
import xstampp.astpa.model.hazacc.IHazAccController;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.linking.LinkingType;
import xstampp.model.AbstractLTLProvider;
import xstampp.model.ObserverValue;
import xstampp.stlsa.Activator;
import xstampp.stlsa.model.controlaction.SecControlActionController;
import xstampp.stlsa.model.results.ConstraintResult;
import xstampp.stlsa.model.results.ConstraintResultController;
import xstampp.stlsa.model.vulloss.VulLossController;
import xstampp.stlsa.util.jobs.SaveJob;
import xstampp.ui.common.ProjectManager;

/**
 * Data Model controller class
 * 
 * @author Fabian Toth
 * @since 2.0
 * 
 */
@XmlRootElement(namespace = "stlsa.model")
@XmlAccessorType(XmlAccessType.NONE)
public class StlsaController extends DataModelController {

  @XmlElement(name = "vulloss")
  private VulLossController vulLossController;

  @XmlElement(name = "secCac")
  private SecControlActionController controlActionController;

  @XmlElement(name = "crc")
  private ConstraintResultController constraintController;

  /**
   * Constructor of the DataModel Controller
   * 
   * @author Fabian Toth
   * 
   */
  public StlsaController() {
    super();
    this.vulLossController = new VulLossController();
    this.constraintController = new ConstraintResultController(this);
    this.controlActionController = new SecControlActionController();
  }

  public StlsaController(Object object) {
    // TODO Auto-generated constructor stub
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

  public UUID setUCASafetyCritical(UUID unsafeControlActionId, boolean safetyCritical) {
    if (unsafeControlActionId == null) {
      return null;
    }

    UUID id = this.controlActionController.setUCASafetyCritical(unsafeControlActionId,
        safetyCritical);
    this.setUnsavedAndChanged(ObserverValue.UNSAFE_CONTROL_ACTION);
    return id;
  }

  
  public UUID setUCASecurityCritical(UUID unsafeControlActionId, boolean securityCritical) {
    if (unsafeControlActionId == null) {
      return null;
    }

    UUID id = this.controlActionController.setUCASecurityCritical(unsafeControlActionId,
        securityCritical);
    this.setUnsavedAndChanged(ObserverValue.UNSAFE_CONTROL_ACTION);
    return id;
  }

  public UUID setUCAPrivacyCritical(UUID unsafeControlActionId, boolean privacyCritical) {
    if (unsafeControlActionId == null) {
      return null;
    }

    UUID id = this.controlActionController.setUCAPrivacyCritical(unsafeControlActionId,
        privacyCritical);
    this.setUnsavedAndChanged(ObserverValue.UNSAFE_CONTROL_ACTION);
    return id;
  }

  public List<ITableModel> getLinkedCausalFactorOfUCA(UUID unsafeControlActionId) {
    if (unsafeControlActionId == null) {
      return null;
    }
    List<UUID> links = getCausalFactorsLinksOfUCA(unsafeControlActionId);
    List<ITableModel> result = new ArrayList<>();
    for (UUID link : links) {
      result.add(this.getCausalFactor(link));
    }
    return result;
  }
  
  public List<UUID> getCausalFactorsLinksOfUCA(UUID unsafeControlActionId) {
    return this.getLinkController().getLinksFor(LinkingType.UCA_CausalFactor_LINK, unsafeControlActionId);
  }
  
  public void setRefinedSecurityConstraint(UUID refinedRuleId, String text) {

    Map<UUID, RefinedSafetyRule> ltlMap = new HashMap<>();
    for (AbstractLTLProvider ltl : getExtendedDataController().getAllScenarios(false, true, false)) {
      ltlMap.put(ltl.getId(), (RefinedSafetyRule) ltl);
    }
    if (ltlMap.containsKey(refinedRuleId)) {
      ltlMap.get(refinedRuleId).setRefinedSafetyConstraint(text);
      setUnsavedAndChanged(ObserverValue.Extended_DATA);
    }

  }

  public ConstraintResultController getConstraintController() {
    return constraintController;
  }

  @Override
  public String getPluginID() {
    return Activator.PLUGIN_ID;
  }

  public void setConstraintController(ConstraintResultController constraintController) {
    this.constraintController = constraintController;
  }

  public List<ConstraintResult> getAllConstraintResults() {
    return this.constraintController.getValuesList();
  }

  @Override
  public IHazAccController getHazAccController() {
    return vulLossController;
  }

  @Override
  public IControlActionController getControlActionController() {
    if(this.controlActionController == null) {
      this.controlActionController = new SecControlActionController();
    }
    return controlActionController;
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
      System.out.println(uca.getClass());
      if (uca.getId().equals(unsafeControlActionId)) {
        ucaExists = true;
        break;
      }
    }
//Not entirely sure why the newer version of the code uses !ucaExists instead. Not entirely sure what the previous code does because of the naming. 
    if (ucaExists) {
      return false;
    }

    if (this.getLinkController().addLink(LinkingType.UCA_HAZ_LINK, unsafeControlActionId,
        hazardId) != null) {
      return true;
    }
    return false;
  }
  
  public boolean removeUCACausalFactorLink(UUID unsafeControlActionId, UUID causalFactorId) {
    return this.getLinkController().deleteLink(LinkingType.UCA_CausalFactor_LINK, unsafeControlActionId,
        causalFactorId);
  }
  
  public boolean addUCACausalFactorLink(UUID unsafeControlActionId, UUID causalFactorId) {
    if ((unsafeControlActionId == null) || (causalFactorId == null)) {
      return false;
    }

    if (!(this.getCausalFactorController().getCausalFactor(causalFactorId) instanceof CausalFactor)) {
      return false;
    }
    boolean ucaExists = false;
    for (ICorrespondingUnsafeControlAction uca : this.getControlActionController().getAllUnsafeControlActions()) {
      if (uca.getId().equals(unsafeControlActionId)) {
        ucaExists = true;
        break;
      }
    }
//Not entirely sure why the newer version of the code uses !ucaExists instead. Not entirely sure what the previous code does because of the naming. 
    if (ucaExists) {
      return false;
    }

    if (this.getLinkController().addLink(LinkingType.UCA_CausalFactor_LINK, unsafeControlActionId,
        causalFactorId) != null) {
      return true;
    }
    return false;
  }
  
}
