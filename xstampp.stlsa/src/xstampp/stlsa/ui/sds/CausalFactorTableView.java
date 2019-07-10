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
import xstampp.astpa.model.interfaces.IControlActionViewDataModel;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.linking.Link;
import xstampp.astpa.model.linking.LinkingType;
import xstampp.stlsa.model.StlsaController;
import xstampp.stlsa.ui.CausalFactorBaseView;
import xstampp.stlsa.ui.UnsafeCAView;
import xstampp.stlsa.ui.causalfactors.CausalFactorEnum;
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
    
    TableViewerColumn CFG = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    CFG.getColumn().setText("Casual factor description"); //$NON-NLS-1$
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
        
        TableItem[] items = CausalFactorTableView.this.getTableViewer().getTable().getItems();
        for (int i = 0; i < items.length; i++) {
          TableEditor editor = new TableEditor(CausalFactorTableView.this.getTableViewer().getTable());
          CCombo combo = new CCombo(CausalFactorTableView.this.getTableViewer().getTable(), SWT.CENTER);
          combo.setText("Casual Factor (Guide)");
          for (CausalFactorEnum CF : CausalFactorEnum.values()) { 
            combo.add(CF.getLabel());
        }
          combo.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent arg0) {
                    // change selection when an item is selected
//                    CCombo ccomboCell = ((CCombo) arg0.getSource());
//                    String selectedText = ccomboCell.getText();
//                    System.out.println(selectedText);
//                    System.out.println(element.getClass());
//                    UUID UcaID = ((UnsafeControlAction) element).getId();
//                    UUID CFID = CausalFactorTableView.this.createCausalFactor(selectedText);
//                    CausalFactorTableView.this.factorid = CausalFactorTableView.this.addCausalFactorToUca(CFID, UcaID);
                }   
              });
          editor.grabHorizontal = true;
          editor.setEditor(combo, items[i], 2);
          }
        
      }
    });
    
    // the Type column is for the unsafe control actions
    TableViewerColumn typeColumn = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    typeColumn.getColumn().setText("Unintentional/ Intentional Type"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(typeColumn.getColumn(),
        new ColumnWeightData(10, 100, true));

    typeColumn.setLabelProvider(new ColumnLabelProvider() {
      
      @Override
      public String getText(Object element) {
//        if ((UnsafeControlAction) element instanceof UnsafeControlAction) {
//        if (((UnsafeControlAction) element).getType().toString() != " ") {
//          return ((UnsafeControlAction) element).getType().toString();
//          }
//        else {
//          return "N.A";
//        }
//      }
      return null;
      }
    });
    

    TableViewerColumn IntentionColumn = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    IntentionColumn.getColumn().setText("UCA ID"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(IntentionColumn.getColumn(),
        new ColumnWeightData(10, 100, true));

    IntentionColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
//        if ((UnsafeControlAction) element instanceof UnsafeControlAction) {
//        if (((UnsafeControlAction) element).getType().toString() != " ") {
//          return ((UnsafeControlAction) element).getType().toString();
//          }
//        else {
//          return "N.A";
//        }
//      }
      return null;
      }
    });
        
    TableViewerColumn CasualFactorID = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    CasualFactorID.getColumn().setText("UCA Type"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(CasualFactorID.getColumn(),
        new ColumnWeightData(10, 100, true));

    CasualFactorID.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
//        UUID UcaID = ((UnsafeControlAction) element).getId();
//        StlsaController stlsaController = ((StlsaController) CausalFactorTableView.this.getDataInterface());
//        List<Link> CFlinks = stlsaController.getLinkController().getLinksFor(LinkingType.UCA_CausalFactor_LINK);
//        List<Link> CFlinkss = stlsaController.getLinkController().getLinksFor(LinkingType.UcaCfLink_Component_LINK);
//        List<UUID> CFlinksss = stlsaController.getLinksOfUCA(UcaID);
//        List<ITableModel> CFlinkssss = stlsaController.getCausalFactorController().getCausalFactors();

        return null;
      }
    });
    
    TableViewerColumn IntentionColumn2 = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    IntentionColumn2.getColumn().setText("Control Action"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(IntentionColumn2.getColumn(),
        new ColumnWeightData(10, 100, true));

    IntentionColumn2.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
//        if ((UnsafeControlAction) element instanceof UnsafeControlAction) {
//        if (((UnsafeControlAction) element).getType().toString() != " ") {
//          return ((UnsafeControlAction) element).getType().toString();
//          }
//        else {
//          return "N.A";
//        }
//      }
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