package main.level.levelgen;

import main.entity.Player;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import main.SuperPusheen;
import main.Commons;
import main.level.tile.Tile;

public class LevelGen {
    

    /* random is a class that can create random numbers.
     * Example: 'int r = random.randInt(20);'
     * r will be a number between (0 to 19) [0 counts as the first value)
     */
    private final int W; // width of the map
    private final int H; // height of the map

    /** This creates noise to create random values for level generation
     * @param w Width of the map [tile]
     * @param h Height of the map [tile] */
    public LevelGen(int w, int h) {
        this.W = w; // assigns the width of the map
        this.H = h; // assigns the height of the map
    }	

    /** Creates and determines if the surface map is ready to be played.
     * @param w Width of the map [tile]
     * @param h Height of the map [tile]
     * @return  An array containing tile id.*/
    public static byte[] createAndValidateTopMap(int w, int h) {
        do { // Keep repeating this loop until it's done...                    
            try {            
                byte[] result = createTopMap(w, h); // creates the terrain.
                int[] count = new int[256]; // creates a new integer array

                /* The '& 0xff' part gets the last 8 bits of the 32-bit integer. */
                for (int i = 0; i < w * h; i++) { // Loops though the Width * Height of the map
                        count[result[i] & 0xff]++; // Increases the data value by 1, trust me it's important.
                }

                return result; // return the resulting map, and use it for the game
            } catch (IOException ex) {
                Logger.getLogger(SuperPusheen.class.getName()).log(
                        Level.SEVERE, null, ex);
            }			

        } while (true); // While there is no returned result, keep looping.
    }

    /** Creates the surface map */
    private static byte[] createTopMap(int w, int h) throws IOException {

        byte[] map = new byte[w * h]; // The tiles of the map

        /* Add sky. */
        for (int y = 0; y < h-2; y++) { // Loops through the height of the map
            for (int x = 0; x < w; x++) { // A loop inside a loop that loops through the width of the map.
                int i = x + y * w; // Current tile being edited.
                map[i] = Tile.sky.ID; // the tile will become sky                            
            }
        }

        /* Add ground. */
        int bh = Commons.BOARD_HEIGHT;
        int[][] GPOS = Commons.GPOS;
        for (int y = h-2; y < h; y++) { // Loops through the height of the map
            for (int[] a : GPOS) {
                int beg = a[0]/16;
                int end = a[1]/16;
                for (int x = beg; x <= end; x++) { // A loop inside a loop that loops through the width of the map.
                    int i = x + y * w; // Current tile being edited.
                    map[i] = Tile.ground.ID; // the tile will become ground                            
                }
            }
        }

        /* Add pipes */                         
        int[][] PPOS = Commons.PPOS;
        //for (int ii = 0; ii < pLen; ii++) { // Loops through the height of the map  
        for (int[] a : PPOS) {
            int xLeft = a[1]/16; // x tile position
            for (int x = xLeft; x <= xLeft+1; x++) {                    
                int yTop = a[2]/16; // y tile position
                for (int y = yTop; y < h-2; y++) {
                    int i = x + y * w; // Current tile being edited.
                    map[i] = Tile.pipe.ID; // the tile will become a pipe.
                }
            }
        }

        /* Add blocks. */
        int y1 = (bh-48)/16; 
        // Ascending blocks
        int[][] BPOS_A = Commons.BPOS_A;
        for (int[] blocks : BPOS_A) { // Loop through the BPOS_A list                
            int x0 = blocks[0]/16; // x tile position
            int numX = blocks[1];
            int numY = blocks[2];
            for (int yi = 0; yi < numY; yi++) { // Loops through vertical way
                int xBeg = x0 + yi;
                int y = y1 - yi;
                for (int xi = 0; xi < numX; xi++) {
                    int x = xBeg + xi;
                    int i = x + y * w;
                    map[i] = Tile.block.ID; // the tile will become a block. 
                }
                numX--;
            }
        }                
        // Descending blocks
        int[][] BPOS_D = Commons.BPOS_D;
        for (int[] blocks : BPOS_D) { // Loop through the BPOS_D list                
            int x0 = blocks[0]/16; // x tile position
            int numX = blocks[1];
            int numY = blocks[2];
            for (int yi = 0; yi < numY; yi++) { // Loops through vertical way             
                int y = y1 - yi;
                for (int xi = 0; xi < numX; xi++) {
                    int x = x0 + xi;
                    int i = x + y * w;
                    map[i] = Tile.block.ID; // the tile will become a block. 
                }
                numX--;
            }
        }
        
        /* Add bricks. */
        int[][] BRPOS = Commons.BRPOS;
        for (int[] a : BRPOS) {
            // Loops through the height of the map
            int x0 = a[0]/16; // x tile position
            int y = a[1]/16; // y tile position
            int bNum = a[2]; // number of bricks in this row
            for (int x = x0; x < x0+bNum; x++) {
                int i = x + y * w; // Current tile being edited.
                map[i] = Tile.brick.ID; // the tile will become a brick.
            }
        }
        
        /* Add question bricks. */
        int[][] QBRPOS = Commons.QBRPOS;
        for (int[] a : QBRPOS) {
            // Loops through the height of the map
            int x0 = a[0]/16; // x tile position
            int y = a[1]/16; // y tile position
            int bNum = a[2]; // number of bricks in this row
            for (int x = x0; x < x0+bNum; x++) {
                int i = x + y * w; // Current tile being edited.
                map[i] = Tile.Qbrick.ID; // the tile will become a question brick.
            }
        }
        
        return map; // returns the map's tiles and data.
    }

    public static void showMap(byte[] map, Player player) {
        int w = 128;
        int h = 128;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = x + y * w;
                if (map[i] == Tile.sky.ID) pixels[i] = 0x5C94FC; // If the tile is sky, then the pixel will be sky blue
                if (map[i] == Tile.pipe.ID) pixels[i] = 0x80D010; // If the tile is pipe, then the pixel will be green
                if (map[i] == Tile.ground.ID) pixels[i] = 0x802e05; // if the tile is ground, then the pixel will be dark brown  
                if (map[i] == Tile.block.ID) pixels[i] = 0xC84C0C; // If the tile is block, then the pixel will be brown         
                if (map[i] == Tile.brick.ID) pixels[i] = 0x802e05; // if the tile is brick, then the pixel will be light brown 
                if (map[i] == Tile.Qbrick.ID) pixels[i] = 0xfc9838; // if the tile is question brick, then the pixel will be yellow  
            }
        }
        pixels[(player.x>>4) + (player.y>>4) * 128] = 0xffaa00;

        img.setRGB(0, 0, w, h, pixels, 0, w);
        JOptionPane.showMessageDialog(null, null, "Map", JOptionPane.PLAIN_MESSAGE, 
                new ImageIcon(img.getScaledInstance(w * 4, h * 4, Image.SCALE_AREA_AVERAGING)));
    }    

    /** *  Yep, LevelGen has a main method.When you run this class it will show a generator.
     * @param args */
    public static void main(String[] args) {

        boolean hasquit = false; // Determines if the player has quit the program or not.
        while (!hasquit) { //If the player has not quit the map
            int w = 3584/16; // width of the map
            int h = 240/16; // height of the map
            byte[] map; // the map
            map = LevelGen.createAndValidateTopMap(w, h); // Map will show the surface.


            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); // creates an image
            int[] pixels = new int[w * h]; // The pixels in the image. (an integer array, the size is Width * height)
            for (int y = 0; y < h; y++) { // Loops through the height of the map
                for (int x = 0; x < w; x++) { // (inner-loop)Loops through the entire width of the map
                    int i = x + y * w; // current tile of the map.

                    /*The colors used in the pixels are hexadecimal (0xRRGGBB). 
                     0xff0000 would be fully red
                     0x00ff00 would be fully blue
                     0x0000ff would be fully green
                     0x000000 would be black
                     and 0xffffff would be white
                     etc. */
                    if (map[i] == Tile.sky.ID) pixels[i] = 0x5C94FC; // If the tile is sky, then the pixel will be sky blue
                    if (map[i] == Tile.pipe.ID) pixels[i] = 0x80D010; // If the tile is pipe, then the pixel will be green
                    if (map[i] == Tile.ground.ID) pixels[i] = 0x802e05; // if the tile is ground, then the pixel will be dark brown  
                    if (map[i] == Tile.block.ID) pixels[i] = 0xC84C0C; // If the tile is block, then the pixel will be brown         
                    if (map[i] == Tile.brick.ID) pixels[i] = 0x802e05; // if the tile is brick, then the pixel will be light brown 
                    if (map[i] == Tile.Qbrick.ID) pixels[i] = 0xfc9838; // if the tile is question brick, then the pixel will be yellow 
                }
            }
            img.setRGB(0, 0, w, h, pixels, 0, w); // sets the pixels into the image

            String[] options = {"Quit"}; //Name of the buttons used for the window.

            int o = JOptionPane.showOptionDialog( // creates a new window dialog (It's an integer because it returns a number)
            null, // this would normally be used for a parent component (parent window), but we don't have one so it's null.
            null, // this would normally be used for a message, but since we use a image so it's null.
            "Map Generator", // Title of the window
            JOptionPane.YES_NO_OPTION, // Option type
            JOptionPane.QUESTION_MESSAGE, // message type (not important)
            new ImageIcon(img.getScaledInstance(w * 4, h * 4, Image.SCALE_AREA_AVERAGING)), // creates the image, and scales it up 4 times as big
            options, // lists the buttons below the image
            null // start value (not important)
            );
            /* Now you noticed that we made the dialog an integer. This is because 
               when you click a button it will return a number.
               Since we passed in 'options', the window will return 0 if you press 
               "Another" and it will return 1 when you press "Quit".
               If you press the red "x" close mark, the window will return -1. */

            // If the dialog returns -1 (red "x" button) or 1 ("Quit" button) then...
            if(o == -1 || o == 0) hasquit = true; // stop the loop and close the program.

        }
    }
}
