package main.java.com.demo.level.tile;

import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * A class that represents a block tile.
 * @author HRK
 */
public class BlockTile extends Tile {

    public BlockTile(int id) {
        super(id);
        
        xS = 6;
        yS = 0;
    }

    @Override
    public void render(int x, int y, Level level, Screen screen) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void tick(int xt, int yt, Level level) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }    
}
