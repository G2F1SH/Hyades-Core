package com.hyades.client.gui;

import com.hyades.client.render.DrawContext;
import com.hyades.client.render.Paint;
import com.hyades.client.render.font.FontRenderer;
import com.hyades.client.render.font.FontPresets;
import com.hyades.client.setting.BooleanSetting;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * 布尔设置：右侧开关按钮。
 */
public class BooleanSettingElement extends SettingElement {

    private final BooleanSetting booleanSetting;

    public BooleanSettingElement(BooleanSetting setting, float x, float y, float width) {
        super(setting, x, y, width);
        this.booleanSetting = setting;
    }

    @Override
    protected void renderControl(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
        boolean enabled = this.booleanSetting.isEnabled();
        float switchWidth = 24.0f;
        float switchHeight = 10.0f;
        float sx = this.x + this.width - switchWidth - PADDING_X;
        float sy = this.y + (this.height - switchHeight) / 2.0f;

        int base = enabled ? Theme.ACCENT : Theme.TEXT_DARK;
        ctx.drawRoundedRect(sx, sy, switchWidth, switchHeight, switchHeight / 2.0f, base);
        float knobX = enabled ? sx + switchWidth - switchHeight : sx;
        ctx.drawCircleFilled(knobX + switchHeight / 2.0f, sy + switchHeight / 2.0f, switchHeight / 2.0f - 1.5f, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        if (!this.hovered || event.button() != 0) {
            return false;
        }
        this.booleanSetting.toggle();
        return true;
    }
}