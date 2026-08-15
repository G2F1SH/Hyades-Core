package com.hyades.client.render.shader;

/**
 * 圆角矩形 SDF 着色器封装。
 * <p>
 * 提供纯色 / 垂直渐变两种模式的 GPU 圆角矩形，通过 uniforms 设置参数。
 * 由于 MC 26.2 的 GUI 渲染已切换到渲染状态提取管线（deferred），直接使用
 * GL 绘制会与 GUI 冲突，因此本类作为独立 GL 工具提供（如离屏 RT、自绘界面），
 * 常规 GUI/HUD 一律走 {@code DrawContext} 的 CPU 降级实现。
 */
public class RoundedRectShader {

    public static final String FRAGMENT = "rounded_rect";
    public static final String VERTEX = "rounded_rect";

    private final ShaderProgram program;

    public RoundedRectShader() {
        this.program = new ShaderProgram(FRAGMENT, VERTEX);
    }

    public boolean isValid() {
        return this.program.isValid();
    }

    /** 绑定程序并设置全部圆角矩形参数；无效时返回 false */
    public boolean setup(float x, float y, float width, float height,
                         float radiusTopLeft, float radiusTopRight, float radiusBottomRight, float radiusBottomLeft,
                         int color, int gradientColor, boolean verticalGradient) {
        if (!this.program.isValid()) {
            return false;
        }
        this.program.use();
        this.program.getUniform("u_Rect").setVec4(x, y, width, height);
        this.program.getUniform("u_Radius").setVec4(radiusTopLeft, radiusTopRight, radiusBottomRight, radiusBottomLeft);
        this.program.getUniform("u_ColorA").setColor(color);
        this.program.getUniform("u_ColorB").setColor(gradientColor);
        this.program.getUniform("u_Gradient").setInt(verticalGradient ? 1 : 0);
        return true;
    }

    public void release() {
        this.program.release();
    }

    public ShaderProgram getProgram() {
        return this.program;
    }
}