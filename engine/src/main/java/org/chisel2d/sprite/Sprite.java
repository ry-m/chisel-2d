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

import org.chisel2d.graphics.Texture;
import org.chisel2d.graphics.TextureManager;

@SuppressWarnings("unused")
public class Sprite {

    // The texture ID registered in the TextureManager
    private final String textureID;

    // Name that represent this sprite
    private final String name;

    // Position, scale and bounding box.
    private final AABB boundingBox = new AABB();

    // Rotation (degrees)
    private float rotation = 0.0f;

    // Is the sprite visible?
    private boolean visible = true;

    // Opacity (1 == fully opaque, 0 == fully transparent)
    private float opacity = 1.0f;

    // Reference to the sprite's current texture
    // TODO: support multiple textures
    private Texture currentTexture = null;

    /**
     * Create a sprite with a name and texture
     * @param name Name to identify the sprite
     * @param imagePath The image resource for the texture
     */
    public Sprite(String name, String imagePath) {
        this.name = name;
        this.textureID = TextureManager.register(imagePath);
    }

    /**
     * Create a sprite with a texture
     * @param imagePath The image resource for the texture
     */
    public Sprite(String imagePath) {
        // Register the texture to be loaded when the engine is initialised.
        this.textureID = TextureManager.register(imagePath);
        this.name = textureID;
    }

    /**
     * Find the texture stored in the TextureManager's database. The sprite's bounding box is updated
     * internally to determine its size
     * @return The texture
     */
    public Texture findTexture() {
        if (currentTexture == null) {
            this.currentTexture = TextureManager.getTexture(textureID);
            boundingBox.setWidth(currentTexture.getWidth());
            boundingBox.setHeight(currentTexture.getHeight());
        }

        return currentTexture;
    }

    /////////////////////////
    // Getters and setters //
    /////////////////////////

    public String getTextureID() {
        return textureID;
    }

    public boolean isVisible() {
        return visible && opacity != 0.0f;
    }

    public float getScale() {
        return boundingBox.getScaleFactor();
    }

    public void setScale(float sf) {
        boundingBox.setScaleFactor(sf);
    }

    public float getX() {
        return boundingBox.getCentreX();
    }

    public void setX(float x) {
        boundingBox.setCentreX(x);
    }

    public float getY() {
        return boundingBox.getCentreY();
    }

    public void setY(float y) {
        boundingBox.setCentreY(y);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = Math.max(0.0f, Math.min(1.0f, opacity));
    }

    /////////////////////////////
    // Modifications to sprite //
    /////////////////////////////

    public void show() {
        this.visible = true;
    }

    public void hide() {
        this.visible = false;
    }

    public void moveX(float value) {
        boundingBox.moveX(value);
    }

    public void moveY(float value) {
        boundingBox.moveY(value);
    }

    public void scaleBy(float sf) {
        boundingBox.scaleBy(sf);
    }

    public void rotateBy(float rotation) {
        this.rotation += rotation;
        if (this.rotation > 360) {
            this.rotation = 0;
        }
    }

    public void changeOpacity(float value) {
        this.opacity = Math.max(0.0f, Math.min(opacity + value, 1.0f));
    }

    /**
     * An overridable update() method provides access to the game tick. This method is called on each tick and should
     * be used for any ongoing tasks for the sprite to perform.
     */
    public void update() {}

    /**
     * @return the name of the sprite
     */
    @Override
    public String toString() {
        return name;
    }
}
