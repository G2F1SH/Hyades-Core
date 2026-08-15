package com.hyades.client.module.impl;

import com.hyades.client.module.Category;
import com.hyades.client.module.Module;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

/**
 * 自动疾跑：开启后持续按住疾跑键。
 */
public class SprintModule extends Module {

    public SprintModule() {
        super("Sprint", "Automatically sprints", Category.MOVEMENT);
    }

    /** options 在客户端入口阶段尚未初始化，延迟到实际使用模块时再获取疾跑键 */
    private KeyMapping sprintKey() {
        return Minecraft.getInstance().options.keySprint;
    }

    @Override
    public void onEnable() {
        this.sprintKey().setDown(true);
    }

    @Override
    public void onDisable() {
        this.sprintKey().setDown(false);
    }
}