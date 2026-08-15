package com.hyades.client.hud;

import com.hyades.client.render.DrawContext;

/**
 * HUD 元素接口：屏幕内渲染一段信息。
 */
public interface IHudElement {

    /** 唯一标识 */
    String getId();

    /** 是否参与渲染 */
    boolean isVisible();

    void render(DrawContext ctx, float mouseX, float mouseY, float deltaTicks);
}