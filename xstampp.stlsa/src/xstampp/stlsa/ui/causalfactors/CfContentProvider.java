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

import java.util.List;
import java.util.UUID;

import messages.Messages;
import xstampp.astpa.model.DataModelController;
import xstampp.astpa.model.causalfactor.CausalFactor;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.interfaces.IUnsafeControlActionDataModel;
import xstampp.stlsa.model.StlsaController;
import xstampp.ui.common.contentassist.ITableContentProvider;

/**
 * 
 * @author Benedikt Markt
 * 
 */
public class CfContentProvider implements ITableContentProvider<ITableModel> {
  private final transient IUnsafeControlActionDataModel cfInterface;
  private boolean DefaultCFAdded = false;
  /**
   * 
   * @author Benedikt Markt
   * @param cfInterface
   *          the interface to the datamodel
   * 
   */
  public CfContentProvider(final IUnsafeControlActionDataModel cfInterface) {
    this.cfInterface = cfInterface;
  }

  @Override
  public List<ITableModel> getAllItems() {
    System.out.println("Get all Items");
    System.out.println(this.cfInterface.toString());
    if(!((DataModelController)this.cfInterface).getCausalFactorController().isAddedCF()) {
      for (CausalFactorEnum CF : CausalFactorEnum.values()) { 
        ((DataModelController)this.cfInterface).getCausalFactorController().addCausalFactor(new CausalFactor(CF.getLabel(), CF.getDisplayableType()));
        }
      ((DataModelController)this.cfInterface).getCausalFactorController().setAddedCF(true);
    }
    return ((DataModelController)this.cfInterface).getCausalFactorController().getCausalFactors();
  }

  @Override
  public List<ITableModel> getLinkedItems(final UUID itemId) {
    return getStlsaController().getLinkedCausalFactorOfUCA(itemId);
  }
  
  public StlsaController getStlsaController() {
    return ((StlsaController)this.cfInterface);
  }
  
  
  
  @Override
  public void addLink(final UUID ucaId, final UUID cfId) {
    CausalFactor templateCf = (CausalFactor) getStlsaController().getCausalFactor(cfId);
    System.out.println("In Add Link");
    System.out.println(templateCf.getIdString());
    CausalFactor newCf = new CausalFactor(templateCf.getText(), templateCf.getIntention());
    UUID newCFId = getStlsaController().getCausalFactorController().addCausalFactor(newCf);
    getStlsaController().addUCACausalFactorLink(ucaId, newCFId);
  }

  @Override
  public void removeLink(final UUID item, final UUID removeItem) {
    getStlsaController().removeUCACausalFactorLink(item, removeItem);
    getStlsaController().getCausalFactorController().removeCausalFactor(removeItem);
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
