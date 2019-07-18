/*******************************************************************************
 * Copyright (c) 2013-2016 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam
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

package xstampp.ui.common.grid;

import messages.Messages;
import xstampp.ui.common.grid.GridWrapper.NebulaGridRowWrapper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * A cell that contains an SWT text editor.
 * 
 * @author Patrick Wickenhaeuser, Benedikt Markt
 * 
 */
public class GridCellComboEditor extends GridCellComposite {

  /**
   * The default Text
   * 
   * @author Benedikt Markt
   */
  public static final String EMPTY_CELL_TEXT = Messages.ClickToEdit;

  private GridWrapper grid = null;
  private Composite compositeArea = null;
  private Combo comboCell = null;
  
  
  
  public Combo getComboCell() {
//  comboCell.addSelectionListener(new SelectionAdapter() {
//  @Override
//  public void widgetSelected(SelectionEvent e) {
//    System.out.println("Selection: " + comboCell.getItem(comboCell.getSelectionIndex()));
//  }
//});
//Get the Combo and add selection listener to get the selected Text
    return comboCell;
  }

  private String currentText = ""; //$NON-NLS-1$
  private boolean hasFocus = false;

  /**
   * Ctor.
   * 
   * @author Patrick Wickenhaeuser
   * 
   * @param gridWrapper
   *          the grid.
   * @param initialText
   *          the intitial text in the editor.
   * 
   */
  public GridCellComboEditor(GridWrapper gridWrapper, String[] options, boolean readOnly) {
    super(gridWrapper, SWT.PUSH);

    this.grid = gridWrapper;

    this.compositeArea = new Composite(this, SWT.FILL);
    this.compositeArea.setLayout(new FillLayout(SWT.HORIZONTAL));
    // this.compositeArea.setRedraw(false);
    if(readOnly) {
    this.comboCell = new Combo(this.compositeArea,  SWT.READ_ONLY | SWT.FILL);}
    else {
      this.comboCell = new Combo(this.compositeArea,  SWT.READ_ONLY);
      }
    

    for(String option : options) {
      this.comboCell.add(option);
    }
    comboCell.setText("Default");
    
    // redirect the mouse events
    this.comboCell.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(MouseEvent e) {
        GridCellComboEditor.this.onMouseUp(e);
      }

      @Override
      public void mouseDown(MouseEvent e) {
        // relative mouse position not known here
        GridCellComboEditor.this.onMouseDown(e, null, null);
      }

      @Override
      public void mouseDoubleClick(MouseEvent e) {
        // intentionally empty
      }
    });

//    this.comboCell.addModifyListener(new ModifyListener() {
//
//      @Override
//      public void modifyText(ModifyEvent e) {
//        GridCellComboEditor.this.currentText = GridCellComboEditor.this.comboCell.getText();
//
//        GridCellComboEditor.this.grid.resizeRows();
//      }
//    });

    this.comboCell.addListener(SWT.FocusOut, new Listener() {

      @Override
      public void handleEvent(Event event) {
        GridCellComboEditor.this.onTextChanged(GridCellComboEditor.this.currentText);
        GridCellComboEditor.this.hasFocus = false;
      }
    });

    this.comboCell.addListener(SWT.FocusIn, new Listener() {

      @Override
      public void handleEvent(Event event) {
        GridCellComboEditor.this.hasFocus = true;
        GridCellComboEditor.this.onEditorFocus();
      }
    });

    this.comboCell.addListener(SWT.KeyUp, new Listener() {

      @Override
      public void handleEvent(Event event) {
        if (event.character == SWT.CR) {
          GridCellComboEditor.this.comboCell.traverse(SWT.TRAVERSE_TAB_NEXT);
        }
      }

    });

    this.comboCell.addVerifyListener(new VerifyListener() {

      @Override
      public void verifyText(VerifyEvent event) {
        // verifies if character is no line break
        event.doit = event.character != SWT.CR;

      }

    });
  }

  @Override
  public void paint(GridCellRenderer renderer, GC gc, NebulaGridRowWrapper item) {
    if (this.isDisposed()) {
      return;
    }
    // this.compositeArea.setRedraw(true);
    // this.setRedraw(false);
    renderer.getBounds();
    gc.getClipping();
    this.compositeArea.setBounds(0, 0, renderer.getDrawBounds().width - 1,
        renderer.getDrawBounds().height);
    this.compositeArea.setVisible(true);
    if (this.hasFocus) {
      this.comboCell.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
    } else {
      this.comboCell.setBackground(this.getBackgroundColor(renderer, gc));
    }

    super.paint(renderer, gc, item);
    // this.compositeArea.setRedraw(false);
  }

  @Override
  public void onMouseDown(MouseEvent e, org.eclipse.swt.graphics.Point relativeMouse,
      Rectangle cellBounds) {
    this.activate();
  }

  /**
   * Gets called when the swt.text gets focus
   * 
   * @author Benedikt Markt
   * 
   */
  public void onEditorFocus() {
    // intentionally empty
  }

  @Override
  public int getPreferredHeight() {

    // upper limit

    // lower limit
    int minHeight = AbstractGridCell.DEFAULT_CELL_HEIGHT * 2;
    if (this.comboCell.isDisposed()) {
      return minHeight;
    }
    int textSize = this.comboCell.getSize().x;
    if ((textSize == 0) && (this.grid.getGrid().getColumnCount() > 0)) {
      this.comboCell.setSize(new Point((this.grid.getGrid().getColumn(1).getWidth() - 33), 0));
    }
//    int preferredHeight = (this.description.getLineHeight() * this.description.getLineCount()) + AbstractGridCell.DEFAULT_CELL_HEIGHT;
    this.comboCell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    return Math.max(minHeight, minHeight);
  }

  @Override
  public void activate() {
    this.comboCell.setFocus();
  }

  /**
   * Gets called when the text changed and the cell lost focus.
   * 
   * @author Patrick Wickenhaeuser
   * 
   * @param newText
   *          the new text.
   * 
   */
  public void onTextChanged(String newText) {
    // intentionally empty
    System.out.println("newText: " + newText);
    System.out.println("Combo Text: "+ this.getComboCell().getText());
  }

  /**
   * Get the composite area filling the cell.
   * 
   * @author Patrick Wickenhaeuser
   * 
   * @return the composite area filling the cell.
   */
  protected Composite getCompositeArea() {
    return this.compositeArea;
  }

  /**
   * Get the text editor.
   * 
   * @author Patrick Wickenhaeuser
   * 
   * @return the text editor.
   */
  public Combo getTextEditor() {

    return this.comboCell;
  }

}