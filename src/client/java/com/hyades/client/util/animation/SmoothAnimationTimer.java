package com.hyades.client.util.animation;

import com.hyades.client.util.math.Easing;
import com.hyades.client.util.math.Easings;

/**
 * 带缓动与重定向能力的平滑动画计时器。
 * <p>
 * 调用 {@link #animate(double, double, Easing)} 会从当前值平滑过渡到目标值。
 * 若目标值与当前值/起点值相同，则会取消动画以避免重复触发。
 */
public class SmoothAnimationTimer {

    private double fromValue;
    private double toValue;
    private Easing easing;
    private long startTime;
    private long durationNanos;
    private boolean active;
    private double currentValue;

    public SmoothAnimationTimer() {
        this(0.0);
    }

    public SmoothAnimationTimer(double initialValue) {
        this.fromValue = initialValue;
        this.toValue = initialValue;
        this.currentValue = initialValue;
        this.easing = Easings.LINEAR;
        this.active = false;
    }

    public double getValueF() {
        return this.currentValue;
    }

    public double getToValue() {
        return this.toValue;
    }

    public void setFromValue(double fromValue) {
        this.fromValue = fromValue;
    }

    public void setToValue(double toValue) {
        this.toValue = toValue;
    }

    /** 直接设置当前值（不触发动画），并同步起点与终点 */
    public void setValue(double value) {
        this.currentValue = value;
        this.fromValue = value;
        this.toValue = value;
        this.active = false;
    }

    /**
     * 从当前值平滑过渡到目标值。
     *
     * @param target   目标值
     * @param duration 持续时间（秒）
     * @param easing   缓动函数
     */
    public void animate(double target, double duration, Easing easing) {
        if (duration <= 0) {
            this.setValue(target);
            return;
        }
        if (target == this.currentValue || (this.active && target == this.toValue)) {
            return;
        }
        this.fromValue = this.currentValue;
        this.toValue = target;
        this.easing = easing == null ? Easings.LINEAR : easing;
        this.startTime = System.nanoTime();
        this.durationNanos = (long) (duration * 1_000_000_000L);
        this.active = true;
    }

    public void update() {
        if (!this.active) {
            return;
        }
        long elapsed = System.nanoTime() - this.startTime;
        double progress;
        if (elapsed >= this.durationNanos) {
            progress = 1.0;
            this.active = false;
        } else {
            progress = this.durationNanos <= 0 ? 1.0 : (double) elapsed / (double) this.durationNanos;
        }
        double eased = this.easing.ease(progress);
        this.currentValue = this.fromValue + (this.toValue - this.fromValue) * eased;
    }

    public boolean isActive() {
        return this.active;
    }

    /** 返回当前缓动进度（0.0 ~ 1.0，可能因缓动短暂越界） */
    public double getProgress() {
        if (!this.active) {
            return 1.0;
        }
        long elapsed = System.nanoTime() - this.startTime;
        if (elapsed >= this.durationNanos) {
            return 1.0;
        }
        if (this.durationNanos <= 0) {
            return 1.0;
        }
        return this.easing.ease((double) elapsed / (double) this.durationNanos);
    }
}
