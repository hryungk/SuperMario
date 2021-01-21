/* 
    This code is directly imported from Miniventure game. 
    (https://github.com/shylor/miniventure.git ).
 */
package main.java.com.demo.gfx;

import java.awt.image.BufferedImage;
import main.java.com.demo.ImageTool;

public class SpriteSheet {
    public int width, height;   // Width and height of the sprite sheet
    public int[] pixels;        // Pixels on the image (integer array)

    public SpriteSheet(BufferedImage image) {
        width = image.getWidth();       // Assigns the width from the image
        height = image.getHeight();     // Assigns the height from the image
        pixels = ImageTool.convertTo1D(image); 
                        
//        // Assigns the pixels of the image.
//        pixels = image.getRGB(0, 0, width, height, null, 0, width); 
//        for (int i = 0; i < pixels.length; i++) {// Loops through all the pixels
//            /* The '& 0xff' part gets the last 8 bits of the 32-bit integer. 
//                Divides the last 8 bits of the pixel by 64. 
//                Doesn't seem to do much at all. */
//            pixels[i] = (pixels[i] & 0xff) / 64; 
//        }
    }
}