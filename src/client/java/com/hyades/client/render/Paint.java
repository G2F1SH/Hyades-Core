package com.hyades.client.render;

import com.hyades.client.util.ColorUtil;

/**
 * 绘制状态（类 Android Paint）。
 * <p>
 * 集中封装颜色、描边、渐变等状态，使用链式 Builder 风格：
 * <pre>{@code
 * Paint paint = new Paint().setColor(0xFF88DDFF).setStrokeWidth(2.0f);
 * }</pre>
 */
public class Paint {

    public enum Style {
        FILL,
        STROKE,
        FILL_AND_STROKE
    }

    public enum StrokeCap {
        BUTT,
        ROUND,
        SQUARE
    }

    private int color = 0xFFFFFFFF;
    private Style style = Style.FILL;
    private float strokeWidth = 1.0f;
    private StrokeCap strokeCap = StrokeCap.BUTT;
    private float alpha = 1.0f;
    private LinearGradient linearGradient;

    public Paint setColor(int color) {
        this.color = color;
        return this;
    }

    public int getColor() {
        return this.color;
    }

    public Paint setStyle(Style style) {
        this.style = style == null ? Style.FILL : style;
        return this;
    }

    public Style getStyle() {
        return this.style;
    }

    public Paint setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    public float getStrokeWidth() {
        return this.strokeWidth;
    }

    public Paint setStrokeCap(StrokeCap strokeCap) {
        this.strokeCap = strokeCap == null ? StrokeCap.BUTT : strokeCap;
        return this;
    }

    public StrokeCap getStrokeCap() {
        return this.strokeCap;
    }

    public Paint setAlpha(float alpha) {
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
        return this;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public Paint setLinGradient(LinearGradient linearGradient) {
        this.linearGradient = linearGradient;
        return this;
    }

    public LinearGradient getLinearGradient() {
        return this.linearGradient;
    }

    public boolean hasGradient() {
        return this.linearGradient != null;
    }

    /** 应用 alpha 后的最终颜色 */
    public int getFinalColor() {
        if (this.alpha >= 1.0f) {
            return this.color;
        }
        return ColorUtil.withAlpha(this.color, this.alpha);
    }
}