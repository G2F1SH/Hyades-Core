package com.hyades.client.render;

import com.hyades.client.util.ColorUtil;

/**
 * 渲染工具函数（静态便捷封装）。
 */
public final class RenderUtil {

    private RenderUtil() {
    }

    /** 中心对齐填充矩形（宽高以中心为基准） */
    public static void drawCenteredRect(DrawContext ctx, float centerX, float centerY, float width, float height, int color) {
        ctx.fillRect(centerX - width / 2.0f, centerY - height / 2.0f, width, height, color);
    }

    /** 画一个填充的普通矩形 */
    public static void drawRect(DrawContext ctx, float x, float y, float width, float height, int color) {
        ctx.fillRect(x, y, width, height, color);
    }

    /** 垂直渐变矩形 */
    public static void drawGradientVertical(DrawContext ctx, float x, float y, float width, float height, int topColor, int bottomColor) {
        ctx.drawGradientVertical(x, y, width, height, topColor, bottomColor);
    }

    /** 水平渐变矩形 */
    public static void drawGradientHorizontal(DrawContext ctx, float x, float y, float width, float height, int leftColor, int rightColor) {
        ctx.drawGradientHorizontal(x, y, width, height, leftColor, rightColor);
    }

    /** 圆角矩形 */
    public static void drawRoundedRect(DrawContext ctx, float x, float y, float width, float height, float radius, int color) {
        ctx.drawRoundedRect(x, y, width, height, radius, color);
    }

    /** 半透明黑色遮罩（压暗一层） */
    public static void drawScrim(DrawContext ctx, float x, float y, float width, float height, float alpha) {
        ctx.fillRect(x, y, width, height, ColorUtil.withAlpha(0x000000, alpha));
    }

    /** 将文本绘制在给定宽度的水平居中位置 */
    public static void drawCenteredString(DrawContext ctx, String text, float centerX, float y,
                                          com.hyades.client.render.font.FontRenderer font, int color) {
        float width = font.getStringWidth(text);
        ctx.drawString(text, centerX - width / 2.0f, y, font, new Paint().setColor(color));
    }
}