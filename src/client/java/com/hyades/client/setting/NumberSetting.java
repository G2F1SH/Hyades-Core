package com.hyades.client.setting;

/**
 * 数值设置项（带范围与步进）。
 */
public class NumberSetting extends Setting<Double> {

    private final double min;
    private final double max;
    private final double step;

    public NumberSetting(String name, String description, double defaultValue, double min, double max, double step) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.setValue(clamp(defaultValue));
    }

    @Override
    public Type getType() {
        return Type.NUMBER;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public double getStep() {
        return this.step;
    }

    public double getDouble() {
        return this.getValue();
    }

    public int getInt() {
        return (int) Math.round(this.getValue());
    }

    public float getFloat() {
        return this.getValue().floatValue();
    }

    public void set(double value) {
        this.setValue(clamp(value));
    }

    public void increment() {
        this.set(this.getValue() + this.step);
    }

    public void decrement() {
        this.set(this.getValue() - this.step);
    }

    private double clamp(double value) {
        return Math.max(this.min, Math.min(this.max, value));
    }
}