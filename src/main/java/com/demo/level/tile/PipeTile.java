/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.demo.level.tile;

import main.java.com.demo.entity.Entity;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 *
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

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void tick(int xt, int yt, Level level) {
        
    }

    @Override
    public void bumpedInto(int xt, int yt, Entity entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
