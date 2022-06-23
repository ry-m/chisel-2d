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
import org.chisel2d.util.Color;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * The window subsystem supports the creation and maintaining of a platform window for macOS, Windows or Linux.
 * Uses the GLFW window library.
 */
final class Window implements Subsystem {

    // Logger
    private static final Logger LOG = LogManager.getLogger();

    // Ensure the window is only created once, else an exception is thrown
    private static boolean created = false;

    // Window handle
    private long window = NULL;

    // Window title (mutable)
    private String title;

    // Window width
    private static int width;

    // Window height
    private static int height;

    // True if the window can be resized by the user
    private final boolean resizable;

    // Clear color
    private final Color clearColor = Color.BLACK;

    /**
     * Constructor, initialising all parameters
     * @param title Window title
     * @param width Window width
     * @param height Window height
     * @param resizable {@code true} if the window can be resized by the user
     */
    Window(String title, int width, int height, boolean resizable) {
        this.title = title;
        Window.width = width;
        Window.height = height;
        this.resizable = resizable;
    }

    /**
     * Modify the window title at runtime
     * @param title New window title
     */
    @SuppressWarnings("unused")
    void updateTitle(String title) {
        if (!created) {
            LOG.error("Cannot update title as window is not yet created");
        } else {
            this.title = title;
            glfwSetWindowTitle(window, title);
        }
    }

    /**
     * Modify the window width and height
     * @param width New window width
     * @param height New window height
     */
    @SuppressWarnings("unused")
    void updateSize(int width, int height) {
        if (!created) {
            LOG.error("Cannot update size as window is not yet created");
        } else {
            Window.width = width;
            Window.height = height;
            glfwSetWindowSize(window, width, height);
        }
    }

    /**
     * Set the background color (glClearColor)
     * @param red Red
     * @param green Green
     * @param blue Blue
     * @param alpha Alpha
     */
    void updateBackgroundColor(int red, int green, int blue, float alpha) {
        clearColor.setRed(red);
        clearColor.setGreen(green);
        clearColor.setBlue(blue);
        clearColor.setAlpha(alpha);

        if (!created) {
            LOG.warn("Cannot update background color as window is not yet created");
        } else {
            glClearColor(clearColor.fRed(), clearColor.fGreen(), clearColor.fBlue(), clearColor.getAlpha());
        }
    }

    /**
     * Get the window width
     * @return Window width
     */
    static int getWidth() {
        return width;
    }

    /**
     * Get the window height
     * @return Window height
     */
    static int getHeight() {
        return height;
    }

    /**
     * Initialise the window subsystem
     */
    @Override
    public void init() {
        LOG.info("Initialising application window...");

        // TODO: redirect to logger?
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            LOG.fatal("Failed to initialise GLFW");
            throw new IllegalStateException("Failed to initialise GLFW");
        }

        glfwDefaultWindowHints();
        // OpenGL version 3.3
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode == null) {
            LOG.warn("Cannot retrieve video mode, window performance may be reduced");
        } else {
            glfwWindowHint(GLFW_REFRESH_RATE, vidMode.refreshRate());
            LOG.info("Detected refresh rate: {}Hz", vidMode.refreshRate());
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
        if (Platform.get() == Platform.MACOSX) {
            // Retina display framebuffer should not be scaled
            glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);
        }

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            LOG.fatal("Failed to create window");
            throw new IllegalStateException("Failed to create window");
        }

        // Center the window (if possible)
        if (vidMode != null) {
            try (MemoryStack stack = stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1);
                IntBuffer pHeight = stack.mallocInt(1);

                glfwGetWindowSize(window, pWidth, pHeight);
                glfwSetWindowPos(
                        window,
                        (vidMode.width() - pWidth.get(0)) / 2,
                        (vidMode.height() - pHeight.get(0)) / 2
                );
            }
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        // Enable V-Sync
        glfwSwapInterval(1);

        // Set color
        glClearColor(clearColor.fRed(), clearColor.fGreen(), clearColor.fBlue(), clearColor.getAlpha());

        created = true;
    }

    /**
     * Start the window subsystem. This shows the window
     */
    @Override
    public void start() {
        LOG.info("Window visibility changed to visible");
        glfwShowWindow(window);
    }

    /**
     * Update the window
     * @return {@code false} if the window should close
     */
    @Override
    public boolean update() {
        glfwPollEvents();
        glfwSwapBuffers(window);

        glClear(GL_COLOR_BUFFER_BIT);

        return !glfwWindowShouldClose(window);
    }

    /**
     * Terminate the window
     */
    @Override
    public void shutdown() {
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
