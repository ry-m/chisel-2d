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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.opengl.GL33.*;

/**
 * A utility class used to create {@code Shader} objects. Ensures shaders are valid for rendering.
 */
public final class ShaderBuilder {

    // Logger
    private static final Logger LOG = LogManager.getLogger();

    // Cannot instantiate this class
    private ShaderBuilder() { }

    /**
     * Shader type (vertex/fragment/program)
     */
    private enum ShaderType {
        VERTEX, FRAGMENT, PROGRAM
    }

    /**
     * Compile a shader
     *
     * @param vertSrc Vertex shader source code
     * @param fragSrc Fragment shader source code
     * @return Newly created shader program
     */
    public static Shader compile(String vertSrc, String fragSrc) {
        // Create and compile vertex shader.
        int vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, vertSrc);
        glCompileShader(vs);
        checkCompileStatus(vs, ShaderType.VERTEX);

        // Create and compile fragment shader.
        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, fragSrc);
        glCompileShader(fs);
        checkCompileStatus(fs, ShaderType.FRAGMENT);

        // Compile program.
        int program = glCreateProgram();
        glAttachShader(program, vs);
        glAttachShader(program, fs);
        glLinkProgram(program);
        checkCompileStatus(program, ShaderType.PROGRAM);

        // Clean-up
        glDeleteShader(vs);
        glDeleteShader(fs);

        return new Shader(program);
    }

    /**
     * Check compile status of a shader, or link status of the program
     *
     * @param id Shader or program id
     * @param type Shader type
     */
    private static void checkCompileStatus(int id, ShaderType type) {
        if (type == ShaderType.PROGRAM) {
            int success = glGetProgrami(id, GL_LINK_STATUS);
            if (success == GL_FALSE) {
                LOG.error("OpenGL shader error: {} ", glGetProgramInfoLog(id));
                throw new IllegalStateException("Failed to link shader program.");
            }
        } else {
            int success = glGetShaderi(id, GL_COMPILE_STATUS);
            if (success == GL_FALSE) {
                LOG.error("OpenGL shader error: {} ", glGetShaderInfoLog(id));
                throw new IllegalStateException("Failed to compile shader of type " + type);
            }
        }
    }
}
