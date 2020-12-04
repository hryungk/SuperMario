/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.demo.level.tile;

import main.java.com.demo.Commons;
import main.java.com.demo.entity.Coin;
import main.java.com.demo.entity.Entity;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 *
 * @author HRK
 */
public class BrickTile extends InteractiveTile {
    
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
        
        if (isHitBottom && hs != null) { // If there is a hidden sprite and is hit on the bottom            
            // When first hit and the hidden sprite is a coin, generate a random number between 0 and 9 (since it already has one coin)
            if (firstTime) { 
                if (hs instanceof Coin) {
                    numHS = (int) (Math.random() * 10);   // Maximum 9 coins + 1 (default)
                }
                else numHS = 0;
                firstTime = false;
            }
            
            // Change it to static question brick after there is no more hidden sprite.
            if (numHS <= 0)
                xS = 16;
            else {
                level.add(new Coin(x, initY, level));
                numHS--;   
            }           
            isHitBottom = false;            
        }
                
        if (removed)
            level.setTile(xt, yt, sky);        
    }
    
    @Override
    public void hurt() {
        if (!isHitBottom && xS == 8 || hs == null)
            super.hurt();        
        else
            ds = 0;  
    }

    @Override
    public void bumpedInto(int xt, int yt, Entity entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }    
}