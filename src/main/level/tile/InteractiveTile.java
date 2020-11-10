/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.level.tile;

import main.Commons;
import main.entity.Entity;
import main.entity.Sprite;
import main.gfx.Screen;
import main.level.Level;

/**
 *
 * @author HRK
 */
public abstract class InteractiveTile extends Tile {
    
    protected final int ES = Commons.ENTITY_SIZE;    
    protected boolean isHitBottom;    // True when the player hits the bottom of this brick.
    protected int initY, y;    
    protected boolean removed;
    protected double ds;    
    protected double dsInit;
    
    public InteractiveTile(int id) {
        super(id);
        
        removed = false;        
        isHitBottom = false;
        ds = 0;
        dsInit = -2.5;
    }
   
    @Override
    public void tick(int xt, int yt, Level level) {
//        if (isHitBottom && bNum != 3) { // When first hit by the player
//            bNum = 3;
//            ds = dsInit;
//        }            
        if (isHitBottom) {
            if ((y == initY && ds == dsInit) || y < initY) {                 
                ds = ds + 0.5;
            }
            else
                ds = 0;        
        }
    }
    
    @Override
    public void render(Screen screen, Level level, int xt, int yt) {
        initY = yt * ES; // permanent y-position        
        if (isHitBottom && (int) (y + ds) <= initY) {
            y = (int) (y + ds);
        }
        else
            y = initY;
    }
    
    /** What happens when you hit the tile (ex: punching a tree) */
    public void hurt() {
        
        isHitBottom = true;
        ds = dsInit;
    }
    
    public void setHit(boolean hit) {
        this.isHitBottom = hit;
    }
    
    public boolean isHitBottom() {
        return isHitBottom;
    }
}
