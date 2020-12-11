package main.java.com.demo.level.tile;

import java.util.Random;
import main.java.com.demo.Commons;
import main.java.com.demo.entity.Entity;
import main.java.com.demo.entity.Player;
import main.java.com.demo.entity.Sprite;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

public abstract class Tile {
    public static int tickCount = 0; //A global tickCount used in the Lava & water tiles.
    protected Random random = new Random(); // Random is used for random numbers (duh!).
    protected final int ES = Commons.ENTITY_SIZE;
    protected final int PPS = Commons.PPS;    

    public static Tile[] tiles = new Tile[256]; // An array of tiles
    public static Tile sky = new SkyTile(0);
    public static Tile ground = new GroundTile(1); 
    public static Tile pipe = new PipeTile(2); // creates a grass tile with the Id of 0, (I don't need to explain the other simple ones)
    public static Tile block = new BlockTile(3); 
    public static int brickID = 4;
    public static int QbrickID = 5;
    public static Tile flag = new FlagTile(6); 
    
//    public static Tile brick = new BrickTile(4); 
//    public static Tile Qbrick = new QuestionBrickTile(5); 
    
    public final byte ID; // Id of the tile
    private boolean steppedOn;
    private Sprite steppingSprite;
    protected int xS, yS; // Location on the sprite sheet [square, 8 pixels]
    protected int wS, hS; // width and height of tile [squares]    
    
    public Tile(int id) {
        ID = (byte) id; // creates the id
//        if (tiles[id] != null) throw new RuntimeException("Duplicate tile ids!"); // You cannot have over-lapping ids
        tiles[id] = this; // Assigns the id
        steppedOn = false;
        wS = hS = 2;    // 2-square-long in each side. 
    }
    
    /** Render method, used in sub-classes
     * @param screen current screen
     * @param level current level
     * @param x x tile position of the current level
     * @param y x tile position of the current level */
    public abstract void render(Screen screen, Level level, int x, int y);
    
    /** Update method
     * @param level current level
     * @param xt x position of the current level
     * @param yt x position of the current level */
    public abstract void tick(int xt, int yt, Level level);

    /** What happens when you run into the tile (ex: run into a cactus)
     * @param xt x-tile position of the entity that bumps into this tile.
     * @param yt x-tile position of the entity that bumps into this tile.
     * @param entity Entity that bumps into this tile. */
    public abstract void bumpedInto(int xt, int yt, Entity entity);    
    
    /** What happens when you hit an item on a tile (ex: Pickaxe on rock)
     * @param xt x-tile position of the tile on which the player hits an item.
     * @param yt y-tile position of the tile on which the player hits an item.
     * @param player The player sprite that interacts with this tile's item.
     * @param attackDir The direction from which the player interacts.
     * @return  true if the player can interact with the item on this tile. */
    public boolean interact(int xt, int yt, Player player, int attackDir) {
        return false;
    }
    
    /** Returns if the player can walk on it, overrides in sub-classes
     * @return True if the sprite can walk on it, false if it's a physical block. */
    public boolean mayPass() {
        return false;
    }    
    /** What happens when a Sprite steps on the tile.
     * @param sprite A sprite which steps on to this tile. */
    public void steppedOn(Sprite sprite) {
        steppedOn = true;
        steppingSprite = sprite;
    }
    
    /** Returns x square position.
     * @return  The x-position in square (8 px) */
    public int getXS() {
        return xS;
    }
    
    /** Returns y square position.
     * @return  The y-position in square (8 px) */
    public int getYS() {
        return yS;
    }
}