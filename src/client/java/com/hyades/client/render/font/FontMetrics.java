package com.hyades.client.render.font;

/**
 * 字体度量（基于 AWT 字体度量）。
 */
public record FontMetrics(float ascent, float descent, float height, float capHeight) {

    /** 行距（ascent + descent） */
    public float lineHeight() {
        return this.ascent + this.descent;
    }
}