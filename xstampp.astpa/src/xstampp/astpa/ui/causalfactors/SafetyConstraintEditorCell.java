package xstampp.astpa.ui.causalfactors;

import java.util.UUID;

import xstampp.astpa.model.causalfactor.interfaces.CausalFactorEntryData;
import xstampp.astpa.model.causalfactor.interfaces.ICausalFactorEntry;
import xstampp.astpa.model.interfaces.ICausalFactorDataModel;
import xstampp.ui.common.grid.GridCellTextEditor;
import xstampp.ui.common.grid.GridWrapper;

public class SafetyConstraintEditorCell extends GridCellTextEditor {

  private UUID componentId;
  private UUID factorId;
  private UUID entryId;
  private ICausalFactorDataModel dataInterface;
  
  public SafetyConstraintEditorCell(GridWrapper gridWrapper,ICausalFactorDataModel dataInterface,
                                    UUID componentId,UUID factorId,ICausalFactorEntry entry) {
    super(gridWrapper, entry.getConstraintText(),true, false,factorId);
    this.dataInterface = dataInterface;
    this.componentId = componentId;
    this.factorId = factorId;
    this.entryId = entry.getId();
  }

  @Override
  public void updateDataModel(String newText) {
    CausalFactorEntryData data = new CausalFactorEntryData(entryId);
    data.setConstraint(newText);
    dataInterface.changeCausalEntry(componentId, factorId, data);
    
  }
  
  @Override
  public void delete() {
    CausalFactorEntryData data = new CausalFactorEntryData(entryId);
    data.setConstraint(null);
    dataInterface.changeCausalEntry(componentId, factorId, data);
  }

  @Override
  public void onTextChange(String newValue) {
    updateDataModel(newValue);
  }
}
