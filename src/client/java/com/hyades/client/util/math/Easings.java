package com.hyades.client.util.math;

/**
 * 缓动函数库。
 * <p>
 * 提供常用的 In / Out / InOut 缓动函数，全部基于 {@link Easing} 函数式接口。
 * 幂次缓动通过 {@link #easeIn(int)} / {@link #easeOut(int)} / {@link #easeInOut(int)} 生成。
 */
public final class Easings {

    public static final Easing LINEAR = t -> t;

    public static final Easing EASE_IN_QUAD = t -> t * t;
    public static final Easing EASE_OUT_QUAD = t -> 1.0 - (t - 1.0) * (t - 1.0);
    public static final Easing EASE_IN_OUT_QUAD = t -> t < 0.5 ? 2.0 * t * t : 1.0 - Math.pow(-2.0 * t + 2.0, 2.0) / 2.0;

    public static final Easing EASE_IN_CUBIC = t -> t * t * t;
    public static final Easing EASE_OUT_CUBIC = t -> 1.0 - Math.pow(1.0 - t, 3.0);
    public static final Easing EASE_IN_OUT_CUBIC = t -> t < 0.5 ? 4.0 * t * t * t : 1.0 - Math.pow(-2.0 * t + 2.0, 3.0) / 2.0;

    public static final Easing EASE_OUT_SINE = t -> Math.sin(t * Math.PI / 2.0);
    public static final Easing EASE_IN_SINE = t -> 1.0 - Math.cos(t * Math.PI / 2.0);
    public static final Easing EASE_IN_OUT_SINE = t -> -(Math.cos(Math.PI * t) - 1.0) / 2.0;

    public static final Easing EASE_OUT_EXPO = t -> t >= 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * t);
    public static final Easing EASE_IN_EXPO = t -> t <= 0.0 ? 0.0 : Math.pow(2.0, 10.0 * t - 10.0);
    public static final Easing EASE_IN_OUT_EXPO = t -> {
        if (t <= 0.0) {
            return 0.0;
        }
        if (t >= 1.0) {
            return 1.0;
        }
        return t < 0.5 ? Math.pow(2.0, 20.0 * t - 10.0) / 2.0 : (2.0 - Math.pow(2.0, -20.0 * t + 10.0)) / 2.0;
    };

    /** 回弹过冲的 easeOut（back） */
    public static final Easing EASE_OUT_BACK = t -> {
        double c1 = 1.70158;
        double c3 = c1 + 1.0;
        return 1.0 + c3 * Math.pow(t - 1.0, 3.0) + c1 * Math.pow(t - 1.0, 2.0);
    };
    public static final Easing EASE_IN_BACK = t -> {
        double c1 = 1.70158;
        double c3 = c1 + 1.0;
        return c3 * t * t * t - c1 * t * t;
    };

    /** 弹性回弹的 easeOut */
    public static final Easing EASE_OUT_ELASTIC = t -> {
        if (t <= 0.0) {
            return 0.0;
        }
        if (t >= 1.0) {
            return 1.0;
        }
        double c4 = 2.0 * Math.PI / 3.0;
        return Math.pow(2.0, -10.0 * t) * Math.sin((t * 10.0 - 0.75) * c4) + 1.0;
    };

    /** 弹跳的 easeOut */
    public static final Easing EASE_OUT_BOUNCE = t -> {
        double n1 = 7.5625;
        double d1 = 2.75;
        if (t < 1.0 / d1) {
            return n1 * t * t;
        }
        if (t < 2.0 / d1) {
            return n1 * (t -= 1.5 / d1) * t + 0.75;
        }
        if (t < 2.5 / d1) {
            return n1 * (t -= 2.25 / d1) * t + 0.9375;
        }
        return n1 * (t -= 2.625 / d1) * t + 0.984375;
    };

    private Easings() {
    }

    /** 生成任意幂次的 easeIn */
    public static Easing easeIn(int power) {
        return t -> Math.pow(t, power);
    }

    /** 生成任意幂次的 easeOut */
    public static Easing easeOut(int power) {
        return t -> 1.0 - Math.pow(1.0 - t, power);
    }

    /** 生成任意幂次的 easeInOut */
    public static Easing easeInOut(int power) {
        return t -> t < 0.5 ? Math.pow(2.0, power - 1.0) * Math.pow(t, power) : 1.0 - Math.pow(-2.0 * t + 2.0, power) / 2.0;
    }

    public static final Easing EASE_OUT_POW2 = easeOut(2);
    public static final Easing EASE_OUT_POW3 = easeOut(3);
    public static final Easing EASE_OUT_POW4 = easeOut(4);
    public static final Easing EASE_OUT_POW5 = easeOut(5);

    /** 反向（翻转）一个缓动函数 */
    public static Easing reversed(Easing easing) {
        return t -> 1.0 - easing.ease(1.0 - t);
    }
}
