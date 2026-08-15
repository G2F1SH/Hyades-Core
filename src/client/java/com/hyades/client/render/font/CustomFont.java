package com.hyades.client.render.font;

import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义字体：基于 AWT {@link Font} 将字形光栅化到 {@link GlyphPage} 图集，
 * 绘制时按纹理页批量 blit，支持任意颜色（图集内存储白色字形，绘制时乘以目标色）。
 */
public class CustomFont {

    /** 光栅化放大倍数，保证高 DPI 下清晰 */
    private static final float RASTER_SCALE = 2.0f;

    private final String name;
    private final float size;
    private final Font awtFont;
    private final FontMetrics metrics;
    private final FontRenderContext frc = new FontRenderContext(null, true, true);

    private final List<GlyphPage> pages = new ArrayList<>();
    private final Map<Character, Glyph> glyphCache = new HashMap<>();
    private final float spaceWidth;
    private Glyph spaceGlyph;

    public CustomFont(String name, float size, Font awtFont) {
        this.name = name;
        this.size = size;
        this.awtFont = awtFont;
        this.metrics = computeMetrics(awtFont);
        this.spaceWidth = computeSpaceWidth(awtFont);
        this.spaceGlyph = new Glyph(' ', this.spaceWidth, size, 0, 0, 0, 0, 0, 0, this.spaceWidth, null);
    }

    private float computeSpaceWidth(Font font) {
        try {
            GlyphVector vector = font.deriveFont(this.size * RASTER_SCALE).createGlyphVector(this.frc, " ");
            return (float) vector.getGlyphMetrics(0).getAdvanceX() / RASTER_SCALE;
        } catch (RuntimeException e) {
            return this.size * 0.5f;
        }
    }

    public String getName() {
        return this.name;
    }

    public float getSize() {
        return this.size;
    }

    public FontMetrics getMetrics() {
        return this.metrics;
    }

    private FontMetrics computeMetrics(Font font) {
        java.awt.image.BufferedImage dummy = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dummy.createGraphics();
        java.awt.FontMetrics fm = g2.getFontMetrics(font.deriveFont(this.size * RASTER_SCALE));
        float ascent = fm.getAscent() / RASTER_SCALE;
        float descent = fm.getDescent() / RASTER_SCALE;
        float height = fm.getHeight() / RASTER_SCALE;
        g2.dispose();
        float capHeight = this.size;
        try {
            Rectangle2D bounds = font.deriveFont(this.size * RASTER_SCALE).createGlyphVector(this.frc, "H").getVisualBounds();
            capHeight = (float) (bounds.getHeight() / RASTER_SCALE);
        } catch (RuntimeException ignored) {
            // 保持默认
        }
        return new FontMetrics(ascent, descent, height, capHeight);
    }

    public Glyph getGlyph(char character) {
        if (character == ' ') {
            return this.spaceGlyph;
        }
        Glyph cached = this.glyphCache.get(character);
        if (cached != null) {
            return cached;
        }
        Glyph glyph = this.rasterize(character);
        if (glyph == null) {
            glyph = this.spaceGlyph;
        }
        this.glyphCache.put(character, glyph);
        return glyph;
    }

    private Glyph rasterize(char character) {
        try {
            Font sizedFont = this.awtFont.deriveFont(this.size * RASTER_SCALE);
            GlyphVector vector = sizedFont.createGlyphVector(this.frc, new char[]{character});
            Rectangle2D bounds = vector.getGlyphPixelBounds(0, this.frc, 0, 0);
            if (bounds.getWidth() < 1.0 || bounds.getHeight() < 1.0) {
                return null;
            }
            int width = (int) Math.ceil(bounds.getWidth()) + 2;
            int height = (int) Math.ceil(bounds.getHeight()) + 2;
            float advance = (float) vector.getGlyphMetrics(0).getAdvanceX() / RASTER_SCALE;

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2.setColor(Color.WHITE);
            g2.setFont(sizedFont);
            g2.drawString(String.valueOf(character), (float) -bounds.getX() + 1.0f, (float) -bounds.getY() + 1.0f);
            g2.dispose();

            float xOffset = (float) bounds.getX() / RASTER_SCALE;
            float yOffset = (float) bounds.getY() / RASTER_SCALE;

            GlyphPage page = this.findOrCreatePage(width, height);
            if (page == null) {
                return null;
            }
            Glyph glyph = page.place(character, image, advance, xOffset, yOffset);
            if (glyph == null) {
                return null;
            }
            page.upload();
            return glyph;
        } catch (RuntimeException e) {
            return null;
        }
    }

    private GlyphPage findOrCreatePage(int width, int height) {
        for (GlyphPage page : this.pages) {
            if (page.canFit(width, height)) {
                return page;
            }
        }
        GlyphPage page = new GlyphPage(GlyphPage.nextIdentifier(this.name));
        this.pages.add(page);
        return page;
    }

    public List<GlyphPage> getPages() {
        return this.pages;
    }

    /** 预加载常用字符（仅生成字形缓存，不强制立即上传纹理） */
    public void preload(CharSequence characters) {
        for (int i = 0; i < characters.length(); i++) {
            char c = characters.charAt(i);
            if (c != ' ') {
                this.getGlyph(c);
            }
        }
    }
}