package main.java.com.demo.level.tile;

import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * A class that represents a pipe tile.
 * @author HRK
 */
public class PipeTile extends Tile {
    
    private int pipeSize;

    public PipeTile(int id) {
        super(id);
        
        xS = 0;
        yS = 0;
    }
    
    public PipeTile(int id, int size) {
        this(id);
        pipeSize = size;        
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
