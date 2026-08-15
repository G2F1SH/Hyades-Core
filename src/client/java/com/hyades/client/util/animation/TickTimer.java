package com.hyades.client.util.animation;

/**
 * 基于毫秒的计时器，用于在指定间隔后触发事件。
 */
public class TickTimer {

    private long lastReset = System.currentTimeMillis();
    private long interval = 1000L;

    public TickTimer() {
    }

    public TickTimer(long intervalMillis) {
        this.interval = intervalMillis;
    }

    public void setInterval(long intervalMillis) {
        this.interval = intervalMillis;
    }

    public void reset() {
        this.lastReset = System.currentTimeMillis();
    }

    public boolean passed(long millis) {
        return System.currentTimeMillis() - this.lastReset >= millis;
    }

    public boolean passedAndReset() {
        if (this.passed(this.interval)) {
            this.reset();
            return true;
        }
        return false;
    }

    public boolean hasTimeLeft() {
        return this.elapsed() < this.interval;
    }

    public long elapsed() {
        return System.currentTimeMillis() - this.lastReset;
    }
}