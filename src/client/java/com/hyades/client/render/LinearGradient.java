package com.hyades.client.render;

import com.hyades.client.util.ColorUtil;

/**
 * 线性渐变定义（起始/结束颜色 + 角度，0° 为垂直向下，90° 为水平向右）。
 */
public final class LinearGradient {

    private final int colorA;
    private final int colorB;
    private final float angleDeg;

    public LinearGradient(int colorA, int colorB) {
        this(colorA, colorB, 90.0f);
    }

    public LinearGradient(int colorA, int colorB, float angleDeg) {
        this.colorA = colorA;
        this.colorB = colorB;
        this.angleDeg = angleDeg;
    }

    public int getColorA() {
        return this.colorA;
    }

    public int getColorB() {
        return this.colorB;
    }

    public float getAngleDeg() {
        return this.angleDeg;
    }

    /** 在渐变区间 t(in 0~1) 处插值颜色 */
    public int sample(float t) {
        return ColorUtil.lerpColor(this.colorA, this.colorB, t);
    }
}