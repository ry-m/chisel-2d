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
import org.chisel2d.graphics.Texture;
import org.chisel2d.graphics.TextureManager;
import org.chisel2d.sprite.Sprite;
import org.chisel2d.sprite.SpriteManager;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL33.*;

/**
 * The {@code Renderer} class renders everything to the display
 */
class Renderer implements Subsystem {

    // Logger
    private static final Logger LOG = LogManager.getLogger();

    // One 'global' shader is used
    private Shader shader = null;

    // VAO
    private int vao = -1;

    // Vertex data (2D sprite with texture and positions).
    private static final float[] VERTICES = {
            // Position     // Texture coordinates.
            0.0f, 1.0f,     0.0f, 1.0f,
            1.0f, 0.0f,     1.0f, 0.0f,
            0.0f, 0.0f,     0.0f, 0.0f,

            0.0f, 1.0f,     0.0f, 1.0f,
            1.0f, 1.0f,     1.0f, 1.0f,
            1.0f, 0.0f,     1.0f, 0.0f
    };

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

        // OpenGL
        vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, VERTICES, GL_STATIC_DRAW);

        glBindVertexArray(vao);
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * Float.BYTES, 0L);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        shader.setValue("image", 0, true);
        glBindVertexArray(0);
    }

    @Override
    public void start() { }

    /**
     * Render things!
     * @return true
     */
    @Override
    public boolean update() {
        shader.setValue("projection", new Matrix4f().setOrtho(
                0.0f, Window.getWidth(), Window.getHeight(), 0.0f, -1.0f, 1.0f
                ), true
        );

        for (Sprite sprite : SpriteManager.getSprites()) {
            if (sprite.isVisible()) {
                renderSprite(sprite);
            }
        }

        return true; // Keep rendering
    }

    @Override
    public void shutdown() {

    }

    /**
     * Render a single sprite.
     *
     * @param sprite Sprite to render
     */
    private void renderSprite(Sprite sprite) {
        shader.use();
        Matrix4f model = new Matrix4f();

        // Retrieve the sprite's texture
        Texture texture = TextureManager.getTexture(sprite.getTextureID());
        sprite.getBoundingBox().setWidth(texture.getWidth());
        sprite.getBoundingBox().setHeight(texture.getHeight());

        // Set model position.
        // Relative to window size. If the sprite is at (0,0), it is in the centre of the window.
        Vector2f position = new Vector2f(
                Window.getWidth() / 2.0f + sprite.getX() - texture.getWidth() * sprite.getScale() / 2.0f,
                Window.getHeight() / 2.0f - sprite.getY() - texture.getHeight() * sprite.getScale() / 2.0f
        );

        model.translate(position.x, position.y, 0.0f);

        // Rotation and scale.
        model.rotate((float) Math.toRadians(sprite.getRotation()), new Vector3f(0, 0, 1));
        model.scale(new Vector3f(new Vector2f(
                // Determine the sprite scale and multiply that by the texture size
                sprite.getScale() * texture.getWidth(),
                sprite.getScale() * texture.getHeight()),
                1.0f)
        );

        shader.setValue("model", model, false);
        shader.setValue("opacity", sprite.getOpacity(), false);

        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 6);

        glBindVertexArray(0);
    }
}
