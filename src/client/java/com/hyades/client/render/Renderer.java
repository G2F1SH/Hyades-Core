package com.hyades.client.render;

import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * 渲染框架的生命周期管理器。
 * <p>
 * 负责创建/复用 {@link DrawContext}、维护"当前绘制上下文"（支持嵌套渲染），
 * 并在进出时清理裁剪栈，避免污染 MC 自身的渲染状态。
 */
public final class Renderer {

    private static DrawContext currentCanvas;

    private Renderer() {
    }

    /**
     * 在给定的 {@link GuiGraphicsExtractor} 上执行一次绘制。
     * <p>
     * 嵌套调用（渲染期间再次调用 {@link #render}）会复用同一个 {@link DrawContext}。
     */
    public static void render(GuiGraphicsExtractor graphics, Consumer<DrawContext> consumer) {
        DrawContext drawContext;
        if (currentCanvas != null) {
            drawContext = currentCanvas;
        } else {
            drawContext = new DrawContext(graphics);
        }
        DrawContext previousCanvas = currentCanvas;
        currentCanvas = drawContext;
        try {
            consumer.accept(drawContext);
        } finally {
            currentCanvas = previousCanvas;
            if (previousCanvas == null) {
                drawContext.clearClipStack();
            }
        }
    }

    /** 当前绘制上下文（可能为 null，表示不在任何渲染块内） */
    public static DrawContext getCurrentCanvas() {
        return currentCanvas;
    }
}