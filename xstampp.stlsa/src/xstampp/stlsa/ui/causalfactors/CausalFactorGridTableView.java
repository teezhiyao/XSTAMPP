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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.UUID;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import xstampp.astpa.model.causalfactor.CausalFactor;
import xstampp.astpa.model.causalfactor.CausalFactorController;
import xstampp.astpa.model.controlaction.UnsafeControlAction;
import xstampp.astpa.model.controlaction.interfaces.IUnsafeControlAction;
import xstampp.astpa.model.controlaction.safetyconstraint.ICorrespondingUnsafeControlAction;
import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.interfaces.IUnsafeControlActionDataModel;
import xstampp.astpa.model.submeasurement.SubMeasurement;
import xstampp.astpa.ui.unsafecontrolaction.DeleteUcaAction;
import xstampp.astpa.ui.unsafecontrolaction.UnsafeControlActionsView;
import xstampp.model.IDataModel;
import xstampp.model.ObserverValue;
import xstampp.stlsa.messages.StlsaMessages;
import xstampp.stlsa.model.StlsaController;
import xstampp.ui.common.ProjectManager;
import xstampp.ui.common.grid.DeleteGridEntryAction;
import xstampp.ui.common.grid.GridCellButton;
import xstampp.ui.common.grid.GridCellComboEditor;
import xstampp.ui.common.grid.GridCellEditor;
import xstampp.ui.common.grid.GridCellText;
import xstampp.ui.common.grid.GridCellTextEditor;
import xstampp.ui.common.grid.GridRow;
import xstampp.ui.common.grid.GridWrapper;
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
		boolean modify = true;
    super.createPartControl(parent,columns);
		this.tableViewer = parent;
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
		for (ICorrespondingUnsafeControlAction uca : list) {
			//fiter by the title of the unsafecontrol action
			if(isFiltered(uca.getTitle(), CA_FILTER)){
				continue;
			}
			GridRow controlActionRow = new GridRow(columns.length,3, new int[] { 0,1 }); 
			controlActionRow.addCell(0,new GridCellText(uca.getIdString()));
	    controlActionRow.addCell(1,new GridCellText(uca.getDescription()));
	    boolean canWrite = checkAccess(uca.getId(), AccessRights.WRITE);

      List<ITableModel> ITablecorresCF = getStlsaController().getLinkedCausalFactorOfUCA(uca.getId());
      List<UUID> corresCF = new ArrayList<>();
      for(ITableModel cf : ITablecorresCF) {
        corresCF.add(((CausalFactor)cf).getId());
      }
	    
      for(int y = 0; y < corresCF.size(); y++) {
        final UUID currentCFUUID = corresCF.get(y);
        CausalFactor currentCf = (CausalFactor) getStlsaController().getCausalFactor(corresCF.get(y));
        
        GridRow cfChildRow = new GridRow(columns.length,3);
        
        //Column 2 - CausalFactor ID
        GridCellText cFId = new GridCellText(currentCf.getIdString(),currentCFUUID);
        cfChildRow.addCell(2, cFId);
        
        //Column 3 - CausalFactor GuideWord/Title
        ArrayList<String> guideWords = new ArrayList<String>();
        for (CausalFactorEnum CF : CausalFactorEnum.values()) { 
          guideWords.add(CF.getLabel());
        }
        GridCellComboEditor cfGuide = new GridCellComboEditor(getGridWrapper(), guideWords, false) {
          @Override
          public void onTextChanged(String newText) {
            System.out.println("Combo Text: "+ this.getComboCell().getText());
            CausalFactor currentCf = (CausalFactor) getStlsaController().getCausalFactor(currentCFUUID);
            currentCf.setTitle(this.getComboCell().getText());
//            this.getComboCell().setText(newText);
          }
        };
        cfChildRow.addCell(3, cfGuide);
       
        //Column 4 - CausalFactor Description
        String cfDescription = currentCf.getDescription();
        GridCellEditor cfDescEditor = new GridCellEditor(getGridWrapper(), cfDescription) {
          @Override
          public void onTextChanged(String newText) {
            System.out.println("CausalFactor Description change:" + newText);
            CausalFactor currentCf = (CausalFactor) getStlsaController().getCausalFactor(currentCFUUID);
            currentCf.setDescription(newText);
          }
        };       
        cfChildRow.addCell(4, cfDescEditor);
        
        //Column 5 
        GridCellComboEditor cfIntention = new GridCellComboEditor(getGridWrapper(), new String[]{"Intentional", "Unintentional"}, true) {
          @Override
          public void onTextChanged(String newText) {
            System.out.println("new CausalFactor Intention: "+ this.getComboCell().getText());
            CausalFactor currentCf = (CausalFactor) getStlsaController().getCausalFactor(currentCFUUID);
            currentCf.setIntention(this.getComboCell().getText());    
//            this.getComboCell().setText(newText);
          }
        };
        
        System.out.println("Title: " + currentCf.getTitle() + "Intention: " + currentCf.getIntention());
        cfGuide.getComboCell().setText(currentCf.getTitle());
        cfIntention.getComboCell().setText(currentCf.getIntention());
        
        cfChildRow.addCell(5, cfIntention);

        controlActionRow.addChildRow(cfChildRow);
      }
	    addNewCausalFactorRow(controlActionRow, (UnsafeControlAction) uca);
		}
	}

  public void addNewCausalFactorRow(GridRow controlActionRow, UnsafeControlAction uca) {
    
    GridRow addCausalFactorRow = new GridRow(columns.length,3);
    addCausalFactorRow.addCell(2, new addNewCausalFactorRowButton(uca));
    controlActionRow.addChildRow(addCausalFactorRow);
    getGridWrapper().addRow(controlActionRow);;      

}
	
  private class addNewCausalFactorRowButton extends GridCellButton {
    
    private UnsafeControlAction uca;

    public addNewCausalFactorRowButton(UnsafeControlAction uca) {
      super("Add New Causal Factor");
      this.uca = uca;
    }

    @Override
    public void onMouseDown(MouseEvent e, org.eclipse.swt.graphics.Point relativeMouse,
        Rectangle cellBounds) {
      if(e.button == 1){
        CausalFactor newCF = new CausalFactor("Manipulated Operation", "Intentional");
        newCF.setParentUUID(this.uca.getId());
        getCausalFactorController().addCausalFactor(newCF);
        getStlsaController().addUCACausalFactorLink(uca.getId(), newCF.getId());
        reloadTable();
        ProjectManager.getLOGGER().debug("Add new Sub Measurement");
      }
      
    }
  }
	
	private void addRows(List<ITableModel> linkedItems,GridRow cfRows, SingleGridCellLinking<CfContentProvider> cfGridCell, int i, String ucaNumber) {
//    while (linkedItems.size() > i ) {
//      linkedItems.remove(i);
//    }
	  if(!linkedItems.isEmpty()) {
	  CausalFactor currentItem = (CausalFactor) linkedItems.get(i);
    cfRows.addCell(2, new GridCellText(currentItem.setIdString(ucaNumber)));
    CausalFactorCell editor = new CausalFactorCell(getGridWrapper(),currentItem.getDescription(), currentItem.getId(), true);
    cfRows.addCell(4,editor);
    cfRows.addCell(5, new GridCellText(currentItem.getIntention()));
    }
	  
	  cfRows.addCell(3, cfGridCell);

//    String intention = "Intended";
//    String title = "Testing add button";
//    String message = "Click to make new CF";
//    cfRows.addCell(6, new AddNewCfButton(message, title, intention ));

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

  public StlsaController getStlsaController() {
    return ((StlsaController) getDataModel());
  }
  
  private CausalFactorController getCausalFactorController() {
    return (CausalFactorController) ((StlsaController) getDataModel()).getCausalFactorController();
  }
  @Override
  protected void addCausalFactor(String title,String intention) {
    getCausalFactorController().addCausalFactor(new CausalFactor(title, intention));

  }

  
  @Override
  public void update(Observable dataModelController, Object updatedValue) {
    super.update(dataModelController, updatedValue);
    System.out.println("updatedValue in CFGridTable: " + updatedValue.toString());
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
  
  private class CausalFactorCell extends GridCellTextEditor {

    public CausalFactorCell(GridWrapper grid, String initialText, UUID cf,
        boolean canDelete) {
      super(grid, initialText, cf);
      setShowDelete(canDelete);
      setReadOnly(!canDelete);
    }

    @Override
    public void delete() {
      deleteEntry();
    }

    @Override
    public void updateDataModel(String newValue) {
      CausalFactor currentCf = (CausalFactor) ((StlsaController) getDataModel()).getCausalFactorController().getCausalFactor(getUUID());
      currentCf.setDescription(newValue);
    }

    @Override
    protected void editorOpening() {
      getDataModel().lockUpdate();
    }

    @Override
    protected void editorClosing() {
      getDataModel().releaseLockAndUpdate(new ObserverValue[] {});
    }
  }
  
}
