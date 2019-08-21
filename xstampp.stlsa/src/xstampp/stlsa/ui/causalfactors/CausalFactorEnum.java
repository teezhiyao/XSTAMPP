package xstampp.stlsa.ui.causalfactors;

public enum CausalFactorEnum implements CausalFactorInterface {
    BLANK("Edit here", Intention.INTENTIONAL),

    MANIPULATED_OPERATION("Manipulated Operation", Intention.INTENTIONAL),
    REPLACED_SENSORS("Replaced Sensors", Intention.INTENTIONAL),
    TAMPERED_FEEDBACK("Tampered Feedback", Intention.INTENTIONAL),
    INTENTIONAL_CONGESTION_OF_FEEDBACK_PATH("Intentional congestion of feedback path", Intention.INTENTIONAL),
    INJECTION_OF_INPUT("Injection of Input", Intention.INTENTIONAL),
    TAMPERED_OR_FABRICATED_SIGNAL("Tampered or fabricated sensor signal", Intention.INTENTIONAL),
    SENDING_MANUFACTURED_CONTROL_ACTION_OVERRIDING_LEGITIMATE_CONTROL_ACTIONS("Sending manufactured control action, overriding legitimate control actions", Intention.INTENTIONAL),
    REPLAYED_CONTROL_INPUT("Replayed control input", Intention.INTENTIONAL),
    JAMMED_CONTROL_INPUT("Jammed Control input", Intention.INTENTIONAL),
    SENDING_MANUFACTURED_CONTROL_INPUT_OVERRIDING_LEGITIMATE_CONTROL_INPUT("Sending manufactured control input, overriding legitimate control input", Intention.INTENTIONAL),
    TAMPERED_OR_MANUFACTURED_INFORMATION("Tampered or manufactured information", Intention.INTENTIONAL),
    INJECTION_OF_MANIPULATED_CONTROL_ALGORITHM("Injection of manipulated control algorithm", Intention.INTENTIONAL),
    UNATHORIZED_CHANGES_TO_THE_CONTROL_ALGORITHM("Unathorized changes to the control algorithm", Intention.INTENTIONAL),
    PROCESS_MODEL_TAMPERED("Process Model tampered", Intention.INTENTIONAL),
            
    INAPPROPRIATE_INEFFECTIVE_OR_MISSING_CONTROL_ACTIONS("Inappropriate, Ineffective or missing control actions", Intention.UNINTENTIONAL),
    DELAYED_OPERATIONS("Delayed Operations", Intention.UNINTENTIONAL),
    CONFLICTING_CONTROL_ACTIONS("Conflicting control actions", Intention.UNINTENTIONAL),
    PROCESS_INPUT_MISSING_OR_WRONG("Process Input missing or wrong", Intention.UNINTENTIONAL),
    UNIDENTIFIED_OR_OUT_OF_RANGE_DISTURBANCE("Unidentified or out-of-range disturbance", Intention.UNINTENTIONAL),
    PROCESS_OUTPUT_CONTRIBUTES_TO_SYSTEM_HAZARD("Process output contributes to system hazard", Intention.UNINTENTIONAL),
    INCORRECT_OR_NO_INFORMATION_PROVIDED("Incorrect or no information provideds", Intention.UNINTENTIONAL),
    MEASUREMENT_INACCURACIES("Measurement inaccuracies", Intention.UNINTENTIONAL),
    FEEDBACK_DELAYS("Feedback delays", Intention.UNINTENTIONAL),
    INADEQUATE_OPERATIONS("Inadequate or missing feedback", Intention.UNINTENTIONAL),
    PROCESS_MODEL_INCONSISTENT_INCOMPLETE_OR_INCORRECT("Process Model inconsistent, incomplete or incorrect", Intention.UNINTENTIONAL),
    INADEQUATE_CONTROL_ALGORITHM_FLAWS_IN_CREATION_PROCESS_CHANGES_INCORRECT_MODIFICATION_OR_ADAPTION("Inadequate Control Algorithm ( Flaws in creation process changes, incorrect modification or adaption", Intention.UNINTENTIONAL),
    SPOOFED_CONTROLLER("Spoofed Controller", Intention.UNINTENTIONAL),
    CONTROL_INPUT_OR_EXTERNAL_INFORMATION_WRONG_OR_MISSING("Control Input or external information wrong or missing", Intention.UNINTENTIONAL);
    
    private final String label;
    private String Description;
    private Intention intention;

    private CausalFactorEnum(String label, Intention intention) {
        this.label = label;
        this.intention = intention;
    }

    public String getDisplayableType() {
        return this.intention.getDisplayableType();
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

