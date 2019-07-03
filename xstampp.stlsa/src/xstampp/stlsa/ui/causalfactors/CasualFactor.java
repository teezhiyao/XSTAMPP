package xstampp.stlsa.ui.causalfactors;

public enum CasualFactor implements CausalFactorInterface {

    MANIPULATED_OPERATION("Manipulated Operation", Intention.INTENTIONAL),
    REPLACED_SENSORS("Replaced Sensors", Intention.INTENTIONAL),
    TAMPERED_FEEDBACK("Tampered Feedback", Intention.INTENTIONAL),
    INTENTIONAL_CONGESTION_OF_FEEDBACK_PATH("Intentional congestion of feedback path", Intention.INTENTIONAL),
    INJECTION_OF_INPUT("Injection of Input", Intention.INTENTIONAL),
    TAMPERED_OR_FABRICATED_SIGNAL("Tampered or fabricated sensor signal", Intention.INTENTIONAL),
       
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

