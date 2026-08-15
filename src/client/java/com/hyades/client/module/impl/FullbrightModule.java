package com.hyades.client.module.impl;

import com.hyades.client.module.Category;
import com.hyades.client.module.Module;
import net.minecraft.client.Minecraft;

/**
 * 全亮度：通过调整 gamma 提亮画面。
 */
public class FullbrightModule extends Module {

    private double previousGamma = 0.5;

    public FullbrightModule() {
        super("Fullbright", "Brightens the world", Category.RENDER);
        this.addSetting(new com.hyades.client.setting.NumberSetting("Brightness", "Brightness multiplier", 1.0, 0.0, 1.0, 0.05));
    }

    @Override
    public void onEnable() {
        Minecraft minecraft = Minecraft.getInstance();
        this.previousGamma = minecraft.options.gamma().get();
        minecraft.options.gamma().set(1.0);
    }

    @Override
    public void onDisable() {
        Minecraft.getInstance().options.gamma().set(this.previousGamma);
    }
}