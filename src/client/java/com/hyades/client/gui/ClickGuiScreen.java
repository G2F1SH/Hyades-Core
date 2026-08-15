package com.hyades.client.gui;

import com.hyades.client.render.DrawContext;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * ClickGUI 屏幕。渲染可拖拽的分类面板，接收鼠标/键盘事件。
 */
public class ClickGuiScreen extends Screen {

    private final CategoryPanel panel;
    private float mouseX;
    private float mouseY;

    public ClickGuiScreen() {
        super(Component.literal("Hyades ClickGUI"));
        this.panel = new CategoryPanel(0, 0, this);
    }

    @Override
    protected void init() {
        float x = (this.width - CategoryPanel.WIDTH) / 2.0f;
        float y = (this.height - this.panel.getHeight()) / 2.0f;
        this.panel.setPosition(x, y);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTicks) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        DrawContext ctx = new DrawContext(graphics);
        ctx.fillRect(0, 0, graphics.guiWidth(), graphics.guiHeight(), 0x3D000000);
        this.panel.render(ctx, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        if (this.panel.mouseClicked(event, doubleClicked)) {
            return true;
        }
        return super.mouseClicked(event, doubleClicked);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.panel.mouseReleased(event)) {
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (this.panel.mouseDragged(event, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (mouseX >= this.panel.getX() && mouseX <= this.panel.getX() + this.panel.getWidth()
                && mouseY >= this.panel.getY() && mouseY <= this.panel.getY() + this.panel.getHeight()) {
            this.panel.mouseScrolled(mouseX, mouseY, horizontal, vertical);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isEscape()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    /** 关闭屏幕 */
    public void close() {
        this.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }
}