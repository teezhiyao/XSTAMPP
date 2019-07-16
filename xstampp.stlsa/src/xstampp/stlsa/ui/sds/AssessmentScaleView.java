/*******************************************************************************
 * Copyright (c) 2013-2017 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam Grahovac, Jarkko
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
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import messages.Messages;
import xstampp.astpa.model.controlaction.safetyconstraint.ICorrespondingUnsafeControlAction;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.interfaces.IUnsafeControlActionDataModel;
import xstampp.astpa.ui.CommonGridView;
import xstampp.astpa.ui.unsafecontrolaction.DeleteUcaAction;
import xstampp.model.IDataModel;
import xstampp.model.ObserverValue;
import xstampp.stlsa.ui.causalfactors.CfContentProvider;
import xstampp.ui.common.ProjectManager;
import xstampp.ui.common.grid.DeleteGridEntryAction;
import xstampp.ui.common.grid.GridCellBlank;
import xstampp.ui.common.grid.GridCellButton;
import xstampp.ui.common.grid.GridCellComboEditor;
import xstampp.ui.common.grid.GridCellEditor;
import xstampp.ui.common.grid.GridRow;
import xstampp.ui.common.grid.GridWrapper;
import xstampp.ui.common.grid.SingleGridCellLinking;

/**
 * View used to handle the unsafe control actions.
 * 
 * @author Benedikt Markt, Patrick Wickenhaeuser, Lukas Balzer
 */
public class AssessmentScaleView extends CommonGridView<IUnsafeControlActionDataModel> {

  public static final String UCA1 = "UCA1."; //$NON-NLS-1$
  /**
   * ViewPart ID.
   */
  public static final String ID = "stlsa.steps.step4_2"; //$NON-NLS-1$

  private static final String CA_FILTER = "Control Action"; //$NON-NLS-1$
  private static final String UCA_FILTER = "unsafe Control Actions"; //$NON-NLS-1$
  private static final String UCAID_FILTER = "UCA ID"; //$NON-NLS-1$
  private static final String HAZ_FILTER = "Hazards"; //$NON-NLS-1$
  private static final String NOHAZ_FILTER = "not hazardous"; //$NON-NLS-1$
  private static final String HAZID_FILTER = "Hazard ID"; //$NON-NLS-1$

  private String[] columns = new String[] {"Severity/Likelihood",
      "Type", "Sub-Measurements","Scale",
      "Details"};  
  /**
   * Constructs an AssessmentScaleView with a filter and the default set of column names
   * defined in the STPA
   */
  public AssessmentScaleView() {
    setUseFilter(true);
  }

  /**
   * Constructs an AssessmentScaleView with a filter
   * 
   * @param columns
   *          must be a string array of size 4, containing the names of the columns
   */
  public AssessmentScaleView(String[] columns) {
    this(columns, true);
  }

  /**
   * 
   * @param columns
   *          must be a string array of size 4, containing the names of the columns
   * @param useFilter
   *          whether to use the filter or not
   */
  public AssessmentScaleView(String[] columns, boolean useFilter) {
    setUseFilter(useFilter);
    this.columns = columns;
  }

  @Override
  protected Map<String, Boolean> getCategories() {
    Map<String, Boolean> categories = new HashMap<>();
    categories.put(CA_FILTER, false);
    categories.put(UCA_FILTER, false);
    categories.put(UCAID_FILTER, false);
    categories.put(HAZ_FILTER, false);
    categories.put(NOHAZ_FILTER, false);
    categories.put(HAZID_FILTER, false);
    return categories;

  }

  @Override
  protected String[] getCategoryArray() {
    return new String[] { CA_FILTER, UCA_FILTER, UCAID_FILTER, HAZID_FILTER,
        NOHAZ_FILTER };
  }

  @Override
  protected void updateFilter() {
    reloadTable();
  }

  /**
   * User interface components.
   * 
   * @author Benedikt Markt, Patrick Wickenhaeuser
   */
  @Override
  public void createPartControl(Composite parent) {
    this.setDataModelInterface(ProjectManager.getContainerInstance()
        .getDataModel(this.getProjectID()));
    super.createPartControl(parent, columns);
//    Button button1 = new Button(parent, SWT.PUSH);
//    Grid grid = new Grid(parent,SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    this.getGridWrapper().setHeaderToolTip("x");
    updateHazards();
    reloadTable();
  }

  @Override
  public void setDataModelInterface(IDataModel dataInterface) {
    super.setDataModelInterface(dataInterface);
    if (columns == null) {
      columns = new String[5];
      columns[0] = Messages.ControlAction;
      for (int i = 0; i < 4; i++) {
        columns[i + 1] = getDataModel().getControlActionController().getUCAHeaders()[i];
      }
    }
  }

  @Override
  public DeleteGridEntryAction<IUnsafeControlActionDataModel> getDeleteAction() {
    return new DeleteUcaAction(getGridWrapper(), getDataModel(), Messages.UnsafeControlActions,
        UCA1);
  }
  @Override
  protected void fillTable() throws SWTException {
    for(int i = 0; i < 2; i++) {
    GridRow controlActionRow = new GridRow(columns.length,3,new int[] {0,1}); 
    addRoww(controlActionRow);
  }

  }

  boolean row1 = false;
  
  public void addRoww(GridRow controlActionRow) {
      
      String[] metricOptions = new String[]{"Severity", "Likelihood"};
      GridCellComboEditor metric = new GridCellComboEditor(getGridWrapper(), metricOptions, true);
      controlActionRow.addCell(0, metric);
      
      String[] metricTypeOptions = new String[]{"Unintentional Causal Scenario", "Intentional Scenario", "Both Unintentional Scenario and Intentional Scenario"};

      GridCellComboEditor metricType = new GridCellComboEditor(getGridWrapper(),metricTypeOptions, false);
      controlActionRow.addCell(1, metricType);
     
      GridRow subMeas = new GridRow(columns.length,3,new int[] {2});
      subMeas.addCell(2, new GridCellEditor(getGridWrapper(), "N.A"));
      subMeas.addCell(2, new addSubButton(controlActionRow));
      
      GridRow details = new GridRow(columns.length,3,new int[] {0,1,2});
      details.addCell(4, new GridCellEditor(getGridWrapper(), "N.A"));
      subMeas.addChildRow(details);
      controlActionRow.addChildRow(subMeas);
      
      
      
    getGridWrapper().addRow(controlActionRow);;      

  }
  
  private class addSubButton extends GridCellButton {
    
    private GridRow controlActionRow;

    public addSubButton(GridRow controlActionRow) {
      this.controlActionRow = controlActionRow;
    }

    @Override
    public void onMouseDown(MouseEvent e, org.eclipse.swt.graphics.Point relativeMouse,
        Rectangle cellBounds) {
      if(e.button == 1){
        GridRow subMeas = new GridRow(columns.length,3,new int[] {2});
        GridCellEditor x = new GridCellEditor(getGridWrapper(), "aaa");
        subMeas.addCell(3, x);
        controlActionRow.addChildRow(subMeas);
//        getGridWrapper().addRow(controlActionRow);;      
        reloadTable();
        ProjectManager.getLOGGER().debug("Add new Sub Measurement");
      }
      
    }
  }
  
  
  @Override
  public String getId() {
    ProjectManager.getLOGGER().info("getID()"); //$NON-NLS-1$
    return AssessmentScaleView.ID;
  }

  @Override
  public String getTitle() {
    return "Unsafe Control Actions Table"; //$NON-NLS-1$
  }

  private void updateHazards() {
    String[] choices = new String[getDataModel().getAllHazards().size()];
    String[] choiceIDs = new String[getDataModel().getAllHazards().size()];
    String[] choiceValues = new String[getDataModel().getAllHazards().size()];
    int index = 0;

    for (ITableModel model : getDataModel().getAllHazards()) {
      choices[index] = "H-" + model.getNumber() + ": " + model.getTitle(); //$NON-NLS-1$ //$NON-NLS-2$
      choiceIDs[index] = "" + model.getNumber(); //$NON-NLS-1$
      choiceValues[index++] = model.getTitle();
    }
    this.addChoices(HAZID_FILTER, choices);
    this.addChoiceValues(HAZID_FILTER, choiceIDs);
    this.addChoices(HAZ_FILTER, choices);
    this.addChoiceValues(HAZ_FILTER, choiceValues);

    ArrayList<String> ucaChoices = new ArrayList<>();
    ArrayList<String> ucaValues = new ArrayList<>();
    for (ICorrespondingUnsafeControlAction uca : getDataModel().getControlActionController()
        .getAllUnsafeControlActions()) {
      ucaChoices.add(uca.getIdString() + " - " + uca.getDescription());
      ucaValues.add("" + uca.getNumber());
    }

    this.addChoices(UCAID_FILTER, ucaChoices.toArray(new String[0]));
    this.addChoiceValues(UCAID_FILTER, ucaValues.toArray(new String[0]));
    this.setUseFilter(NOHAZ_FILTER, false);
  }

  @Override
  public void update(Observable dataModelController, Object updatedValue) {
    System.out.println("updatedValue in assessment: " + updatedValue.toString());
    if (!getGridWrapper().fetchUpdateLock()) {
      super.update(dataModelController, updatedValue);
      ObserverValue type = (ObserverValue) updatedValue;
      switch (type) {
      case UNSAFE_CONTROL_ACTION:
      case LINKING:
      case HAZARD:
        updateHazards();
      case CONTROL_ACTION:
        try {
          this.reloadTable();
        } catch (SWTException e) {
          dataModelController.deleteObserver(this);
        }
        break;

      default:
        break;
      }
    }
  }

  @Override
  public void dispose() {
    this.getDataModel().deleteObserver(this);
    super.dispose();
  }

}
