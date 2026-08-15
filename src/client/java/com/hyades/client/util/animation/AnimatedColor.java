package com.hyades.client.util.animation;

import com.hyades.client.util.ColorUtil;

/**
 * 颜色补间动画器：在给定起止颜色之间随进度插值。
 */
public class AnimatedColor {

    private int fromColor;
    private int toColor;
    private double progress;

    public AnimatedColor(int fromColor, int toColor) {
        this.fromColor = fromColor;
        this.toColor = toColor;
    }

    public AnimatedColor(int color) {
        this(color, color);
    }

    public void setRange(int fromColor, int toColor) {
        this.fromColor = fromColor;
        this.toColor = toColor;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public double getProgress() {
        return this.progress;
    }

    /** 推进进度（0~1），并返回当前插值颜色 */
    public int update(double delta) {
        this.progress = Math.max(0.0, Math.min(1.0, this.progress + delta));
        return ColorUtil.lerpColor(this.fromColor, this.toColor, (float) this.progress);
    }

    public int getColor() {
        return ColorUtil.lerpColor(this.fromColor, this.toColor, (float) this.progress);
    }

    public int getFromColor() {
        return this.fromColor;
    }

    public int getToColor() {
        return this.toColor;
    }
}