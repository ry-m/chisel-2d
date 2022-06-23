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

package org.chisel2d.util;

@SuppressWarnings("unused")
public class Color {

    public static Color BLACK = new Color(0);

    public static Color WHITE = new Color(255);

    public static Color RED = new Color(255, 0, 0);

    public static Color LIME = new Color(0, 255, 0);

    public static Color BLUE = new Color(0, 0, 255);

    public static Color YELLOW = new Color(255, 255, 0);

    public static Color CYAN = new Color(0, 255, 255);

    public static Color MAGENTA = new Color(255, 0, 255);

    public static Color SILVER = new Color(192);

    public static Color GRAY = new Color(128);

    public static Color MAROON = new Color(128, 0, 0);

    public static Color OLIVE = new Color(128, 128, 0);

    public static Color GREEN = new Color(0, 128, 0);

    public static Color PURPLE = new Color(128, 0, 128);

    public static Color TEAL = new Color(0, 128, 128);

    public static Color NAVY = new Color(0, 0, 128);

    // RGB values. Between 0 and 255.
    private int red, green, blue;

    // Transparency (1.0 == opaque, 0.0 == transparent).
    private float alpha;

    /**
     * Create a color with all RGBA fields initialised.
     *
     * @param red Red
     * @param green Green
     * @param blue Blue
     * @param alpha Alpha
     */
    public Color(int red, int green, int blue, float alpha) {
        this.red = validate(red);
        this.green = validate(green);
        this.blue = validate(blue);
        this.alpha = validate(alpha);
    }

    /**
     * Create an opaque color.
     *
     * @param red Red
     * @param green Green
     * @param blue Blue
     */
    public Color(int red, int green, int blue) {
        this(red, green, blue, 1.0f);
    }

    /**
     * Create a grey transparent color.
     *
     * @param grey Grey
     * @param alpha Alpha
     */
    public Color(int grey, float alpha) {
        this(grey, grey, grey, alpha);
    }

    /**
     * Create a grey color.
     *
     * @param grey Grey
     */
    public Color(int grey) {
        this(grey, 1.0f);
    }

    /**
     * Retrieve the red value.
     *
     * @return Red.
     */
    public int getRed() {
        return red;
    }

    /**
     * Set the red value.
     *
     * @param red Red.
     */
    public void setRed(int red) {
        this.red = validate(red);
    }

    /**
     * Retrieve the green value.
     *
     * @return Green.
     */
    public int getGreen() {
        return green;
    }

    /**
     * Set the green value.
     *
     * @param green Green.
     */
    public void setGreen(int green) {
        this.green = validate(green);
    }

    /**
     * Retrieve the blue value.
     *
     * @return Blue.
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Set the blue value.
     *
     * @param blue Blue.
     */
    public void setBlue(int blue) {
        this.blue = validate(blue);
    }

    /**
     * Retrieve the alpha value.
     *
     * @return Alpha.
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * Set the alpha value.
     *
     * @param alpha Alpha.
     */
    public void setAlpha(float alpha) {
        this.alpha = validate(alpha);
    }

    /**
     * Float conversion of red.
     *
     * @return Red as float between 0.0 and 1.0.
     */
    public float fRed() {
        return red / 255.0f;
    }

    /**
     * Float conversion of green.
     *
     * @return Green as float between 0.0 and 1.0.
     */
    public float fGreen() {
        return green / 255.0f;
    }

    /**
     * Float conversion of blue.
     *
     * @return Blue as float between 0.0 and 1.0.
     */
    public float fBlue() {
        return blue / 255.0f;
    }

    /**
     * Ensure the RGB value is within the range 0-255 inclusive.
     *
     * @param rgbValue Value to check.
     * @return The value, or a truncation if it is out of range.
     */
    private int validate(int rgbValue) {
        return Math.max(0, Math.min(rgbValue, 255));
    }

    /**
     * Ensure the alpha value is within the range 0.0-1.0 inclusive.
     * @param alphaValue Value to check.
     * @return The value, or a truncation if it is out of range.
     */
    private float validate(float alphaValue) {
        return Math.max(0.0f, Math.min(alphaValue, 1.0f));
    }
}

