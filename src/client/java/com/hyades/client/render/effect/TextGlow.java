package com.hyades.client.render.effect;

import com.hyades.client.render.DrawContext;
import com.hyades.client.render.Paint;
import com.hyades.client.render.font.FontRenderer;
import com.hyades.client.util.ColorUtil;

/**
 * CPU 文本发光效果：以主色绘制文本并叠加多层半透明光晕（八方向偏移），
 * 无需 GPU 后处理，作为真实高斯辉光的轻量替代。
 */
public final class TextGlow {

    private static final int[] OFFSETS_X = {0, 1, 1, 1, 0, -1, -1, -1};
    private static final int[] OFFSETS_Y = {-1, -1, 0, 1, 1, 1, 0, -1};

    private TextGlow() {
    }

    /**
     * 绘制带辉光的文本。
     *
     * @param glowRadius 光晕半径（像素）
     * @param glowAlpha  光晕不透明度 0.0~1.0
     */
    public static void drawGlowingText(DrawContext ctx, String text, float x, float y,
                                       int textColor, int glowColor, float glowRadius, float glowAlpha,
                                       FontRenderer font) {
        if (glowRadius <= 0.0f) {
            ctx.drawString(text, x, y, font, new Paint().setColor(textColor));
            return;
        }
        float alphaStep = glowAlpha / (glowRadius + 1.0f);
        for (float r = glowRadius; r >= 0.0f; r -= 1.0f) {
            float layerAlpha = alphaStep * (glowRadius - r + 1.0f);
            int layerColor = ColorUtil.withAlpha(glowColor, Math.min(1.0f, layerAlpha));
            for (int d = 0; d < OFFSETS_X.length; d++) {
                float dx = OFFSETS_X[d] * r;
                float dy = OFFSETS_Y[d] * r;
                ctx.drawString(text, x + dx, y + dy, font, new Paint().setColor(layerColor));
            }
        }
        ctx.drawString(text, x, y, font, new Paint().setColor(textColor));
    }
}