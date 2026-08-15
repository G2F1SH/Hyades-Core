package com.hyades.client.util.animation;

/**
 * 动画计时器基类。
 * <p>
 * 维护动画的起点值、目标值、开始时间与持续时间，提供基于时间戳的补间。
 */
public class AnimationTimer {

    private double fromValue;
    private double toValue;
    private long startTime;
    private long durationNanos;
    private boolean active;

    public AnimationTimer() {
        this(0.0, 0.0);
    }

    public AnimationTimer(double fromValue, double toValue) {
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.active = false;
    }

    public void setFromValue(double fromValue) {
        this.fromValue = fromValue;
    }

    public void setToValue(double toValue) {
        this.toValue = toValue;
    }

    public double getFromValue() {
        return this.fromValue;
    }

    public double getToValue() {
        return this.toValue;
    }

    /** 以秒为单位设置持续时间 */
    public void setDuration(double seconds) {
        this.durationNanos = (long) (seconds * 1_000_000_000L);
    }

    /** 启动动画（从当前 fromValue 过渡到 toValue） */
    public void start() {
        this.startTime = System.nanoTime();
        this.active = true;
    }

    /** 停止动画并把进度置为终点 */
    public void stop() {
        this.active = false;
    }

    public boolean isActive() {
        return this.active;
    }

    /** 返回 0.0 ~ 1.0 的线性进度，未开始返回 0，已结束返回 1 */
    public double getProgress() {
        if (!this.active) {
            return 1.0;
        }
        long elapsed = System.nanoTime() - this.startTime;
        if (elapsed >= this.durationNanos) {
            this.active = false;
            return 1.0;
        }
        if (this.durationNanos <= 0) {
            return 1.0;
        }
        return (double) elapsed / (double) this.durationNanos;
    }

    /** 返回未经缓动的线性插值结果 */
    public double getLinearValue() {
        return this.fromValue + (this.toValue - this.fromValue) * this.getProgress();
    }
}
