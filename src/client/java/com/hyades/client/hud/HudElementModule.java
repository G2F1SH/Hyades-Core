package com.hyades.client.hud;

import com.hyades.client.module.Category;
import com.hyades.client.module.Module;

/**
 * 把 {@link HudElement} 包装成模块：开关即控制 HUD 元素显隐，
 * 使其能出现在 ClickGUI 的 HUD 分类中。
 */
public class HudElementModule extends Module {

    private final HudElement hudElement;

    public HudElementModule(HudElement hudElement, String displayName) {
        super(displayName, "HUD element: " + hudElement.getId(), Category.HUD);
        this.hudElement = hudElement;
    }

    public HudElement getHudElement() {
        return this.hudElement;
    }

    @Override
    public void onEnable() {
        this.hudElement.setVisible(true);
    }

    @Override
    public void onDisable() {
        this.hudElement.setVisible(false);
    }
}