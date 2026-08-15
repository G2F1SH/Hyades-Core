package com.hyades.client.setting;

/**
 * 布尔开关设置项。
 */
public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description, defaultValue);
    }

    @Override
    public Type getType() {
        return Type.BOOLEAN;
    }

    public boolean isEnabled() {
        return this.getValue();
    }

    public void set(boolean value) {
        this.setValue(value);
    }

    public void toggle() {
        this.setValue(!this.getValue());
    }
}