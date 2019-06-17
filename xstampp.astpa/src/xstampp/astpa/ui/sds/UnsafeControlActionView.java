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

package xstampp.astpa.ui.sds;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import messages.Messages;
import xstampp.astpa.model.ATableModel;
import xstampp.astpa.model.controlaction.interfaces.IControlAction;
import xstampp.astpa.model.controlaction.interfaces.IUnsafeControlAction;
import xstampp.astpa.model.controlstructure.interfaces.IConnection;
import xstampp.astpa.model.controlstructure.interfaces.IRectangleComponent;
import xstampp.astpa.model.interfaces.IControlActionViewDataModel;
import xstampp.astpa.ui.UnsafeCAView;
import xstampp.model.ObserverValue;

/**
 * @author Jarkko Heidenwag
 * 
 */
public class UnsafeControlActionView extends UnsafeCAView<IControlActionViewDataModel> {

  /**
   * @author Jarkko Heidenwag
   * 
   */
  public static final String ID = "astpa.steps.step2_5"; //$NON-NLS-1$

  /**
   * @author Jarkko Heidenwag
   * 
   */
  public UnsafeControlActionView() {
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
    CAColumn.getColumn().setText("Control Actions"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(CAColumn.getColumn(),
        new ColumnWeightData(10, 100, true));

    CAColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        if (element instanceof IControlAction) {
          List<IUnsafeControlAction> lst = ((IControlAction) element).getUnsafeControlActions();
//          System.out.println(lst.get(0).getType());
//          System.out.println(lst.get(0).getIdString());
          if(lst.size() > 0) {
            return lst.get(0).getType().toString();
          }
          else {
            return "N.A";
          }
        }
        return null;
      }
    });
    
    // the Type column is for the unsafe control actions
    TableViewerColumn typeColumn = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    typeColumn.getColumn().setText("Type"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(typeColumn.getColumn(),
        new ColumnWeightData(10, 100, true));

    typeColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        if (element instanceof IControlAction) {
          IRectangleComponent comp = UnsafeControlActionView.this.getDataInterface()
              .getComponent(((IControlAction) element).getComponentLink());
          if (comp == null) {
            return null;
          }
          IConnection conn = UnsafeControlActionView.this.getDataInterface()
              .getConnection(comp.getRelative());
          if (conn == null) {
            return null;
          }
          comp = UnsafeControlActionView.this.getDataInterface()
              .getComponent(conn.getSourceAnchor().getOwnerId());
          return comp.getText();
        }
        return null;
      }
    });
    
    // the Hazard column is for the unsafe control actions
    TableViewerColumn HazardColumn = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    HazardColumn.getColumn().setText("Possible Hazards"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(HazardColumn.getColumn(),
        new ColumnWeightData(10, 100, true));

    HazardColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        if (element instanceof IControlAction) {
          IRectangleComponent comp = UnsafeControlActionView.this.getDataInterface()
              .getComponent(((IControlAction) element).getComponentLink());
          if (comp == null) {
            return null;
          }
          IConnection conn = UnsafeControlActionView.this.getDataInterface()
              .getConnection(comp.getRelative());
          if (conn == null) {
            return null;
          }
          comp = UnsafeControlActionView.this.getDataInterface()
              .getComponent(conn.getSourceAnchor().getOwnerId());
          return comp.getText();
        }
        return null;
      }
    });
    
//    // the target column is for the unsafe control actions
//    TableViewerColumn distanceColumn = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
//    distanceColumn.getColumn().setText("Destination");//$NON-NLS-1$
//    getTableColumnLayout().setColumnData(distanceColumn.getColumn(),
//        new ColumnWeightData(10, 100, true));
//
//    distanceColumn.setLabelProvider(new ColumnLabelProvider() {
//
//      @Override
//      public String getText(Object element) {
//        if (element instanceof IControlAction) {
//          IRectangleComponent comp = UnsafeControlActionView.this.getDataInterface()
//              .getComponent(((IControlAction) element).getComponentLink());
//          if (comp == null) {
//            return null;
//          }
//          IConnection conn = UnsafeControlActionView.this.getDataInterface()
//              .getConnection(comp.getRelative());
//          if (conn == null) {
//            return null;
//          }
//          comp = UnsafeControlActionView.this.getDataInterface()
//              .getComponent(conn.getTargetAnchor().getOwnerId());
//          return comp.getText();
//        }
//        return null;
//      }
//    });
    
        
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

//    List<ICorrespondingUnsafeControlAction> CAlist = this.getDataInterface().getControlActionController().getAllUnsafeControlActions();
//    List<IUnsafeControlAction> UCA = new ArrayList<IUnsafeControlAction>();
//    for (IControlAction CA : CAlist) {
//      List<IUnsafeControlAction> tempUCA = CA.getUnsafeControlActions();
//      for (IUnsafeControlAction unsafe : tempUCA) {
//        System.out.println("Hi here");
//        System.out.println(unsafe.getType().toString());
//        UCA.add((IUnsafeControlAction) unsafe);
//      }
//  }     
    System.out.println("Trying to updatetable in unsafe control action view");
    UnsafeControlActionView.this.getTableViewer().setInput(this.getDataInterface().getControlActionController().getUCAList(null));
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
    return UnsafeControlActionView.ID;
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
    UnsafeControlActionView.this.getDataInterface().setControlActionDescription(uuid, description);
  }

  @Override
  protected void updateTitle(UUID id, String title) {
    getDataInterface().setControlActionTitle(id, title);
  }
}
