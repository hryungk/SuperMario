package main.java.com.demo.entity;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Entity {
    private BufferedImage image;
    private byte[] pixels;
    public boolean removed; // Determines if the entity should be removed from the level
    // Below variables need to be defined in children classes. 
    public int x, y;// x & y coordinates on the map    
    int width, height; // width and height of the entity    
    protected int xS, yS; // Location on the sprite sheet    

    public Entity () {
        init();
    }
    
    protected void init() {
        removed = false;
    }
    
    public void setImage(BufferedImage image) {
        this.image = image;
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();  
        width = image.getWidth();
        height = image.getHeight();                
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public byte[] getPixels() {
        return pixels;
    }
    
    /** Removes the entity from the world */
    public void remove() {
        removed = true; // sets the removed value to true
    }
    
    public void setX (int x) {
        this.x = x;
    }
    
    public void setY (int y) {
        this.y = y;
    }
    
    public int getX() {        
        return x;
    }

    public int getY() {        
        return y;
    }    
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    /** Checks if this entity intersects with a rectangle.
     * @param x0 x position of the top left corner of the rectangle.
     * @param y0 y position of the top left corner of the rectangle.
     * @param x1 x position of the bottom right corner of the rectangle.
     * @param y1 y position of the bottom right corner of the rectangle.
     * @return  True if the current entity and the rectangle intersects. */
    public boolean intersects(int x0, int y0, int x1, int y1) {
        /* if (x position + horizontal radius) is NOT smaller than x0 AND... 
         * if (y position + vertical radius) is NOT smaller than y0 AND... 
         * if (x position - horizontal radius) is NOT larger than x1 AND... 
         * if (y position - vertical radius) is NOT larger than y1. Then return true.
         *  */
//        int xr = width / 2;
//        int yr = height / 2;
//        return !(x + xr < x0 || y + yr < y0 || x - xr > x1 || y - yr > y1); 
        int w = width;
        int h = height;
        return !(x + w < x0 || y + h < y0 || x1 < x || y1 < y); 
    }
    
    public boolean intersects(Entity e) {
        /* if (x position + horizontal radius) is NOT smaller than x0 AND... 
         * if (y position + vertical radius) is NOT smaller than y0 AND... 
         * if (x position - horizontal radius) is NOT larger than x1 AND... 
         * if (y position - vertical radius) is NOT larger than y1. Then return true.
         *  */
//        int xr = width / 2;
//        int yr = height / 2;
//        return !(x + xr < x0 || y + yr < y0 || x - xr > x1 || y - yr > y1); 
        int x0 = e.getX();
        int y0 = e.getY();
        int x1 = x0 + e.getWidth();
        int y1 = y0 + e.getHeight();
        int w = width;
        int h = height;
        return !(x + w < x0 || y + h < y0 || x1 < x || y1 < y); 
    }
    
    /** Extended in Sprite.java.
     * @param e The entity that wants to interact with this entity.
     * @return  true if this entity blocks e, false if this entity lets e pass it. */
    public boolean blocks(Entity e) {
        return true;
    }
}