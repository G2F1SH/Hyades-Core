package com.hyades.client.gui;

import com.hyades.client.render.DrawContext;
import com.hyades.client.setting.MultiSelectSetting;
import java.util.Set;
import net.minecraft.client.input.MouseButtonEvent;

/**
 * 多选设置：点击在候选项中循环切换勾选。
 */
public class MultiSelectSettingElement extends SettingElement {

    private final MultiSelectSetting multiSelectSetting;
    private int currentOptionIndex;

    public MultiSelectSettingElement(MultiSelectSetting setting, float x, float y, float width) {
        super(setting, x, y, width);
        this.multiSelectSetting = setting;
        this.currentOptionIndex = 0;
    }

    @Override
    protected void renderControl(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
        Set<String> selected = this.multiSelectSetting.getSelected();
        String label = selected.isEmpty() ? "None" : String.join(", ", selected);
        if (label.length() > 24) {
            label = label.substring(0, 24) + "...";
        }
        int color = this.hovered ? Theme.PRIMARY : Theme.TEXT;
        this.drawRightText(ctx, label, color, PADDING_X);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        if (!this.hovered || event.button() != 0) {
            return false;
        }
        String[] options = this.multiSelectSetting.getOptions();
        if (options.length == 0) {
            return true;
        }
        String option = options[this.currentOptionIndex % options.length];
        this.multiSelectSetting.toggle(option);
        this.currentOptionIndex = (this.currentOptionIndex + 1) % options.length;
        return true;
    }
}