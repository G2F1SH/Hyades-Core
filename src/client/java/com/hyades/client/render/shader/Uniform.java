package com.hyades.client.render.shader;

import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import java.nio.FloatBuffer;

/**
 * Shader uniform 封装，缓存位置并提供常用 setter。
 */
public final class Uniform {

    private final int location;
    private final String name;

    public Uniform(int programId, String name) {
        this.name = name;
        this.location = GL20.glGetUniformLocation(programId, name);
    }

    public String getName() {
        return this.name;
    }

    /** 该 uniform 是否在当前程序中有效（位置 >= 0） */
    public boolean isValid() {
        return this.location >= 0;
    }

    public void setInt(int value) {
        if (this.isValid()) {
            GL20.glUniform1i(this.location, value);
        }
    }

    public void setFloat(float value) {
        if (this.isValid()) {
            GL20.glUniform1f(this.location, value);
        }
    }

    public void setVec2(float x, float y) {
        if (this.isValid()) {
            GL20.glUniform2f(this.location, x, y);
        }
    }

    public void setVec3(float x, float y, float z) {
        if (this.isValid()) {
            GL20.glUniform3f(this.location, x, y, z);
        }
    }

    public void setVec4(float x, float y, float z, float w) {
        if (this.isValid()) {
            GL20.glUniform4f(this.location, x, y, z, w);
        }
    }

    /** 以 0xAARRGGBB 的 int 颜色上传为归一化 vec4 */
    public void setColor(int argb) {
        float a = ((argb >> 24) & 0xFF) / 255.0f;
        float r = ((argb >> 16) & 0xFF) / 255.0f;
        float g = ((argb >> 8) & 0xFF) / 255.0f;
        float b = (argb & 0xFF) / 255.0f;
        this.setVec4(r, g, b, a);
    }

    public void setMatrix4(float[] matrix) {
        if (this.isValid() && matrix != null && matrix.length == 16) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer buffer = stack.mallocFloat(16);
                buffer.put(matrix).flip();
                GL20.glUniformMatrix4fv(this.location, false, buffer);
            }
        }
    }
}