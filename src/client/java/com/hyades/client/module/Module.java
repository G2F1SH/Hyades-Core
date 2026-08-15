package com.hyades.client.module;

import com.hyades.client.setting.Setting;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.glfw.GLFW;

/**
 * 功能模块基类。负责开关状态、键位绑定与设置项管理。
 * <p>
 * 具体的模块逻辑通过重写 {@link #onEnable()} / {@link #onDisable()} 接入。
 */
public abstract class Module {

    private final String name;
    private final String description;
    private final Category category;
    private final List<Setting<?>> settings = new ArrayList<>();

    private boolean enabled;
    private int keyBind;
    private boolean prevKeyDown;

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getKeyBind() {
        return this.keyBind;
    }

    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }

    public String getKeyBindName() {
        if (this.keyBind == 0) {
            return "None";
        }
        return switch (this.keyBind) {
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "Right Shift";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "Left Shift";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "Right Control";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "Left Control";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "Right Alt";
            case GLFW.GLFW_KEY_LEFT_ALT -> "Left Alt";
            default -> "Key " + this.keyBind;
        };
    }

    /** 模块被启用时调用（默认空实现，供子类覆写） */
    public void onEnable() {
    }

    /** 模块被禁用时调用（默认空实现，供子类覆写） */
    public void onDisable() {
    }

    public void toggle() {
        if (this.enabled) {
            this.disable();
        } else {
            this.enable();
        }
    }

    public void enable() {
        if (this.enabled) {
            return;
        }
        this.enabled = true;
        this.onEnable();
    }

    public void disable() {
        if (!this.enabled) {
            return;
        }
        this.enabled = false;
        this.onDisable();
    }

    public void addSetting(Setting<?> setting) {
        this.settings.add(setting);
    }

    public List<Setting<?>> getSettings() {
        return this.settings;
    }

    public boolean hasSettings() {
        return !this.settings.isEmpty();
    }

    /** 按名称查找设置项 */
    @SuppressWarnings("unchecked")
    public <T extends Setting<?>> T getSetting(String name) {
        for (Setting<?> setting : this.settings) {
            if (setting.getName().equalsIgnoreCase(name)) {
                return (T) setting;
            }
        }
        return null;
    }

    /**
     * 在客户端 tick 中处理键位切换（边沿触发）。
     *
     * @param glfwWindow GLFW 窗口句柄
     */
    public void updateKeyState(long glfwWindow) {
        if (this.keyBind == 0) {
            return;
        }
        boolean down = GLFW.glfwGetKey(glfwWindow, this.keyBind) == GLFW.GLFW_PRESS;
        if (down && !this.prevKeyDown) {
            this.toggle();
        }
        this.prevKeyDown = down;
    }
}