/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.level.tile;

import main.entity.Entity;
import main.entity.Sprite;
import main.gfx.Screen;
import main.level.Level;

/**
 *
 * @author HRK
 */
public class BlockTile extends Tile {

    public BlockTile(int id) {
        super(id);
        
        xS = 6;
        yS = 0;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void tick(int xt, int yt, Level level) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void hurt(int x, int y, Sprite source, int dmg, int attackDir) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bumpedInto(int xt, int yt, Entity entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
