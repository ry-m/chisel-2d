/*
 * MIT License
 *
 * Copyright (c) 2022 Ryan Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.chisel2d.graphics;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

/**
 * Represents a shader in an OpenGL context.
 */
@SuppressWarnings("unused")
public final class Shader {

    // Program ID.
    private final int programId;

    /**
     * Constructor.
     * @param programId Program ID of the compiled shader program.
     */
    Shader(int programId) {
        this.programId = programId;
    }

    /**
     * Use this shader.
     */
    public void use() {
        glUseProgram(programId);
    }

    /**
     * Set an integer uniform value.
     *
     * @param name Uniform name.
     * @param value Value.
     * @param useShader Use the shader.
     */
    public void setValue(String name, int value, boolean useShader) {
        if (useShader) use();
        glUniform1i(locationOf(name), value);
    }

    /**
     * Set a float uniform value.
     *
     * @param name Uniform name.
     * @param value Value.
     * @param useShader Use the shader.
     */
    public void setValue(String name, float value, boolean useShader) {
        if (useShader) use();
        glUniform1f(locationOf(name), value);
    }

    /**
     * Set a boolean uniform value.
     *
     * @param name Uniform name.
     * @param value Value.
     * @param useShader Use the shader.
     */
    public void setValue(String name, boolean value, boolean useShader) {
        if (useShader) use();
        glUniform1i(locationOf(name), value ? 1 : 0);
    }

    /**
     * Set a Vector2f uniform value.
     *
     * @param name Uniform name.
     * @param value Value.
     * @param useShader Use the shader.
     */
    public void setValue(String name, Vector2f value, boolean useShader) {
        if (useShader) use();
        glUniform2f(locationOf(name), value.x(), value.y());
    }

    /**
     * Set a Vector3f uniform value.
     *
     * @param name Uniform name.
     * @param value Value.
     * @param useShader Use the shader.
     */
    public void setValue(String name, Vector3f value, boolean useShader) {
        if (useShader) use();
        glUniform3f(locationOf(name), value.x(), value.y(), value.z());
    }

    /**
     * Set a Vector4f uniform value.
     *
     * @param name Uniform name.
     * @param value Value.
     * @param useShader Use the shader.
     */
    public void setValue(String name, Vector4f value, boolean useShader) {
        if (useShader) use();
        glUniform4f(locationOf(name), value.x(), value.y(), value.z(), value.w());
    }

    /**
     * Set a Matrix4f uniform value.
     *
     * @param name Uniform name.
     * @param value Value.
     * @param useShader Use the shader.
     */
    public void setValue(String name, Matrix4f value, boolean useShader) {
        if (useShader) use();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);
        glUniformMatrix4fv(locationOf(name), false, buffer);
    }

    /**
     * Get the uniform location for a name.
     *
     * @param name Name.
     * @return Uniform location.
     */
    private int locationOf(String name) {
        return glGetUniformLocation(programId, name);
    }
}