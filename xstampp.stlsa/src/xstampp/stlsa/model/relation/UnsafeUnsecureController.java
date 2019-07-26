package xstampp.stlsa.model.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import xstampp.astpa.model.controlaction.UnsafeControlAction;
import xstampp.astpa.model.controlaction.interfaces.IControlAction;
import xstampp.astpa.model.controlaction.interfaces.IUnsafeControlAction;
import xstampp.astpa.model.interfaces.IExtendedDataModel;
import xstampp.model.IValueCombie;
import xstampp.model.ObserverValue;
import xstampp.stlsa.model.StlsaController;
import xstampp.astpa.model.controlaction.ControlAction;
//import xstampp.stpapriv.model.PrivacyController;
//import xstampp.stpapriv.model.controlaction.ControlAction;
//import xstampp.stpapriv.model.controlaction.UnsecureControlAction;


public class UnsafeUnsecureController extends Observable implements Observer{
	private Map<UUID,ControlEntry> controlentries;
	private Map<UUID,UnsafeControlAction> variablesList;
	private ControlEntry linkedCAE;
	private StlsaController model;
	private boolean controlActionProvided;
	
	public UnsafeUnsecureController(IExtendedDataModel model) {
		this.variablesList = new HashMap<>();
		this.controlentries  = new HashMap<>();
		this.model = (StlsaController) model;
		model.addObserver(this);
		clear();
		
		
	}
	
	public void clear() {
		this.linkedCAE = null;
		this.controlentries.clear();
		if(getModel() != null){
			this.fetchControlActions();
		}
	}
	

		/**
		 * @param includeDontCare TODO
		 * @return the valuesList
		 * @see ProcessModelValue
		 */
		public List<ControlEntry> getValuesList(boolean includeDontCare) {
			ArrayList<ControlEntry> returnedValues = new ArrayList<>();
			this.fetchControlActions();
			for (ControlEntry value : this.controlentries.values()) {
				
					returnedValues.add(value);
				
			}
			Collections.sort(returnedValues);
			return returnedValues;
		}

		public int getValueCount() {
			return this.controlentries.size();
		}
		/**
		 * returns a ProcessModelValue 
		 * 
		 * @param index the index in the list of values
		 * @return the ProcessModelValue stored at the given index
		 * @see ProcessModelValue
		 */
		public ControlEntry getValue(int index) {
			return this.controlentries.get(index);
		}
		/**
		 * @param value the value which is to add to the valuesList
		 */
		public void addValue(ControlEntry value) {
			this.controlentries.put(value.getId(),value);
		}

		public boolean removeValue(int index) {
			return this.controlentries.remove(index) != null;
		}
	/**
	 * @return the variablesList
	 */
	public ArrayList<UnsafeControlAction> getVariablesList() {
		return new ArrayList<UnsafeControlAction>(this.variablesList.values());
	}

	/**
	 * returns a ProcessModelVariables 
	 * 
	 * @param index the index in the list of values
	 * @return the ProcessModelVariable stored at the given index
	 * @see ProcessModelVariables
	 */
	public UnsafeControlAction getVariable(int index) {
		return this.variablesList.get(index);
	}
	
	public int getVariablesCount() {
		return this.variablesList.size();
	}
	/**
	 * @param varible the value which is to add to the valuesList
	 */
	public void addVariable(UnsafeControlAction varible) {
		varible.setNumber(this.variablesList.size()+1);
		this.variablesList.put(varible.getId(),varible);
	}
	
	
	
	/**
	 * this method pulls all control actions and fills the 
	 * list of dependent variables with the information from 
	 * this{@link #fetchProcessComponents(IExtendedDataModel)}
	 * 
	 * @param model the data model which should be used
	 */
	private void fetchControlActions(){
		this.controlentries.clear();
		  // get the controlActions
	      for (IControlAction entry : getModel().getAllControlActionsU()) {
	    	  ControlEntry temp= new ControlEntry();
	    	  ControlAction tempaction= (ControlAction) entry;
	    	  if(!tempaction.getUnsafeControlActions().isEmpty()){
	    		  for(IUnsafeControlAction action: tempaction.getUnsafeControlActions()){
	    			  UnsafeControlAction tempUCA= (UnsafeControlAction) action;
		    		  temp= getEntryFor(entry, getModel().getIvaluesWhenCAProvided(entry.getId()));
//		    		  temp.setPrivacyCritical(tempUCA.isPrivacyCritical);
//		    		  temp.setSafetyCritical(tempUCA.isSafetyCritical);
//		    		  temp.setSecurityCritical(tempUCA.isSecurityCritical);
		    		  temp.setUnsecureControlAction(tempUCA.getDescription());
		    		  temp.setId(tempUCA.getId());
		    		  this.controlentries.put(action.getId(),temp);
		    	  }
	    	  }
	      }

	}

	
	private ControlEntry getEntryFor(IControlAction entry,List<IValueCombie> combies){
		ControlEntry tempCAEntry = new ControlEntry();
  	    ControlAction entry2= (ControlAction)entry;
		tempCAEntry.setComments(entry2.getDescription());
		tempCAEntry.setControlAction(entry2.getTitle());
		tempCAEntry.setNumber(entry2.getNumber());
		tempCAEntry.setIdCA(entry.getId());	    	  

		
		
		return tempCAEntry;
	}


		

		
	//********************************************************************************************************************
	// Management of the control action dependencies																	
			

		/**
		 * @return the dependenciesIFProvided
		 */
		public Collection<ControlEntry> getDependenciesIFProvided() {
			return this.controlentries.values();
		}
		

		/**
		 * 
		 * @param providedContext whether the context is 'provided' or not
		 * @param id the id which is provided by astpa for the requested controlAction
		 * @return the control action entry stored in the context map for the given id
		 */
		public ControlEntry getControlActionEntry(boolean providedContext,UUID id){
			
			return this.controlentries.get(id);
		}
		/**
		 * @return the linkedCAE
		 */
		public ControlEntry getLinkedCAE() {
			return this.linkedCAE;
		}



		public boolean isControlActionProvided(){
			return controlActionProvided;
		}

		/**
		 * @return the model
		 */
		public StlsaController getModel() {
			return this.model;
		}

		/**
		 * @param model the model to set
		 */
		public void setModel(StlsaController model) {
			this.model = (StlsaController) model;
		}

		@Override
		public void update(Observable arg0, Object updatedValue) {
			final ObserverValue value= (ObserverValue) updatedValue; 
			switch(value){
      case LINKING:
      case UNSAFE_CONTROL_ACTION:
      case CONTROL_ACTION:
				  
					new Runnable() {
						@Override
						public void run() {
							setChanged();
							notifyObservers(value);
						}
					}.run();
				default:
					break;
				
			}
		}


}
