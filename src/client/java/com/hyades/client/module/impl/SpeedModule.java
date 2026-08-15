package com.hyades.client.module.impl;

import com.hyades.client.module.Category;
import com.hyades.client.module.Module;
import com.hyades.client.setting.MultiSelectSetting;
import com.hyades.client.setting.NumberSetting;

/**
 * 速度增强（占位实现，仅演示设置系统）。
 */
public class SpeedModule extends Module {

    public SpeedModule() {
        super("Speed", "Movement speed boost", Category.MOVEMENT);
        this.addSetting(new NumberSetting("Speed", "Horizontal speed multiplier", 1.4, 1.0, 2.5, 0.1));
        this.addSetting(new MultiSelectSetting("Modes", "Active speed modes",
                new String[]{"Strafe", "Bhop", "AutoJump", "LowHop"}, new String[]{"Strafe"}));
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }
}