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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import messages.Messages;
import xstampp.astpa.model.controlaction.interfaces.IControlAction;
import xstampp.astpa.model.controlstructure.interfaces.IConnection;
import xstampp.astpa.model.controlstructure.interfaces.IRectangleComponent;

/**
 * @author Jarkko Heidenwag
 * 
 */
public class UnsafeControlActionView extends xstampp.astpa.ui.sds.UnsafeControlActionView {

  /**
   * @author Jarkko Heidenwag
   * 
   */
  public static final String ID = "stlsa.steps.step3_1"; //$NON-NLS-1$

  @Override
  public String getId() {
    return UnsafeControlActionView.ID;
  }

  @Override
  public String getTitle() {
    return Messages.ControlActions;
  }
  
//  @Override
//  public void createPartControl(Composite parent) {
//
//    super.createPartControl(parent);
//    // the source column is for the unsafe control actions
//    TableViewerColumn sourceColumn = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
//    sourceColumn.getColumn().setText("Source"); //$NON-NLS-1$
//    getTableColumnLayout().setColumnData(sourceColumn.getColumn(),
//        new ColumnWeightData(10, 100, true));
//
//    sourceColumn.setLabelProvider(new ColumnLabelProvider() {
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
//              .getComponent(conn.getSourceAnchor().getOwnerId());
//          return comp.getText();
//        }
//        return null;
//      }
//    });
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
//    this.updateTable();
//    getAddNewItemButton().setEnabled(false);
//    getAddNewItemButton().setToolTipText(Messages.ControlActionView_1);
//  }
}
