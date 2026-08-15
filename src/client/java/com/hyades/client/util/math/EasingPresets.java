package com.hyades.client.util.math;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓动预设库，对 {@link Easings} 与常用缓动曲线的语义化封装。
 */
public final class EasingPresets {

    private static final Map<String, Easing> NAMED = new ConcurrentHashMap<>();

    public static final Easing LINEAR = Easings.LINEAR;
    public static final Easing EASE_OUT_QUAD = Easings.EASE_OUT_QUAD;
    public static final Easing EASE_OUT_CUBIC = Easings.EASE_OUT_CUBIC;
    public static final Easing EASE_OUT_SINE = Easings.EASE_OUT_SINE;
    public static final Easing EASE_OUT_BACK = Easings.EASE_OUT_BACK;
    public static final Easing EASE_OUT_ELASTIC = Easings.EASE_OUT_ELASTIC;
    public static final Easing EASE_OUT_BOUNCE = Easings.EASE_OUT_BOUNCE;

    /** 类似 Material Design 的标准缓动 */
    public static final Easing STANDARD = new CubicBezier(0.2f, 0.0f, 0.0f, 1.0f);
    /** 进入：先快后慢 */
    public static final Easing ACCELERATE = new CubicBezier(0.3f, 0.0f, 1.0f, 1.0f);
    /** 离开：先慢后快 */
    public static final Easing DECELERATE = new CubicBezier(0.0f, 0.0f, 0.0f, 1.0f);
    /** 强调的弹性离开 */
    public static final Easing EMERGING = new CubicBezier(0.4f, 0.0f, 1.0f, 1.0f);

    private EasingPresets() {
    }

    public static Easing byName(String name) {
        Easing easing = NAMED.get(name);
        if (easing != null) {
            return easing;
        }
        Easing created = switch (name) {
            case "LINEAR" -> LINEAR;
            case "EASE_OUT_QUAD" -> EASE_OUT_QUAD;
            case "EASE_OUT_CUBIC" -> EASE_OUT_CUBIC;
            case "EASE_OUT_SINE" -> EASE_OUT_SINE;
            case "EASE_OUT_BACK" -> EASE_OUT_BACK;
            case "EASE_OUT_ELASTIC" -> EASE_OUT_ELASTIC;
            case "EASE_OUT_BOUNCE" -> EASE_OUT_BOUNCE;
            case "STANDARD" -> STANDARD;
            case "ACCELERATE" -> ACCELERATE;
            case "DECELERATE" -> DECELERATE;
            case "EMERGING" -> EMERGING;
            default -> null;
        };
        if (created == null) {
            created = LINEAR;
        }
        NAMED.put(name, created);
        return created;
    }
}
