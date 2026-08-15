package com.hyades.client.render.effect;

import net.minecraft.util.ARGB;

/**
 * CPU 高斯模糊，用于阴影 / 发光等离线像素处理。
 * <p>
 * 采用一维高斯核的水平 + 垂直两趟分离卷积，复杂度 O(n * radius)。
 */
public final class GaussianBlur {

    private GaussianBlur() {
    }

    /**
     * 对像素数组执行高斯模糊。
     *
     * @param pixels 原始像素（0xAARRGGBB）
     * @param width  图像宽
     * @param height 图像高
     * @param radius 模糊半径（>= 1）
     * @return 模糊后的新数组；radius <= 1 时返回原数组
     */
    public static int[] blur(int[] pixels, int width, int height, int radius) {
        if (radius <= 1 || pixels.length != width * height) {
            return pixels;
        }
        radius = Math.min(radius, 24);
        float[] kernel = buildKernel(radius);
        int[] tmp = new int[pixels.length];
        int[] out = new int[pixels.length];
        blurHorizontal(pixels, tmp, width, height, radius, kernel);
        blurHorizontal(tmp, out, width, height, radius, kernel);
        return out;
    }

    private static float[] buildKernel(int radius) {
        float sigma = Math.max(1.0f, radius / 2.5f);
        float twoSigma2 = 2.0f * sigma * sigma;
        int size = radius * 2 + 1;
        float[] kernel = new float[size];
        float sum = 0.0f;
        for (int i = -radius; i <= radius; i++) {
            float weight = (float) Math.exp(-(i * i) / twoSigma2);
            kernel[i + radius] = weight;
            sum += weight;
        }
        for (int i = 0; i < size; i++) {
            kernel[i] /= sum;
        }
        return kernel;
    }

    private static void blurHorizontal(int[] src, int[] dst, int width, int height, int radius, float[] kernel) {
        int center = radius;
        for (int y = 0; y < height; y++) {
            int rowStart = y * width;
            for (int x = 0; x < width; x++) {
                int r = 0, g = 0, b = 0, a = 0;
                for (int i = -radius; i <= radius; i++) {
                    int sampleX = Math.max(0, Math.min(width - 1, x + i));
                    int color = src[rowStart + sampleX];
                    float weight = kernel[i + center];
                    r += (color & 0x00FF0000) * weight;
                    g += (color & 0x0000FF00) * weight;
                    b += (color & 0x000000FF) * weight;
                    a += ((color >>> 24) & 0xFF) * weight;
                }
                dst[rowStart + x] = (int) a << 24 | (int) r & 0x00FF0000 | (int) g & 0x0000FF00 | (int) b & 0x000000FF;
            }
        }
    }

    /** 对模糊结果做轻微不透明度混合（可用于调整发光强度） */
    public static int[] adjust(int[] pixels, int width, int height, float alphaMul) {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = ARGB.color(Math.round(ARGB.alpha(pixels[i]) * alphaMul), pixels[i]);
        }
        return pixels;
    }
}