package com.hyades.client.util.math;

/**
 * 三次贝塞尔缓动函数。
 * <p>
 * 以两个控制点 {@code (x1, y1)} / {@code (x2, y2)} 定义一条三次贝塞尔曲线，
 * 并将归一化时间 t 投影到曲线上（通过二分求根，保证单调曲线的 x 可逆）。
 * 控制点的 x 分量应位于 [0, 1] 区间以保证曲线单调递增。
 */
public class CubicBezier implements Easing {

    private final float x1;
    private final float y1;
    private final float x2;
    private final float y2;

    public CubicBezier(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public double ease(double t) {
        if (t <= 0.0) {
            return 0.0;
        }
        if (t >= 1.0) {
            return 1.0;
        }
        float s = solveForX((float) t);
        return bezierY(s);
    }

    /**
     * 在曲线上根据目标 x 求参数 s（二分搜索，epsilon = 1e-5）。
     */
    private float solveForX(float targetX) {
        float low = 0.0f;
        float high = 1.0f;
        float mid = 0.5f;
        while (high - low > 1.0e-5f) {
            mid = (low + high) / 2.0f;
            float x = bezierX(mid);
            if (x < targetX) {
                low = mid;
            } else {
                high = mid;
            }
        }
        return mid;
    }

    private float bezierX(float s) {
        float u = 1.0f - s;
        return 3.0f * u * u * s * x1 + 3.0f * u * s * s * x2 + s * s * s;
    }

    private float bezierY(float s) {
        float u = 1.0f - s;
        return 3.0f * u * u * s * y1 + 3.0f * u * s * s * y2 + s * s * s;
    }
}
