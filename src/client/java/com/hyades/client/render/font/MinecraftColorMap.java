package com.hyades.client.render.font;

import com.hyades.client.util.ColorUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * Minecraft 颜色码（§ + 字符）到 ARGB 颜色的映射。
 */
public final class MinecraftColorMap {

    private static final Map<Character, Integer> COLORS = new HashMap<>();

    static {
        put('0', 0x00, 0x00, 0x00);
        put('1', 0x00, 0x00, 0xAA);
        put('2', 0x00, 0xAA, 0x00);
        put('3', 0x00, 0xAA, 0xAA);
        put('4', 0xAA, 0x00, 0x00);
        put('5', 0xAA, 0x00, 0xAA);
        put('6', 0xFF, 0xAA, 0x00);
        put('7', 0xAA, 0xAA, 0xAA);
        put('8', 0x55, 0x55, 0x55);
        put('9', 0x55, 0x55, 0xFF);
        put('a', 0x55, 0xFF, 0x55);
        put('b', 0x55, 0xFF, 0xFF);
        put('c', 0xFF, 0x55, 0x55);
        put('d', 0xFF, 0x55, 0xFF);
        put('e', 0xFF, 0xFF, 0x55);
        put('f', 0xFF, 0xFF, 0xFF);
    }

    private MinecraftColorMap() {
    }

    private static void put(char code, int r, int g, int b) {
        COLORS.put(code, ColorUtil.rgb(r, g, b));
    }

    /** 返回颜色码对应的 ARGB 颜色，未知码返回 null */
    public static Integer getColor(char code) {
        return COLORS.get(Character.toLowerCase(code));
    }

    /** 是否为合法的颜色码（含 r 重置码） */
    public static boolean isFormatCode(char code) {
        char lower = Character.toLowerCase(code);
        return COLORS.containsKey(lower) || lower == 'r';
    }
}