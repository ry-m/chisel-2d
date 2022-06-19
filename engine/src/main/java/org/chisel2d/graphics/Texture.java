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

import static org.lwjgl.opengl.GL33.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL33.glBindTexture;

public class Texture {

    // Path to texture image
    private final String path;

    // Texture ID
    private final int id;

    // Size of image
    private final int width, height;

    // Texture mask. If a pixel is fully transparent, the value is true for that pixel. Else, it is false.
    // Opaque == false
    // Transparent == true
    private final boolean[][] mask;

    /**
     * Create a texture
     *
     * @param id Texture ID
     */
    Texture(String path, int id, int width, int height, boolean[][] mask) {
        this.path = path;
        this.id = id;
        this.width = width;
        this.height = height;
        this.mask = mask;
    }

    public String getPath() {
        return path;
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean[][] getMask() {
        return mask;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }
}
