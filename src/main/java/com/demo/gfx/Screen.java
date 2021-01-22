/* 
    This code is directly imported from Miniventure game. 
    (https://github.com/shylor/miniventure.git ).
 */
package main.java.com.demo.gfx;

import main.java.com.demo.Commons;

public class Screen {

    public final int W, H;      // Width and height of the screen [pixel]
    public int xOffset;         // x offset of the screen [pixel]
    public int yOffset;         // y offset of the screen [pixel]  
    public int[] pixels;        // Pixels to be drawn on the screen
    private final SpriteSheet sheet;   // Sprite sheet used in the game   
    public static final int BIT_MIRROR_X = 0x01;    // Mirroring horizontally
    public static final int BIT_MIRROR_Y = 0x02;    // Mirroring vertically

    /**
     * Constructor
     *
     * @param w An integer containing width of the screen.
     * @param h An integer containing height of the screen.
     * @param sheet Sprite sheet used in the game.
     */
    public Screen(int w, int h, SpriteSheet sheet) {
        this.sheet = sheet; // Assigns the sprite-sheet
        W = w;              // Assigns width of the screen
        H = h;              // Assigns height of the screen

        pixels = new int[w * h]; // Array of all the pixels on the screen.
        xOffset = yOffset = 0;  // Initialize the offsets.
    }

    /**
     * Clears all the colors on the screen.
     *
     * @param color The color to fill the screen with.
     */
    public void clear(int color) {
        // Loops through each pixel on the scren.
        for (int i = 0; i < pixels.length; i++) {
            // Turns each pixel into a single color (clearing the screen!).
            pixels[i] = color;
        }
    }

    /**
     * Renders an object from the sprite sheet based on screen coordinates, tile
     * (SpriteSheet location), colors, and bits (for mirroring).
     *
     * @param x0 The x position in the map [pixel]
     * @param y0 The y position in the map [pixel]
     * @param square The index of the current entity's location in the sprite
     * sheet [square]
     * @param bits An integer used to determine mirroring the image
     */
    public void render(int x0, int y0, int square, int bits) {
        x0 -= xOffset;  // Horizontal offset of the screen
        y0 -= yOffset;  // Vertical offset of the screen
        // Determines if the image should be mirrored horizontally.
        boolean mirrorX = (bits & BIT_MIRROR_X) > 0; 
        // Determines if the image should be mirrored vertically.
        boolean mirrorY = (bits & BIT_MIRROR_Y) > 0; 

        int PPS = Commons.PPS;   // Pixels per square of the sprite sheet (8 px)
        int sw = sheet.width;    // Width of the sprite sheet (256 px)
        int colNum = sw / PPS;   // Number of squres in a row in sprite sheet 
        int xs = square % colNum;// x position of the squre in sprite sheet
        int ys = square / colNum;// y position of the square in sprite sheet
        int toffs = xs * PPS + ys * PPS * sw; // Beginning index of the square 
                                              // in the sheet.pixels array

        for (int y = 0; y < PPS; y++) { // Loops along the height of the square.
            if (y + y0 < 0 || y + y0 >= H) { // If the pixel is out of bounds,
                continue;               // Skip the rest of the loop.
            }
            
            int yeff = y;       // Effective y pixel
            if (mirrorY) {      // If vertical mirroring is true,
                yeff = (PPS - 1) - y; // Reverses the pixel for mirroring effect
            }
            
            // Loops along the width of the square.
            for (int x = 0; x < PPS; x++) { 
                if (x + x0 < 0 || x + x0 >= W) {// If the pixel is out of bounds
                    continue;   // Skip the rest of the loop.
                }
                
                int xeff = x;   // Effective x pixel
                if (mirrorX) {  // If horizontal mirroring is true,
                    xeff = (PPS - 1) - x;  // Reverses the pixel for mirroring 
                }
                
                // Gets the color based on the passed in colors value.
                int col = sheet.pixels[toffs + xeff + yeff * sw]; 
                if (col != -11447983) // Background (gray) color of the sheet
                {   // Inserts the colors into the image.
                    pixels[(x + x0) + (y + y0) * W] = col; 
                }
            }
        }
    }

    /**
     * Renders a letter from the sprite sheet based on screen coordinates, tile
     * (SpriteSheet location), colors, and bits (for mirroring).
     * 
     * @param x0 The x position in the map [pixel]
     * @param y0 The y position in the map [pixel]
     * @param square The index of the current entity's location in the sprite
     * sheet [square]
     * @param color An integer containing color for the text
     * @param bits An integer used to determine mirroring the image
     */
    public void renderFont(int x0, int y0, int square, int color, int bits) {
        x0 -= xOffset;  // Horizontal offset of the screen
        y0 -= yOffset;  // Vertical offset of the screen
        // Determines if the image should be mirrored horizontally.
        boolean mirrorX = (bits & BIT_MIRROR_X) > 0; 
        // Determines if the image should be mirrored vertically.
        boolean mirrorY = (bits & BIT_MIRROR_Y) > 0; 

        int PPS = Commons.PPS;   // Pixels per square of the sprite sheet (8 px)
        int sw = sheet.width;    // Width of the sprite sheet (256 px)
        int colNum = sw / PPS;   // Number of squres in a row in sprite sheet 
        int xs = square % colNum;// x position of the squre in sprite sheet
        int ys = square / colNum;// y position of the square in sprite sheet
        int toffs = xs * PPS + ys * PPS * sw; // Beginning index of the square 
                                              // in the sheet.pixels array

        for (int y = 0; y < PPS; y++) { // Loops along the height of the square.
            if (y + y0 < 0 || y + y0 >= H) { // If the pixel is out of bounds,
                continue;               // Skip the rest of the loop.
            }
            
            int yeff = y;       // Effective y pixel
            if (mirrorY) {      // If vertical mirroring is true,
                yeff = (PPS - 1) - y; // Reverses the pixel for mirroring effect
            }
            
            // Loops along the width of the square.
            for (int x = 0; x < PPS; x++) { 
                if (x + x0 < 0 || x + x0 >= W) {// If the pixel is out of bounds
                    continue;   // Skip the rest of the loop.
                }
                
                int xeff = x;   // Effective x pixel
                if (mirrorX) {  // If horizontal mirroring is true,
                    xeff = (PPS - 1) - x;  // Reverses the pixel for mirroring 
                }
                
                // Gets the color based on the passed in colors value.
                int col = sheet.pixels[toffs + xeff + yeff * sw]; 
                if (col != -16777216) // Black color 
                {   // Inserts the colors into the image.
                    pixels[(x + x0) + (y + y0) * W] = color; 
                }
            }
        }
    }

    
    /**
     * Sets the offset of the screen.
     * @param xOffset Horizontal offset of the screen
     * @param yOffset Vertical offset of the screen
     */
    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;     // Assigns the horizontal offset.
        this.yOffset = yOffset;     // Assigns the vertical offset. 
    }

    /**
     * Gets the sprite sheet.
     * @return Sprite sheet used in the game.
     */
    public SpriteSheet getSheet() {
        return sheet;
    }
}
