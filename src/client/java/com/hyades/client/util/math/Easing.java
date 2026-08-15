package com.hyades.client.util.math;

/**
 * 缓动函数式接口。
 * 输入为归一化时间 t（0.0 ~ 1.0），输出为缓动后的进度（通常 0.0 ~ 1.0，部分缓动可超出）。
 */
@FunctionalInterface
public interface Easing {
    double ease(double t);
}
