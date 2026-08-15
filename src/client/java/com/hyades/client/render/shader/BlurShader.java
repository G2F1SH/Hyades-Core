package com.hyades.client.render.shader;

/**
 * 分离式高斯模糊着色器封装（水平 / 垂直两趟）。
 * <p>
 * 与 {@link RoundedRectShader} 相同，作为独立 GL 工具提供；
 * GUI/HUD 中的模糊效果使用 {@code DrawContext#drawBlur} 的 CPU 降级实现。
 */
public class BlurShader {

    public static final String FRAGMENT = "blur";
    public static final String VERTEX = "blur";

    private final ShaderProgram program;

    public BlurShader() {
        this.program = new ShaderProgram(FRAGMENT, VERTEX);
    }

    public boolean isValid() {
        return this.program.isValid();
    }

    /** 设置单趟模糊参数；无效时返回 false */
    public boolean setupPass(boolean horizontal, float radius) {
        if (!this.program.isValid()) {
            return false;
        }
        this.program.use();
        this.program.getUniform("u_Radius").setFloat(radius);
        this.program.getUniform("u_Direction").setVec2(horizontal ? 1.0f : 0.0f, horizontal ? 0.0f : 1.0f);
        this.program.getUniform("u_TextureSize").setVec2(0f, 0f);
        return true;
    }

    public void release() {
        this.program.release();
    }

    public ShaderProgram getProgram() {
        return this.program;
    }
}