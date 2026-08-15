#version 150

in vec2 v_FragPos;
in vec2 v_Size;

uniform vec4 u_Rect;     // x, y, width, height
uniform vec4 u_Radius;   // tl, tr, br, bl
uniform vec4 u_ColorA;
uniform vec4 u_ColorB;
uniform int u_Gradient;  // 0 = solid, 1 = vertical gradient

out vec4 fragColor;

float roundedRectSDF(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x = (p.y > 0.0) ? r.x : r.z;
    vec2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

void main() {
    vec2 halfSize = v_Size * 0.5;
    float dist = roundedRectSDF(v_FragPos, halfSize, u_Radius);
    float alpha = 1.0 - smoothstep(-1.0, 1.0, dist);

    vec3 color = u_ColorA.rgb;
    if (u_Gradient == 1) {
        float t = clamp((v_FragPos.y + halfSize.y) / max(v_Size.y, 0.0001), 0.0, 1.0);
        color = mix(u_ColorA.rgb, u_ColorB.rgb, t);
    }
    fragColor = vec4(color, u_ColorA.a * alpha);
}