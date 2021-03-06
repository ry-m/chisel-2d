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

package org.chisel2d.demo;

import org.chisel2d.ChiselApp;
import org.chisel2d.Timer;
import org.chisel2d.sprite.Sprite;
import org.chisel2d.sprite.SpriteManager;
import org.chisel2d.util.Color;

public class App extends ChiselApp {

    private final Sprite smile = new Sprite("demo/src/main/resources/smile.png");

    public static void main(String[] args) {
        new App().launch("My Demo App");
    }

    @Override
    protected void setup() {
        setBackgroundColor(Color.WHITE);
        SpriteManager.add(smile);
    }

    @Override
    protected void onTick() {
        smile.setX((float) (150 * Math.sin(Timer.getNumTicks() / 40.0)));
        smile.setY((float) (65 * Math.cos(Timer.getNumTicks() / 40.0)));
    }
}
