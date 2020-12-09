/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.demo.entity;

import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/** Whenever the player scores points, a new score string object is created.
 * @author HRK
 */
public class ScoreString extends Sprite {
    
    private String scoreStr;  // score in string
    private double scoreX, scoreY;    // location of the score string
    private final int YFIN; // final location of y
                
    public ScoreString(int x, int y, int score, Level level) {
        super(level);
        
        this.x = x;
        this.y = y;
        
        scoreX = x;
        scoreY = y;
        
        this.score = score;
        scoreStr = Integer.toString(score);       
        YFIN = y;
    }

    @Override
    public void tick() {
        // Update score location        
        if (level.player.getX() + level.player.width - level.getOffset() >= level.player.getPMax())
            scoreX += Math.max(0,level.player.getNetDx());
        scoreY -= 0.5;
        x = (int) scoreX;
        y = (int) scoreY;
        if (y < YFIN - ES)
            remove();
    }
        
    @Override
    public void render(Screen screen) {
        Font.draw(scoreStr, screen, x, y, Color.WHITE);
    }

    @Override
    protected void touchedBy(Sprite sprite) {
        
    }    
}
