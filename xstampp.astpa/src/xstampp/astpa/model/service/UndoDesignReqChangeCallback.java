package xstampp.astpa.model.service;

import xstampp.astpa.haz.ITableModel;
import xstampp.astpa.model.interfaces.IDesignRequirementViewDataModel;
import xstampp.model.ObserverValue;

public class UndoDesignReqChangeCallback extends UndoTableModelChangeCallback<IDesignRequirementViewDataModel> {

  public UndoDesignReqChangeCallback(IDesignRequirementViewDataModel dataModel, ITableModel model) {
    super(dataModel, model);
  }

  @Override
  protected void undoDescription(String description) {
    getDataModel().setDesignRequirementDescription(getEntryId(), description);
  }

  @Override
  protected void undoTitle(String title) {
    getDataModel().setDesignRequirementTitle(getEntryId(), title);
  }

  @Override
  public ObserverValue getChangeConstant() {
    return ObserverValue.DESIGN_REQUIREMENT;
  }


}