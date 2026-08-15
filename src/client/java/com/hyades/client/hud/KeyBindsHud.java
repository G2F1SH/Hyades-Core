package com.hyades.client.hud;

import com.hyades.client.gui.Theme;
import com.hyades.client.module.Module;
import com.hyades.client.module.ModuleManager;
import com.hyades.client.render.DrawContext;
import com.hyades.client.render.Paint;
import com.hyades.client.render.font.FontRenderer;
import com.hyades.client.render.font.FontPresets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

/**
 * 键位速查：列出所有绑定按键的模块（右下角）。默认关闭。
 */
public class KeyBindsHud extends HudElement {

    private static final float ROW_HEIGHT = 10.0f;
    private static final float PADDING = 5.0f;

    public KeyBindsHud() {
        super("KeyBinds", -1, -1);
    }

    private List<Module> getBoundModules() {
        List<Module> bound = new ArrayList<>();
        for (Module module : ModuleManager.INSTANCE.getModules()) {
            if (module.getKeyBind() != 0) {
                bound.add(module);
            }
        }
        return bound;
    }

    private float getRowWidth() {
        FontRenderer nameFont = FontPresets.defaultFont(9.0f);
        FontRenderer keyFont = FontPresets.defaultFont(8.5f);
        float max = 0.0f;
        for (Module module : this.getBoundModules()) {
            max = Math.max(max, nameFont.getStringWidth(module.getName()) + 10.0f + keyFont.getStringWidth(module.getKeyBindName()));
        }
        return max;
    }

    @Override
    public float getWidth() {
        return this.getRowWidth() + PADDING * 2;
    }

    @Override
    public float getHeight() {
        return this.getBoundModules().size() * ROW_HEIGHT + PADDING;
    }

    @Override
    protected void renderElement(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!this.isMoved()) {
            this.x = minecraft.getWindow().getGuiScaledWidth() - this.getWidth() - 4.0f;
            this.y = minecraft.getWindow().getGuiScaledHeight() - this.getHeight() - 4.0f;
        }
        List<Module> bound = this.getBoundModules();
        if (bound.isEmpty()) {
            return;
        }

        FontRenderer nameFont = FontPresets.defaultFont(9.0f);
        FontRenderer keyFont = FontPresets.defaultFont(8.5f);
        float width = this.getWidth();
        float height = bound.size() * ROW_HEIGHT + PADDING;
        ctx.drawRoundedRect(this.x, this.y, width, height, 5.0f, 0xB40B0E12);
        ctx.outlineRect(this.x, this.y, width, height, 1.0f, Theme.BORDER);

        float rowY = this.y + PADDING / 2.0f;
        for (Module module : bound) {
            ctx.drawString(module.getName(), this.x + PADDING, rowY + 1.0f, nameFont, new Paint().setColor(Theme.TEXT));
            String key = module.getKeyBindName();
            float keyWidth = keyFont.getStringWidth(key);
            ctx.drawString(key, this.x + width - PADDING - keyWidth, rowY + 1.0f, keyFont, new Paint().setColor(Theme.PRIMARY));
            rowY += ROW_HEIGHT;
        }
    }
}