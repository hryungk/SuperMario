package main.java.com.demo.gfx;

import main.java.com.demo.Commons;

public class Font {
    /* This is all the characters that will be translated to the screen. (The spaces are important). 
    The order of the letters in the chars string is represented in the order that they appear in the sprite-sheet. */
    private static String chars = "" + 
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ      " + 
                    "0123456789.,!?'\"-+=/\\%()<>:;©    " + 
                    "";
    /* Note: I am thinking of changing this system in the future so that it's much simpler -David */
    
    private static final int PPS = Commons.PPS; // Size of the tile in the spritesheet. (8)   

    /** Draws the message to the x & y coordinates on the screen.
     * @param msg A String including the message to be displayed
     * @param screen Screen currently displayed
     * @param x Starting x position of the message
     * @param y y position of the message
     * @param col An integer representing a color */
    public static void draw(String msg, Screen screen, int x, int y, int col) {
        int colNum = screen.sheet.width / PPS;  // Number of tiles in horizontal direction on the spritesheet. (256 / 8 = 32)  
        int yTile = Commons.FONT_Y;
        msg = msg.toUpperCase(); // turns all the characters you type in into upper case letters.
        for (int i = 0; i < msg.length(); i++) { // Loops through all the characters that you typed
            int ix = chars.indexOf(msg.charAt(i)); // the current letter in the message loop
            if (ix >= 0) { // if that character's position is larger than or equal to 0 then...
                screen.renderFont(x + i * PPS, y, ix + yTile * colNum, col, 0); // render the character on the screen
            }
        }
    }            
}