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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.UUID;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import messages.Messages;
import xstampp.astpa.model.ATableModel;
import xstampp.astpa.model.causalfactor.CausalFactor;
import xstampp.astpa.model.causalfactor.CausalFactorController;
import xstampp.astpa.model.controlaction.UnsafeControlAction;
import xstampp.astpa.model.controlaction.interfaces.IControlAction;
import xstampp.astpa.model.controlaction.safetyconstraint.ICorrespondingUnsafeControlAction;
import xstampp.astpa.model.interfaces.IControlActionViewDataModel;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.stlsa.model.StlsaController;
import xstampp.astpa.model.controlaction.ControlAction;
import xstampp.stlsa.ui.EmptyBaseView;
//import xstampp.stpapriv.model.controlaction.UnsafeControlAction;
import xstampp.model.ObserverValue;

/**
 * @author Jarkko Heidenwag
 * 
 */
public class AssessmentOverview extends EmptyBaseView<IControlActionViewDataModel> {

  /**
   * @author Jarkko Heidenwag
   * 
   */
  public static final String ID = "stlsa.steps.step5"; //$NON-NLS-1$
  protected UUID factorid;

  /**
   * @author Jarkko Heidenwag
   * 
   */
  public AssessmentOverview() {
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
    
    TableViewerColumn controlActionCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    controlActionCol.getColumn().setText("Control Action"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(controlActionCol.getColumn(), new ColumnWeightData(10, 100, true));
    controlActionCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        if (element instanceof CausalFactor) {
          UUID ucaId = ((CausalFactor) element).getParentUUID();
          ControlAction ca = (ControlAction) AssessmentOverview.this.getStlsaController().getControlActionForUca(ucaId);
          return ca.getTitle();
        }
        else if(element instanceof UnsafeControlAction) {
          return getStlsaController().getControlActionForUca(((UnsafeControlAction) element).getId()).getTitle();
        }
        else if(element instanceof ControlAction) {
          return ((ControlAction) element).getTitle();
        }
        return null;
      }  
    });  
    
    TableViewerColumn ucaTypeCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    ucaTypeCol.getColumn().setText("UCA Type"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(ucaTypeCol.getColumn(), new ColumnWeightData(10, 100, true));
    ucaTypeCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        if (element instanceof CausalFactor) {
          UUID ucaId = ((CausalFactor) element).getParentUUID();
          UnsafeControlAction uca = (UnsafeControlAction) AssessmentOverview.this.getStlsaController().getControlActionController().getUnsafeControlAction(ucaId);
          return uca.getType().toString();
        }
        else if(element instanceof UnsafeControlAction) {
          return ((UnsafeControlAction) element).getType().toString();
        }
        else if(element instanceof ControlAction) {
          return "N.A";
        }
        return null;
      }  
    });
      
    TableViewerColumn ucaIdCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    ucaIdCol.getColumn().setText("UCA ID"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(ucaIdCol.getColumn(), new ColumnWeightData(10, 100, true));
    ucaIdCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        if (element instanceof CausalFactor) {
          UUID ucaId = ((CausalFactor) element).getParentUUID();
          UnsafeControlAction uca = (UnsafeControlAction) AssessmentOverview.this.getStlsaController().getControlActionController().getUnsafeControlAction(ucaId);
          return uca.getIdString();
        }
        else if(element instanceof UnsafeControlAction) {
          return ((UnsafeControlAction) element).getIdString();
        }
        else if(element instanceof ControlAction) {
          return "N.A";
        }
        return null;
      }  
    });
        
    
    TableViewerColumn causalFactorID = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    causalFactorID.getColumn().setText("Causal Factor ID"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(causalFactorID.getColumn(), new ColumnWeightData(10, 100, true));
    causalFactorID.setLabelProvider(new ColumnLabelProvider() {
      
      @Override
      public String getText(Object element) {
        if (element instanceof CausalFactor) {
          return ((CausalFactor) element).getIdString();
        }
        else if(element instanceof UnsafeControlAction) {
          return "N.A";
        }
        else if(element instanceof ControlAction) {
          return "N.A";
        }
        return null;
      }  
    });
    
    TableViewerColumn intentionCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    intentionCol.getColumn().setText("Unintentional/ Intentional Type"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(intentionCol.getColumn(), new ColumnWeightData(10, 100, true));
    intentionCol.setLabelProvider(new ColumnLabelProvider() {
      
      @Override
      public String getText(Object element) {
        if (element instanceof CausalFactor) {
          return ((CausalFactor) element).getIntention();
        }
        else if(element instanceof UnsafeControlAction) {
          return "N.A";
        }
        else if(element instanceof ControlAction) {
          return "N.A";
        }
        return null;
      }  
    });
    
    TableViewerColumn severCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    severCol.getColumn().setText("Severity"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(severCol.getColumn(), new ColumnWeightData(10, 100, true));
    severCol.setLabelProvider(new ColumnLabelProvider() {
      
      @Override
      public String getText(Object element) {

        if (element instanceof CausalFactor) {
          int scale = ((CausalFactor)element).getSubMeasurements("Severity");
          if(scale == -999) {
            return "N.A";
          }
          else{
            return Integer.toString(scale);
          }
        }
        else if(element instanceof UnsafeControlAction) {
          return "N.A";
        }
        else if(element instanceof ControlAction) {
          return "N.A";
        }
        return null;
      }        

    });
        
    TableViewerColumn likelihoodCol = new TableViewerColumn(this.getTableViewer(), SWT.CENTER);
    likelihoodCol.getColumn().setText("Likelihood"); //$NON-NLS-1$
    getTableColumnLayout().setColumnData(likelihoodCol.getColumn(), new ColumnWeightData(10, 100, true));
    likelihoodCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(Object element) {
        if (element instanceof CausalFactor) {
          int scale = ((CausalFactor)element).getSubMeasurements("Likelihood");
          if(scale == -999) {
            return "N.A";
          }
          else{
            return Integer.toString(scale);
          }
        }
        else if(element instanceof UnsafeControlAction) {
          return "N.A";
        }
        else if(element instanceof ControlAction) {
          return "N.A";
        }
        return null;
      }  
    });    
    
    
    this.updateTable();
    getAddNewItemButton().setEnabled(false);
    getAddNewItemButton().setToolTipText(Messages.ControlActionView_1);
    
    // the Button for exporting csv
    Button exportButton = new Button(super.buttonComposite, SWT.PUSH);
    GridData gridData = new GridData(SWT.NONE, SWT.NONE, false, false);
    final int buttonSize = 46;
    gridData.widthHint = buttonSize;
    gridData.heightHint = buttonSize;
    exportButton.setLayoutData(gridData);
    exportButton.setImage(ADD);
    exportButton.addListener(SWT.Selection, new Listener() {

      @Override
      public void handleEvent(Event event) {
        List<ITableModel> cfList = getStlsaController().getAllLinkedCausalFactor();
        System.out.println("test");
        List<List<String>> rows = new ArrayList<List<String>>();
        for(ITableModel element : cfList) {
          List<String> eachRow = new ArrayList<String>();
          if (element instanceof CausalFactor) {

          UUID ucaId = ((CausalFactor) element).getParentUUID();
          ControlAction ca = (ControlAction) AssessmentOverview.this.getStlsaController().getControlActionForUca(ucaId);
          eachRow.add(ca.getTitle());
            
          UnsafeControlAction uca = (UnsafeControlAction) AssessmentOverview.this.getStlsaController().getControlActionController().getUnsafeControlAction(ucaId);
          eachRow.add(uca.getType().toString());
          
          eachRow.add(uca.getIdString());

          eachRow.add(((CausalFactor) element).getIdString());
          eachRow.add(((CausalFactor) element).getIntention());
          
          }
          rows.add(eachRow);
        }

        try {
          FileWriter csvWriter = new FileWriter("exportedcsv.csv");
          csvWriter.append("Control Action");
          csvWriter.append(",");
          csvWriter.append("UCA Type");
          csvWriter.append(",");
          csvWriter.append("UCA ID");
          csvWriter.append(",");
          csvWriter.append("Causal Factor ID");
          csvWriter.append(",");
          csvWriter.append("Intentional/Unintentional");
          csvWriter.append(",");
          csvWriter.append("Severity");
          csvWriter.append(",");
          csvWriter.append("Likelihood");
          csvWriter.append("\n");


        for (List<String> rowData : rows) {
            csvWriter.append(String.join(",", rowData));
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
        }
       catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      }
    });
    
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
    List<ITableModel> cfelements = getStlsaController().getAllLinkedCausalFactor();
    List<ICorrespondingUnsafeControlAction> ucaelements = getStlsaController().getUCAList(null);
    for(ICorrespondingUnsafeControlAction uca : ucaelements) {
      if(getStlsaController().getLinkedCausalFactorOfUCA(uca.getId()).size() == 0) {
        cfelements.add(uca);
      }
    }
    List<IControlAction> CAelements = getStlsaController().getAllControlActions();
    for(IControlAction ca : CAelements) {
      if(ca.getUnsafeControlActions().size() == 0) {
        cfelements.add(ca);
      }
    }
    
    AssessmentOverview.this.getTableViewer().setInput(cfelements);
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
    return AssessmentOverview.ID;
  }

  @Override
  public String getTitle() {
    return "Assessment Overview";
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
    AssessmentOverview.this.getDataInterface().setControlActionDescription(uuid, description);
  }

  @Override
  protected void updateTitle(UUID id, String title) {
    getDataInterface().setControlActionTitle(id, title);
  }
  
  //Helper methods below 
  public void setCausalFactor(UUID CFID, String CFText) {
    ((StlsaController) AssessmentOverview.this.getDataInterface()).setCausalFactorText(CFID, CFText);
  }
  
  public UUID addCausalFactorToUca(UUID CFID, UUID UcaID) {
    System.out.println(CFID.toString());
    UUID factorid = ((StlsaController) AssessmentOverview.this.getDataInterface()).addCausalFactor(CFID, UcaID);
    return factorid;
  }
  public UUID createCausalFactor(String selectedText) {
  StlsaController dataController = ((StlsaController) AssessmentOverview.this.getDataInterface());
  CausalFactorController CFController = (CausalFactorController) dataController.getCausalFactorController();
  UUID CFID = CFController.addCausalFactor(new CausalFactor(selectedText));
  return CFID;
  }
}