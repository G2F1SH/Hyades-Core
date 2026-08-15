package com.hyades.client.setting;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 多选设置项（从候选集中勾选）。
 */
public class MultiSelectSetting extends Setting<Set<String>> {

    private final String[] options;

    public MultiSelectSetting(String name, String description, String[] options, String[] defaults) {
        super(name, description, new LinkedHashSet<>());
        this.options = options == null ? new String[0] : options;
        if (defaults != null) {
            for (String option : defaults) {
                this.getValue().add(option);
            }
        }
    }

    @Override
    public Type getType() {
        return Type.MULTI_SELECT;
    }

    public String[] getOptions() {
        return this.options;
    }

    public boolean isEnabled(String option) {
        return this.getValue().contains(option);
    }

    public void toggle(String option) {
        if (this.getValue().contains(option)) {
            this.getValue().remove(option);
        } else {
            this.getValue().add(option);
        }
    }

    public Set<String> getSelected() {
        return this.getValue();
    }
}