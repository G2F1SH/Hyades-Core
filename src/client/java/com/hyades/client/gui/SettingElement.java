package com.hyades.client.gui;

import com.hyades.client.render.DrawContext;
import com.hyades.client.render.Paint;
import com.hyades.client.render.RenderUtil;
import com.hyades.client.render.font.FontRenderer;
import com.hyades.client.render.font.FontPresets;
import com.hyades.client.setting.Setting;
import com.hyades.client.util.ColorUtil;

/**
 * 设置项行元素基类。每个设置占一行，左侧显示名称，右侧显示控件。
 */
public abstract class SettingElement extends UIElement {

    public static final float ROW_HEIGHT = 15.0f;
    public static final float PADDING_X = 6.0f;

    protected final Setting<?> setting;

    protected SettingElement(Setting<?> setting, float x, float y, float width) {
        super(x, y, width, ROW_HEIGHT);
        this.setting = setting;
    }

    public Setting<?> getSetting() {
        return this.setting;
    }

    @Override
    protected void renderElement(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
        FontRenderer font = FontPresets.defaultFont(9.5f);
        if (this.hovered) {
            ctx.fillRect(this.x, this.y, this.width, this.height, Theme.HOVER_OVERLAY);
        }
        ctx.drawString(this.setting.getName(), this.x + PADDING_X,
                this.y + (this.height - font.getHeight()) / 2.0f - 1.0f,
                font, new Paint().setColor(Theme.TEXT_DIM));
        this.renderControl(ctx, mouseX, mouseY, deltaTicks);
    }

    protected abstract void renderControl(DrawContext ctx, float mouseX, float mouseY, float deltaTicks);

    protected void drawRightText(DrawContext ctx, String text, int color, float rightPadding) {
        FontRenderer font = FontPresets.defaultFont(9.0f);
        float textWidth = font.getStringWidth(text);
        RenderUtil.drawCenteredString(ctx, text, this.x + this.width - rightPadding - textWidth / 2.0f,
                this.y + (this.height - font.getHeight()) / 2.0f - 1.0f, font, color);
    }
}