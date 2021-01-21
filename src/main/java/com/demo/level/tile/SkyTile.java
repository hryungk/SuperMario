package main.java.com.demo.level.tile;

import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * A class that represents a sky tile.
 * @author HRK
 */
public class SkyTile extends Tile {

    /**
     * Constructor.
     * @param id An integer containing the tile ID.
     */
    public SkyTile(int id) {
        super(id);
    }

     /**
     * Update method.
     *
     * @param xt x tile position of the current level [tile]
     * @param yt y tile position of the current level [tile]
     * @param level Current level
     */
    @Override
    public void tick(int xt, int yt, Level level) {
        
    }    
    
    /**
     * Render method, used in sub-classes.
     *     
     * @param xt x tile position of the current level [tile]
     * @param yt x tile position of the current level [tile]
     * @param level Current level
     * @param screen Current screen     
     */
    @Override
    public void render(int xt, int yt, Level level, Screen screen) {
        
    }
    
    /**
     * Determines if the player can pass by it.
     * Sky is always passable.
     * @return True if the sprite can pass by it, false if it's a physical block
     */
    @Override
    public boolean mayPass() {
        return true;
    }        
}
