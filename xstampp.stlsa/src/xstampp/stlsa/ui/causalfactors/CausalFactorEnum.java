package xstampp.stlsa.ui.causalfactors;

public enum CausalFactorEnum implements CausalFactorInterface {

    MANIPULATED_OPERATION("Manipulated Operation", Intention.INTENTIONAL),
    REPLACED_SENSORS("Replaced Sensors", Intention.INTENTIONAL),
    TAMPERED_FEEDBACK("Tampered Feedback", Intention.INTENTIONAL),
    INTENTIONAL_CONGESTION_OF_FEEDBACK_PATH("Intentional congestion of feedback path", Intention.INTENTIONAL),
    INJECTION_OF_INPUT("Injection of Input", Intention.INTENTIONAL),
    TAMPERED_OR_FABRICATED_SIGNAL("Tampered or fabricated sensor signal", Intention.INTENTIONAL),
    SENDING_MANUFACTURED_CONTROL_ACTION("Sending manufactured control action", Intention.INTENTIONAL),
    OVERRIDING_LEGITIMATE_CONTROL_ACTIONS("Overriding legitimate control actions", Intention.INTENTIONAL),
    REPLAYED_CONTROL_INPUT("Replayed control input", Intention.INTENTIONAL),
    JAMMED_CONTROL_INPUT("Jammed Control input", Intention.INTENTIONAL),
    SENDING_MANUFACTURED_CONTROL_INPUT("Sending manufactured control input", Intention.INTENTIONAL),
    OVERRIDING_LEGITIMATE_CONTROL_INPUT("Overriding legitimate control input", Intention.INTENTIONAL),
    TAMPERED_OR_MANUFACTURED_INFORMATION("Tampered or manufactured information", Intention.INTENTIONAL),
    INJECTION_OF_MANIPULATED_CONTROL_ALGORITHM("Injection of manipulated control algorithm", Intention.INTENTIONAL),
    UNAUTHORIZED_CHANGES_TO_THE_CONTROL_ALGORITHM("Unauthorized changes to the control algorithm", Intention.INTENTIONAL),
    PROCESS_MODEL_TAMPERED("Process Model tampered", Intention.INTENTIONAL),
            
    INAPPROPRIATE_INEFFECTIVE_OR_MISSING_CONTROL_ACTIONS("Delayed Operations", Intention.UNINTENTIONAL),
    DELAYED_OPERATIONS("Delayed Operations", Intention.UNINTENTIONAL),
    CONFLICTING_CONTROL_ACTIONS("Delayed Operations", Intention.UNINTENTIONAL),
    PROCESS_INPUT_MISSING_OR_WRONG("Delayed Operations", Intention.UNINTENTIONAL),
    UNIDENTIFIED_OR_OUT_OF_RANGE_DISTURBANCE("Delayed Operations", Intention.UNINTENTIONAL),
    PROCESS_OUTPUT_CONTRIBUTES_TO_SYSTEM_HAZARD("Delayed Operations", Intention.UNINTENTIONAL),
    INCORRECT_OR_NO_INFORMATION_PROVIDED("Delayed Operations", Intention.UNINTENTIONAL),
    MEASUREMENT_INACCURACIES("Delayed Operations", Intention.UNINTENTIONAL),
    FEEDBACK_DELAYS("Delayed Operations", Intention.UNINTENTIONAL),
    INADEQUATE_OPERATIONS("Delayed Operations", Intention.UNINTENTIONAL),
    PROCESS_MODEL_INCONSISTENT_INCOMPLETE_OR_INCORRECT("Delayed Operations", Intention.UNINTENTIONAL),
    INADEQUATE_CONTROL_ALGORITHM_FLAWS_IN_CREATION_PROCESS_CHANGES_INCORRECT_MODIFICATION_OR_ADAPTION("Delayed Operations", Intention.UNINTENTIONAL),
    SPOOFED_CONTROLLER("Delayed Operations", Intention.UNINTENTIONAL),
    CONTROL_INPUT_OR_EXTERNAL_INFORMATION_WRONG_OR_MISSING("Delayed Operations", Intention.UNINTENTIONAL);
    
    private final String label;
    private String Description;
    private final Intention type;

    private CausalFactorEnum(String label, Intention type) {
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

