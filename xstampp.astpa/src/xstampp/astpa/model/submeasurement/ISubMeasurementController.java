/*******************************************************************************
 * Copyright (C) 2017 Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner Institute of SoftwareTechnology,
 * Software Engineering Group University of Stuttgart, Germany. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the Eclipse Public License
 * v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Lukas Balzer - initial API and implementation
 ******************************************************************************/
package xstampp.astpa.model.submeasurement;

import java.util.List;
import java.util.SortedMap;
import java.util.UUID;

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
import xstampp.model.AbstractLTLProvider;
import xstampp.model.ObserverValue;

public interface ISubMeasurementController {

  /**
   * Adds a simple {@link ITableModel} that is not linked to a {@link ICausalComponent} but which is
   * can be used to create a Entry in the causal analysis. Notifies all observers with
   * {@link ObserverValue#CAUSAL_FACTOR}.
   * 
   * @return the {@link UUID} of the {@link ITableModel}
   */
  UUID addSubMeasurement();
  UUID addSubMeasurement(SubMeasurement subMeasurement);

  /**
   * 
   * @param subMeasurementId
   *          the {@link UUID} of a {@link ITableModel}
   * @param subMeasurementText
   *          the text that should be set as the {@link ITableModel#getText()}
   * @return <b style="color:blue">true</b> if the given id is valid and the text is different to
   *         the current <br> <b style="color:blue">false</b> otherwise
   */
  boolean setSubMeasurementText(UUID subMeasurementId, String subMeasurementText);

  boolean removeSubMeasurement(UUID subMeasurement);

  void prepareForExport(IHazAccController hazAccController, IRectangleComponent root,
      IExtendedDataController extendedDataController, IControlActionController caController,
      LinkController linkController, ISDSController sdsController);

  void prepareForSave(IHazAccController hazAccController, List<Component> list,
      List<AbstractLTLProvider> allRefinedRules,
      List<ICorrespondingUnsafeControlAction> allUnsafeControlActions,
      LinkController linkController);

  boolean isUseScenarios();

  void setUseScenarios(boolean useScenarios);

  List<ITableModel> getSafetyConstraints();

  UUID addSafetyConstraint(String text);

  /**
   * 
   * @param id
   *          the id of a constraint that has been created in the causal analysis
   * @return the {@link ITableModel} or a {@link BadReferenceModel} if the {@link UUID} doesn't
   *         exist
   */
  public ITableModel getSafetyConstraint(UUID id);

  /**
   * Returns the constraint text for the causal factor entry or an empty String if no
   * safetyConstraint was found.
   * 
   * @param id
   *          the {@link UUID} which was assigned to an {@link SubMeasurementEntry}
   * @return the description of the {@link ICausalSafetyConstraint}, or an empty String if no
   *         constraint was found for the given id.
   */
  String getConstraintTextFor(UUID id);

  /**
   * 
   * @param type
   *          One of {@link ComponentType}
   * @return true if the type is a valid component for the causal analysis
   */
  boolean validateCausalComponent(ComponentType type);

  /**
   * getter for a causal factor stored in the causal factor controller
   * 
   * @param subMeasurementId
   *          the {@link UUID} that was given to a causal factor by creation
   * @return the {@link ITableModel} for the given id or a singleton instance of
   *         {@link BadReferenceModel} if no {@link ITableModel} was found with the given id
   */
  ITableModel getSubMeasurement(UUID subMeasurementId);

  /**
   * @return a list containing all subMeasurements as {@link ITableModel}s, changes to this list
   *         doesn't affect the causal factors list
   */
  List<ITableModel> getSubMeasurements();

  /**
   * Creates a sorted mapping of a List of {@link Link}s to a {@link ITableModel}. The list set as
   * value contains all {@link LinkingType#UcaCfLink_Component_LINK}s between a
   * {@link LinkingType#UCA_SubMeasurement_LINK} of a <b>specific {@link ITableModel}</b> and the id
   * of a {@link ICausalComponent}.<br> The {@link List} is mapped to the <b>specific
   * {@link ITableModel}</b> of that list.
   * 
   * @param component
   *          a component that appears in the causal analysis
   * @param linkController
   *          the {@link LinkController} that contains the {@link Link}s
   * @return A {@link SortedMap} that links a {@link List} of {@link Link}s of type
   *         {@link LinkingType#UcaCfLink_Component_LINK} to a {@link ITableModel}s.
   */
  SortedMap<ITableModel, List<Link>> getSubMeasurementBasedMap(ICausalComponent component,
      LinkController linkController);

  /**
   * Creates a sorted mapping of a List of {@link Link}s to a {@link IUnsafeControlAction}. The list
   * set as value contains all {@link LinkingType#UcaCfLink_Component_LINK}s between a
   * {@link LinkingType#UCA_SubMeasurement_LINK} of a <b>specific {@link IUnsafeControlAction}</b> and
   * the id of a {@link ICausalComponent}.<br> The {@link List} is mapped to the <b>specific
   * {@link IUnsafeControlAction}</b> of that list.
   * 
   * @param component
   *          a component that appears in the causal analysis
   * @param linkController
   *          the {@link LinkController} that contains the {@link Link}s
   * @return A {@link SortedMap} that links a {@link List} of {@link Link}s of type
   *         {@link LinkingType#UcaCfLink_Component_LINK} to a {@link IUnsafeControlAction}s.
   */
  SortedMap<IUnsafeControlAction, List<Link>> getUCABasedMap(ICausalComponent component,
      LinkController linkController, IControlActionController caController);

  /**
   * Creates a sorted mapping of a List of {@link Link}s to a {@link IUnsafeControlAction}. The list
   * set as The value is a {@link List} containing: <ul>
   * <li>{@link LinkingType#CausalEntryLink_SC2_LINK}'s between an
   * {@link LinkingType#UcaCfLink_Component_LINK} and the {@link UUID} of a Safety Constraint if one
   * is defined for the respective {@link LinkingType#UcaCfLink_Component_LINK} <li>all
   * {@link LinkingType#CausalEntryLink_HAZ_LINK}s between a
   * {@link LinkingType#UcaCfLink_Component_LINK} of a <b>specific {@link IUnsafeControlAction}</b>
   * and the id of the hazModel. </ul> for each {@link LinkingType#UcaCfLink_Component_LINK} defined
   * for the given hazard.<br> The {@link List} is mapped to the <b>specific
   * {@link IUnsafeControlAction}</b> of that list.
   * 
   * @param hazModel
   *          a hazard model given as {@link ITableModel}
   * @param linkController
   *          the {@link LinkController} that contains the {@link Link}s
   * @return A {@link SortedMap} that links a {@link List} of {@link Link}s of type
   *         {@link LinkingType#CausalEntryLink_HAZ_LINK} or
   *         {@link LinkingType#CausalEntryLink_SC2_LINK} to a {@link IUnsafeControlAction}s.
   */
  SortedMap<IUnsafeControlAction, List<Link>> getHazardBasedMap(ITableModel hazModel,
      LinkController linkController, IControlActionController caController);

  /**
   * this determines if in the causal analysis the causal factor and UCA column are switched, in
   * A-STPA they are classically ordered CF, UCA but it might be better to change to UCA, CF which
   * will than list all Causal Factors for one UCA rather than all UCAs for a single Causal (which
   * might be 1 UCA).
   * 
   * @return if the Causal Factor and UCA column in the Causal Factors Table are switched
   */
  boolean analyseFactorsPerUCA();

  /**
   * this determines if in the causal analysis the causal factor and UCA column are switched, in
   * A-STPA they are classically ordered CF, UCA but it might be better to change to UCA, CF which
   * will than list all Causal Factors for one UCA rather than all UCAs for a single Causal (which
   * might be 1 UCA).
   * 
   * @param analyseFactorsPerUCA
   *          if the Causal Factor and UCA column in the Causal Factors Table should be switched
   */
  void setAnalyseFactorsPerUCA(boolean analyseFactorsPerUCA);
  boolean isAddedCF();
  void setAddedCF(boolean addedCF);
}
