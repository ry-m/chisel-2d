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
import org.chisel2d.util.Task;

/**
 * The {@code Timer} class maintains the frames and updates per second
 */
public class Timer implements Subsystem {

    // Logger
    private static final Logger LOG = LogManager.getLogger();

    // The game tick task. Called on a 'tick' update
    private final Task tick;

    private long lastTime = System.nanoTime();

    private double delta = 0.0;

    private double ns;

    private long timer = System.currentTimeMillis();

    private int updates = 0;

    private int frames = 0;

    /**
     * Constructor
     * @param tick Game tick task
     */
    public Timer(Task tick) {
        this.tick = tick;
    }

    @Override
    public void init() {
        ns = 1_000_000_000.0 / ChiselApp.getUPS();
    }

    @Override
    public void start() {
        LOG.info("Target UPS: {}", ChiselApp.getUPS());
    }

    @Override
    public boolean update() {
        long now = System.nanoTime();
        delta += (now - lastTime) / ns;
        lastTime = now;

        if (delta >= 1.0) {
            tick.invoke();
            updates++;
            delta--;
        }

        frames++;

        if (System.currentTimeMillis() - timer > 1_000) {
            timer += 1_000;
            LOG.info("FPS: {}, UPS: {}", frames, updates);
            updates = frames = 0;
        }

        return true;
    }

    @Override
    public void shutdown() { }
}
