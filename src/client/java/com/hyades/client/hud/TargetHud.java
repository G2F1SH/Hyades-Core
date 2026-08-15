package com.hyades.client.hud;

import com.hyades.client.gui.Theme;
import com.hyades.client.render.DrawContext;
import com.hyades.client.render.Paint;
import com.hyades.client.render.font.FontRenderer;
import com.hyades.client.render.font.FontPresets;
import com.hyades.client.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * 目标信息 HUD：显示准星指向实体的名称与生命值条。默认关闭。
 */
public class TargetHud extends HudElement {

    private static final float WIDTH = 130.0f;
    private static final float HEIGHT = 26.0f;

    public TargetHud() {
        super("Target", 4, 26);
    }

    @Override
    public float getWidth() {
        return WIDTH;
    }

    @Override
    public float getHeight() {
        return HEIGHT;
    }

    @Override
    protected void renderElement(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity target = minecraft.crosshairPickEntity;
        if (!(target instanceof LivingEntity living)) {
            return;
        }

        float w = this.getWidth();
        float h = this.getHeight();
        ctx.drawRoundedRect(this.x, this.y, w, h, 5.0f, 0xB40B0E12);
        ctx.outlineRect(this.x, this.y, w, h, 1.0f, Theme.BORDER);
        ctx.drawRoundedRectGradient(this.x, this.y, 3.0f, h, 2.0f,
                new com.hyades.client.render.LinearGradient(Theme.DANGER, Theme.WARNING, 90.0f));

        FontRenderer nameFont = FontPresets.axiformaBold(9.5f);
        String name = living.getName().getString();
        if (name.length() > 16) {
            name = name.substring(0, 16);
        }
        ctx.drawString(name, this.x + 8.0f, this.y + 5.0f, nameFont, new Paint().setColor(Theme.TEXT));

        // 生命条
        float health = living.getHealth();
        float maxHealth = living.getMaxHealth();
        float ratio = maxHealth <= 0.0f ? 0.0f : Math.max(0.0f, Math.min(1.0f, health / maxHealth));
        int healthColor = ratio > 0.6f ? Theme.ACCENT : ratio > 0.3f ? Theme.WARNING : Theme.DANGER;
        float barX = this.x + 8.0f;
        float barY = this.y + h - 8.0f;
        float barWidth = w - 16.0f;
        ctx.drawRoundedRect(barX, barY, barWidth, 3.0f, 1.5f, ColorUtil.withAlpha(0xFFFFFF, 0.25f));
        ctx.drawRoundedRect(barX, barY, barWidth * ratio, 3.0f, 1.5f, healthColor);

        String healthText = (int) Math.ceil(health) + " / " + (int) Math.ceil(maxHealth);
        FontRenderer healthFont = FontPresets.defaultFont(7.5f);
        ctx.drawString(healthText, this.x + 8.0f, this.y + 11.0f, healthFont, new Paint().setColor(Theme.TEXT_DIM));
    }
}