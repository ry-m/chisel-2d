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

package org.chisel2d;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.chisel2d.graphics.Shader;
import org.chisel2d.graphics.ShaderBuilder;
import org.chisel2d.graphics.TextureManager;

/**
 * The {@code Renderer} class renders everything to the display
 */
class Renderer implements Subsystem {

    // Logger
    private static final Logger LOG = LogManager.getLogger();

    // One 'global' shader is used
    private Shader shader = null;

    // Vertex shader source (GLSL).
    static final String VERTEX_SHADER_SRC = """
            #version 330 core
            layout (location = 0) in vec4 vertex; // <vec2 position, vec2 texCoords>

            out vec2 TexCoords;

            uniform mat4 model;
            uniform mat4 projection;

            void main()
            {
                TexCoords = vertex.zw;
                gl_Position = projection * model * vec4(vertex.xy, 0.0, 1.0);
            }""";

    // Fragment shader source (GLSL).
    static final String FRAGMENT_SHADER_SRC = """
            #version 330 core
            in vec2 TexCoords;
            out vec4 color;

            uniform sampler2D image;
            uniform float opacity;

            void main()
            {
                vec4 texColor = texture(image, TexCoords);
                if (texColor.w < 0.1)
                    discard;
                if (texColor.w == 1.0) {
                    texColor.w = opacity;
                } else {
                    texColor.w -= 1.0 - opacity;
                }
                    
                color = texColor;
            }""";

    // Package-private constructor
    Renderer() { }

    /**
     * Create and compile the shader and textures
     */
    @Override
    public void init() {
        LOG.info("Compiling shader...");
        shader = ShaderBuilder.compile(Renderer.VERTEX_SHADER_SRC, Renderer.FRAGMENT_SHADER_SRC);
        LOG.info("Creating textures...");
        TextureManager.loadAll();
    }

    @Override
    public void start() { }

    /**
     * Render things!
     * @return true
     */
    @Override
    public boolean update() {
        return true; // Keep rendering
    }

    @Override
    public void shutdown() {

    }
}
