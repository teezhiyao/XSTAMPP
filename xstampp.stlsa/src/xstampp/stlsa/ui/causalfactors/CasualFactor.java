package xstampp.stlsa.ui.causalfactors;

public enum CasualFactor implements CausalFactorInterface {

    MANIPULATED_OPERATION("Manipulated Operation", Intention.INTENTIONAL),
    REPLACED_SENSORS("Replaced Sensors", Intention.INTENTIONAL),
    TAMPERED_FEEDBACK("Tampered Feedback", Intention.INTENTIONAL),
    
    DELAYED_OPERATIONS("Delayed Operations", Intention.UNINTENTIONAL),
    ETHIOPIAN("Ethiopian Blend", Intention.UNINTENTIONAL);
    
    private final String label;
    private String Description;
    private final Intention type;

    private CasualFactor(String label, Intention type) {
        this.label = label;
        this.type = type;
    }

    public String getDisplayableType() {
        return type.getDisplayableType();
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
      return Description;
    }

    public void setDescription(String description) {
      Description = description;
    }

}

