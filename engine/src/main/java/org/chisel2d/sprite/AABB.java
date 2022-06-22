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

package org.chisel2d.sprite;

import org.joml.Vector2f;

/**
 * Represents a bounding box around a sprite texture.
 * All values are absolute.
 */
@SuppressWarnings("unused")
public class AABB {

    // The absolute centre (x, y) of the boundary.
    private float centreX, centreY;

    // The width and height of the boundary.
    private float width, height;

    // The scale applied to the sprite.
    private float scaleFactor = 1.0f;

    public AABB() { }

    public float getCentreX() {
        return centreX;
    }

    public void setCentreX(float centreX) {
        this.centreX = centreX;
    }

    public float getCentreY() {
        return centreY;
    }

    public void setCentreY(float centreY) {
        this.centreY = centreY;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * @return the absolute left coordinate of the bounding box.
     */
    public float getLeft() {
        return centreX - scaleFactor * (width / 2);
    }

    /**
     * @return the absolute right coordinate of the bounding box.
     */
    public float getRight() {
        return centreX + scaleFactor * (width / 2);
    }

    /**
     * @return the absolute bottom coordinate of the bounding box.
     */
    public float getBottom() {
        return centreY - scaleFactor * (height / 2);
    }

    /**
     * @return the absolute top coordinate of the bounding box.
     */
    public float getTop() {
        return centreY + scaleFactor * (height / 2);
    }

    public float getAbsoluteWidth() {
        return width * scaleFactor;
    }

    public float getAbsoluteHeight() {
        return height * scaleFactor;
    }

    /**
     * Retrieve the top left corner of the boundary.
     *
     * @return An (x,y) pair of absolute values, considering scale.
     */
    public Vector2f getTopLeft() {
        return new Vector2f(getLeft(), getTop());
    }

    /**
     * Retrieve the bottom right corner of the boundary.
     *
     * @return An (x,y) pair of absolute values, considering scale.
     */
    public Vector2f getBottomRight() {
        return new Vector2f(getRight(), getBottom());
    }

    /**
     * Retrieve the top right corner of the boundary.
     *
     * @return An (x,y) pair of absolute values, considering scale.
     */
    public Vector2f getTopRight() {
        return new Vector2f(getRight(), getTop());
    }

    /**
     * Retrieve the bottom left corner of the boundary.
     *
     * @return An (x,y) pair of absolute values, considering scale.
     */
    public Vector2f getBottomLeft() {
        return new Vector2f(getLeft(), getBottom());
    }

    public void moveX(float value) {
        centreX += value;
    }

    public void moveY(float value) {
        centreY += value;
    }

    public void scaleBy(float sf) {
        scaleFactor += sf;
        if (scaleFactor <= 0.0f) {
            scaleFactor = 0.0f;
        }
    }
}