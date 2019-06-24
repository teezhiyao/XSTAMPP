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

package xstampp.astpa.model;

import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import xstampp.astpa.model.interfaces.IEntryWithNameId;
import xstampp.astpa.model.interfaces.ITableModel;

/**
 * Abstract class for everything that can be shown in a table
 * 
 * @author Fabian Toth
 * @since 2.1
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class ATableModel extends EntryWithSeverity
    implements ITableModel, IEntryWithNameId {

  @XmlAttribute
  private UUID createdBy;

  @XmlAttribute
  private Boolean changed;

  @XmlAttribute
  private Boolean hasTemporaryId;

  @XmlElement
  private UUID id;

  @XmlElement
  private String title;

  @XmlElement
  private String description;

  @XmlElement
  private int number;

  @XmlElement
  private String idString;

  @XmlElement
  private String links;
  
  /**
   * constructor of a table model
   * 
   * @param title
   *          the title of the new element
   * @param description
   *          the description of the new element
   * @param number
   *          the number of the new element
   * 
   * @author Fabian Toth
   */
  public ATableModel(String title, String description, int number) {
    this.id = UUID.randomUUID();
    this.title = title;
    this.description = description;
    this.number = number;
    this.changed = null;
  }

  public ATableModel(String title, String description, int number, boolean createTempId) {
    this(title, description, number);
    if(createTempId ) {
      this.hasTemporaryId = createTempId;
    } else {
      this.hasTemporaryId = null;
    }
  }

  public ATableModel(String title, String description, boolean createTempId) {
    this(title, description);
    if(createTempId ) {
      this.hasTemporaryId = createTempId;
    } else {
      this.hasTemporaryId = null;
    }
  }

  /**
   * Empty constructor used for JAXB. Do not use it!
   * 
   * @author Fabian Toth
   */
  public ATableModel() {
    this("", "", -1);
  }

  public ATableModel(String title, String description) {
    this(title, description, -1);
  }

  public ATableModel(ITableModel model, int i)
  {
    this.id = model.getId();
    this.title = model.getTitle();
    this.description = model.getDescription();
    this.number = model.getNumber();
    this.changed = false;
    if(model instanceof EntryWithSeverity) {
      this.setSeverity(((EntryWithSeverity) model).getSeverity());
    }
 }
  
  /**
   * creates a table model that has the same title and description as the given model,
   * the number of the model is either also copied if model.hasTemporaryId is false or reset otherwise
   * 
   * @param model
   * @param i
   * @param createTempId
   */
  public ATableModel(ITableModel model, int i, boolean createTempId) {
    this(model, i);
    if(model instanceof ATableModel && ((ATableModel) model).hasTemporaryId!= null && !createTempId) {
      this.number = -1;
    }
    this.hasTemporaryId = createTempId;
  }
  


  public static <T> boolean move(boolean up, UUID id, List<T> list) {
    for (int i = 0; i < list.size(); i++) {
      if (((ITableModel) list.get(i)).getId().equals(id)) {
        T downModel = null;
        int moveIndex = i;
        /*
         * if up is true than the ITable model with the given id should move up if this is
         * possible(if there is a model right to it in the list) than the model which is right to it
         * is moved down else the model itself is moved down
         */
        if (up && i + 1 >= list.size()) {
          return false;
        } else if (up) {
          downModel = ((T) list.get(i + 1));
          moveIndex = i;
        } else if (i == 0) {
          return false;
        } else {
          downModel = ((T) list.get(i));
          moveIndex = i - 1;
        }

        list.remove(downModel);
        list.add(moveIndex, downModel);
        return true;
      }
    }
    return false;
  }

  public boolean hasChanged() {
    return changed == null ? false : changed;
  }

  public void setChanged() {
    changed = changed == null ? null : true;
  }

  public boolean setChanged(boolean changed) {
    this.changed = this.changed == null ? null : changed;
    return changed;
  }

  /**
   * Setter for the description
   * 
   * @param description
   *          the new description, if <b>null</b> or the current text is given the method returns with <b>null</b>
   * 
   * @author Fabian Toth
   * @author Lukas Balzer
   * @return  The old description if the description has been changed null otherwise
   */
  public String setDescription(String description) {
    if (this.description == null || !this.description.equals(description)) {
      String result = this.description;
      this.description = description;
      setChanged();
      return result;
    }
    return null;
  }

  @Override
  public String getDescription() {
    return this.description == null ? "..." : this.description;
  }

  /**
   * Setter for the title
   * 
   * @param title
   *          the new title, if <b>null</b> or the current text is given the method returns with <b>null</b> 
   * 
   * @author Fabian Toth
   * @author Lukas Balzer
   * @return The old title if the title has been changed null otherwise
   */
  public String setTitle(String title) {
    if (this.title == null || !this.title.equals(title)) {
      String result = this.title;
      this.title = title;
      setChanged();
      return result;
    }
    return null;
  }

  @Override
  public String getTitle() {
    return this.title == null ? "" : this.title;
  }

  @Override
  public String getText() {
    return this.getTitle();
  }

  @Override
  public UUID getId() {
    return this.id;
  }

  /**
   * Setter for the id
   * 
   * @param id
   *          the new id
   * 
   * @author Fabian Toth
   * @return TODO
   */
  public boolean setId(UUID id) {
    if (this.id == null || !this.id.equals(id)) {
      this.id = id;
      return true;
    }
    return false;
  }

  @Override
  public int getNumber() {
    return this.number;
  }

  @Override
  public String getIdString() {
    if(hasTemporaryId != null && hasTemporaryId) {
      return "[" + Integer.toString(this.number) + "]";
    }
    return Integer.toString(this.number);
  }

  /**
   * Setter for the number
   * 
   * @param number
   *          the new number
   * 
   * @author Fabian Toth
   * @return
   */
  public boolean setNumber(int number) {
    if (this.number != number) {
      this.number = number;
      setChanged();
      return true;
    }
    return false;
  }

  /**
   * @return the links
   */
  public String getLinks() {
    return this.links;
  }

  /**
   * @param links
   *          the links to set
   * @return
   */
  public boolean setLinks(String links) {
    if ((this.links == null && links != null)
        || !(this.links == null || this.links.equals(links))) {
      this.links = links;
      setChanged();
      return true;
    }
    return false;
  }

  public void setCreatedBy(UUID createdBy) {
    this.createdBy = createdBy;
  }

  public UUID getCreatedBy() {
    return createdBy;
  }
  
  public Boolean getHasTemporaryId() {
    return hasTemporaryId;
  }

  public void prepareForExport() {
    this.idString = this.getIdString();
  }

  public void prepareForSave() {
    this.idString = null;
    this.links = null;
  }
}
