package com.hyades.client.gui;

/**
 * 全局配色方案（深色主题）。
 */
public final class Theme {

    private Theme() {
    }

    public static final int PRIMARY = 0xFF58A6FF;
    public static final int ACCENT = 0xFF7EE787;
    public static final int WARNING = 0xFFD29922;
    public static final int DANGER = 0xFFFF7B72;

    public static final int BACKGROUND = 0xCC101318;
    public static final int PANEL = 0xF216161B;
    public static final int PANEL_LIGHT = 0xFF1E232C;
    public static final int BORDER = 0xFF30363D;

    public static final int TEXT = 0xFFE6EDF3;
    public static final int TEXT_DIM = 0xFF8B949E;
    public static final int TEXT_DARK = 0xFF484F58;

    /** 悬停高亮（叠加层，配 base 一起用） */
    public static final int HOVER_OVERLAY = 0x14FFFFFF;
    public static final int PRESSED_OVERLAY = 0x28FFFFFF;

    /** 顶部渐变（品牌色） */
    public static final int GRADIENT_TOP = 0xFF1F6FEB;
    public static final int GRADIENT_BOTTOM = 0xFF0D1117;
}