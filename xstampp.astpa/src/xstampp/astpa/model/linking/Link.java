package xstampp.astpa.model.linking;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;

final class Link {
  @XmlAttribute
  private UUID linkA;

  @XmlAttribute
  private UUID linkB;

  Link(UUID a, UUID b) {
    linkA = a;
    linkB = b;
  }

  Link() {
    linkA = null;
    linkB = null;
  }

  UUID getLinkA() {
    return linkA;
  }

  UUID getLinkB() {
    return linkB;
  }

  boolean links(UUID part) {
    return part != null && (part.equals(this.linkA) || part.equals(this.linkB));
  }

  UUID getLinkFor(UUID part) {
    if (part.equals(this.linkA)) {
      return this.linkB;
    }
    if (part.equals(this.linkB)) {
      return this.linkA;
    }
    return null;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof UUID) {
      return this.links((UUID) obj);
    }
    if (obj instanceof Link) {
      return ((Link) obj).links(this.linkA) && ((Link) obj).links(this.linkB);
    }
    return super.equals(obj);
  }
}
