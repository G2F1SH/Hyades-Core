package com.hyades.client.render.font;

/**
 * 语义化字体预设，GUI / HUD 代码最常用的入口。
 */
public final class FontPresets {

    private FontPresets() {
    }

    public static FontRenderer axiformaBold(float size) {
        return Fonts.getRenderer("Axiforma", size, Fonts.FontFormat.BOLD);
    }

    public static FontRenderer axiforma(float size) {
        return Fonts.getRenderer("Axiforma", size, Fonts.FontFormat.PLAIN);
    }

    public static FontRenderer defaultFont(float size) {
        return Fonts.getRenderer("SansSerif", size, Fonts.FontFormat.PLAIN);
    }

    public static FontRenderer materialIcons(float size) {
        return Fonts.getRenderer("MaterialIcons", size, Fonts.FontFormat.PLAIN);
    }

    public static FontRenderer zenIcon(float size) {
        return Fonts.getRenderer("zenicon", size, Fonts.FontFormat.PLAIN);
    }
}