package com.hyades.client.render.shader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

/**
 * Shader 源码加载器：从模组资源 {@code assets/hyades-core/shaders/} 读取 GLSL，
 * 支持 {@code #import "file.glsl"} 编译期内联。
 */
public final class ShaderSource {

    private static final Pattern IMPORT_PATTERN = Pattern.compile("^\\s*#import\\s+[\"']?([\\w./-]+)[\"']?\\s*$", Pattern.MULTILINE);

    private ShaderSource() {
    }

    /**
     * 读取 shader 文件并内联 import。
     *
     * @param fileName 例如 {@code "rounded_rect.fsh"}
     * @return GLSL 源码；读取失败返回 null
     */
    public static String getByFileName(String fileName) {
        return load("shaders/" + fileName, new HashSet<>());
    }

    private static String load(String path, Set<String> seen) {
        Identifier identifier = Identifier.fromNamespaceAndPath("hyades-core", path);
        try (InputStream stream = Minecraft.getInstance().getResourceManager().open(identifier)) {
            if (stream == null) {
                return null;
            }
            String source = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            Matcher matcher = IMPORT_PATTERN.matcher(source);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                String importName = matcher.group(1);
                String key = importName;
                String replacement = "";
                if (seen.add(key)) {
                    String imported = load("shaders/" + importName, seen);
                    if (imported != null) {
                        replacement = imported;
                    }
                }
                matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(buffer);
            return buffer.toString();
        } catch (Exception e) {
            return null;
        }
    }
}