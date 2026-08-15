package com.hyades.client.render.font;

import java.awt.GraphicsEnvironment;
import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

/**
 * 字体缓存与加载入口。
 * <p>
 * 优先从模组资源 {@code assets/hyades-core/fonts/&lt;name&gt;.ttf} 加载 TTF 字体，
 * 找不到时回退到系统字体（未知字体名回退到 SansSerif）。
 */
public final class Fonts {

    public enum FontFormat {
        PLAIN,
        BOLD,
        ITALIC,
        BOLD_ITALIC
    }

    private static final Map<String, FontRenderer> CACHE = new HashMap<>();
    private static final String ASSET_DIR = "fonts/";

    private Fonts() {
    }

    public static FontRenderer getRenderer(String name, float size) {
        return getRenderer(name, size, FontFormat.PLAIN);
    }

    public static FontRenderer getRenderer(String name, float size, FontFormat format) {
        String key = name.toLowerCase(Locale.ROOT) + "-" + size + "-" + format;
        return CACHE.computeIfAbsent(key, k -> new FontRenderer(name, size, loadCustomFont(name, size, format)));
    }

    /** 从模组资源或系统字体创建自定义字体 */
    public static CustomFont loadCustomFont(String name, float size, FontFormat format) {
        java.awt.Font awt = resolveAwtFont(name, format);
        return new CustomFont(name, size, awt);
    }

    private static java.awt.Font resolveAwtFont(String name, FontFormat format) {
        int style = switch (format) {
            case BOLD -> Font.BOLD;
            case ITALIC -> Font.ITALIC;
            case BOLD_ITALIC -> Font.BOLD | Font.ITALIC;
            default -> Font.PLAIN;
        };
        try {
            Identifier identifier = Identifier.fromNamespaceAndPath("hyades-core", ASSET_DIR + name.toLowerCase(Locale.ROOT) + ".ttf");
            try (InputStream stream = Minecraft.getInstance().getResourceManager().open(identifier)) {
                java.awt.Font loaded = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, stream);
                return loaded.deriveFont(style, 1.0f);
            } catch (Exception ignored) {
                // 资源缺失或非法 TTF，回退
            }
        } catch (Exception ignored) {
            // 找不到资源
        }
        return systemFallback(name, style);
    }

    private static java.awt.Font systemFallback(String name, int style) {
        String family = name;
        boolean known = false;
        try {
            for (String available : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
                if (available.equalsIgnoreCase(name)) {
                    known = true;
                    break;
                }
            }
        } catch (Exception ignored) {
            known = false;
        }
        if (!known) {
            family = java.awt.Font.SANS_SERIF;
        }
        return new java.awt.Font(family, style, 1).deriveFont(1.0f);
    }
}