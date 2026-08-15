package com.hyades.client.hud;

import com.hyades.client.gui.Theme;
import com.hyades.client.module.ModuleManager;
import com.hyades.client.render.DrawContext;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;

/**
 * HUD 渲染器（单例）。在 {@code Hud#extractRenderState} 注入点渲染所有启用的 HUD 元素，
 * 并支持拖拽编辑（需开启 HUD 编辑模式）。
 */
public final class HudRenderer {

    public static final HudRenderer INSTANCE = new HudRenderer();

    private final List<HudElementModule> elements = new ArrayList<>();
    private boolean visible = true;
    private boolean dragMode;
    private HudElement dragging;
    private float dragOffsetX;
    private float dragOffsetY;

    private HudRenderer() {
    }

    public void register(HudElementModule element) {
        if (!this.elements.contains(element)) {
            this.elements.add(element);
            ModuleManager.INSTANCE.register(element);
        }
    }

    public List<HudElementModule> getElements() {
        return this.elements;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void toggleDragMode() {
        this.dragMode = !this.dragMode;
        if (!this.dragMode) {
            this.dragging = null;
        }
    }

    public boolean isDragMode() {
        return this.dragMode;
    }

    public void render(DrawContext ctx, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        float mouseX = (float) minecraft.mouseHandler.getScaledXPos(minecraft.getWindow());
        float mouseY = (float) minecraft.mouseHandler.getScaledYPos(minecraft.getWindow());
        float deltaTicks = deltaTracker.getGameTimeDeltaTicks();

        this.handleDrag(minecraft, mouseX, mouseY);
        if (!this.visible) {
            return;
        }
        for (HudElementModule element : this.elements) {
            if (element.isEnabled() && element.getHudElement().isVisible()) {
                element.getHudElement().render(ctx, mouseX, mouseY, deltaTicks);
            }
        }
        // 编辑模式提示框
        if (this.dragMode) {
            ctx.fillRect(0, 0, 110, 14, 0xAA000000);
            ctx.drawString("HUD Edit Mode", 3, 3, com.hyades.client.render.font.FontPresets.defaultFont(8.5f),
                    new com.hyades.client.render.Paint().setColor(Theme.ACCENT));
        }
    }

    private void handleDrag(Minecraft minecraft, float mouseX, float mouseY) {
        if (!this.dragMode) {
            return;
        }
        if (minecraft.mouseHandler.isLeftPressed()) {
            if (this.dragging == null) {
                for (HudElementModule element : this.elements) {
                    HudElement hud = element.getHudElement();
                    if (element.isEnabled() && hud.isVisible() && hud.contains(mouseX, mouseY)) {
                        this.dragging = hud;
                        this.dragOffsetX = mouseX - hud.getX();
                        this.dragOffsetY = mouseY - hud.getY();
                        break;
                    }
                }
            } else {
                float maxX = Math.max(0, minecraft.getWindow().getGuiScaledWidth() - this.dragging.getWidth());
                float maxY = Math.max(0, minecraft.getWindow().getGuiScaledHeight() - this.dragging.getHeight());
                this.dragging.setX(Math.max(0.0f, Math.min(mouseX - this.dragOffsetX, maxX)));
                this.dragging.setY(Math.max(0.0f, Math.min(mouseY - this.dragOffsetY, maxY)));
            }
        } else {
            this.dragging = null;
        }
    }
}