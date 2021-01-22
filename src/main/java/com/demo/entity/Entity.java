package main.java.com.demo.entity;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Entity {

    public boolean removed;       // Determines if the entity should be removed.
    // Variables below need to be defined in children classes. 
    private BufferedImage image;  // Image representing the entity
    private byte[] pixels;        // Pixels for the image
    int width, height;            // Width and height of the entity    
    public int x, y;              // x & y coordinates on the map [pixel]
    protected int xS, yS;         // Location on the sprite sheet  [square]  

    /**
     * Constructor.
     */
    public Entity() {
        init();
    }

    /**
     * Initialize variables.
     */
    private void init() {
        removed = false;
    }

    /**
     * Sets the image of the entity.
     *
     * @param image The image to be assigned to this entity.
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        width = image.getWidth();
        height = image.getHeight();
    }

    /**
     * Gets the image of the entity.
     *
     * @return Image object for this entity.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Gets the pixels for the image.
     *
     * @return A byte array containing pixels of the image of the entity.
     */
    public byte[] getPixels() {
        return pixels;
    }

    /**
     * Removes the entity from the world.
     */
    public void remove() {
        removed = true;
    }

    /**
     * Sets the x position to a value.
     *
     * @param x An integer containing x coordinate of the entity.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y position to a value.
     *
     * @param y An integer containing y coordinate of the entity.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets the x position of the entity.
     *
     * @return An integer containing the x coordinate of the entity.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y position of the entity.
     *
     * @return An integer containing the y coordinate of the entity.
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the width of the entity image.
     *
     * @return An integer containing the width of the entity image.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the entity image.
     *
     * @return An integer containing the height of the entity image.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Checks if this entity intersects with a rectangle.
     *
     * @param x0 x position of the top left corner of the rectangle.
     * @param y0 y position of the top left corner of the rectangle.
     * @param x1 x position of the bottom right corner of the rectangle.
     * @param y1 y position of the bottom right corner of the rectangle.
     * @return True if the current entity and the rectangle intersect.
     */
    public boolean intersects(int x0, int y0, int x1, int y1) {
        return !(x + width < x0 || y + height < y0 || x1 < x || y1 < y);
    }

    /**
     * Checks if this entity intersects with another entity.
     *
     * @param e An entity to check the intersection with current entity.
     * @return True if the current entity and e intersect.
     */
    public boolean intersects(Entity e) {
        int x0 = e.getX();
        int y0 = e.getY();
        int x1 = x0 + e.getWidth();
        int y1 = y0 + e.getHeight();
        return intersects(x0, y0, x1, y1);
    }

    /**
     * Extended in Sprite.java.
     *
     * @param e The entity that wants to interact with this entity.
     * @return True if this entity blocks e, false if this entity lets e pass
     * it.
     */
    public boolean blocks(Entity e) {
        return true;
    }
}
