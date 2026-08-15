package com.hyades.client.render.font;

import com.hyades.client.render.DrawContext;
import com.hyades.client.render.Paint;
import com.hyades.client.util.ColorUtil;
import java.util.List;

/**
 * 逻辑字体句柄：字体名 + 字号，封装 § 格式码解析、换行与字形绘制。
 */
public class FontRenderer {

    private final String name;
    private final float size;
    private final CustomFont font;

    public FontRenderer(String name, float size, CustomFont font) {
        this.name = name;
        this.size = size;
        this.font = font;
    }

    public String getName() {
        return this.name;
    }

    public float getSize() {
        return this.size;
    }

    public CustomFont getCustomFont() {
        return this.font;
    }

    public FontMetrics getMetrics() {
        return this.font.getMetrics();
    }

    public float getHeight() {
        return this.font.getMetrics().height();
    }

    public float getStringWidth(String text) {
        if (text == null || text.isEmpty()) {
            return 0.0f;
        }
        float width = 0.0f;
        boolean formatting = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (formatting) {
                formatting = false;
                continue;
            }
            if (c == '\u00a7') {
                formatting = true;
                continue;
            }
            if (c == '\n') {
                continue;
            }
            width += this.font.getGlyph(c).advance();
        }
        return width;
    }

    /**
     * 绘制文本（支持 § 颜色码与 \n 换行）。
     *
     * @param y 基线（baseline）Y 坐标
     */
    public void drawString(DrawContext ctx, String text, float x, float y, Paint paint) {
        if (text == null || text.isEmpty()) {
            return;
        }
        int color = paint.getFinalColor();
        float lineY = y;
        List<String> lines = java.util.Arrays.asList(text.split("\n", -1));
        for (int i = 0; i < lines.size(); i++) {
            this.drawSingleLine(ctx, lines.get(i), x, lineY, color);
            lineY += this.getHeight();
        }
    }

    public void drawString(DrawContext ctx, String text, float x, float y, int color) {
        this.drawString(ctx, text, x, y, new Paint().setColor(color));
    }

    /** 绘制彩虹字（逐字符取色） */
    public void drawStringRainbow(DrawContext ctx, String text, float x, float y, float speed, float offset) {
        float cursor = x;
        boolean formatting = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (formatting) {
                formatting = false;
                continue;
            }
            if (c == '\u00a7') {
                formatting = true;
                continue;
            }
            int rainbow = ColorUtil.getRainbowColor(speed, offset + i * 0.02f, 0.6f, 1.0f);
            Glyph glyph = this.font.getGlyph(c);
            this.drawGlyph(ctx, glyph, cursor, y, rainbow);
            cursor += glyph.advance();
        }
    }

    private void drawSingleLine(DrawContext ctx, String text, float x, float y, int defaultColor) {
        int currentColor = defaultColor;
        float cursor = x;
        boolean formatting = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (formatting) {
                Integer codeColor = MinecraftColorMap.getColor(c);
                if (codeColor != null) {
                    currentColor = codeColor;
                } else if (Character.toLowerCase(c) == 'r') {
                    currentColor = defaultColor;
                }
                formatting = false;
                continue;
            }
            if (c == '\u00a7') {
                formatting = true;
                continue;
            }
            Glyph glyph = this.font.getGlyph(c);
            this.drawGlyph(ctx, glyph, cursor, y, currentColor);
            cursor += glyph.advance();
        }
    }

    private void drawGlyph(DrawContext ctx, Glyph glyph, float x, float baselineY, int color) {
        if (glyph.isSpace() || glyph.width() <= 0.0f || glyph.height() <= 0.0f || glyph.page() == null) {
            return;
        }
        float sx = x + glyph.xOffset();
        float sy = baselineY + glyph.yOffset();
        GlyphPage page = glyph.page();
        ctx.drawTexturedSubRect(page.getIdentifier(), sx, sy, glyph.width(), glyph.height(),
                glyph.u0(), glyph.v0(), GlyphPage.SIZE, GlyphPage.SIZE, color);
    }
}