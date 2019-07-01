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
import xstampp.astpa.model.controlaction.ControlAction;
import xstampp.astpa.model.controlaction.UnsafeControlAction;
import xstampp.astpa.model.controlaction.interfaces.IControlAction;
import xstampp.astpa.model.controlstructure.interfaces.IConnection;
import xstampp.astpa.model.controlstructure.interfaces.IRectangleComponent;
import xstampp.astpa.model.interfaces.IControlActionViewDataModel;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.stlsa.ui.UnsafeCAView;
//import xstampp.stpapriv.model.controlaction.UnsafeControlAction;
import xstampp.model.ObserverValue;

/**
 * @author Jarkko Heidenwag
 * 
 */
public class CasualFactorTableView extends UnsafeCAView<IControlActionViewDataModel> {

  /**
   * @author Jarkko Heidenwag
   * 
   */
  public static final String ID = "stlsa.steps.step3_2"; //$NON-NLS-1$

  /**
   * @author Jarkko Heidenwag
   * 
   */
  public CasualFactorTableView() {
    super(true);
    setUpdateValues(EnumSet.of(ObserverValue.UNSAFE_CONTROL_ACTION));
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
    
    // the Control Action column is for the unsafe control actions
    TableViewerColumn CAColumn = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    CAColumn.getColumn().setText("Causal factor (Guide word)"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(CAColumn.getColumn(),
        new ColumnWeightData(10, 100, true));

    CAColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {

// Code assumes that all UnsafeControlAction have a parent ControlAction. 
        if ((UnsafeControlAction) element instanceof UnsafeControlAction) {
            return CasualFactorTableView.this.getDataInterface().getControlActionController().getControlActionFor(((UnsafeControlAction) element).getId()).getTitle();
        }
        return null;
      }
    });
    
    // the Type column is for the unsafe control actions
    TableViewerColumn typeColumn = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    typeColumn.getColumn().setText("Casual factor"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(typeColumn.getColumn(),
        new ColumnWeightData(10, 100, true));

    typeColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        if ((UnsafeControlAction) element instanceof UnsafeControlAction) {
        if (((UnsafeControlAction) element).getType().toString() != " ") {
          return ((UnsafeControlAction) element).getType().toString();
          }
        else {
          return "N.A";
        }
      }
      return null;
      }
    });
    

    TableViewerColumn IntentionColumn = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    IntentionColumn.getColumn().setText("Intentional/Unintentional"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(IntentionColumn.getColumn(),
        new ColumnWeightData(10, 100, true));

    IntentionColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        if ((UnsafeControlAction) element instanceof UnsafeControlAction) {
        if (((UnsafeControlAction) element).getType().toString() != " ") {
          return ((UnsafeControlAction) element).getType().toString();
          }
        else {
          return "N.A";
        }
      }
      return null;
      }
    });
        
    TableViewerColumn CasualFactorID = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    CasualFactorID.getColumn().setText("Casual Factor ID"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(CasualFactorID.getColumn(),
        new ColumnWeightData(10, 100, true));

    CasualFactorID.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        if ((UnsafeControlAction) element instanceof UnsafeControlAction) {
        if (((UnsafeControlAction) element).getType().toString() != " ") {
          return ((UnsafeControlAction) element).getType().toString();
          }
        else {
          return "N.A";
        }
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

    CasualFactorTableView.this.getTableViewer().setInput(this.getDataInterface().getControlActionController().getUCAList(null));
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
    return CasualFactorTableView.ID;
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
    CasualFactorTableView.this.getDataInterface().setControlActionDescription(uuid, description);
  }

  @Override
  protected void updateTitle(UUID id, String title) {
    getDataInterface().setControlActionTitle(id, title);
  }
}
