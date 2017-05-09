/*******************************************************************************
 * Copyright (c) 2013, 2017 Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner Institute of Software
 * Technology, Software Engineering Group University of Stuttgart, Germany
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package xstampp.ui.common.shell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import xstampp.util.ColorManager;

/**
 * defines an Application modal shell which should be used for all dialogs in XSTAMPP.
 * 
 * <p>Example implementation:
 * 
 * <pre>
 * public class ExampleShell extends ModalShell {
 * private String content;
 *
 *  public ExampleShell() {
 *    super("Example", "Example"); setSize(300, 200); 
 *  }
 *
 *  &#64;Override
 *  protected void createCenter(Shell shell) {
 *    Label exampleLabel = new Label(shell, SWT.None);
 *    exampleLabel.setText("Example");
 *    exampleLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
 *    Text exampleText = new Text(shell, SWT.None);
 *    exampleText.addModifyListener(new ModifyListener() { //
 *      &#64;Override 
 *      public void modifyText(ModifyEvent e) { 
 *        content = ((Text) e.getSource()).getText();
 *        canAccept(); 
 *      } 
 *    }); 
 *    GridData textData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
 *    nameText.setLayoutData(textData); 
 *  }
 * 
 *  &#64;Override 
 *  protected boolean doAccept() {
 *    return writeContent(); 
 *  } 
 *  
 *  &#64;Override 
 *  protected boolean validate() {
 *    try { 
 *      return !content.isEmpty(); 
 *      } catch (NullPointerException exc) {
 *        invalidate("Input invalid!");
 *        return false; 
 *        } 
 *      }
 * </pre>
 * 
 * @author Lukas Balzer - initial implementation
 *
 */
public abstract class ModalShell {

  /**
   * When this style constant is added to the style of this shell an 'Apply' button is added to this
   * shell which calls {@link #doAccept()}.
   */
  public static final int APPLYABLE = 1 << 2;
  /**
   * When this style constant is added to the style of this shell the content of it is packed to its
   * minimum size after creation.
   */
  public static final int PACKED = 1 << 3;
  private String title;
  private Point size;
  private String acceptLabel;
  private Label invalidLabel;
  private Button okButton;
  private Button applyButton;
  private boolean isAccepted;
  private Object returnValue;
  private int style;

  /**
   * Constructs a ModalShell with he given title and a <code>Cancel</code> Button that closes the
   * shell without calling {@link #doAccept()} and a <code>Ok</code> Button that calls
   * {@link #doAccept()} and closes the application depending on the returned boolean.
   * @param style
   *          One of the integer constants defined in this class <ul> <li> {@link #APPLYABLE} <li>
   *          {@link #PACKED} </ul>
   * 
   * 
   */
  public ModalShell(String title, String acceptLabel, int style) {
    this.title = title;
    this.acceptLabel = acceptLabel;
    this.style = style;
    setSize(300, 200);
    this.isAccepted = false;

  }

  /**
   * Constructs a ModalShell with he given title and a <code>Cancel</code> Button that closes the
   * shell without calling {@link #doAccept()} and a <code>Ok</code> Button that calls
   * {@link #doAccept()} and closes the application depending on the returned boolean.
   * 
   * @param title
   *          the title of the Shell
   * @param acceptLabel
   *          the label of the Button that accepts the entry
   */
  public ModalShell(String title, String acceptLabel) {
    this(title, acceptLabel, 0);

  }

  /**
   * calls {@link #ModalShell(String, String)} with <code>Ok</code> as second argument.
   * 
   * @param title
   *          the title of the Shell
   */
  public ModalShell(String title) {
    this(title, "Ok");
  }

  /**
   * calls {@link #ModalShell(String,integer)} with <code>Ok</code> as second
   * argument.
   */
  public ModalShell(String title, int style) {
    this(title, "Ok", style);
  }

  /**
   * Opens a {@link SWT#APPLICATION_MODAL} shell with the given title and the content defined in
   * {@link #createCenter(Shell)} plus has a cancel and a ok/accept (defined by {@link #acceptLabel}
   * button.
   */
  public void open() {
    final Shell shell = new Shell(Display.getCurrent().getActiveShell(),
        SWT.APPLICATION_MODAL | SWT.SHEET);
    GridLayout gridLayout = new GridLayout(2, false);
    shell.setLayout(gridLayout);
    if (this.size != null) {
      shell.setSize(this.size);
    }
    shell.setMinimumSize(300, 100);
    shell.setText(title);
    createCenter(shell);
    Composite footer = new Composite(shell, SWT.None);
    footer.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, 1));

    footer.setLayout(new GridLayout(3, false));
    this.invalidLabel = new Label(footer, SWT.WRAP);
    invalidLabel.setForeground(ColorManager.COLOR_RED);
    invalidLabel.setText("invalid content!");
    invalidLabel.setVisible(false);
    invalidLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));


    GridData gridData = new GridData(SWT.RIGHT, SWT.BOTTOM, false, false);
    gridData.widthHint = SWT.DEFAULT;
    gridData.heightHint = SWT.DEFAULT;
    if ((this.style & APPLYABLE) != 0) {

      footer.setLayout(new GridLayout(4, false));
      applyButton = new Button(footer, SWT.PUSH);
      applyButton.setText("Apply");
      applyButton.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent ev) {
          doAccept();
        }
      });
      applyButton.setLayoutData(gridData);
      applyButton.setEnabled(false);
    }

    okButton = new Button(footer, SWT.PUSH);
    okButton.setText(acceptLabel);
    okButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent ev) {
        if (doAccept()) {
          isAccepted = true;
          shell.close();
        }
      }
    });
    okButton.setLayoutData(gridData);
    okButton.setEnabled(false);

    Button cancelButton = new Button(footer, SWT.PUSH);
    cancelButton.setText("Cancel");
    cancelButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent ev) {
        shell.close();
      }
    });
    cancelButton.setLayoutData(gridData);
    canAccept();
    if ((this.style & PACKED) != 0) {
      shell.pack();
    }
    shell.open();
    while (!shell.isDisposed()) {
      if (!Display.getDefault().readAndDispatch()) {
        Display.getDefault().sleep();
      }
    }

  }

  /**
   * This method sets the content to be invalid and informs the user by displaying a text label in
   * the footer of the dialog. This text is hidden again when {@link #canAccept()} is called
   * 
   * @param error
   *          a text informing the user why the content is not valid
   */
  public void invalidate(String error) {
    if (!invalidLabel.isDisposed()) {
      this.invalidLabel.setText(error);
      this.invalidLabel.setVisible(true);
    }
  }

  /**
   * This method sets the content to be valid and informs the user by displaying a text label in the
   * footer of the dialog. This text is hidden again when {@link #canAccept()} is called
   * 
   * @param message
   *          a text informing the user of certain actions or things that have been updated
   */
  public void setMessage(String message) {
    if (!invalidLabel.isDisposed()) {
      this.invalidLabel.setForeground(ColorManager.COLOR_GREEN);
      this.invalidLabel.setText(message);
      this.invalidLabel.setVisible(true);
    }
  }

  /**
   * setter for the text in the headline of the shell.
   * 
   * @param acceptLabel
   *          text in the headline of the shell
   */
  public void setAcceptLabel(String acceptLabel) {
    this.acceptLabel = acceptLabel;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setSize(int width, int height) {
    this.size = new Point(width, height);
  }

  /**
   * this method should be called whenever a change is made. It calls {@link #validate()} and sets
   * the ok/accept button (in-)active accordingly
   */
  public final boolean canAccept() {
    try {
      boolean valid = validate();
      this.okButton.setEnabled(valid);
      if ((this.style & APPLYABLE) != 0) {
        this.applyButton.setEnabled(valid);
      }
      setUnchecked();
      return valid;
    } catch (Exception exc) {
      exc.printStackTrace();
      return false;
    }
  }

  /**
   * This method is called by {@link #canAccept()} and should return whether the content is valid or
   * not. The returned value decides whether the ok/accept button is enabled or disabled
   * 
   * @return if the shells content is valid
   */
  protected abstract boolean validate();

  /**
   * This method is called whenever the ok/accept button is pressed. Depending on the return value
   * the modal shell is closed.
   * 
   * <p>if the content can not be accepted and the method returns false, the
   * {@link ModalShell#invalidate(String)} method can be called informing the user about details
   * 
   * @return if the input should be accepted or not
   */
  protected abstract boolean doAccept();

  /**
   * Is called during the shell set up and also gets the main shell as parent with a <b>two column
   * gridLayout</b>.
   * 
   * <p><i>Note: The Layout of the given shell should not be changed by implementations</i>
   * 
   * @param parent
   *          the parent shell which is displayed to the user
   */
  protected abstract void createCenter(Shell parent);

  private final void setUnchecked() {
    if (!invalidLabel.isDisposed()) {
      this.invalidLabel.setVisible(false);
    }
  }

  public boolean isAccepted() {
    return isAccepted;
  }

  public Object getReturnValue() {
    return returnValue;
  }

  protected void setReturnValue(Object returnValue) {
    this.returnValue = returnValue;
  }

  protected final class TextInput {
    private String text = "";

    public TextInput(Shell shell, int style, String label, String initialValue) {
      GridData labelData = new GridData(SWT.FILL, SWT.BOTTOM, true, true, 2, 1);

      Label nameLabel = new Label(shell, SWT.None);
      nameLabel.setText(label);
      nameLabel.setLayoutData(labelData);

      Text nameText = new Text(shell, style);
      nameText.setText(initialValue);
      nameText.addModifyListener(new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent ev) {
          text = ((Text) ev.getSource()).getText();
          canAccept();
        }
      });
      GridData textData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
      nameText.setLayoutData(textData);
    }

    public TextInput(Shell shell, int style, String label) {
      this(shell, style, label, ""); //$NON-NLS-1$
    }

    public String getText() {
      return text;
    }
  }
}