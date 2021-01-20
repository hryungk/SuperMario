package main.java.com.demo.level.levelgen;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import main.java.com.demo.Commons;
import main.java.com.demo.level.tile.*;

/**
 * Creates a map for a level.
 *
 * @author HRK
 */
public class LevelGen {
        
    private static final int ES = Commons.ENTITY_SIZE; // Tile size [pixel]
    private static final int W_S = 3584 / ES;// Width of the level's map [tile]
    private static final int H_S = 240 / ES; // Height of the level's map [tile]    
    private final int W, H;        // Width and Height of the level's map [tile]
    private static Tile[] tileMap;

    public LevelGen(int w, int h) {
        W = w;
        H = h;
    }
    
    /**
     * Creates and determines if the surface map is ready to be played.
     *
     * @return A byte array containing the tile IDs for the map.
     */
    public byte[] createAndValidateTopMap() {
        do { // Keep repeating this loop until it's done...                    

            byte[] result = createTopMap();     // Creates the terrain.
            int[] count = new int[256];         // Creates a new integer array.

            // The '& 0xff' part gets the last 8 bits of the 32-bit integer.
            for (int i = 0; i < W * H; i++) {   // Loops through the map
                count[result[i] & 0xff]++;      // Increases the data value by 1
                                                // Trust me it's important.
            }
            return result; // Return the resulting map, and use it for the game.
        } while (true);    // While there is no returned result, keep looping.
    }

    /**
     * Creates a surface map.
     *
     * @return A byte array containing the tile IDs for the map.
     */
    private byte[] createTopMap() {

        byte[] idMap = new byte[W * H]; // The tile IDs of the map        
        tileMap = new Tile[W * H];

        // Add sky to the entire map.
        for (int y = 0; y < H; y++) {    // Loops through the height of the map.
            for (int x = 0; x < W; x++) {// Loops through the width of the map.
                int i = x + y * W;       // Current tile being edited.
                idMap[i] = Tile.sky.ID;  // Add tile ID to the ID array.
                tileMap[i] = Tile.sky;   // Add tile to the map array.
            }
        }

        // Add ground.
        int bh = Commons.BOARD_HEIGHT;
        int[][] GPOS = Commons.GPOS;
        for (int y = H - 2; y < H; y++) {// Loops through the height of the map.
            for (int[] a : GPOS) {
                int beg = a[0] / ES;
                int end = a[1] / ES;
                // Loops through the width of the map.
                for (int x = beg; x <= end; x++) {
                    int i = x + y * W;          // Current tile being edited.
                    idMap[i] = Tile.ground.ID;  // Add tile ID to the ID array.
                    tileMap[i] = Tile.ground;   // Add tile to the map array.
                }
            }
        }

        // Add pipes.
        int[][] PPOS = Commons.PPOS;
        for (int[] a : PPOS) {
            int xLeft = a[1] / ES;              // x tile position
            for (int x = xLeft; x <= xLeft + 1; x++) {
                int yTop = a[2] / ES;           // y tile position
                for (int y = yTop; y < H - 2; y++) {
                    int i = x + y * W;          // Current tile being edited.
                    idMap[i] = Tile.pipe.ID;    // Add tile ID to the ID array.
                    tileMap[i] = Tile.pipe;     // Add tile to the map array.
                }
            }
        }

        // Add ascending blocks.
        int y1 = (bh - 48) / ES;
        int[][] BPOS_A = Commons.BPOS_A;
        for (int[] blocks : BPOS_A) {           // Loops through the BPOS_A list                
            int x0 = blocks[0] / ES;            // x tile position
            int numX = blocks[1];
            int numY = blocks[2];
            for (int yi = 0; yi < numY; yi++) { // Loops through vertically.
                int xBeg = x0 + yi;
                int y = y1 - yi;
                for (int xi = 0; xi < numX; xi++) {// Loops through horizontally
                    int x = xBeg + xi;
                    int i = x + y * W;
                    idMap[i] = Tile.block.ID;   // Add tile ID to the ID array.
                    tileMap[i] = Tile.block;    // Add tile to the map array.
                }
                numX--;
            }
        }
        // Add descending blocks
        int[][] BPOS_D = Commons.BPOS_D;
        for (int[] blocks : BPOS_D) {           // Loop through the BPOS_D list                
            int x0 = blocks[0] / ES;            // x tile position
            int numX = blocks[1];
            int numY = blocks[2];
            for (int yi = 0; yi < numY; yi++) { // Loops through vertically.            
                int y = y1 - yi;
                for (int xi = 0; xi < numX; xi++) {
                    int x = x0 + xi;
                    int i = x + y * W;
                    idMap[i] = Tile.block.ID;   // Add tile ID to the ID array.
                    tileMap[i] = Tile.block;    // Add tile to the map array.
                }
                numX--;
            }
        }

        // Add bricks.
        int tileId = 6;
        int[][] BRPOS = Commons.BRPOS;
        for (int[] a : BRPOS) {
            // Loops through the height of the map
            int x0 = a[0] / ES;         // x tile position
            int y = a[1] / ES;          // y tile position
            int bNum = a[2];            // Number of bricks in this row
            for (int x = x0; x < x0 + bNum; x++) {
                int i = x + y * W;      // Current tile being edited.
                InteractiveTile bt = new BrickTile(4, x, y);
                idMap[i] = bt.ID;       // Add tile ID to the ID array.
                tileMap[i] = bt;        // Add tile to the map array.
                tileId++;
            }
        }

        // Add question bricks.
        int[][] QBRPOS = Commons.QBRPOS;
        for (int[] a : QBRPOS) {
            // Loops through the height of the map
            int x0 = a[0] / ES;     // x tile position
            int y = a[1] / ES;      // y tile position
            int bNum = a[2];        // Number of bricks in this row
            for (int x = x0; x < x0 + bNum; x++) {
                int i = x + y * W;  // Current tile being edited.
                InteractiveTile qbt = new QuestionBrickTile(5, x, y);
                idMap[i] = qbt.ID;  // Add tile ID to the ID array.
                tileMap[i] = qbt;   // Add tile to the map array.
                tileId++;
            }
        }

        // Add the flag.
        FlagTile flagTile = new FlagTile(6);
        Tile.flag = flagTile;
        int x = ((FlagTile) Tile.flag).getX() / ES;
        int y = ((FlagTile) Tile.flag).getY() / ES;
        int i = x + y * W;          // Current tile being edited.        
        idMap[i] = Tile.flag.ID;    // Add tile ID to the ID array.
        tileMap[i] = Tile.flag;     // Add tile to the map array.
        tileId++;

        return idMap;               // Returns the map's tiles and data.
    }
    
    public Tile[] getTileMap() {
        return tileMap;
    }

    /**
     * Run this class to show a generator.
     *
     * @param args
     */
    public static void main(String[] args) {

        LevelGen levelGen = new LevelGen(W_S, H_S);
        boolean hasquit = false; // True if the player has quit the map.
        while (!hasquit) {       // If the player has not quit the map
            byte[] map = levelGen.createAndValidateTopMap();

            BufferedImage img = new BufferedImage(W_S, H_S, 
                    BufferedImage.TYPE_INT_RGB);    // Creates an image
            int[] pixels = new int[W_S * H_S];      // The pixels in the image. 
            // Loops through the height of the map
            for (int y = 0; y < H_S; y++) { 
                // Loops through the width of the map
                for (int x = 0; x < W_S; x++) { 
                    int i = x + y * W_S;            // Current tile of the map.

                    // The colors used in the pixels are hexadecimal (0xRRGGBB).
                    if (map[i] == Tile.sky.ID) {    // If the tile is sky
                        pixels[i] = 0x5C94FC;   // The pixel will be sky blue.
                    }
                    if (map[i] == Tile.pipe.ID) {   // If the tile is pipe
                        pixels[i] = 0x80D010;       // The pixel will be green.
                    }
                    if (map[i] == Tile.ground.ID) { // if the tile is ground
                        pixels[i] = 0x802e05;   // The pixel will be dark brown.  
                    }
                    if (map[i] == Tile.block.ID) {  // If the tile is block
                        pixels[i] = 0xC84C0C;       // The pixel will be brown.         
                    }
                    if (map[i] == Tile.brickID) {   // If the tile is brick
                        pixels[i] = 0x802e05;  // The pixel will be light brown. 
                    }
                    if (map[i] == Tile.QbrickID) {  // If the tile is q brick
                        pixels[i] = 0xfc9838;       // The pixel will be yellow.
                    }
                    if (map[i] == Tile.flag.ID) {   // If the tile is the flag
                        pixels[i] = 0xffffff;       // The pixel will be white.
                    }                    
                }
            }
            // Sets the pixels into the image.
            img.setRGB(0, 0, W_S, H_S, pixels, 0, W_S); 

            String[] options = {"Quit"}; // Name of the buttons for the window.

            // Creates a new window dialog.
            int o = JOptionPane.showOptionDialog( 
                    null,           // Parent component (parent window)
                    null,           // Message component (We use an image.)
                    "Map Generator",// Title of the window
                    JOptionPane.YES_NO_OPTION,   // Option type
                    JOptionPane.QUESTION_MESSAGE,// Message type (not important)
                    // Creates the image, and scales it up 4 times as big.
                    new ImageIcon(img.getScaledInstance(W_S * 4, H_S * 4, 
                            Image.SCALE_AREA_AVERAGING)), 
                    options,
                    null            // Start value (not important)
            );
            /* Now you noticed that we made the dialog an integer. This is  
               because when you click a button it will return a number.
               Since we passed in 'options', the window will return 0 if you  
               press "Quit". If you press the red "x" close mark, the window 
               will return -1.*/
            // If the dialog returns 0 ("Quit" button) or -1 (red "x" button) 
            if (o == 0 || o == -1) {
                hasquit = true; // stop the loop and close the program.
            }
        }
    }
}
