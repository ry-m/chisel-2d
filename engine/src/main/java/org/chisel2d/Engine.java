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

import java.util.Arrays;
import java.util.List;

class Engine {

    // Logger
    private static final Logger LOG = LogManager.getLogger();

    // Is the engine running?
    private static boolean running = false;

    // Subsystems
    private final Subsystem[] subsystems;

    Engine(Subsystem[] subsystems) {
        this.subsystems = subsystems;
    }

    void start() {
        if (running) {
            LOG.fatal("Duplicate call to Engine.start() method");
            throw new IllegalStateException("The application is already running");
        }

        List<Subsystem> subsystemsList = Arrays.asList(subsystems);

        // Initialise all subsystems, then start
        subsystemsList.forEach(Subsystem::init);
        subsystemsList.forEach(Subsystem::start);

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
        subsystemsList.forEach(Subsystem::shutdown);
    }
}
