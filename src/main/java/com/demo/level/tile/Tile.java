package main.java.com.demo.level.tile;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * An abstract class that represents a tile.
 * @author HRK
 */
public abstract class Tile {

    protected final int ES = Commons.ENTITY_SIZE;
    protected final int PPS = Commons.PPS;

    public static Tile[] tiles = new Tile[256]; // An array of tiles
    public static Tile sky = new SkyTile(0); // Creates a sky tile with Id #0
    public static Tile ground = new GroundTile(1);
    public static Tile pipe = new PipeTile(2);
    public static Tile block = new BlockTile(3);
    public static Tile flag = new FlagTile(6);
    // We need separate objects for InteractiveTiles.
    public static int brickID = 4;
    public static int QbrickID = 5;

    public final byte ID; // Id of the tile
    protected int xS, yS; // Location on the sprite sheet [square, 8 pixels]
    protected int wS, hS; // Width and height of tile [squares]    

    public Tile(int id) {
        ID = (byte) id;   // Assigns the ID.
        tiles[id] = this; // Adds the tile to corresponding ID location in tiles
        wS = hS = 2;      // Tiles are 2-square-long in each side. 
    }

    /**
     * Update method.
     *
     * @param xt x tile position of the current level [tile]
     * @param yt y tile position of the current level [tile]
     * @param level Current level
     */
    public abstract void tick(int xt, int yt, Level level);

    /**
     * Render method, used in sub-classes.
     *     
     * @param xt x tile position of the current level [tile]
     * @param yt x tile position of the current level [tile]
     * @param level Current level
     * @param screen Current screen     
     */
    public abstract void render(int xt, int yt, Level level, Screen screen);

    /**
     * Determines if the player can pass by it, overrides in sub-classes.
     *
     * @return True if the sprite can pass by it, false if it's a physical block
     */
    public boolean mayPass() {
        return false;
    }

    /**
     * Returns x square position.
     *
     * @return The x-position in squares
     */
    public int getXS() {
        return xS;
    }

    /**
     * Returns y square position.
     *
     * @return The y-position in squares
     */
    public int getYS() {
        return yS;
    }
}
