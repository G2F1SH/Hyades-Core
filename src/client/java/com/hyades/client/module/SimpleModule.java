package com.hyades.client.module;

/**
 * 简单模块：无需子类化，通过回调接入启用/禁用逻辑。
 * <p>
 * 适合其他模组快速接入框架：
 * <pre>{@code
 * api.createModule("MyModule", "Does something", Category.MISC)
 *     .onEnable(() -> { ... })
 *     .onDisable(() -> { ... })
 *     .withKeyBind(GLFW.GLFW_KEY_J);
 * }</pre>
 */
public class SimpleModule extends Module {

    private Runnable onEnableAction = () -> {
    };
    private Runnable onDisableAction = () -> {
    };

    public SimpleModule(String name, String description, Category category) {
        super(name, description, category);
    }

    /** 设置启用回调 */
    public SimpleModule onEnable(Runnable action) {
        this.onEnableAction = action == null ? () -> {
        } : action;
        return this;
    }

    /** 设置禁用回调 */
    public SimpleModule onDisable(Runnable action) {
        this.onDisableAction = action == null ? () -> {
        } : action;
        return this;
    }

    /** 设置按键绑定 */
    public SimpleModule withKeyBind(int glfwKeyCode) {
        this.setKeyBind(glfwKeyCode);
        return this;
    }

    @Override
    public void onEnable() {
        this.onEnableAction.run();
    }

    @Override
    public void onDisable() {
        this.onDisableAction.run();
    }
}