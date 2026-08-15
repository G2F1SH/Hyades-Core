package com.hyades.client.util.math;

/**
 * 帧率无关的插值工具。
 * <p>
 * 使用指数平滑（exponential smoothing）：{@code current = target + (current - target) * exp(-speed * delta)}，
 * 使动画速度与帧率解耦。
 */
public final class LerpUtil {

    private static long lastTime = System.nanoTime();
    private static float delta = 0.0f;

    private LerpUtil() {
    }

    /**
     * 更新帧间时间差，应在每帧开始时调用一次。
     */
    public static void update() {
        long now = System.nanoTime();
        long diff = now - lastTime;
        lastTime = now;
        delta = diff / 1_000_000_000.0f;
        // 防止长时间卡顿导致的巨大 delta（例如窗口拖拽 / 断点）
        if (delta > 0.1f) {
            delta = 0.1f;
        }
    }

    public static float getDelta() {
        return delta;
    }

    /** 帧率无关的线性插值，speed 越大收敛越快 */
    public static float lerp(float current, float target, float speed) {
        float factor = 1.0f - (float) Math.exp(-speed * delta);
        return current + (target - current) * factor;
    }

    public static double lerp(double current, double target, double speed) {
        double factor = 1.0 - Math.exp(-speed * delta);
        return current + (target - current) * factor;
    }

    /** 普通（非帧率相关）线性插值 */
    public static float lerpPlain(float current, float target, float amount) {
        return current + (target - current) * amount;
    }

    public static double lerpPlain(double current, double target, double amount) {
        return current + (target - current) * amount;
    }

    public static float clamp(float value, float min, float max) {
        return value < min ? min : (Math.min(value, max));
    }
}
