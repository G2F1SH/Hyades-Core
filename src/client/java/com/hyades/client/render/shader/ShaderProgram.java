package com.hyades.client.render.shader;

import com.hyades.HyadesCore;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * GLSL Shader 程序封装。
 * <p>
 * 从资源文件加载 {@code .fsh} / {@code .vsh}，编译链接失败时优雅降级
 * （{@link #isValid()} 返回 false，调用方跳过特效而非崩溃）。
 */
public class ShaderProgram {

    private final int programId;
    private boolean valid;
    private final Map<String, Uniform> uniforms = new HashMap<>();

    /**
     * @param fragmentName 片段着色器文件名（不含扩展名，加载 {@code .fsh}）
     * @param vertexName   顶点着色器文件名（不含扩展名，加载 {@code .vsh}）
     */
    public ShaderProgram(String fragmentName, String vertexName) {
        this.programId = GL20.glCreateProgram();
        int fragmentShader = compileShader(GL20.GL_FRAGMENT_SHADER, ShaderSource.getByFileName(fragmentName + ".fsh"));
        int vertexShader = compileShader(GL20.GL_VERTEX_SHADER, ShaderSource.getByFileName(vertexName + ".vsh"));
        if (fragmentShader == 0 || vertexShader == 0) {
            this.valid = false;
            this.cleanup(fragmentShader, vertexShader);
            return;
        }
        GL20.glAttachShader(this.programId, fragmentShader);
        GL20.glAttachShader(this.programId, vertexShader);
        GL20.glLinkProgram(this.programId);
        if (GL20.glGetProgrami(this.programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            HyadesCore.LOGGER.warn("Shader link failed: {}", GL20.glGetProgramInfoLog(this.programId));
            this.valid = false;
            this.cleanup(fragmentShader, vertexShader);
            return;
        }
        GL20.glDeleteShader(fragmentShader);
        GL20.glDeleteShader(vertexShader);
        this.valid = true;
    }

    private void cleanup(int fragmentShader, int vertexShader) {
        if (fragmentShader != 0) {
            GL20.glDeleteShader(fragmentShader);
        }
        if (vertexShader != 0) {
            GL20.glDeleteShader(vertexShader);
        }
        GL20.glDeleteProgram(this.programId);
    }

    private static int compileShader(int type, String source) {
        if (source == null || source.isEmpty()) {
            HyadesCore.LOGGER.warn("Skipping shader compilation: empty source (type={})", type);
            return 0;
        }
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            HyadesCore.LOGGER.warn("Shader compile failed: {}", GL20.glGetShaderInfoLog(shader));
            GL20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void use() {
        if (this.valid) {
            GL20.glUseProgram(this.programId);
        }
    }

    public void release() {
        GL20.glUseProgram(0);
    }

    public Uniform getUniform(String name) {
        return this.uniforms.computeIfAbsent(name, n -> new Uniform(this.programId, n));
    }

    public void delete() {
        if (this.programId != 0) {
            GL20.glDeleteProgram(this.programId);
            this.valid = false;
        }
    }
}