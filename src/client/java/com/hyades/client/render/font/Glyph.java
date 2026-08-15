package com.hyades.client.render.font;

/**
 * 单个字形：屏幕尺寸、所在图集页的像素 UV、绘制偏移与步进。
 */
public record Glyph(char character, float width, float height,
                    float u0, float v0, float u1, float v1,
                    float xOffset, float yOffset, float advance,
                    GlyphPage page) {

    public boolean isSpace() {
        return this.character == ' ';
    }
}