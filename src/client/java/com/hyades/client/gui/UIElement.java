package com.hyades.client.gui;

import com.hyades.client.render.DrawContext;
import com.hyades.client.render.Rectangle;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * GUI 元素基类：负责位置、命中测试与事件分发的公共部分。
 * <p>
 * 纯绘制/命中对象，不继承 MC 控件体系，由所在容器负责事件路由。
 */
public abstract class UIElement {

    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected boolean visible = true;
    protected boolean hovered;

    protected UIElement(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public final void render(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
        if (!this.visible) {
            return;
        }
        this.hovered = this.contains(mouseX, mouseY);
        this.renderElement(ctx, mouseX, mouseY, deltaTicks);
    }

    protected abstract void renderElement(DrawContext ctx, float mouseX, float mouseY, float deltaTicks);

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        return false;
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        return false;
    }

    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        return false;
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
    }

    public boolean contains(float px, float py) {
        return px >= this.x && px <= this.x + this.width && py >= this.y && py <= this.y + this.height;
    }

    public Rectangle getBounds() {
        return Rectangle.ofXYWH(this.x, this.y, this.width, this.height);
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isHovered() {
        return this.hovered;
    }
}