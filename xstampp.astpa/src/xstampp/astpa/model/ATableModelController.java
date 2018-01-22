package xstampp.astpa.model;

import java.util.Observable;

import xstampp.astpa.model.interfaces.ITableModel;
import xstampp.astpa.model.service.UndoTextChange;
import xstampp.model.ObserverValue;

public abstract class ATableModelController extends Observable {

  public ATableModelController() {
  }

  private boolean setModelText(ITableModel model, String newText, ObserverValue value, boolean changeTitle) {
    if (model != null) {
      boolean changed;
      String oldText;
      if (changeTitle) {
        changed = !newText.equals(((ATableModel) model).getTitle());
        oldText = ((ATableModel) model).setTitle(newText);
      } else {
        changed = !newText.equals(((ATableModel) model).getDescription());
        oldText = ((ATableModel) model).setDescription(newText);
      }
      if (changed) {
        UndoTextChange textChange = new UndoTextChange(oldText, newText, value);
        textChange.setConsumer((text) -> setModelText(model, text, value, changeTitle));
        setChanged();
        notifyObservers(textChange);
        return true;
      }
    }
    return false;
  }

  public boolean setModelTitle(ITableModel model, String newText, ObserverValue value) {
    return setModelText(model, newText, value, true);
  }

  public boolean setModelDescription(ITableModel model, String newText, ObserverValue value) {
    return setModelText(model, newText, value, false);
  }
}