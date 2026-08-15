package com.hyades.client.hud;

import com.hyades.client.render.DrawContext;

/**
 * HUD 元素抽象基类：持有屏幕位置与可见性，提供命中测试。
 * <p>
 * 位置可用像素（GUI 缩放后坐标）。派生类实现 {@link #renderElement} 完成绘制。
 */
public abstract class HudElement implements IHudElement {

    private final String id;
    protected float x;
    protected float y;
    protected boolean visible = true;
    protected boolean moved;

    protected HudElement(String id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
        this.moved = true;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
        this.moved = true;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.moved = true;
    }

    public boolean isMoved() {
        return this.moved;
    }

    public boolean contains(float px, float py) {
        return px >= this.x && px <= this.x + this.getWidth() && py >= this.y && py <= this.y + this.getHeight();
    }

    public abstract float getWidth();

    public abstract float getHeight();

    @Override
    public void render(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
        this.renderElement(ctx, mouseX, mouseY, deltaTicks);
    }

    protected abstract void renderElement(DrawContext ctx, float mouseX, float mouseY, float deltaTicks);
}