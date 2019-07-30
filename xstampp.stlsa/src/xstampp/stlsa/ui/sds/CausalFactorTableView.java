/*******************************************************************************
 * Copyright (c) 2013, 2017 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam Grahovac, Jarkko
 * Heidenwag, Benedikt Markt, Jaqueline Patzek, Sebastian Sieber, Fabian Toth, Patrick Wickenhäuser,
 * Aliaksei Babkovich, Aleksander Zotov).
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/

package xstampp.stlsa.ui.sds;

import java.util.EnumSet;
import java.util.Observable;
import java.util.UUID;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import messages.Messages;
import xstampp.astpa.model.ATableModel;
import xstampp.astpa.model.causalfactor.CausalFactor;
import xstampp.astpa.model.causalfactor.CausalFactorController;
import xstampp.astpa.model.controlaction.UnsafeControlAction;
import xstampp.astpa.model.interfaces.IControlActionViewDataModel;
import xstampp.stlsa.model.StlsaController;
import xstampp.astpa.model.controlaction.ControlAction;
import xstampp.stlsa.ui.CausalFactorBaseView;
//import xstampp.stpapriv.model.controlaction.UnsafeControlAction;
import xstampp.model.ObserverValue;

/**
 * @author Jarkko Heidenwag
 * 
 */
public class CausalFactorTableView extends CausalFactorBaseView<IControlActionViewDataModel> {

  /**
   * @author Jarkko Heidenwag
   * 
   */
  public static final String ID = "stlsa.steps.step4_1"; //$NON-NLS-1$
  protected UUID factorid;

  /**
   * @author Jarkko Heidenwag
   * 
   */
  public CausalFactorTableView() {
    super(true);
    setUpdateValues(EnumSet.of(ObserverValue.CAUSAL_FACTOR));
  }

  /**
   * Create contents of the view part.
   * 
   * @author Jarkko Heidenwag
   * @param parent
   *          The parent composite
   */
  @Override
  public void createPartControl(Composite parent) {
    super.createPartControl(parent);
    
    TableViewerColumn CFDescCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    CFDescCol.getColumn().setText("Casual factor description"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(CFDescCol.getColumn(), new ColumnWeightData(10, 300, true));
      
    CFDescCol.setLabelProvider(new ColumnLabelProvider() {
      @Override
      public String getText(Object element) {

        if (element instanceof CausalFactor) {
          return ((CausalFactor) element).getDescription();
        }
        return null;
      }      

    });
    
    // the Type column is for the unsafe control actions
    TableViewerColumn intentionCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    intentionCol.getColumn().setText("Unintentional/ Intentional Type"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(intentionCol.getColumn(),
        new ColumnWeightData(10, 100, true));

    intentionCol.setLabelProvider(new ColumnLabelProvider() {
      
      @Override
      public String getText(Object element) {
        if (element instanceof CausalFactor) {
          return ((CausalFactor) element).getIntention();
        }
        return null;
      }  
    });
    

    TableViewerColumn ucaIdCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    ucaIdCol.getColumn().setText("UCA ID"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(ucaIdCol.getColumn(),
        new ColumnWeightData(10, 100, true));

    ucaIdCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        if (element instanceof CausalFactor) {
          UUID ucaId = ((CausalFactor) element).getParentUUID();
          UnsafeControlAction uca = (UnsafeControlAction) CausalFactorTableView.this.getStlsaController().getControlActionController().getUnsafeControlAction(ucaId);
          return uca.getIdString();
        }
        return null;
      }  
    });
        
    TableViewerColumn ucaTypeCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    ucaTypeCol.getColumn().setText("UCA Type"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(ucaTypeCol.getColumn(),
        new ColumnWeightData(10, 100, true));

    ucaTypeCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        if (element instanceof CausalFactor) {
          UUID ucaId = ((CausalFactor) element).getParentUUID();
          UnsafeControlAction uca = (UnsafeControlAction) CausalFactorTableView.this.getStlsaController().getControlActionController().getUnsafeControlAction(ucaId);
          return uca.getType().toString();
        }
        return null;
      }  
    });
    
    TableViewerColumn controlActionCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    controlActionCol.getColumn().setText("Control Action"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(controlActionCol.getColumn(),
        new ColumnWeightData(10, 100, true));

    controlActionCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        if (element instanceof CausalFactor) {
          UUID ucaId = ((CausalFactor) element).getParentUUID();
          ControlAction ca = (ControlAction) CausalFactorTableView.this.getStlsaController().getControlActionForUca(ucaId);
          return ca.getTitle();
        }
        return null;
      }  
    });    
    
    
    this.updateTable();
    getAddNewItemButton().setEnabled(false);
    getAddNewItemButton().setToolTipText(Messages.ControlActionView_1);
    
    
  }

  @Override
  protected void deleteEntry(ATableModel model) {
    resetCurrentSelection();
    this.getDataInterface().removeControlAction(model.getId());
  }

  /**
   * @author Jarkko Heidenwag
   * 
   */
  @Override
  public void updateTable() {

    CausalFactorTableView.this.getTableViewer().setInput(getStlsaController().getAllLinkedCausalFactor());
  }

  
  public StlsaController getStlsaController() {
    return (StlsaController) this.getDataInterface();
  }
  
  public CausalFactorController getCfController() {
    return (CausalFactorController) getStlsaController().getCausalFactorController();
  }
  
  
  @Override
  public void update(Observable dataModelController, Object updatedValue) {
    super.update(dataModelController, updatedValue);
    ObserverValue type = (ObserverValue) updatedValue;
    switch (type) {
    case UNSAFE_CONTROL_ACTION:
      this.refreshView();
      break;
    default:
      break;
    }
  }

  @Override
  public String getId() {
    return CausalFactorTableView.ID;
  }

  @Override
  public String getTitle() {
    return Messages.ControlActions;
  }

  @Override
  public void dispose() {
    this.getDataInterface().deleteObserver(this);
    super.dispose();
  }

  @Override
  protected void moveEntry(UUID id, boolean moveUp) {
    getDataInterface().moveEntry(false, moveUp, id, ObserverValue.UNSAFE_CONTROL_ACTION);
  }

  @Override
  protected void addNewEntry() {
  }

  @Override
  protected void updateDescription(UUID uuid, String description) {
    CausalFactorTableView.this.getDataInterface().setControlActionDescription(uuid, description);
  }

  @Override
  protected void updateTitle(UUID id, String title) {
    getDataInterface().setControlActionTitle(id, title);
  }
  
  //Helper methods below 
  public void setCausalFactor(UUID CFID, String CFText) {
    ((StlsaController) CausalFactorTableView.this.getDataInterface()).setCausalFactorText(CFID, CFText);
  }
  
  public UUID addCausalFactorToUca(UUID CFID, UUID UcaID) {
    System.out.println(CFID.toString());
    UUID factorid = ((StlsaController) CausalFactorTableView.this.getDataInterface()).addCausalFactor(CFID, UcaID);
    return factorid;
  }
  public UUID createCausalFactor(String selectedText) {
  StlsaController dataController = ((StlsaController) CausalFactorTableView.this.getDataInterface());
  CausalFactorController CFController = (CausalFactorController) dataController.getCausalFactorController();
  UUID CFID = CFController.addCausalFactor(new CausalFactor(selectedText));
  return CFID;
  }
}