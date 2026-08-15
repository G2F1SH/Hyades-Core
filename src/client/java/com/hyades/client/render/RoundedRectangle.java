package com.hyades.client.render;

/**
 * 圆角矩形（四个角各自独立半径）。
 */
public record RoundedRectangle(float x, float y, float width, float height, float radiusTopLeft,
                               float radiusTopRight, float radiusBottomRight, float radiusBottomLeft) {

    /** 四个角统一半径 */
    public static RoundedRectangle ofXYWHR(float x, float y, float width, float height, float radius) {
        return new RoundedRectangle(x, y, width, height, radius, radius, radius, radius);
    }

    /** 顺序：左上、右上、右下、左下 */
    public static RoundedRectangle ofXYWHRadii(float x, float y, float width, float height, float[] radii) {
        if (radii == null || radii.length != 4) {
            throw new IllegalArgumentException("radii must contain exactly 4 elements");
        }
        return new RoundedRectangle(x, y, width, height, radii[0], radii[1], radii[2], radii[3]);
    }

    public float getX2() {
        return this.x + this.width;
    }

    public float getY2() {
        return this.y + this.height;
    }

    public float maxRadius() {
        return Math.max(Math.max(this.radiusTopLeft, this.radiusTopRight),
                Math.max(this.radiusBottomRight, this.radiusBottomLeft));
    }
}