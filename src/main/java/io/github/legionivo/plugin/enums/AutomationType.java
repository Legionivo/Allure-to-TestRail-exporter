package io.github.legionivo.plugin.enums;

public enum AutomationType {
    UI_SELENIUM(1),
    BACKEND(2),
    None(3);

    private int value;

    AutomationType(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
