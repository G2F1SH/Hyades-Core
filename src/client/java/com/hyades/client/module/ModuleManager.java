package com.hyades.client.module;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/**
 * 模块管理器（单例）。负责注册、按分类查询与按键处理。
 */
public final class ModuleManager {

    public static final ModuleManager INSTANCE = new ModuleManager();

    private final List<Module> modules = new ArrayList<>();
    private final List<Runnable> registrationCallbacks = new ArrayList<>();

    private ModuleManager() {
    }

    public void register(Module module) {
        if (!this.modules.contains(module)) {
            this.modules.add(module);
            this.registrationCallbacks.forEach(Runnable::run);
        }
    }

    public void unregister(Module module) {
        this.modules.remove(module);
    }

    public void unregisterAll() {
        this.modules.forEach(Module::disable);
        this.modules.clear();
    }

    public List<Module> getModules() {
        return this.modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        List<Module> result = new ArrayList<>();
        for (Module module : this.modules) {
            if (module.getCategory() == category) {
                result.add(module);
            }
        }
        return result;
    }

    public Module getModule(String name) {
        for (Module module : this.modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    /** 当模块注册集合变化时回调（GUI 刷新用） */
    public void addRegistrationCallback(Runnable callback) {
        this.registrationCallbacks.add(callback);
    }

    /** 处理所有模块的按键切换 */
    public void updateKeyStates() {
        long window = Minecraft.getInstance().getWindow().handle();
        if (window == 0L) {
            return;
        }
        for (Module module : this.modules) {
            module.updateKeyState(window);
        }
    }

    public void setModuleEnabled(Module module, boolean enabled) {
        if (enabled) {
            module.enable();
        } else {
            module.disable();
        }
    }
}