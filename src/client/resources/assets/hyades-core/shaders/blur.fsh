#version 150

uniform sampler2D u_Texture;
uniform vec2 u_TextureSize;
uniform vec2 u_Direction; // (1,0) horizontal pass, (0,1) vertical pass
uniform float u_Radius;

in vec2 v_TexCoord;

out vec4 fragColor;

void main() {
    vec2 texelSize = 1.0 / u_TextureSize;
    float sigma = max(1.0, u_Radius);
    float twoSigma2 = 2.0 * sigma * sigma;
    float weightSum = 0.0;
    vec4 color = vec4(0.0);
    int radius = int(ceil(sigma * 2.0));
    for (int i = -radius; i <= radius; i++) {
        float weight = exp(-float(i * i) / twoSigma2);
        vec2 offset = u_Direction * (float(i) * texelSize);
        color += texture(u_Texture, v_TexCoord + offset) * weight;
        weightSum += weight;
    }
    fragColor = color / max(weightSum, 0.0001);
}