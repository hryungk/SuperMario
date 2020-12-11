/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.demo.level.tile;

import main.java.com.demo.Commons;
import main.java.com.demo.entity.Entity;
import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 *
 * @author HRK
 */
public class FlagTile extends Tile {

    private int x, y, dy;
    private boolean reachedBottom;
    protected int score;    // score the player get when interact with the hidden sprite.
    protected String scoreStr;  // score in string
    protected int scoreX, scoreY, yFin;    // location of the score string, final location of y
    
    public FlagTile(int id) {
        super(id);
        
        initFlagTile();
    }
    
    private void initFlagTile() {
        
        x = Commons.X_MAX - ES / 2;
        y = Commons.GROUND - ES - 127 - ES;
        dy = 0;
        
        xS = 12;
        yS = 2;
        
        yFin = y;
        scoreStr = "";
        scoreX = x + ES + 8;
        scoreY = y + 127;
    }
    
    @Override
    public void render(Screen screen, Level level, int xt, int yt) {
        int sw = screen.getSheet().width;   // width of sprite sheet (256)
        int colNum = sw / PPS;    // Number of squares in a row (32)     
        
        for (int ys = 0; ys < hS; ys++) {
            for (int xs = 0; xs < wS; xs++) {
                screen.render(x + xs * PPS, y + ys * PPS, (xS + xs) + (yS + ys) * colNum, 0); // Loops through all the squares to render them all on the screen.                    
            }
        }  
        
        // Render score location once died
        if (level.player.reachedEnd()){
            Font.draw(scoreStr, screen, scoreX, scoreY, Color.WHITE);
        }   
    }

    @Override
    public void tick(int xt, int yt, Level level) {
        reachedBottom = y >= Commons.GROUND - ES - ES;
        if (level.player.reachedEnd() && !reachedBottom) {            
            dy = 1;            
            y += dy;
            
            // Update score location        
            if (scoreY > yFin){                
                scoreY--;
            }     
        } 
    }

    @Override
    public void bumpedInto(int xt, int yt, Entity entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }    
    
    public boolean reachedBottom() {
        return reachedBottom;
    }
    
    public void setScore(int num) {
        score = num;
        scoreStr = Integer.toString(num);
    }
}
