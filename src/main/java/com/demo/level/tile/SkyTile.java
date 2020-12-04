/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.demo.level.tile;

import main.java.com.demo.entity.Entity;
import main.java.com.demo.entity.Sprite;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 *
 * @author HRK
 */
public class SkyTile extends Tile {

    public SkyTile(int id) {
        super(id);
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
     /** Returns if the player can walk on it, overrides in sub-classes  */
    @Override
    public boolean mayPass() {
        return true;
    }    
    
}
