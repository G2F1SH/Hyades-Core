package com.hyades.client.render;

/**
 * 不可变矩形（左上角 + 右下角）。
 */
public record Rectangle(float x1, float y1, float x2, float y2) {

    public static Rectangle ofXYWH(float x, float y, float width, float height) {
        return new Rectangle(x, y, x + width, y + height);
    }

    public static Rectangle ofCorners(float x1, float y1, float x2, float y2) {
        return new Rectangle(x1, y1, x2, y2);
    }

    public float getWidth() {
        return this.x2 - this.x1;
    }

    public float getHeight() {
        return this.y2 - this.y1;
    }

    public float getCenterX() {
        return (this.x1 + this.x2) / 2.0f;
    }

    public float getCenterY() {
        return (this.y1 + this.y2) / 2.0f;
    }

    public boolean contains(float x, float y) {
        return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2;
    }

    public Rectangle offset(float dx, float dy) {
        return new Rectangle(this.x1 + dx, this.y1 + dy, this.x2 + dx, this.y2 + dy);
    }

    public Rectangle inset(float amount) {
        return new Rectangle(this.x1 + amount, this.y1 + amount, this.x2 - amount, this.y2 - amount);
    }

    public Rectangle intersection(Rectangle other) {
        float maxX1 = Math.max(this.x1, other.x1);
        float maxY1 = Math.max(this.y1, other.y1);
        float minX2 = Math.min(this.x2, other.x2);
        float minY2 = Math.min(this.y2, other.y2);
        return maxX1 < minX2 && maxY1 < minY2
                ? Rectangle.ofCorners(maxX1, maxY1, minX2, minY2)
                : Rectangle.ofCorners(0, 0, 0, 0);
    }
}