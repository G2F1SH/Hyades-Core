package com.hyades.client.setting;

/**
 * 模块设置项基类。
 *
 * @param <T> 设置值类型
 */
public abstract class Setting<T> {

    public enum Type {
        BOOLEAN,
        MODE,
        NUMBER,
        MULTI_SELECT
    }

    private final String name;
    private final String description;
    private T value;

    protected Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
    }

    public abstract Type getType();

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}