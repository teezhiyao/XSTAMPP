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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.UUID;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import messages.Messages;
import xstampp.astpa.model.ATableModel;
import xstampp.astpa.model.causalfactor.CausalFactor;
import xstampp.astpa.model.causalfactor.CausalFactorController;
import xstampp.astpa.model.controlaction.UnsafeControlAction;
import xstampp.astpa.model.interfaces.IControlActionViewDataModel;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.submeasurement.SubMeasurement;
import xstampp.astpa.model.submeasurement.SubMeasurementController;
import xstampp.stlsa.model.StlsaController;
import xstampp.astpa.model.controlaction.ControlAction;
import xstampp.stlsa.ui.CausalFactorBaseView;
import xstampp.usermanagement.api.AccessRights;
//import xstampp.stpapriv.model.controlaction.UnsafeControlAction;
import xstampp.model.ObserverValue;

/**
 * @author Jarkko Heidenwag
 * 
 */
public class SeverLikeliEvalTableView extends CausalFactorBaseView<IControlActionViewDataModel> {

  /**
   * @author Jarkko Heidenwag
   * 
   */
  public static final String ID = "stlsa.steps.step4_3"; //$NON-NLS-1$
  protected UUID factorid;

  /**
   * @author Jarkko Heidenwag
   * 
   */
  public SeverLikeliEvalTableView() {
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
    
    List<ITableModel> allSub = getSubMeasurementController().getSubMeasurement();
    for(int i = 0; i < allSub.size(); i++) {
      final SubMeasurement currentSub = (SubMeasurement) allSub.get(i);
      final String subMeasurementTitle = currentSub.getSubMeasurement();
      if(currentSub.getSubMeasurement() == "N.A") {

      }
      else {
      TableViewerColumn subMeasurementCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
      subMeasurementCol.getColumn().setText(currentSub.getSeverityLikelihood() + "-" + currentSub.getSubMeasurement() + "(" + currentSub.getType() +")"); //$NON-NLS-1$
      getTableColumnLayout().setColumnData(subMeasurementCol.getColumn(), new ColumnWeightData(10, 100, true));
      subMeasurementCol.setLabelProvider(new ColumnLabelProvider() {
        @Override
        public String getText(Object element) {
          if (element instanceof CausalFactor) {
            int scale = ((CausalFactor)element).getSubMeasurements(subMeasurementTitle);
            if(scale == -999) {
              return "N.A";
            }
            else{
              return Integer.toString(scale);
            }
          }
          
          return null;
        }  
      });
      
      subMeasurementCol.setEditingSupport(new EditingSupport(getTableViewer())  {

        @Override
        protected boolean canEdit(Object element) {
          if(((CausalFactor) element).getIntention().contentEquals(currentSub.getType())) {
            return true;
          };
          return false;
        }


        @Override
        protected CellEditor getCellEditor(Object element) {
          return new TextCellEditor(getTableViewer().getTable());
        }

        @Override
        protected Object getValue(Object element) {
          if (element instanceof CausalFactor) {
            int scale = ((CausalFactor)element).getSubMeasurements(subMeasurementTitle);
            if(scale == -999) {
              return "N.A";
            }
            else{
              return Integer.toString(scale);
            }
          }
          return getValue(((ATableModel) element).getTitle());
        }

        @Override
        protected void setValue(Object element, Object value) {
          System.out.println("element: " + element.getClass().toString());
          System.out.println("value: " + value.toString());  
          if (element instanceof CausalFactor) {
            System.out.println(subMeasurementTitle);
            ((CausalFactor)element).setSubMeasurements(subMeasurementTitle,Integer.parseInt(value.toString()));
          }
          SeverLikeliEvalTableView.this.refreshView();
          //          UUID uuid = ((ATableModel) element).getId();
//          updateTitle(uuid, String.valueOf(value));
        }

        protected Object getValue(String string) {
          return string;
        }
      });

      }
    }
    
    
    TableViewerColumn severityCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    severityCol.getColumn().setText("Severity"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(severityCol.getColumn(),
        new ColumnWeightData(10, 100, true));

    severityCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
//        if (element instanceof CausalFactor) {
//            HashMap<String, Integer> severLst= ((CausalFactor) element).getSubMeasurements();
//            int severSum = 0;
//            if(severLst.isEmpty()) {
//              for (Map.Entry<String, Integer> pair : severLst.entrySet()) {
//                System.out.println("sever pair key" + pair.getKey().toString());
//                if(pair.getKey().toString() == "Severity")
//                  severSum += pair.getValue();
//                }
//            }
//            else {}
//            return Integer.toString(severSum);
//            }
        return null;
      }  
    });
    
    TableViewerColumn likelihoodCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    likelihoodCol.getColumn().setText("Likelihood"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(likelihoodCol.getColumn(),
        new ColumnWeightData(10, 100, true));

    likelihoodCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
//        if (element instanceof CausalFactor) {
//          HashMap<String, Integer> severLst= ((CausalFactor) element).getSubMeasurements();
//          int likeliSum = 0;
//          for (Map.Entry<String, Integer> pair : severLst.entrySet()) {
//            if(pair.getKey().toString() == "Likelihood")
//              likeliSum += pair.getValue();
//            }
//          return Integer.toString(likeliSum);
//          }
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

    SeverLikeliEvalTableView.this.getTableViewer().setInput(getStlsaController().getAllLinkedCausalFactor());
  }

  
  public StlsaController getStlsaController() {
    return (StlsaController) this.getDataInterface();
  }
  
  public CausalFactorController getCfController() {
    return (CausalFactorController) getStlsaController().getCausalFactorController();
  }
  
  public SubMeasurementController getSubMeasurementController() {
    return (SubMeasurementController) getStlsaController().getSubMeasurementController();
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
    return SeverLikeliEvalTableView.ID;
  }

  @Override
  public String getTitle() {
    return Messages.SeverityLikelihoodTable;
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
    SeverLikeliEvalTableView.this.getDataInterface().setControlActionDescription(uuid, description);
  }

  @Override
  protected void updateTitle(UUID id, String title) {
    getDataInterface().setControlActionTitle(id, title);
  }
  
  //Helper methods below 
  public void setCausalFactor(UUID CFID, String CFText) {
    ((StlsaController) SeverLikeliEvalTableView.this.getDataInterface()).setCausalFactorText(CFID, CFText);
  }
  
//  public UUID addCausalFactorToUca(UUID CFID, UUID UcaID) {
//    System.out.println(CFID.toString());
//    UUID factorid = ((StlsaController) SeverLikeliEvalTableView.this.getDataInterface()).addCausalFactor(CFID, UcaID);
//    return factorid;
//  }
//  public UUID createCausalFactor(String selectedText) {
//  StlsaController dataController = ((StlsaController) SeverLikeliEvalTableView.this.getDataInterface());
//  CausalFactorController CFController = (CausalFactorController) dataController.getCausalFactorController();
//  UUID CFID = CFController.addCausalFactor(new CausalFactor(selectedText));
//  return CFID;
//  }
}