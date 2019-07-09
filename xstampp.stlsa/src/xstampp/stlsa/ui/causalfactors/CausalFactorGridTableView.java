/*******************************************************************************
 * Copyright (c) 2013-2017 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

import xstampp.astpa.model.DataModelController;
import xstampp.astpa.model.causalfactor.CausalFactor;
import xstampp.astpa.model.causalfactor.CausalFactorController;
import xstampp.astpa.model.controlaction.UnsafeControlAction;
import xstampp.astpa.model.controlaction.interfaces.IUnsafeControlAction;
import xstampp.astpa.model.controlaction.safetyconstraint.ICorrespondingUnsafeControlAction;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.interfaces.IUnsafeControlActionDataModel;
import xstampp.astpa.ui.unsafecontrolaction.DeleteUcaAction;
import xstampp.astpa.ui.unsafecontrolaction.UnsafeControlActionsView;
import xstampp.model.IDataModel;
import xstampp.model.ITableEntry;
import xstampp.model.ObserverValue;
import xstampp.stlsa.messages.StlsaMessages;
import xstampp.stlsa.model.StlsaController;
import xstampp.stlsa.ui.unsecurecontrolaction.UcaContentProvider;
import xstampp.ui.common.ProjectManager;
import xstampp.ui.common.grid.DeleteGridEntryAction;
import xstampp.ui.common.grid.GridCellLinking;
import xstampp.ui.common.grid.GridCellText;
import xstampp.ui.common.grid.GridRow;
import xstampp.ui.common.grid.SingleGridCellLinking;
import xstampp.usermanagement.api.AccessRights;

/**
 * View used to handle the unsafe control actions.
 * 
 * @author Benedikt Markt, Patrick Wickenhaeuser, Lukas Balzer
 */
public class CausalFactorGridTableView extends UnsafeControlActionsView{

  public static final String UCA1 = "UCA1.";
  /**
	 * ViewPart ID.
	 */
	public static final String ID = "stlsa.steps.step3_2"; //$NON-NLS-1$
  private static final String ID_FILTER = "ID";
	private static final String CA_FILTER="Control Action"; //$NON-NLS-1$
	private static final String UCA_FILTER="unsecure Control Actions"; //$NON-NLS-1$
	private static final String UCAID_FILTER="UCA ID"; //$NON-NLS-1$
	private static final String HAZ_FILTER="Vulnerabilities"; //$NON-NLS-1$
	private static final String NOHAZ_FILTER="not vulnerable"; //$NON-NLS-1$
	private static final String HAZID_FILTER="Vulnerability ID"; //$NON-NLS-1$

  private String[] columns = new String[] {"UCA ID",
    "UCA Description", "Causal Factor ID","Causal factor (Guide word)",
    "Casual factor", "Unintentional/Intentional"};


	/**
	 * Interfaces to communicate with the data model.
	 */
	private CfContentProvider cfContentProvider = null;
  private Composite tableViewer;

	public CausalFactorGridTableView() {
		setUseFilter(true);
	}
	
  /**
   * Constructs an UnsafeControlActionsView with a filter
   * 
   * @param columns
   *          must be a string array of size 4, containing the names of the columns
   */
  public CausalFactorGridTableView(String[] columns) {
    super(columns);
  }

  /**
   * 
   * @param columns
   *          must be a string array of size 4, containing the names of the columns
   * @param useFilter
   *          whether to use the filter or not
   */
  public CausalFactorGridTableView(String[] columns, boolean useFilter) {
    setUseFilter(useFilter);
    this.columns = columns;
  }

	@Override
	protected Map<String, Boolean> getCategories() {
		Map<String, Boolean> categories= new HashMap<>();
    categories.put(ID_FILTER, false);
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
		return new String[]{ID_FILTER,CA_FILTER,UCA_FILTER,UCAID_FILTER,HAZ_FILTER,HAZID_FILTER,NOHAZ_FILTER};
	}
	

	
	/**
	 * User interface components.
	 * 
	 * @author Benedikt Markt, Patrick Wickenhaeuser
	 */
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent,columns);
		this.tableViewer = parent;
//		this.tableViewer.addListener(PROP_INPUT, new Listener() {
//	    public void handleEvent(Event e)
//	    {
//	        Table table = (Table)e.widget;
//	        TableViewer tableViewer = (TableViewer)table.getData("tableViewer");
//	         System.out.print("In Event");
//	        System.out.println(e.toString());
//	    }
//	    });
    updateCausalFactors();
	}
	
	@Override
	public void setDataModelInterface(IDataModel dataInterface) {
	  super.setDataModelInterface(dataInterface);
    this.cfContentProvider = new CfContentProvider(getDataModel());
	}
	@Override
	public DeleteGridEntryAction<IUnsafeControlActionDataModel> getDeleteAction() {
	  return new DeleteUcaAction(getGridWrapper(), getDataModel(),StlsaMessages.UnsecureControlActions,UCA1);
	}
	
	@Override
	protected void fillTable() throws SWTException{
	  List<ICorrespondingUnsafeControlAction> list = getDataModel().getUCAList(null);
		if (list.isEmpty()) {
			return;
		}
		for (ICorrespondingUnsafeControlAction cAction : list) {
			//fiter by the title of the unsafecontrol action
			if(isFiltered(cAction.getTitle(), CA_FILTER)){
				continue;
			}
			GridRow controlActionRow = new GridRow(columns.length,3, new int[] { 0,1 }); 
			//GridRow is not from a library, the last argument specify which column will only have 1 row. In this case, the first 2 columns only has 1 row.
			controlActionRow.addCell(0,new GridCellText(cAction.getIdString()));
	    controlActionRow.addCell(1,new GridCellText(cAction.getDescription()));
	    boolean canWrite = checkAccess(cAction.getId(), AccessRights.WRITE);

	    
	    List<ITableModel> linkedItems = this.cfContentProvider.getLinkedItems(cAction.getId());
	    int maxHeight = linkedItems.size();
	    
	    if(linkedItems.isEmpty()) {
        GridRow cfRows = new GridRow(columns.length,3);
        SingleGridCellLinking<CfContentProvider> cfGridCell = new SingleGridCellLinking<CfContentProvider>(cAction.getId(), this.cfContentProvider, getGridWrapper(), canWrite);     
        cfRows.addCell(3, cfGridCell);
        controlActionRow.addChildRow(cfRows);
	    }
	    else {
      for (int i = 0; i < maxHeight; i++) {
        GridRow cfRows = new GridRow(columns.length,3);
        SingleGridCellLinking<CfContentProvider> cfGridCell = new SingleGridCellLinking<CfContentProvider>(cAction.getId(), this.cfContentProvider, getGridWrapper(), canWrite, i);     
        addRows(linkedItems, cfRows, cfGridCell, i, cAction.getIdString());
        controlActionRow.addChildRow(cfRows);
      }
	    }
      getGridWrapper().addRow(controlActionRow);      
		}
	}


	private void addRows(List<ITableModel> linkedItems,GridRow cfRows, SingleGridCellLinking<CfContentProvider> cfGridCell, int i, String ucaNumber) {
//    while (linkedItems.size() > i ) {
//      linkedItems.remove(i);
//    }
	  if(!linkedItems.isEmpty()) {
	  CausalFactor currentItem = (CausalFactor) linkedItems.get(i);
    cfRows.addCell(2, new GridCellText(currentItem.getIdString(ucaNumber)));
    cfRows.addCell(5, new GridCellText(currentItem.getIntention()));
    }
	  cfRows.addCell(3, cfGridCell);
	  cfRows.addCell(4, new GridCellText("Hello"));
	}
	
	@Override
	public String getId() {
	  ProjectManager.getLOGGER().info("getID()"); //$NON-NLS-1$
		return CausalFactorGridTableView.ID;
	}


	@Override
	public String getTitle() {
		return "Unsecure Control Actions Table"; //$NON-NLS-1$
	}
	
	/**
   * !isFiltered(this.getDataModel().getUCANumber(allWrongTiming.get(i).getId()),UCAID) &&
   * !isFiltered(allWrongTiming.get(i).getDescription(),UCA)
   * 
	 * @param uca
	 * @return true if the uca is filtered out and should not be used
	 */
	private boolean isUCAFiltered(IUnsafeControlAction uca){
		switch (getActiveCategory()) {
		case UCAID_FILTER:
			return isFiltered(this.getDataModel().getUCANumber(uca.getId()),UCAID_FILTER);
		case UCA_FILTER:
			return isFiltered(uca.getDescription(),UCA_FILTER);
		case HAZ_FILTER:
			if(this.getDataModel().getLinkedHazardsOfUCA(uca.getId()).size() == 0){
				return true;
			}
			for(ITableModel model : this.getDataModel().getLinkedHazardsOfUCA(uca.getId())){
          if (!isFiltered(model.getTitle(), HAZ_FILTER)
              || !isFiltered(model.getDescription(), HAZ_FILTER)) {
					return false;
				}
				return true;
			}
		case NOHAZ_FILTER:
			if(this.getDataModel().getLinkedHazardsOfUCA(uca.getId()).size() != 0){
				return true;
			}
		case HAZID_FILTER:
			if(this.getDataModel().getLinkedHazardsOfUCA(uca.getId()).size() == 0){
				return true;
			}
			for(ITableModel model : this.getDataModel().getLinkedHazardsOfUCA(uca.getId())){
				if(!isFiltered(model.getNumber(),HAZID_FILTER)){
					return false;
				}
			}
      return true;
		default:
			return isFiltered(uca.getDescription(),UCA_FILTER);
		}
	}

	private void updateCausalFactors(){
		String[] choices= new String[getCausalFactorController().getCausalFactors().size()];
		String[] choiceIDs= new String[getCausalFactorController().getCausalFactors().size()];
		String[] choiceValues= new String[getCausalFactorController().getCausalFactors().size()];
		int index = 0;
	
		for (ITableModel model : getCausalFactorController().getCausalFactors()) {
			choices[index] = "V-" + model.getNumber() + ": "+ model.getTitle(); //$NON-NLS-1$ //$NON-NLS-2$
			choiceIDs[index] = "" + model.getNumber(); //$NON-NLS-1$
			choiceValues[index++] = model.getTitle();
		}
		this.addChoices(HAZID_FILTER, choices);
		this.addChoiceValues(HAZID_FILTER,choiceIDs);
		this.addChoices(HAZ_FILTER, choices);
		this.addChoiceValues(HAZ_FILTER,choiceValues);
	}

  private CausalFactorController getCausalFactorController() {
    return (CausalFactorController) ((StlsaController) getDataModel()).getCausalFactorController();
  }
  
  @Override
  public void update(Observable dataModelController, Object updatedValue) {

    super.update(dataModelController, updatedValue);
    ObserverValue type = (ObserverValue) updatedValue;;
    switch (type) {
      case UNSAFE_CONTROL_ACTION:
      case CAUSAL_FACTOR:
        updateCausalFactors();
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

  @Override
  public void dispose() {
    this.getDataModel().deleteObserver(this);
    super.dispose();
  }
	
}
