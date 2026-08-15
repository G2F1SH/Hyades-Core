package com.hyades.client.render.font;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

/**
 * 一页字形图集（256×256 纹理），按行打包字形，负责把字形像素上传到 GPU 并注册到纹理管理器。
 */
public class GlyphPage {

    public static final int SIZE = 256;
    private static final AtomicInteger PAGE_COUNTER = new AtomicInteger();

    private final Identifier identifier;
    private final String label;
    private final NativeImage image;
    private final DynamicTexture texture;
    private int cursorX;
    private int cursorY;
    private int rowHeight;

    public GlyphPage(Identifier pageIdentifier) {
        this.identifier = pageIdentifier;
        this.label = pageIdentifier.toString();
        this.image = new NativeImage(SIZE, SIZE, true);
        this.texture = new DynamicTexture(() -> this.label, this.image);
        Minecraft.getInstance().getTextureManager().register(this.identifier, this.texture);
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    /** 当前页面能否容纳 width×height 的字形（必要时自动换行） */
    public boolean canFit(int width, int height) {
        if (width > SIZE || height > SIZE) {
            return false;
        }
        if (this.cursorX + width > SIZE) {
            return this.cursorY + this.rowHeight + height <= SIZE;
        }
        return this.cursorY + this.rowHeight + height <= SIZE;
    }

    /**
     * 将字形像素放入图集，返回对应的 {@link Glyph}（UV 为图集内的像素坐标）。
     * 若图集已满则返回 null，调用方应新建页面。
     */
    public Glyph place(char character, BufferedImage glyphImage, float advance, float xOffset, float yOffset) {
        int width = glyphImage.getWidth();
        int height = glyphImage.getHeight();
        if (width > SIZE || height > SIZE) {
            return null;
        }
        if (this.cursorX + width > SIZE) {
            this.cursorX = 0;
            this.cursorY += this.rowHeight;
            this.rowHeight = 0;
        }
        if (this.cursorY + height > SIZE) {
            return null;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = glyphImage.getRGB(x, y);
                this.image.setPixel(this.cursorX + x, this.cursorY + y, argb);
            }
        }

        float u0 = this.cursorX;
        float v0 = this.cursorY;
        float u1 = this.cursorX + width;
        float v1 = this.cursorY + height;
        this.cursorX += width + 1;
        this.rowHeight = Math.max(this.rowHeight, height);

        return new Glyph(character, width, height, u0, v0, u1, v1, xOffset, yOffset, advance, this);
    }

    /** 把当前页面像素上传到 GPU */
    public void upload() {
        this.texture.upload();
    }

    public static Identifier nextIdentifier(String prefix) {
        return Identifier.fromNamespaceAndPath("hyades-core", "font/" + prefix + "-page" + PAGE_COUNTER.incrementAndGet());
    }
}