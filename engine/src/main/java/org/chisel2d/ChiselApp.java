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

public abstract class ChiselApp {

    // Logger
    private static final Logger LOG = LogManager.getLogger();

    // Default updates per second
    private static final int DEFAULT_UPS = 60;

    // Default window title
    private static final String DEFAULT_TITLE = "Window";

    // Default window width
    private static final int DEFAULT_WIDTH = 800;

    // Default window height
    private static final int DEFAULT_HEIGHT = 600;

    // Has the application launched yet?
    private static boolean launched = false;

    // Window
    private Window window = null;

    // Protected constructor
    protected ChiselApp() { }

    /**
     * Provides access to the application loop on each game tick
     */
    protected abstract void onTick();

    /**
     * Provides an opportunity to set up sprites and assets before the application window appears
     */
    protected abstract void setup();

    /**
     * Launch the application with all window parameters specified
     * @param title Window title
     * @param width Window width
     * @param height Window height
     * @param resizable Should the window be user-resizable?
     */
    protected void launch(String title, int width, int height, boolean resizable) {
        if (launched) {
            LOG.error("Attempt to launch application again blocked, please only launch once");
            return;
        }

        launched = true;

        // Validate window title
        String windowTitle = title == null
                ? DEFAULT_TITLE
                : title;

        LOG.info(
            "Creating application instance: '{}' at {}x{}, resizable={}",
            windowTitle, width, height, resizable
        );

        window = new Window(windowTitle, width, height, resizable);

        LOG.info("Calling client 'setup()' method...");
        setup();

        new Engine(new Subsystem[] {
            // The window MUST be initialised first to ensure the OpenGL context is created, which is a requirement
            // for many of the other subsystems. The order is important.
            window,
            new Renderer(),
            new Timer(this::onTick)
        }).start();
    }

    /**
     * Launch the application with the title and dimensions specified
     * @param title Window title
     * @param width Window width
     * @param height Window height
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    protected void launch(String title, int width, int height) {
        launch(title, width, height, true);
    }

    /**
     * Launch the application with the title and resizable flag specified
     * @param title Window title
     * @param resizable Should the window be user-resizable?
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    protected void launch(String title, boolean resizable) {
        launch(title, DEFAULT_WIDTH, DEFAULT_HEIGHT, resizable);
    }

    /**
     * Launch the application with the window dimensions and resizable flag specified
     * @param width Window width
     * @param height Window height
     * @param resizable Should the window be user-resizable?
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    protected void launch(int width, int height, boolean resizable) {
        launch(null, width, height, resizable);
    }

    /**
     * Launch the application with the window title specified
     * @param title Window title
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    protected void launch(String title) {
        launch(title, false);
    }

    /**
     * Launch the application with the window dimensions specified
     * @param width Window width
     * @param height Window height
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    protected void launch(int width, int height) {
        launch(null, width, height);
    }

    /**
     * Launch the application with the default window specifications
     */
    @SuppressWarnings("unused")
    protected void launch() {
        launch(null);
    }

    /**
     * Get the updates-per-second speed, specified by {@code DEFAULT_UPS} or by the client
     * @return updates-per-second speed
     */
    static int getUPS() {
        // For now, the UPS cannot be changed by the user
        return DEFAULT_UPS;
    }

    /**
     * Set the window background color
     * @param color Color
     */
    @SuppressWarnings("unused")
    protected void setBackgroundColor(Color color) {
        if (window != null) {
            window.updateBackgroundColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }
    }

    /**
     * Set the window background color
     * @param red Red
     * @param green Green
     * @param blue Blue
     * @param alpha Alpha
     */
    @SuppressWarnings("unused")
    protected void setBackgroundColor(int red, int green, int blue, float alpha) {
        setBackgroundColor(new Color(red, green, blue, alpha));
    }

    /**
     * Change the window's title
     * @param newTitle The new title
     */
    @SuppressWarnings("unused")
    protected void setWindowTitle(String newTitle) {
        if (window != null) {
            window.updateTitle(newTitle);
        }
    }

    /**
     * Change the window's size
     * @param newWidth New window width
     * @param newHeight New window height
     */
    @SuppressWarnings("unused")
    protected void setWindowSize(int newWidth, int newHeight) {
        if (window != null) {
            window.updateSize(newWidth, newHeight);
        }
    }
}
