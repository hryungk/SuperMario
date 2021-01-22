/* 
    This code is directly imported from Miniventure game. 
    (https://github.com/shylor/miniventure.git ).
 */
package main.java.com.demo.gfx;

import main.java.com.demo.Commons;

/**
 * This is all the characters that will be translated to the screen. (The spaces
 * are important). The order of the letters in the chars string is represented
 * in the order that they appear in the sprite-sheet.
 */
public class Font {

    private static String chars = ""
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ      "
            + "0123456789.,!?'\"-+=/\\%()<>:;©    "
            + "";
    /* Note: I am thinking of changing this system in the future so that it's 
    much simpler -David */

    // Size of the tile in the sprite sheet. (8 px) 
    private static final int PPS = Commons.PPS;   

    /**
     * Draws the message to the x & y coordinates on the screen.
     *
     * @param msg A String including the message to be displayed
     * @param screen Screen currently displayed
     * @param x x starting position of the message
     * @param y y starting position of the message
     * @param col An integer representing a color
     */
    public static void draw(String msg, Screen screen, int x, int y, int col) {
        // Number of tiles in a row on the sprite sheet. (256 / 8 = 32).
        int colNum = screen.getSheet().width / PPS;  
        int yTile = 30;     // Letters are located 30th row in the sprite sheet. 
        msg = msg.toUpperCase(); // Turns all the characters in into upper case.
        // Loops through all the characters in msg.
        for (int i = 0; i < msg.length(); i++) { 
            // The current letter in the message loop.
            int ix = chars.indexOf(msg.charAt(i)); 
            if (ix >= 0) {  // If the character is found in chars list
                 // Render the character on the screen.
                screen.renderFont(x + i * PPS, y, ix + yTile * colNum, col, 0);
            }
        }
    }
}
