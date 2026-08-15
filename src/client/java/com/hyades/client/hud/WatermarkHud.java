package com.hyades.client.hud;

import com.hyades.client.gui.Theme;
import com.hyades.client.render.DrawContext;
import com.hyades.client.render.LinearGradient;
import com.hyades.client.render.Paint;
import com.hyades.client.render.effect.TextGlow;
import com.hyades.client.render.font.FontRenderer;
import com.hyades.client.render.font.FontPresets;

/**
 * 左上角水印：品牌渐变胶囊 + 发光标题。
 */
public class WatermarkHud extends HudElement {

    public WatermarkHud() {
        super("Watermark", 4, 4);
    }

    @Override
    public float getWidth() {
        FontRenderer title = FontPresets.axiformaBold(11.0f);
        FontRenderer version = FontPresets.defaultFont(8.0f);
        return title.getStringWidth("HYADES") + 34.0f + version.getStringWidth("v1.0.0") + 6.0f;
    }

    @Override
    public float getHeight() {
        return 19.0f;
    }

    @Override
    protected void renderElement(DrawContext ctx, float mouseX, float mouseY, float deltaTicks) {
        float w = this.getWidth();
        float h = this.getHeight();

        ctx.drawRoundedRect(this.x, this.y, w, h, 5.0f, 0xB40B0E12);
        ctx.outlineRect(this.x, this.y, w, h, 1.0f, Theme.BORDER);
        ctx.drawRoundedRectGradient(this.x, this.y, 3.0f, h, 2.0f,
                new LinearGradient(Theme.GRADIENT_TOP, Theme.ACCENT, 90.0f));

        FontRenderer title = FontPresets.axiformaBold(11.0f);
        FontRenderer version = FontPresets.defaultFont(8.0f);
        float baseline = this.y + (h - title.getHeight()) / 2.0f + title.getMetrics().ascent();

        TextGlow.drawGlowingText(ctx, "HYADES", this.x + 9.0f, baseline, 0xFFFFFFFF, Theme.PRIMARY, 2.0f, 0.35f, title);
        ctx.drawString("v1.0.0", this.x + 9.0f + title.getStringWidth("HYADES") + 6.0f,
                this.y + (h - version.getHeight()) / 2.0f + version.getMetrics().ascent(),
                version, new Paint().setColor(Theme.TEXT_DIM));
    }
}