package com.hyades.client.module.impl;

import com.hyades.client.module.Category;
import com.hyades.client.module.Module;

/**
 * 摔落保护（占位实现，仅演示设置系统）。
 */
public class NoFallModule extends Module {

    public NoFallModule() {
        super("NoFall", "Prevents fall damage", Category.PLAYER);
        this.addSetting(new com.hyades.client.setting.ModeSetting("Mode", "Bypass mode",
                new String[]{"Matrix", "Packet", "Verus"}, "Packet"));
        this.addSetting(new com.hyades.client.setting.BooleanSetting("Toggle On Land", "Disable after landing", false));
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }
}