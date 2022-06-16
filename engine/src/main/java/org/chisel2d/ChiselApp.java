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

import java.util.List;

public abstract class ChiselApp {

    // Logger
    private static final Logger LOG = LogManager.getLogger();

    // Default window title
    private static final String DEFAULT_TITLE = "Window";

    // Default window width
    private static final int DEFAULT_WIDTH = 800;

    // Default window height
    private static final int DEFAULT_HEIGHT = 600;

    // Protected constructor
    protected ChiselApp() { }

    // Is the app running?
    private static boolean running = false;

    /**
     * Launch the application with all window parameters specified
     * @param title Window title
     * @param width Window width
     * @param height Window height
     * @param resizable Should the window be user-resizable?
     */
    protected static void launch(String title, int width, int height, boolean resizable) {
        if (running) {
            throw new IllegalStateException("The application is already running");
        }

        // Validate window title
        String windowTitle = title == null
                ? DEFAULT_TITLE
                : title;

        LOG.info(
            "Creating application instance: '{}' at {}x{}, resizable={}",
            windowTitle, width, height, resizable
        );

        List<Subsystem> subsystems = List.of(
                new Window(windowTitle, width, height, resizable)
        );

        // Initialise all subsystems, then start
        subsystems.forEach(Subsystem::init);
        subsystems.forEach(Subsystem::start);

        // Main loop
        running = true;
        while (running) {
            for (Subsystem subsystem : subsystems) {
                // Perform a loop update
                running = subsystem.update();
                if (!running) {
                    LOG.info(
                        "Application shutdown requested by subsystem: {}",
                        subsystem.getClass().getSimpleName()
                    );

                    break; // Halt loop execution
                }
            }
        }

        // Termination stage
        subsystems.forEach(Subsystem::shutdown);
    }

    /**
     * Launch the application with the title and dimensions specified
     * @param title Window title
     * @param width Window width
     * @param height Window height
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    protected static void launch(String title, int width, int height) {
        launch(title, width, height, true);
    }

    /**
     * Launch the application with the title and resizable flag specified
     * @param title Window title
     * @param resizable Should the window be user-resizable?
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    protected static void launch(String title, boolean resizable) {
        launch(title, DEFAULT_WIDTH, DEFAULT_HEIGHT, resizable);
    }

    /**
     * Launch the application with the window dimensions and resizable flag specified
     * @param width Window width
     * @param height Window height
     * @param resizable Should the window be user-resizable?
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    protected static void launch(int width, int height, boolean resizable) {
        launch(null, width, height, resizable);
    }

    /**
     * Launch the application with the window title specified
     * @param title Window title
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    protected static void launch(String title) {
        launch(title, false);
    }

    /**
     * Launch the application with the window dimensions specified
     * @param width Window width
     * @param height Window height
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    protected static void launch(int width, int height) {
        launch(null, width, height);
    }

    /**
     * Launch the application with the default window specifications
     */
    @SuppressWarnings("unused")
    protected static void launch() {
        launch(null);
    }
}
