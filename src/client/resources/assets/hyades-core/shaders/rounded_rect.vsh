#version 150

in vec2 v_LocalPos;
in vec2 v_Size;

out vec2 v_FragPos;

void main() {
    v_FragPos = v_LocalPos;
    gl_Position = vec4(v_LocalPos, 0.0, 1.0);
}