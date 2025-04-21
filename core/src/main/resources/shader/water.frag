#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;
uniform vec2 u_resolution;
uniform float u_waveStrength;
uniform float u_waveSpeed;

varying vec4 v_color;
varying vec2 v_texCoords;

const float PI = 3.1415926535897932;

// Water parameters
const float emboss = 0.50;
const float intensity = 2.4;
const int steps = 6;
const float frequency = 6.0;
const int angle = 7; // better when a prime

// Reflection parameters
const float delta = 60.0;
const float gain = 400.0;
const float reflectionCutOff = 0.012;
const float reflectionIntensity = 100000.0;

float col(vec2 coord, float time) {
    float delta_theta = 2.0 * PI / float(angle);
    float col = 0.0;
    float theta = 0.0;

    for (int i = 0; i < steps; i++) {
        vec2 adjc = coord;
        theta = delta_theta * float(i);
        adjc.x += cos(theta) * time * u_waveSpeed * 0.5 + time * 0.3;
        adjc.y -= sin(theta) * time * u_waveSpeed * 0.5 - time * 0.3;
        col = col + cos((adjc.x * cos(theta) - adjc.y * sin(theta)) * frequency) * intensity;
    }

    return cos(col);
}

void main() {
    float time = u_time * 1.3;

    // Scale texture coordinates to match the shader's expected range
    vec2 coord = v_texCoords * u_resolution;

    vec2 p = coord / u_resolution, c1 = p, c2 = p;
    float cc1 = col(c1, time);

    c2.x += u_resolution.x / delta;
    float dx = emboss * (cc1 - col(c2, time)) / delta * u_waveStrength;

    c2.x = p.x;
    c2.y += u_resolution.y / delta;
    float dy = emboss * (cc1 - col(c2, time)) / delta * u_waveStrength;

    c1.x += dx * 2.0;
    c1.y = c1.y + dy * 2.0;

    // Keep texture coordinates within bounds
    c1 = clamp(c1, 0.0, 1.0);

    float alpha = 1.0 + dot(vec2(dx, dy), vec2(dx, dy)) * gain;

    float ddx = dx - reflectionCutOff;
    float ddy = dy - reflectionCutOff;
    if (ddx > 0.0 && ddy > 0.0) {
        alpha = pow(alpha, ddx * ddy * reflectionIntensity);
    }

    // Apply texture with ripple distortion and highlights
    vec4 texColor = texture2D(u_texture, c1) * alpha;

    // Add dynamic lighting
    float light = sin(c1.y * 30.0 + time * 2) * 0.1 + 0.9;
    texColor.rgb *= light;

    gl_FragColor = texColor * v_color;
}
