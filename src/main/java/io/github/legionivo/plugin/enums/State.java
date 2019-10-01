package io.github.legionivo.plugin.enums;

public enum State {

    DRAFT(1),
    READY_FOR_TESTING(2),
    AUTOMATED(3),
    READY_FOR_AUTOMATION(4);

    int value;

    State(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
