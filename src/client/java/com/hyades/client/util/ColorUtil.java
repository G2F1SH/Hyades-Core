package com.hyades.client.util;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

/**
 * 颜色工具类，基于 {@link ARGB}（MC 的 ARGB32 颜色工具）。
 * <p>
 * 所有颜色均以 {@code int}（0xAARRGGBB）表示，与 MC 26.2 约定一致。
 */
public final class ColorUtil {

    private ColorUtil() {
    }

    public static int withAlpha(int color, int alpha) {
        return ARGB.color(alpha, ARGB.red(color), ARGB.green(color), ARGB.blue(color));
    }

    public static int withAlpha(int color, float alpha) {
        return ARGB.color(alpha, color);
    }

    public static int rgb(int r, int g, int b) {
        return ARGB.color(r, g, b);
    }

    public static int argb(int a, int r, int g, int b) {
        return ARGB.color(a, r, g, b);
    }

    /** 线性插值两个颜色 */
    public static int lerpColor(int from, int to, float amount) {
        return ARGB.color(
                (int) Mth.lerp(amount, ARGB.alpha(from), ARGB.alpha(to)),
                (int) Mth.lerp(amount, ARGB.red(from), ARGB.red(to)),
                (int) Mth.lerp(amount, ARGB.green(from), ARGB.green(to)),
                (int) Mth.lerp(amount, ARGB.blue(from), ARGB.blue(to)));
    }

    /** 混合两个颜色（按 mix 比例取后者权重） */
    public static int blend(int colorA, int colorB, float mixB) {
        return lerpColor(colorA, colorB, mixB);
    }

    /** 生成彩虹色（HSB 色相循环），hue 0.0~1.0 */
    public static int getRainbowColor(float hue, float saturation, float brightness) {
        return HSBtoARGB(hue, saturation, brightness, 255);
    }

    /** 基于时间偏移的循环彩虹色 */
    public static int getRainbowColor(float speed, float offset, float saturation, float brightness) {
        double hue = (System.currentTimeMillis() / 1000.0f * speed + offset) % 1.0;
        return getRainbowColor((float) hue, saturation, brightness);
    }

    public static int HSBtoARGB(float hue, float saturation, float brightness, int alpha) {
        int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return ARGB.color(alpha, r, g, b);
    }

    public static float getAlpha(int color) {
        return ARGB.alphaFloat(color);
    }

    public static int getRed(int color) {
        return ARGB.red(color);
    }

    public static int getGreen(int color) {
        return ARGB.green(color);
    }

    public static int getBlue(int color) {
        return ARGB.blue(color);
    }

    public static int getAlphaInt(int color) {
        return ARGB.alpha(color);
    }

    /** 调暗一个颜色（乘以 factor，0.0~1.0） */
    public static int darken(int color, float factor) {
        return ARGB.scaleRGB(color, factor, factor, factor);
    }

    /** 调亮一个颜色 */
    public static int brighten(int color, float factor) {
        int r = Math.min(255, (int) (ARGB.red(color) * factor));
        int g = Math.min(255, (int) (ARGB.green(color) * factor));
        int b = Math.min(255, (int) (ARGB.blue(color) * factor));
        return ARGB.color(ARGB.alpha(color), r, g, b);
    }
}
