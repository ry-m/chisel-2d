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
import org.apache.commons.io.FileUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Utility class for creating textures.
 */
@SuppressWarnings("unused")
public final class TextureManager {

    // Logger.
    private static final Logger LOG = LogManager.getLogger();

    // Utility class.
    private TextureManager() { }

    /**
     * Data structure for an image. Contains the image data (bytes), its width and height.
     */
    private record Image(ByteBuffer data, int width, int height, int channels) { }

    /**
     * Create a new texture specifying all the parameters.
     *
     * @param texturePath Texture path.
     * @param filterMin GL_TEXTURE_MIN_FILTER
     * @param filterMax GL_TEXTURE_MAX_FILTER
     * @param wrapS GL_TEXTURE_WRAP_S
     * @param wrapT GL_TEXTURE_WRAP_T
     * @param alpha True for RGBA (else RGB).
     * @return The new texture.
     */
    public static Texture create(String texturePath,
                                 int filterMin,
                                 int filterMax,
                                 int wrapS,
                                 int wrapT,
                                 boolean alpha) {
        Image image = loadImage(texturePath);

        // Mask initialised to false (default value) -- all pixels are currently "opaque".
        boolean[][] mask = new boolean[image.width()][image.height()];

        int format;
        if (image.channels() == 3) {
            format = GL_RGB;
        } else if (image.channels() == 4) {
            format = GL_RGBA;
            getMask(image, mask);
        } else {
            throw new IllegalStateException("Image has unsupported number of color channels: " + image.channels);
        }

        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterMin);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterMax);

        glTexImage2D(
                GL_TEXTURE_2D, // Texture ID.
                0, // Level.
                format, // Format (rgb/rgba).
                image.width(), // Image width.
                image.height(), // Image height.
                0, // Border.
                GL_RGBA, // Format.
                GL_UNSIGNED_BYTE, // Data format.
                image.data() // Image data.
        );

        glGenerateMipmap(GL_TEXTURE_2D);

        // Unbind.
        glBindTexture(GL_TEXTURE_2D, 0);

        // Free image data from memory.
        freeImage(image);

        return new Texture(id, image.width(), image.height(), mask);
    }

    /**
     * Create a texture from a path with default values.
     *
     * @param texturePath Texture path.
     * @return Newly created texture.
     */
    public static Texture create(String texturePath) {
        return create(texturePath, GL_NEAREST, GL_NEAREST, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, true);
    }

    /**
     * Load an image from file.
     *
     * @param imagePath The image path (from a classpath context).
     * @return The image.
     */
    private static Image loadImage(String imagePath) {
        // Retrieve the absolute path.
        File image = FileUtils.getFile(imagePath);
        String absolutePath = image.getAbsolutePath();
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            absolutePath = File.separator + absolutePath;
        }

        LOG.info("Loading image resource: {}", imagePath);

        // STBImage.stbi_set_flip_vertically_on_load(true);

        try (MemoryStack stack = stackPush()) {
            // Create image param data.
            IntBuffer width = stack.ints(0);
            IntBuffer height = stack.ints(0);
            IntBuffer numChannels = stack.ints(0);

            // Load the image.
            ByteBuffer data = STBImage.stbi_load(absolutePath, width, height, numChannels, 0);
            int imageWidth = width.get(0);
            int imageHeight = height.get(0);
            int imageChannels = numChannels.get(0);

            // If data is null, the image could not be loaded/decoded.
            if (data == null) {
                LOG.error("STB image load failure: {}", STBImage.stbi_failure_reason());
                throw new IllegalStateException("Failed to load/decode image at " + absolutePath);
            }

            LOG.info("Texture loaded, size="
                    + imageWidth + "x" + imageHeight
                    + ", format=" + (imageChannels == 4 ? "RGBA" : "RGB")
            );

            return new Image(data, imageWidth, imageHeight, imageChannels);
        }
    }

    /**
     * Retrieve the mask for the image. Any pixel that is transparent in the image, the corresponding
     * true/false value is assigned in the array. For example, if the pixel at [4, 5] is transparent, the
     * value at [4, 5] in the array is true.
     *
     * @param image Image
     * @param maskArray Array to store the mask
     */
    private static void getMask(Image image, boolean[][] maskArray) {
        // Copy the ByteBuffer.
        ByteBuffer buf = image.data().duplicate();
        int tp = 0;

        // For each pixel in the image.
        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                byte[] dest = new byte[4];
                buf.get(4 * (x + image.height * y), dest, 0, 4);

                // Determine the alpha value at this location.
                if (dest[3] == 0) {
                    tp++;
                    maskArray[x][y] = true;
                }
            }
        }

        //if (ChiselApp.isDebugEnabled()) {
        //    LOG.debug("Image transparency: " + DecimalFormat.getPercentInstance().format(
        //            (double)tp / (image.width * image.height)
        //    ));
        //}
    }

    /**
     * Free image data from memory.
     *
     * @param image Image
     */
    private static void freeImage(Image image) {
        STBImage.stbi_image_free(image.data);
    }
}