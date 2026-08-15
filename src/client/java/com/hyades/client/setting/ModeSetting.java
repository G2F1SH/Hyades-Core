package com.hyades.client.setting;

import java.util.Arrays;

/**
 * 模式选择设置项（在有限集合中循环切换）。
 */
public class ModeSetting extends Setting<String> {

    private final String[] modes;
    private int index;

    public ModeSetting(String name, String description, String[] modes, String defaultValue) {
        super(name, description, defaultValue);
        this.modes = modes == null || modes.length == 0 ? new String[]{"Default"} : modes;
        this.index = Math.max(0, Arrays.asList(this.modes).indexOf(defaultValue));
        if (this.getValue() == null || !Arrays.asList(this.modes).contains(this.getValue())) {
            this.setValue(this.modes[0]);
        }
    }

    @Override
    public Type getType() {
        return Type.MODE;
    }

    public String[] getModes() {
        return this.modes;
    }

    public String getMode() {
        return this.getValue();
    }

    public void setMode(String mode) {
        int idx = Arrays.asList(this.modes).indexOf(mode);
        if (idx >= 0) {
            this.index = idx;
            this.setValue(mode);
        }
    }

    /** 切换到下一个模式（末尾循环回开头） */
    public void cycle() {
        this.index = (this.index + 1) % this.modes.length;
        this.setValue(this.modes[this.index]);
    }
}