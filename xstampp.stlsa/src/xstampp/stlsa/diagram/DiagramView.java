package xstampp.stlsa.diagram;

import xstampp.stlsa.model.StlsaController;
import xstampp.stpapriv.model.controlaction.ControlAction;
import xstampp.stpapriv.model.results.ConstraintResult;

public class DiagramView extends xstampp.stpapriv.diagram.DiagramView {
	public static final String ID = "xstampp.stlsa.diagram";
	StlsaController controller;
	ControlAction controlAction;
  ConstraintResult ca;

  public void setInput(ConstraintResult ca, StlsaController dataInterface) {
    this.controller=dataInterface;
    this.controlAction = ca.getTemp();
    this.ca = ca;    
  }
}
