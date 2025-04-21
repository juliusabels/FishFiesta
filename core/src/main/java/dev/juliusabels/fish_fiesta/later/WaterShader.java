/**
 * WaterShader.java
 * Documentation and implementation of the Fish Fiesta water shader
 */
package dev.juliusabels.fish_fiesta.later;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * This class encapsulates the water shader implementation and provides utility
 * methods for configuring and using it.
 */
public class WaterShader {
    private ShaderProgram shader;
    private float time = 0f;

    // Default parameters
    private float waveStrength = 1.0f;
    private float waveSpeed = 0.5f;

    /**
     * Creates a new water shader
     * @param vertPath Path to vertex shader
     * @param fragPath Path to fragment shader
     */
    public WaterShader(String vertPath, String fragPath) {
        ShaderProgram.pedantic = false; // For development only
        shader = new ShaderProgram(
            Gdx.files.internal(vertPath),
            Gdx.files.internal(fragPath)
        );

        if (!shader.isCompiled()) {
            Gdx.app.error("WaterShader", "Compilation failed: " + shader.getLog());
        }
    }

    /**
     * Use this shader for rendering
     */
    public void begin() {
        shader.bind();
        shader.setUniformf("u_time", time);
        shader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shader.setUniformf("u_waveStrength", waveStrength);
        shader.setUniformf("u_waveSpeed", waveSpeed);
    }

    /**
     * Update shader time based on delta time
     * @param delta Time elapsed since last frame
     */
    public void update(float delta) {
        time += delta;
    }

    /**
     * Set wave strength (amplitude of distortion)
     * @param strength 0.0-2.0 recommended (1.0 is default)
     */
    public void setWaveStrength(float strength) {
        this.waveStrength = strength;
    }

    /**
     * Set wave movement speed
     * @param speed 0.0-1.0 recommended (0.5 is default)
     */
    public void setWaveSpeed(float speed) {
        this.waveSpeed = speed;
    }

    /**
     * Get the underlying shader program
     */
    public ShaderProgram getShaderProgram() {
        return shader;
    }

    /**
     * Clean up resources
     */
    public void dispose() {
        if (shader != null) {
            shader.dispose();
        }
    }

    /**
     * VERTEX SHADER CODE:
     *
     * attribute vec4 a_position;
     * attribute vec4 a_color;
     * attribute vec2 a_texCoord0;
     *
     * uniform mat4 u_projTrans;
     *
     * varying vec4 v_color;
     * varying vec2 v_texCoords;
     *
     * void main() {
     *     v_color = a_color;
     *     v_texCoords = a_texCoord0;
     *     gl_Position = u_projTrans * a_position;
     * }
     *
     * FRAGMENT SHADER CODE:
     *
     * #ifdef GL_ES
     * precision mediump float;
     * #endif
     *
     * uniform sampler2D u_texture;
     * uniform float u_time;
     * uniform vec2 u_resolution;
     * uniform float u_waveStrength;
     * uniform float u_waveSpeed;
     *
     * varying vec4 v_color;
     * varying vec2 v_texCoords;
     *
     * const float PI = 3.1415926535897932;
     *
     * // Water parameters
     * const float emboss = 0.50;
     * const float intensity = 2.4;
     * const int steps = 6;
     * const float frequency = 6.0;
     * const int angle = 7; // better when a prime
     *
     * // Reflection parameters
     * const float delta = 60.0;
     * const float gain = 400.0;
     * const float reflectionCutOff = 0.012;
     * const float reflectionIntensity = 100000.0;
     *
     * float col(vec2 coord, float time) {
     *     float delta_theta = 2.0 * PI / float(angle);
     *     float col = 0.0;
     *     float theta = 0.0;
     *
     *     for (int i = 0; i < steps; i++) {
     *         vec2 adjc = coord;
     *         theta = delta_theta * float(i);
     *         adjc.x += cos(theta) * time * u_waveSpeed * 0.5 + time * 0.15;
     *         adjc.y -= sin(theta) * time * u_waveSpeed * 0.5 - time * 0.15;
     *         col = col + cos((adjc.x * cos(theta) - adjc.y * sin(theta)) * frequency) * intensity;
     *     }
     *
     *     return cos(col);
     * }
     *
     * void main() {
     *     float time = u_time * 1.3;
     *
     *     // Use original texture coordinates for sampling
     *     vec2 originalCoords = v_texCoords;
     *
     *     // Scale pattern to control wave size (smaller value = bigger waves)
     *     vec2 patternCoords = v_texCoords * 0.5;
     *     vec2 coord = patternCoords * 100.0; // Scale for internal calculations
     *
     *     float cc1 = col(coord, time);
     *
     *     // Calculate distortion for water effect
     *     float dx = emboss * (cc1 - col(coord + vec2(10.0, 0.0), time)) * 0.012 * u_waveStrength;
     *     float dy = emboss * (cc1 - col(coord + vec2(0.0, 10.0), time)) * 0.012 * u_waveStrength;
     *
     *     // Apply distortion to texture coordinates
     *     vec2 distortedCoords = originalCoords + vec2(dx, dy) * 1.0;
     *
     *     // Sample the texture with distorted coordinates
     *     vec4 texColor = texture2D(u_texture, distortedCoords);
     *
     *     // Calculate highlight effect for water surface
     *     float alpha = 1.0 + dot(vec2(dx, dy), vec2(dx, dy)) * gain;
     *
     *     float ddx = dx - reflectionCutOff;
     *     float ddy = dy - reflectionCutOff;
     *     if (ddx > 0.0 && ddy > 0.0) {
     *         alpha = pow(alpha, ddx * ddy * reflectionIntensity);
     *     }
     *
     *     // Apply highlight effect
     *     texColor = texColor * mix(1.0, alpha, 0.4);
     *
     *     // Add dynamic lighting effect
     *     float light = sin(patternCoords.y * 3.0 + time) * 0.15 + 0.9;
     *     texColor.rgb *= light;
     *
     *     gl_FragColor = texColor * v_color;
     * }
     */

    /**
     * USAGE EXAMPLE:
     *
     * // In create()
     * waterShader = new WaterShader("shaders/water.vert", "shaders/water.frag");
     *
     * // In render()
     * waterShader.update(Gdx.graphics.getDeltaTime());
     * batch.setShader(waterShader.getShaderProgram());
     * waterShader.begin();
     *
     * // Draw your textured elements here
     * batch.draw(textureRegion, x, y, width, height);
     *
     * batch.end();
     * batch.setShader(null); // Reset to default shader
     *
     * // In dispose()
     * waterShader.dispose();
     */

    /**
     * CUSTOMIZATION GUIDE:
     *
     * Wave Size:
     * - Modify `patternCoords = v_texCoords * 0.5` in the shader
     * - Lower values (0.25, 0.1) = bigger waves
     * - Higher values (1.0, 2.0) = smaller waves
     *
     * Wave Strength:
     * - Use setWaveStrength() or adjust the 0.012 factor in the distortion calculation
     * - Higher values = more distortion
     *
     * Wave Speed:
     * - Use setWaveSpeed() or adjust the 0.15 constants in the col() function
     * - Higher values = faster waves
     *
     * Lighting Effect:
     * - Adjust the light calculation: sin(patternCoords.y * 3.0 + time) * 0.15 + 0.9
     * - First number (3.0) = wave frequency
     * - Second number (0.15) = wave intensity
     * - Third number (0.9) = base brightness
     */
}
