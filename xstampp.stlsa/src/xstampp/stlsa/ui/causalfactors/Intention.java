package xstampp.stlsa.ui.causalfactors;

public enum Intention implements CausalFactorInterface {

  INTENTIONAL("Intentional"), UNINTENTIONAL("Unintentional");
  private final String type;

  private Intention(final String type) {
      this.type = type;
  }

  public String getDisplayableType() {
      return type;
  }
}