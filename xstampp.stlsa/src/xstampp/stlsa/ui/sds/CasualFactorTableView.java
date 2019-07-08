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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import messages.Messages;
import xstampp.astpa.model.ATableModel;
import xstampp.astpa.model.causalfactor.CausalFactor;
import xstampp.astpa.model.causalfactor.CausalFactorController;
import xstampp.astpa.model.controlaction.UnsafeControlAction;
import xstampp.astpa.model.controlaction.interfaces.UnsafeControlActionType;
import xstampp.astpa.model.interfaces.IControlActionViewDataModel;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.linking.Link;
import xstampp.astpa.model.linking.LinkingType;
import xstampp.stlsa.model.StlsaController;
import xstampp.stlsa.ui.UnsafeCAView;
import xstampp.stlsa.ui.causalfactors.CausalFactorEnum;
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
  protected UUID factorid;

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
    
    TableViewerColumn CFG = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    CFG.getColumn().setText("Casual factor"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(CFG.getColumn(), new ColumnWeightData(10, 300, true));
      
    CFG.setLabelProvider(new ColumnLabelProvider() {
      @Override
      public void update(ViewerCell cell) {
        final Object element = cell.getElement();
        cell.setText(getText(element));
        Image image = getImage(element);
        cell.setImage(image);
        cell.setBackground(getBackground(element));
        cell.setForeground(getForeground(element));
        cell.setFont(getFont(element));
        
        TableItem[] items = CasualFactorTableView.this.getTableViewer().getTable().getItems();
        for (int i = 0; i < items.length; i++) {
          TableEditor editor = new TableEditor(CasualFactorTableView.this.getTableViewer().getTable());
          CCombo combo = new CCombo(CasualFactorTableView.this.getTableViewer().getTable(), SWT.CENTER);
          combo.setText("Casual Factor (Guide)");
          for (CausalFactorEnum CF : CausalFactorEnum.values()) { 
            combo.add(CF.getLabel());
        }
          combo.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent arg0) {
                    // change selection when an item is selected
                    CCombo ccomboCell = ((CCombo) arg0.getSource());
                    String selectedText = ccomboCell.getText();
                    System.out.println(selectedText);
                    System.out.println(element.getClass());
                    UUID UcaID = ((UnsafeControlAction) element).getId();
                    UUID CFID = CasualFactorTableView.this.createCausalFactor(selectedText);
                    CasualFactorTableView.this.factorid = CasualFactorTableView.this.addCausalFactorToUca(CFID, UcaID);
                }   
              });
          editor.grabHorizontal = true;
          editor.setEditor(combo, items[i], 2);
          }
        
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
        UUID UcaID = ((UnsafeControlAction) element).getId();
        StlsaController stlsaController = ((StlsaController) CasualFactorTableView.this.getDataInterface());
        List<Link> CFlinks = stlsaController.getLinkController().getLinksFor(LinkingType.UCA_CausalFactor_LINK);
        List<Link> CFlinkss = stlsaController.getLinkController().getLinksFor(LinkingType.UcaCfLink_Component_LINK);
        List<UUID> CFlinksss = stlsaController.getLinksOfUCA(UcaID);
        List<ITableModel> CFlinkssss = stlsaController.getCausalFactorController().getCausalFactors();
        System.out.println(CFlinks.size());
        System.out.println(CFlinkss.size());
        System.out.println(CFlinksss.size());
        System.out.println(CFlinkssss.size());

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
  
  //Helper methods below 
  public void setCausalFactor(UUID CFID, String CFText) {
    ((StlsaController) CasualFactorTableView.this.getDataInterface()).setCausalFactorText(CFID, CFText);
  }
  
  public UUID addCausalFactorToUca(UUID CFID, UUID UcaID) {
    System.out.println(CFID.toString());
    UUID factorid = ((StlsaController) CasualFactorTableView.this.getDataInterface()).addCausalFactor(CFID, UcaID);
    return factorid;
  }
  public UUID createCausalFactor(String selectedText) {
  StlsaController dataController = ((StlsaController) CasualFactorTableView.this.getDataInterface());
  CausalFactorController CFController = (CausalFactorController) dataController.getCausalFactorController();
  UUID CFID = CFController.addCausalFactor(new CausalFactor(selectedText));
  return CFID;
  }
}