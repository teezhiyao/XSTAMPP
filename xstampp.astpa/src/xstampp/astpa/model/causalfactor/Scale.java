package xstampp.astpa.model.causalfactor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "scale")
public class Scale {

  @XmlElement(name = "scaletitle")
  private String scaletitle;
  @XmlElement(name = "scaleint")
  private int integer;

  public Scale() {
  }
  
  public Scale(String title,Integer scaleInt) {
    this.scaletitle = title;
    this.integer = scaleInt;
  }
  
  public int getInteger() {
    return integer;
  }

  public void setInteger(int integer) {
    this.integer = integer;
  }

  public String getTitle() {
    return this.scaletitle;
  }
  
  
}
