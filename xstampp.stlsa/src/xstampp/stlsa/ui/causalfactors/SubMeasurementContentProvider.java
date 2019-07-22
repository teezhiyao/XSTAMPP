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

package xstampp.stlsa.ui.causalfactors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import xstampp.astpa.model.DataModelController;
import xstampp.astpa.model.controlaction.UnsafeControlAction;
import xstampp.astpa.model.controlaction.safetyconstraint.ICorrespondingUnsafeControlAction;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.submeasurement.SubMeasurement;
import xstampp.astpa.model.interfaces.ISubMeasurementDataModel;
import xstampp.stlsa.model.StlsaController;
import xstampp.ui.common.contentassist.ITableContentProvider;

/**
 * 
 * @author Benedikt Markt
 * 
 */
public class SubMeasurementContentProvider implements ITableContentProvider<ITableModel> {
  private final transient ISubMeasurementDataModel cfInterface;
  private boolean DefaultCFAdded = false;
  /**
   * 
   * @author Benedikt Markt
   * @param cfInterface
   *          the interface to the datamodel
   * 
   */
  public SubMeasurementContentProvider(final ISubMeasurementDataModel cfInterface) {
    this.cfInterface = cfInterface;
  }

  @Override
  public List<ITableModel> getAllItems() {
    System.out.println("Get all Items");
    System.out.println(this.cfInterface.toString());
//    if(!((DataModelController)this.cfInterface).getSubMeasurementController().isAddedCF()) {
//      for (SubMeasurementEnum CF : SubMeasurementEnum.values()) { 
//        ((DataModelController)this.cfInterface).getSubMeasurementController().addSubMeasurement(new SubMeasurement(CF.getLabel(), CF.getDisplayableType()));
//        }
//      ((DataModelController)this.cfInterface).getSubMeasurementController().setAddedCF(true);
//    }
    return ((DataModelController)this.cfInterface).getSubMeasurementController().getSubMeasurement();
  }

  @Override
  public List<ITableModel> getLinkedItems(final UUID itemId) {
    return getStlsaController().getLinkedSubMeasurementOfUCA(itemId);
  }
  
  @Override
  public List<ITableModel> getAllLinkedItems() {
    List<ITableModel> result = new ArrayList<ITableModel>();
    for (ICorrespondingUnsafeControlAction uca : getStlsaController().getUCAList(null)) {
    List<ITableModel> links = getStlsaController().getLinkedSubMeasurementOfUCA(((UnsafeControlAction)uca).getId());
    for(ITableModel link : links) {
      result.add(link);
    }
    }
    return result;
  }
  
  public StlsaController getStlsaController() {
    return ((StlsaController)this.cfInterface);
  }
  
  
  
  @Override
  public void addLink(final UUID ucaId, final UUID cfId) {
//    SubMeasurement templateCf = (SubMeasurement) getStlsaController().getSubMeasurement(cfId);
//    SubMeasurement newCf = new SubMeasurement(templateCf.getTitle(), templateCf.getIntention());
//    UUID newCFId = getStlsaController().getSubMeasurementController().addSubMeasurement(newCf);
//    newCf.setParentUUID(ucaId);
//    getStlsaController().addUCASubMeasurementLink(ucaId, newCFId);
    
  }

  @Override
  public void removeLink(final UUID item, final UUID removeItem) {
    getStlsaController().removeUCASubMeasurementLink(item, removeItem);
    getStlsaController().getSubMeasurementController().removeSubMeasurement(removeItem);
  }

  @Override
  public String getPrefix() {
    return "";
  }

  @Override
  public String getEmptyMessage() {
    return "Not Selected";
  }
}
