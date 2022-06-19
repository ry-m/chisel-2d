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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class TextureManager {

    // Logger
    private static final Logger LOG = LogManager.getLogger();

    // Textures that are registered
    private static final Map<String, Texture> registeredTextures = new HashMap<>();

    // Textures to be loaded
    private static final Stack<Map.Entry<String, String>> textureBuffer = new Stack<>();

    private TextureManager() { }

    /**
     * Data structure for an image. Contains the image data (bytes), its width and height.
     */
    private record Image(ByteBuffer data, int width, int height, int channels) { }

    /**
     * Register a texture from path
     * @param path Path to the image resource
     */
    public static void register(String path) {
        // Textures from files are named after their file name.
        // To register a texture with a different name, it must be registered manually
        // using the TextureManager.register(String path, String name) signature.
        register(FilenameUtils.getBaseName(path), path);
    }

    /**
     * Register a texture with a specified name
     * @param name Name of the texture
     * @param path Path to the image resource
     */
    public static void register(String name, String path) {
        textureBuffer.push(new AbstractMap.SimpleEntry<>(name, path));
    }

    /**
     * Load all textures from the {@code textureBuffer}. Should be done during initialisation stages.
     */
    public static void loadAll() {
        while (!textureBuffer.isEmpty()) {
            Map.Entry<String, String> entry = textureBuffer.pop();
            // Key = name, value = texture path
            load(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Create a texture from an image file with the texture name specified
     * @param path Path to image
     * @param name Name for the texture, must be unique
     */
    private static void load(String name, String path) {
        // Get the absolute path of the image
        String absPath = FileUtils.getFile(path).getAbsolutePath();

        // If the name exists in the texture library, check to see if the texture path is the same.
        // If it is the same, the same texture has already been loaded. Else, it is a duplicate name.
        if (registeredTextures.containsKey(name)) {
            // Check to see if the path is the same
            if (Objects.equals(registeredTextures.get(name).getPath(), absPath)) {
                LOG.info("Texture already loaded at '{}'", path);
                return;
            } else {
                LOG.error("Texture name '{}' already exists, but texture path '{}' is different", name, path);
                throw new IllegalStateException("Texture name must be unique");
            }
        }

        Texture texture = create(absPath, GL_NEAREST, GL_NEAREST, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE);
        LOG.info("New texture registered at '{}'", name);
        registeredTextures.put(name, texture);
    }

    /**
     * Retrieve a texture that is registered in the {@code textureLibrary} map
     * @param name Name of the texture
     * @return The texture associated with the name, or null if it does not exist
     */
    @SuppressWarnings("unused")
    public static Texture getTexture(String name) {
        return registeredTextures.get(name);
    }

    /**
     * Create a new texture specifying all the parameters.
     *
     * @param texturePath Texture path.
     * @param filterMin GL_TEXTURE_MIN_FILTER
     * @param filterMax GL_TEXTURE_MAX_FILTER
     * @param wrapS GL_TEXTURE_WRAP_S
     * @param wrapT GL_TEXTURE_WRAP_T
     * @return The new texture.
     */
    public static Texture create(String texturePath,
                                 int filterMin,
                                 int filterMax,
                                 int wrapS,
                                 int wrapT) {
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

        return new Texture(texturePath, id, image.width(), image.height(), mask);
    }

    /**
     * Load an image from file.
     *
     * @param imagePath The image path (from a classpath context).
     * @return The image.
     */
    private static Image loadImage(String imagePath) {
        LOG.info("Loading image resource '{}'", imagePath);

        try (MemoryStack stack = stackPush()) {
            // Create image param data.
            IntBuffer width = stack.ints(0);
            IntBuffer height = stack.ints(0);
            IntBuffer numChannels = stack.ints(0);

            // Load the image.
            ByteBuffer data = STBImage.stbi_load(imagePath, width, height, numChannels, 0);
            int imageWidth = width.get(0);
            int imageHeight = height.get(0);
            int imageChannels = numChannels.get(0);

            // If data is null, the image could not be loaded/decoded.
            if (data == null) {
                LOG.error("STB image load failure: " + STBImage.stbi_failure_reason());
                throw new IllegalStateException("Failed to load/decode image at " + imagePath);
            }

            LOG.info("Texture loaded: x={}, y={}, format={}", imageWidth, imageHeight,
                    (imageChannels == 4 ? "RGBA" : "RGB")
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

        // For each pixel in the image.
        for (int y = 0; y < image.height(); y++) {
            for (int x = 0; x < image.width(); x++) {
                byte[] dest = new byte[4];
                buf.get(4 * (x + image.height * y), dest, 0, 4);

                // Determine the alpha value at this location.
                if (dest[3] == 0) {
                    maskArray[x][y] = true;
                }
            }
        }
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
