package com.hyades.client.gui;

import com.hyades.client.render.DrawContext;
import com.hyades.client.setting.ModeSetting;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * 模式设置：点击循环切换模式。
 */
public class ModeSettingElement extends SettingElement {

    private final ModeSetting modeSetting;

    public ModeSettingElement(ModeSetting setting, float x, float y, float width) {
        super(setting, x, y, width);
        this.modeSetting = setting;
    }

    @Override
    protected void renderControl(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
        String mode = this.modeSetting.getMode();
        int color = this.hovered ? Theme.PRIMARY : Theme.TEXT;
        this.drawRightText(ctx, mode, color, PADDING_X);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        if (!this.hovered) {
            return false;
        }
        if (event.button() == 0) {
            this.modeSetting.cycle();
            return true;
        }
        if (event.button() == 1) {
            String[] modes = this.modeSetting.getModes();
            int index = 0;
            for (int i = 0; i < modes.length; i++) {
                if (modes[i].equals(this.modeSetting.getMode())) {
                    index = i;
                    break;
                }
            }
            this.modeSetting.setMode(modes[(index + modes.length - 1) % modes.length]);
            return true;
        }
        return false;
    }
}