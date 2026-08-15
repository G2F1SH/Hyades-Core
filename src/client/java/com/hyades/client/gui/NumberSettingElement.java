package com.hyades.client.gui;

import com.hyades.client.render.DrawContext;
import com.hyades.client.render.Paint;
import com.hyades.client.render.font.FontRenderer;
import com.hyades.client.render.font.FontPresets;
import com.hyades.client.setting.NumberSetting;
import java.text.DecimalFormat;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * 数值设置：显示 {@code - value +} 控件。
 */
public class NumberSettingElement extends SettingElement {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.##");

    private final NumberSetting numberSetting;

    public NumberSettingElement(NumberSetting setting, float x, float y, float width) {
        super(setting, x, y, width);
        this.numberSetting = setting;
    }

    @Override
    protected void renderControl(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
        FontRenderer font = FontPresets.defaultFont(9.0f);
        String value = FORMAT.format(this.numberSetting.getDouble());
        float valueWidth = font.getStringWidth(value);
        float controlRight = this.x + this.width - PADDING_X;
        float cx = controlRight - valueWidth / 2.0f - 26.0f;
        float textY = this.y + (this.height - font.getHeight()) / 2.0f - 1.0f;

        // 减号 / 加号按钮
        ctx.drawString("-", cx - 22.0f, textY, font, new Paint().setColor(Theme.TEXT_DIM));
        ctx.drawString("+", cx + valueWidth / 2.0f + 18.0f, textY, font, new Paint().setColor(Theme.PRIMARY));
        ctx.drawString(value, cx, textY, font, new Paint().setColor(Theme.TEXT));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        if (!this.hovered) {
            return false;
        }
        if (event.button() == 0) {
            this.numberSetting.increment();
            return true;
        }
        if (event.button() == 1) {
            this.numberSetting.decrement();
            return true;
        }
        return false;
    }
}