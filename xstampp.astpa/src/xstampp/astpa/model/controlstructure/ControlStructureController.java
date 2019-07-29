/*******************************************************************************
 * Copyright (c) 2013, 2017 A-STPA Stupro Team Uni Stuttgart (Lukas Balzer, Adam Grahovac, Jarkko
 * Heidenwag, Benedikt Markt, Jaqueline Patzek, Sebastian Sieber, Fabian Toth, Patrick Wickenhäuser,
 * Aliaksei Babkovich, Aleksander Zotov).
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/

package xstampp.astpa.model.controlstructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import xstampp.astpa.model.controlstructure.components.Anchor;
import xstampp.astpa.model.controlstructure.components.CSConnection;
import xstampp.astpa.model.controlstructure.components.Component;
import xstampp.astpa.model.controlstructure.components.ComponentType;
import xstampp.astpa.model.controlstructure.components.ConnectionType;
import xstampp.astpa.model.controlstructure.interfaces.IComponent;
import xstampp.astpa.model.controlstructure.interfaces.IConnection;
import xstampp.astpa.model.controlstructure.interfaces.IRectangleComponent;
import xstampp.astpa.preferences.ASTPADefaultConfig;
import xstampp.model.ObserverValue;

/**
 * Controller-class for working with the control structure diagram
 * 
 * @author Fabian Toth
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ControlStructureController extends Observable {

  @XmlElement(name = "component")
  private Component root;

  @XmlElementWrapper(name = "rootComponents")
  @XmlElement(name = "root")
  private List<Component> rootComponents;

  @XmlElementWrapper(name = "connections")
  @XmlElement(name = "connection")
  private List<CSConnection> connections;

  private final Map<UUID, IRectangleComponent> componentTrash;
  private final Map<UUID, Integer> componentIndexTrash;
  private final Map<UUID, IConnection> connectionTrash;
  private final Map<UUID, List<UUID>> removedLinks;
  private Component activeRoot;

  @XmlTransient
  private boolean changed = true;

  @XmlTransient
  private PMSTATE step2Initialiesed = PMSTATE.UNKNOWN;

  @XmlAttribute
  private Boolean useMultiRoots;

  private enum PMSTATE {
    UNKNOWN, INITIALISED, UNTOUCHED;
  }

  /**
   * Constructor of the control structure controller
   * 
   * @author Fabian Toth
   */
  public ControlStructureController() {
    this(false);
  }

  public ControlStructureController(boolean testable) {
    if (!testable) {
      useMultiRoots = ASTPADefaultConfig.getInstance().USE_MULTI_CONTROL_STRUCTURES;
    }
    this.connections = new ArrayList<>();
    this.rootComponents = new ArrayList<>();
    this.rootComponents.add(new Component("level 0", new Rectangle(), ComponentType.ROOT));
    this.componentIndexTrash = new HashMap<>();
    this.componentTrash = new HashMap<>();
    this.connectionTrash = new HashMap<>();
    this.removedLinks = new HashMap<>();
  }

  /**
   * Adds a new component to a parent with the given values.
   * 
   * @param parentId
   *          the id of the parent
   * @param layout
   *          the layout of the new component
   * @param text
   *          the text of the new component
   * @param type
   *          the type of the new component
   * @param index
   *          the index at which the component should be included
   * @return the id of the created component. Null if the component could not be added
   * 
   * @author Fabian Toth
   */
  public UUID addComponent(UUID parentId, Rectangle layout, String text, ComponentType type,
      Integer index) {
    return addComponent(null, parentId, layout, text, type, index);
  }

  /**
   * Adds a new component to a parent with the given values.
   * 
   * @param controlActionId
   *          an id of a ControlAction
   * @param parentId
   *          the id of the parent
   * @param layout
   *          the layout of the new component
   * @param text
   *          the text of the new component
   * @param type
   *          the type of the new component
   * @param index
   *          the index at which the component should be included
   * @return the id of the created component. Null if the component could not be added
   * @author Fabian Toth,Lukas Balzer
   */
  public UUID addComponent(UUID controlActionId, UUID parentId, Rectangle layout, String text,
      ComponentType type, Integer index) {
    Component newComp = new Component(controlActionId, text, layout, type);
    IComponent parent = this.getInternalComponent(parentId);
    if (parent != null) {
      ((Component) parent).addChild(newComp, index);
      return newComp.getId();
    }
    return null;
  }

  /**
   * Creates a new root with the given values.
   * 
   * @param layout
   *          the layout of the new component
   * @param text
   *          the text of the new component
   * @return the id of the created component. Null if the component could not be added
   * 
   * @author Fabian Toth
   */
  public UUID setRoot(Rectangle layout, String text) {
    Component newComp = new Component(text, layout, ComponentType.ROOT);
    this._internalGetRoots().add(newComp);
    setActiveRoot(newComp.getId());
    return newComp.getId();
  }

  /**
   * returns whether or not the dataModel controlled by this controller has been changed and sets
   * the changed flag to false if there are changes
   * 
   * @author Lukas Balzer
   */
  public boolean hasChanges() {
    if (changed) {
      changed = false;
      return true;
    }
    return false;
  }

  /**
   * Searches for the component with the given id and changes the layout of it
   * 
   * @param componentId
   *          the id of the component
   * @param layout
   *          the new text
   * @param step1
   *          if the layout of step 1 should be changed
   * @return true if the text could be changed
   * 
   * @author Fabian Toth
   * 
   */
  public boolean changeComponentLayout(UUID componentId, Rectangle layout, boolean step1) {
    // every time the layout is changed the controller checks if both
    // steps have been initialized and if not synchronizes the two layouts
    if (!step1) {
      step2Initialiesed = PMSTATE.INITIALISED;
    }
    // if it is unkown if the step two was initially opened till now,
    // than this is calculated by comparing the layouts of the two steps
    else if (this.step2Initialiesed.equals(PMSTATE.UNKNOWN) && step1) {
      this.step2Initialiesed = PMSTATE.UNTOUCHED;
      for (IRectangleComponent child : _getActiveRoot().getChildren()) {
        if (!child.getLayout(true).equals(child.getLayout(false))) {
          step2Initialiesed = PMSTATE.INITIALISED;
          break;
        }
      }
    }

    Component component = this.getInternalComponent(componentId);
    if (component != null) {
      component.setLayout(layout, step1);
      if (step2Initialiesed.equals(PMSTATE.UNTOUCHED)) {
        component.setLayout(layout, !step1);
      }
      changed = true;
      return true;
    }
    return false;
  }

  /**
   * Searches for the component with the given id and changes the text of it
   * 
   * @param componentId
   *          the id of the component
   * @param text
   *          the new text
   * @return true if the text could be changed
   * 
   * @author Fabian Toth, Lukas Balzer
   */
  public boolean changeComponentText(UUID componentId, String text) {
    Component component = this.getInternalComponent(componentId);
    return component != null && component.setText(text);
  }

  /**
   * Searches recursively for the component with the given id and removes it
   * 
   * @param componentId
   *          the id of the component to delete
   * @return true if this controller contained the specified element
   * 
   * @author Fabian Toth
   */
  public boolean removeComponent(UUID componentId) {
    if (componentId != null) {
      Component component = this.getInternalComponent(componentId);
      this.removeAllLinks(componentId);
      this.componentTrash.put(componentId, component);
      this.componentIndexTrash.put(componentId,
          this._getActiveRoot().getChildren().indexOf(component));
      if (this._getActiveRoot().removeChild(componentId)) {
        changed = true;
      } else if (_getActiveRoot().getId().equals(componentId)
          && rootComponents.remove(activeRoot)) {
        changed = true;
      }
    }
    return changed;
  }

  public boolean moveEntry(boolean allWay, boolean moveUp, UUID id) {
    return _getActiveRoot().moveComponent(allWay, moveUp, id);
  }

  /**
   * This methode recovers a Component which was deleted before, from the componentTrash
   * 
   * @author Lukas Balzer
   * 
   * @param parentId
   *          the id of the parent
   * @param componentId
   *          the id of the component to recover
   * @return whether the component could be recoverd or not
   */
  public boolean recoverComponent(UUID parentId, UUID componentId) {
    if (this.componentTrash.containsKey(componentId)) {
      Component parent = this.getInternalComponent(parentId);
      boolean success = parent.addChild((Component) this.componentTrash.get(componentId),
          this.componentIndexTrash.get(componentId));
      this.componentTrash.remove(componentId);
      if (this.removedLinks.containsKey(componentId)) {
        for (UUID connectionId : this.removedLinks.get(componentId)) {
          this.recoverConnection(connectionId);
        }
        this.removedLinks.remove(componentId);
      }
      if (success) {
        changed = true;
        return true;
      }
    }

    return false;

  }

  /**
   * Searches recursively for the component with the given id
   * 
   * @param componentId
   *          the id of the child
   * @return the component with the given id or null
   * 
   * @author Fabian Toth
   */
  public IRectangleComponent getComponent(UUID componentId) {
    return this._getActiveRoot().getChild(componentId);
  }

  /**
   * Gets all components of the root level
   * 
   * @return the the components
   * 
   * @author Fabian Toth
   */
  public IRectangleComponent getRoot() {
    return this.internalRoot(0);
  }

  public List<IRectangleComponent> getRoots() {
    List<IRectangleComponent> apiRoots = new ArrayList<>();
    apiRoots.addAll(_internalGetRoots());
    return apiRoots;
  }

  /**
   * Adds a new connection with the given values
   * 
   * @param sourceAnchor
   *          the anchor at the source component
   * @param targetAnchor
   *          the anchor at the target component
   * @param connectionType
   *          the type of the connection
   * @return the id of the new connection
   * 
   * @author Fabian Toth
   */
  public UUID addConnection(Anchor sourceAnchor, Anchor targetAnchor,
      ConnectionType connectionType) {
    CSConnection newConn = new CSConnection(sourceAnchor, targetAnchor, connectionType);
    this.connections.add(newConn);
    return newConn.getId();
  }

  /**
   * Searches for the connection with the given id and changes the connection type to the new value
   * 
   * @param connectionId
   *          the id of the connection to change
   * @param connectionType
   *          the new connection type
   * @return true if the connection type could be changed
   * 
   * @author Fabian Toth
   */
  public boolean changeConnectionType(UUID connectionId, ConnectionType connectionType) {
    IConnection connection = this.getConnection(connectionId);
    if (connection != null) {
      ((CSConnection) connection).setConnectionType(connectionType);
      changed = true;
      return true;
    }
    return false;
  }

  /**
   * 
   * @param componentId
   *          The id with which the component is registered in the data model
   * @param oldParentId
   *          the id of the component which served as old parent component
   * @param newParentId
   *          the id of the component which should serve as new parent component
   * @param layoutStep0
   *          the layout the component should have in the fundamentals step after the move
   * @param layoutStep2
   *          the layout the component should have in stpa step 2 after the move
   * @return true if a component was found for the given componentId and it could be set as child of
   *         the new parent
   */
  public boolean changeComponentParent(UUID componentId, UUID oldParentId, UUID newParentId,
      Rectangle layoutStep0, Rectangle layoutStep2) {
    Component component = getInternalComponent(componentId);

    Component parentComp = getInternalComponent(newParentId);
    UndoChangeParentCallback callback = new UndoChangeParentCallback(this, componentId, oldParentId,
        newParentId);

    if (parentComp != null && removeComponent(componentId)) {
      Rectangle oldLayoutStep1 = component.getLayout(true);
      Rectangle oldLayoutStep2 = component.getLayout(false);

      Rectangle newLayoutStep1 = layoutStep0;
      Rectangle newLayoutStep2 = layoutStep2;
      callback.setNewLayout(newLayoutStep1, newLayoutStep2);
      callback.setOldLayout(oldLayoutStep1, oldLayoutStep2);
      component.setLayout(newLayoutStep1, true);
      component.setLayout(newLayoutStep2, false);
      if (parentComp.addChild(component, -1)) {
        if (this.removedLinks.containsKey(componentId)) {
          for (UUID connectionId : this.removedLinks.get(componentId)) {
            this.recoverConnection(connectionId);
          }
          this.removedLinks.remove(componentId);
        }
        setChanged();
        notifyObservers(ObserverValue.CONTROL_STRUCTURE);
        return true;
      }

    }
    return false;
  }

  public boolean addBendPoint(UUID connectionId, int x, int y) {
    IConnection connection = getConnection(connectionId);
    boolean result = ((CSConnection) connection).addBendPoint(x, y);
    if (result) {
      setChanged();
      notifyObservers(ObserverValue.CONTROL_STRUCTURE);
    }
    return result;
  }

  public boolean removeBendPoint(UUID connectionId, int x, int y) {
    IConnection connection = getConnection(connectionId);
    boolean result = ((CSConnection) connection).removeBendPoint(x, y);
    if (result) {
      setChanged();
      notifyObservers(ObserverValue.CONTROL_STRUCTURE);
    }
    return result;
  }

  public boolean changeBendPoint(UUID connectionId, int oldX, int oldY, int x, int y) {
    IConnection connection = getConnection(connectionId);
    boolean result = ((CSConnection) connection).removeBendPoint(oldX, oldY);
    if (result) {
      ((CSConnection) connection).addBendPoint(x, y);
    }
    if (result) {
      setChanged();
      notifyObservers(ObserverValue.CONTROL_STRUCTURE);
    }
    return ((CSConnection) connection).addBendPoint(x, y);
  }

  public List<Point> getBendPoints(UUID connectionId) {
    IConnection connection = getConnection(connectionId);
    return ((CSConnection) connection).getBendPoints();
  }

  /**
   * Searches for the connection with the given id and changes the targetId to the new value
   * 
   * @param connectionId
   *          the id of the connection to change
   * @param targetAnchor
   *          the new source anchor
   * @param withPm
   *          <b><span style="color:blue;">true</span></b> if the given orientation is meant for the
   *          visualization of the control structure with process models<br>
   *          <b><span style="color:blue;">false</span></b> otherwise
   * @return true if the targetId could be changed
   * 
   * @author Fabian Toth
   */
  public boolean changeConnectionTarget(UUID connectionId, Anchor targetAnchor, boolean withPm) {
    IConnection connection = this.getConnection(connectionId);
    if (connection != null) {
      ((CSConnection) connection).setTargetAnchor(targetAnchor, withPm);
      changed = true;
      return true;
    }
    return false;
  }

  /**
   * Searches for the connection with the given id and changes the sourceId to the new value
   * 
   * @param connectionId
   *          the id of the connection to change
   * @param sourceAnchor
   *          the new source anchor
   * @param withPm
   *          <b><span style="color:blue;">true</span></b> if the given orientation is meant for the
   *          visualization of the control structure with process models<br>
   *          <b><span style="color:blue;">false</span></b> otherwise
   * @return true if the sourceId could be changed
   * 
   * @author Fabian Toth
   */
  public boolean changeConnectionSource(UUID connectionId, Anchor sourceAnchor, boolean withPm) {
    IConnection connection = this.getConnection(connectionId);
    if (connection != null) {
      ((CSConnection) connection).setSourceAnchor(sourceAnchor, withPm);
      return true;
    }
    return false;
  }

  /**
   * Deletes the connection with the given id
   * 
   * @param connectionId
   *          the id of the connection
   * @return true if this component contained the specified element
   * 
   * @author Fabian Toth
   */
  public boolean removeConnection(UUID connectionId) {
    IConnection connection = this.getConnection(connectionId);
    if (this.connections.remove(connection)) {
      this.connectionTrash.put(connectionId, connection);
      return true;
    }
    return false;
  }

  /**
   * This methode recovers a Connection which was deleted before, from the connectionTrash
   * 
   * @author Lukas Balzer
   * 
   * @param connectionId
   *          the id of the component to recover
   * @return whether the Connection could be recovered or not
   */
  public boolean recoverConnection(UUID connectionId) {
    if (this.connectionTrash.containsKey(connectionId)) {
      boolean success = this.connections.add((CSConnection) this.connectionTrash.get(connectionId));
      this.connectionTrash.remove(connectionId);
      if (success) {
        changed = true;
        return true;
      }
    }
    return false;

  }

  /**
   * Gets the connection with the given id
   * 
   * @param connectionId
   *          the id of the connection
   * @return the connection with the given id
   * 
   * @author Fabian Toth
   */
  public IConnection getConnection(UUID connectionId) {
    for (IConnection connection : this.connections) {
      if (connection.getId().equals(connectionId)) {
        return connection;
      }
    }
    return null;
  }

  /**
   * Removes all links that are connected to the component with the given id
   * 
   * @author Fabian Toth,Lukas Balzer
   * 
   * @param componentId
   *          the id of the component
   * @return true if the connections have been deleted
   */
  private boolean removeAllLinks(UUID componentId) {
    List<IConnection> connectionList = new ArrayList<>();
    this.removedLinks.put(componentId, new ArrayList<UUID>());
    for (CSConnection connection : this.connections) {
      if (connection.connectsComponent(componentId)) {
        UUID tmpID = connection.getId();
        connectionList.add(connection);
        this.connectionTrash.put(tmpID, connection);
        this.removedLinks.get(componentId).add(tmpID);
      }
    }
    return this.connections.removeAll(connectionList);
  }

  /**
   * Gets all connections of the control structure diagram
   * 
   * @author Fabian Toth
   * 
   * @return all connections
   */
  public List<IConnection> getConnections() {
    List<IConnection> result = new ArrayList<>();
    for (CSConnection connection : this.connections) {
      result.add(connection);
    }
    return result;
  }

  /**
   * Gets all components of an internal type. Do not use outside the data model.
   * 
   * @author Fabian Toth
   * 
   * @return all components
   */
  public List<Component> getInternalComponents() {
    return this._getActiveRoot().getInternalChildren();
  }

  /**
   * Overwrites the layout of step3 with the layout of step1
   * 
   * @author Lukas Balzer
   * 
   * @param id
   *          the id of the component
   * @return true, if the layout has been synchronized
   */
  public boolean sychronizeLayout() {
    boolean result = false;
    for (CSConnection connection : this.connections) {
      result = connection.synchronizeAnchors() || result;
    }
    for (Component child : this._getActiveRoot().getInternalChildren()) {
      result = child.sychronizeLayout() || result;
    }
    if (result) {
      step2Initialiesed = PMSTATE.UNTOUCHED;
      return true;
    }
    return false;
  }

  /**
   * 
   * @author Lukas Balzer
   * 
   * @return the amount of components currently in the trash
   */
  public int getComponentTrashSize() {
    return this.componentTrash.size();
  }

  /**
   * 
   * @author Lukas Balzer
   * 
   * @return the amount of components currently in the trash
   */
  public int getConnectionTrashSize() {
    return this.connectionTrash.size();
  }

  /**
   * is called the first time the cs is opened sets a boolean which indicates that the 1. step must
   * be initialized
   *
   * @author Lukas Balzer
   *
   */
  public void initializeCSS() {
  }

  /**
   * this funktion
   * 
   * @param componentId
   *          the id of the component
   * @return the relative of the component which belongs to the given id
   */
  public UUID getRelativeOfComponent(UUID componentId) {
    return getInternalComponent(componentId).getRelative();
  }

  /**
   * @param componentId
   *          the id of the component
   * @param relativeId
   *          the relative to set
   */
  public void setRelativeOfComponent(UUID componentId, UUID relativeId) {
    Component comp = getInternalComponent(componentId);
    if (comp != null) {
      comp.setRelative(relativeId);
    }
  }

  /**
   * @param componentId
   *          the id of the component
   * @param isSafetyCritical
   *          the isSafetyCritical to set
   */
  public boolean setSafetyCritical(UUID componentId, boolean isSafetyCritical) {
    Component comp = getInternalComponent(componentId);
    if (comp != null) {
      return comp.setSafetyCritical(isSafetyCritical);
    }
    return false;
  }

  /**
   * returns whether a component is safety critical or not
   * 
   * @author Lukas Balzer
   *
   * @param componentId
   *          the id of the component
   * @return if the component is safety critical, also false if the uuid fits no component
   */
  public boolean isSafetyCritical(UUID componentId) {
    Component comp = getInternalComponent(componentId);
    if (comp != null) {
      return comp.isSafetyCritical();
    }
    return false;
  }

  /**
   * @param componentId
   *          the id of the component
   * @param comment
   *          the comment to set
   */
  public void setComment(UUID componentId, String comment) {
    Component comp = getInternalComponent(componentId);
    if (comp != null) {
      comp.setComment(comment);
    }
  }

  /**
   *
   * @author Lukas
   *
   * @param componentId
   *          the id of the component
   * @param variableID
   *          the variable which should be rmoved
   * @return whether or not the add was successful, it also returns false if the given uuid belongs
   *         to no component
   */
  public boolean addUnsafeProcessVariable(UUID componentId, UUID variableID) {
    Component comp = getInternalComponent(componentId);
    if (comp != null) {
      return comp.addUnsafeProcessVariable(variableID);
    }
    return false;
  }

  /**
   *
   * @author Lukas
   *
   * @param componentId
   *          the id of the component
   * @param variableID
   *          the variable which should be rmoved
   * @return whether or not the remove was successful, it also returns false if the given uuid
   *         belongs to no component
   */
  public boolean removeUnsafeProcessVariable(UUID componentId, UUID variableID) {
    Component comp = getInternalComponent(componentId);
    if (comp != null) {
      comp.removeUnsafeProcessVariable(variableID);
    }
    return false;
  }

  /**
   *
   * @author Lukas
   *
   * @param componentId
   *          the id of the component
   * @return a map cointaining all process variables provided as keys to a safe/unsafe boolean
   */
  public Map<IRectangleComponent, Boolean> getRelatedProcessVariables(UUID componentId) {
    Component comp = getInternalComponent(componentId);
    Map<IRectangleComponent, Boolean> values = new HashMap<>();
    if (comp != null) {
      List<UUID> upv = comp.getUnsafeProcessVariables();
      IConnection conn = getConnection(comp.getRelative());
      Component target = getInternalComponent(conn.getTargetAnchor().getOwnerId());
      if (target == null || target.getComponentType() != ComponentType.CONTROLLER) {
        return values;
      }
      for (IRectangleComponent child : target.getChildren()) {
        if (child.getComponentType() == ComponentType.PROCESS_MODEL) {
          for (IRectangleComponent variable : child.getChildren()) {
            if (variable.getComponentType() == ComponentType.PROCESS_VARIABLE) {
              values.put(variable, upv.contains(variable.getId()));
            }
          }
        }
      }
    }
    return values;
  }

  public void setUseMultiRoots(Boolean useMultiRoots) {
    this.useMultiRoots = useMultiRoots;
  }

  public Boolean useMultiRoots() {
    return useMultiRoots;
  }

  public boolean usesHAZXData() {
    for (Component comp : this.getInternalComponents()) {
      if (comp.getComponentType().equals(ComponentType.CONTAINER))
        return true;
      if (comp.getComponentType().equals(ComponentType.DASHEDBOX))
        return true;
      if (!comp.getComment().isEmpty())
        return true;
      if (!comp.getUnsafeProcessVariables().isEmpty())
        return true;
    }
    return false;
  }

  public void setActiveRoot(UUID rootId) {
    for (Component root : this._internalGetRoots()) {
      if (root.getId().equals(rootId)) {
        this.activeRoot = root;
      }
    }
  }

  /**
   * Searches recursively for the internal component with the given id
   * 
   * @param componentId
   *          the id of the child
   * @return the component with the given id
   * 
   * @author Fabian Toth
   */
  private Component getInternalComponent(UUID componentId) {
    return this._getActiveRoot().getChild(componentId);
  }

  private Component _getActiveRoot() {
    if (activeRoot == null) {
      this.activeRoot = this.internalRoot(0);
    }
    return activeRoot;
  }

  private Component internalRoot(int i) {
    try {
      return _internalGetRoots().get(i);
    } catch (IndexOutOfBoundsException exc) {
      return new Component();
    }
  }

  private List<Component> _internalGetRoots() {
    if (this.root != null) {
      this.rootComponents = new ArrayList<>();
      root.setText("level 0");
      this.rootComponents.add(root);
      this.root = null;
    }
    return this.rootComponents;
  }

  public void sync(ControlStructureController controller) {
    this.useMultiRoots = controller.useMultiRoots;
    this.step2Initialiesed = controller.step2Initialiesed;
    this.changed = controller.changed;
    if (controller.rootComponents != null) {
      this.rootComponents = new ArrayList<>();
      this.rootComponents.add(controller.rootComponents.get(0).clone());
      connections.clear();
      for (CSConnection child : controller.connections) {
        connections.add(child.clone());
      }
    }
  }

}
