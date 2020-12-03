package main.gfx;

import main.Commons;

public class Screen {
    public int xOffset; // the x offset of the screen [pixel].
    public int yOffset; // the y offset of the screen [pixel].

    public static final int BIT_MIRROR_X = 0x01; // used for mirroring an image 
    public static final int BIT_MIRROR_Y = 0x02; // used for mirroring an image

    public final int W, H; // width and height of the screen [pixels]
    public int[] pixels; // pixels to be drawn on the screen
    
    public SpriteSheet sheet; // sprite sheet used in the game   


    public Screen(int w, int h, SpriteSheet sheet) {
        this.sheet = sheet; // assigns the sprite-sheet
        W = w; // assigns width of the screen
        H = h; // assigns height of the screen

        pixels = new int[w * h]; // integer array of all the pixels on the screen.
        setOffset(0, 0);
    }

    /** Clears all the colors on the screen */
    public void clear(int color) {
        for (int i = 0; i < pixels.length; i++) // Loops through each pixel on the scren
            pixels[i] = color; // turns each pixel into a single color (clearing the screen!)
    }

    /** Renders an object from the sprite sheet based on screen coordinates, tile (SpriteSheet location), colors, and bits (for mirroring)
     * @param x0 The x position in the map [pixel]
     * @param y0 The y position in the map [pixel]
     * @param square The index of the current entity's location in the sprite sheet [square]
     * @param bits */
    public void render(int x0, int y0, int square, int bits) {
        x0 -= xOffset; // horizontal offset of the screen
        y0 -= yOffset; // vertical offset of the screen
        boolean mirrorX = (bits & BIT_MIRROR_X) > 0; // determines if the image should be mirrored horizontally.
        boolean mirrorY = (bits & BIT_MIRROR_Y) > 0; // determines if the image should be mirrored vertically.

        /* Whenever you see a '%' sign it means that java will divide the two numbers and get the remainder instead of the answer
         * 	For example:
         * 	6 / 3 = 2. because 3 goes into 6 two times.
         *  6 % 3 = 0. because 3 goes into 6 evenly and the remainder is 0.
         *  However:
         *  8 / 3 = 2. because java doesn't round the number and gets the whole number out of it.
         *  8 % 3 = 2. because when you divide the two number, 3 goes into 6 two times and the remainder is 2.
         *  
         *  Math lesson over :) */

        int PPS = Commons.PPS;  // pixels per square of the sprite sheet (8)
        int sw = sheet.width;   // width of the sprite sheet (256)
        int colNum = sw / PPS;    // number of squres in a row in the sprite sheet (32)
        int xs = square % colNum; // gets the x position of the tile in the sprite sheet
        int ys = square / colNum; // gets the y position of the tile in the sprite sheet
        int toffs = xs * PPS + ys * PPS * sw; // Gets beginning index of the tile in the sheet.pixels array
        
        for (int y = 0; y < PPS; y++) { // Loops along the height of the height of the tile
            int yeff = y; // effective y pixel
            if (mirrorY) yeff = (PPS-1) - y; // Reverses the pixel for a mirroring effect
            if (y + y0 < 0 || y + y0 >= H) continue; // If the pixel is out of bounds, then skip the rest of the loop.
            for (int x = 0; x < PPS; x++) { // Loops along the height of the width of the tile
                if (x + x0 < 0 || x + x0 >= W) continue; // If the pixel is out of bounds, then skip the rest of the loop.
                int xeff = x; // effective x pixel
                if (mirrorX) xeff = (PPS-1) - x;  // Reverses the pixel for a mirroring effect
//                int col = (colors >> (sheet.pixels[xs + ys * sheet.width + toffs] * 8)) & 255; // gets the color based on the passed in colors value.
//                if (col < 255) pixels[(x + xp) + (y + yp) * W] = col; // Inserts the colors into the image.
                int col = sheet.pixels[toffs + xeff + yeff * sw]; // gets the color based on the passed in colors value.
                if (col != -11447983)   // gray color
                    pixels[(x + x0) + (y + y0) * W] = col; // Inserts the colors into the image.
            }
        }
    }

    
    /** Renders an object from the sprite sheet based on screen coordinates, tile (SpriteSheet location), colors, and bits (for mirroring) */
    public void renderFont(int x0, int y0, int square, int colors, int bits) {
        x0 -= xOffset; // horizontal offset of the screen
        y0 -= yOffset; // vertical offset of the screen
        boolean mirrorX = (bits & BIT_MIRROR_X) > 0; // determines if the image should be mirrored horizontally.
        boolean mirrorY = (bits & BIT_MIRROR_Y) > 0; // determines if the image should be mirrored vertically.

        int PPS = Commons.PPS;  // pixels per square of the sprite sheet (8)
        int sw = sheet.width;   // width of the sprite sheet (256)
        int colNum = sw / PPS;    // number of squres in a row in the sprite sheet (32)
        int xs = square % colNum; // gets the x position of the tile in the sprite sheet
        int ys = square / colNum; // gets the y position of the tile in the sprite sheet
        int toffs = xs * PPS + ys * PPS * sw; // Gets beginning index of the tile in the sheet.pixels array

        for (int y = 0; y < PPS; y++) { // Loops along the height of the height of the tile
            int yeff = y; // effective y pixel
            if (mirrorY) yeff = (PPS-1) - y; // Reverses the pixel for a mirroring effect
            if (y + y0 < 0 || y + y0 >= H) continue; // If the pixel is out of bounds, then skip the rest of the loop.
            for (int x = 0; x < PPS; x++) { // Loops along the height of the width of the tile
                if (x + x0 < 0 || x + x0 >= W) continue; // If the pixel is out of bounds, then skip the rest of the loop.
                int xeff = x; // effective x pixel
                if (mirrorX) xeff = (PPS-1) - x;  // Reverses the pixel for a mirroring effect
//                int col = (colors >> (sheet.pixels[xeff + yeff * sw + toffs] * 8)) & 255; // gets the color based on the passed in colors value.
//                if (col < 255) pixels[(x + x0) + (y + y0) * W] = col; // Inserts the colors into the image.  
                int col = sheet.pixels[toffs + xeff + yeff * sw]; // gets the color based on the passed in colors value.
                if (col != -16777216)   // black color
                    pixels[(x + x0) + (y + y0) * W] = colors; // Inserts the colors into the image.
            }
        }        
    }
    /** Sets the offset of the screen */
    public void setOffset(int xOffset, int yOffset) {
            this.xOffset = xOffset; // assigns the horizontal offset
            this.yOffset = yOffset; // assigns the vertical offset
    }
    
    public SpriteSheet getSheet() {
        return sheet;
    }
}