package main.java.com.demo.level.tile;

import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * A class that represents a ground tile.
 * @author HRK
 */
public class GroundTile extends Tile {

    public GroundTile(int id) {
        super(id);        
        xS = 4;
        yS = 0;
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
}
