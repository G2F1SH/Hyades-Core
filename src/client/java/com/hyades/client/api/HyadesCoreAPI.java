package com.hyades.client.api;

import com.hyades.client.gui.ClickGuiScreen;
import com.hyades.client.hud.HudElement;
import com.hyades.client.hud.HudElementModule;
import com.hyades.client.hud.HudRenderer;
import com.hyades.client.module.Category;
import com.hyades.client.module.Module;
import com.hyades.client.module.ModuleManager;
import com.hyades.client.module.SimpleModule;
import net.minecraft.client.Minecraft;

/**
 * Hyades Core 公共 API。
 * <p>
 * 供其他模组作为前置模组使用：注册模块、注册 HUD 元素、打开 ClickGUI、切换 HUD 编辑模式。
 * 推荐通过 {@link HyadesCoreClientExtension} 入口点在客户端初始化阶段获取本 API，
 * 也可从任意客户端代码直接调用 {@link #INSTANCE}。
 *
 * <pre>{@code
 * api.registerModule(new MyModule());
 * api.registerHudElement(new MyHudElement(), "MyHud");
 * api.openClickGui();
 * }</pre>
 */
public final class HyadesCoreAPI {

    /** API 单例 */
    public static final HyadesCoreAPI INSTANCE = new HyadesCoreAPI();

    private HyadesCoreAPI() {
    }

    /** 模块管理器 */
    public ModuleManager getModuleManager() {
        return ModuleManager.INSTANCE;
    }

    /** 注册一个模块（会立即出现在 ClickGUI 对应分类中） */
    public void registerModule(Module module) {
        ModuleManager.INSTANCE.register(module);
    }

    /** 便捷创建并注册一个简单模块 */
    public SimpleModule createModule(String name, String description, Category category) {
        SimpleModule module = new SimpleModule(name, description, category);
        ModuleManager.INSTANCE.register(module);
        return module;
    }

    /** 注册一个 HUD 元素，返回包装它的模块（可用以控制显隐） */
    public HudElementModule registerHudElement(HudElement element, String displayName) {
        HudElementModule module = new HudElementModule(element, displayName);
        HudRenderer.INSTANCE.register(module);
        return module;
    }

    /** HUD 渲染器（可整体开关、编辑） */
    public HudRenderer getHudRenderer() {
        return HudRenderer.INSTANCE;
    }

    /** 打开 ClickGUI */
    public void openClickGui() {
        Minecraft.getInstance().gui.setScreen(new ClickGuiScreen());
    }

    /** 切换 HUD 拖拽编辑模式 */
    public void toggleHudDragMode() {
        HudRenderer.INSTANCE.toggleDragMode();
    }

    public boolean isHudDragMode() {
        return HudRenderer.INSTANCE.isDragMode();
    }
}