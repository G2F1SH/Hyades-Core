package com.hyades.client.util.animation;

import com.hyades.client.util.math.Easing;

/**
 * 弹簧动画：使用阻尼简谐运动方程模拟弹簧回弹效果。
 * <p>
 * 通过阻尼比控制回弹幅度（{@code damping < 1} 时产生过冲回弹）。
 */
public class SpringAnimation {

    private double value;
    private double target;
    private double velocity;
    private double stiffness = 180.0;
    private double damping = 14.0;
    private double mass = 1.0;

    public SpringAnimation() {
        this(0.0);
    }

    public SpringAnimation(double initialValue) {
        this.value = initialValue;
        this.target = initialValue;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public double getTarget() {
        return this.target;
    }

    public void setStiffness(double stiffness) {
        this.stiffness = stiffness;
    }

    public void setDamping(double damping) {
        this.damping = damping;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getValueF() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
        this.velocity = 0.0;
    }

    /** 直接设置目标并重新起步（同步清除速度） */
    public void settle(double target) {
        this.target = target;
        this.setValue(target);
    }

    /**
     * 按帧推进弹簧运动。
     *
     * @param deltaSeconds 帧间时间差（秒）
     */
    public void update(double deltaSeconds) {
        double force = -this.stiffness * (this.value - this.target);
        double dampingForce = -this.damping * this.velocity;
        double acceleration = (force + dampingForce) / this.mass;
        this.velocity += acceleration * deltaSeconds;
        this.value += this.velocity * deltaSeconds;
    }

    public boolean isSettled(double epsilon) {
        return Math.abs(this.value - this.target) < epsilon && Math.abs(this.velocity) < epsilon;
    }
}