/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.level.tile;

import main.Commons;
import main.entity.Entity;
import main.gfx.Screen;
import main.level.Level;

/**
 *
 * @author HRK
 */
public class BrickTile extends InteractiveTile {

    private final int ES = Commons.ENTITY_SIZE;
    
    public BrickTile(int id, int xt, int yt) {
        super(id, xt, yt);
        
        xS = 8;
        yS = 0;
    }
    
    /** Render method, used in sub-classes
     * @param screen current screen
     * @param level current level
     * @param xt x tile position of the current level
     * @param yt x tile position of the current level */
    @Override
    public void render(Screen screen, Level level, int xt, int yt) {        
        super.render(screen, level, xt, yt);
        
        int sw = screen.getSheet().width;   // width of sprite sheet (256)
        int colNum = sw / Commons.PPS;    // Number of squares in a row (32)
//        screen.render(x * ES + 0, y * ES + 0, xS + yS * colNum, 0); // renders the top-left part of the brick        
        
        int PPS = Commons.PPS;
        for (int ys = 0; ys < hS; ys++) {
            for (int xs = 0; xs < wS; xs++) {
                screen.render(xt * ES + xs * PPS, y + ys * PPS, (xS + xs) + (yS + ys) * colNum, 0); // Loops through all the squares to render them all on the screen.                    
            }
        }  
    }

    @Override
    public void tick(int xt, int yt, Level level) {
        super.tick(xt, yt, level);
        if (removed)
            level.setTile(xt, yt, sky);        
    }

    @Override
    public void bumpedInto(int xt, int yt, Entity entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
