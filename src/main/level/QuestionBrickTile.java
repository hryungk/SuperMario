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
public class QuestionBrickTile extends Tile {
    
    private final int ES = Commons.ENTITY_SIZE;
    private int bNum, bCounter, dbNum, scale;
    
    public QuestionBrickTile(int id) {
        super(id);
        
        xS = 10;
        yS = 0;
        scale = 100;
        bNum = 0;
        bCounter = tickCount;
        dbNum = 1;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        
        int xt = xS + bNum * 2; // animation based on walk distance (0 is standing still and 2 is moving)  
        
        int sw = screen.getSheet().width;   // width of sprite sheet (256)
        int colNum = sw / Commons.PPS;    // Number of squares in a row (32)
//        screen.render(x * ES + 0, y * ES + 0, xS + yS * colNum, 0); // renders the top-left part of the brick        
        
        int PPS = Commons.PPS;
        for (int ys = 0; ys < hS; ys++) {
            for (int xs = 0; xs < wS; xs++) {
                screen.render(x * ES + xs * PPS, y * ES + ys * PPS, (xt + xs) + (yS + ys) * colNum, 0); // Loops through all the squares to render them all on the screen.                    
            }
        }
    }

    @Override
    public void tick(int xt, int yt, Level level) {
//        bCounter++;        
        
        switch (bNum) {
            case 0: // bright 
                dbNum = Math.abs(dbNum);
                if (bCounter > 2*scale) {
                    bNum += dbNum;
                    bCounter = 0;
                } else
                    bCounter++;
                break;
            case 1: // intermediate
                if (bCounter > scale) {
                    bNum += dbNum;
                    bCounter = 0;
                } else
                    bCounter++;                
                break;
            case 2: // dark
                if (bCounter > scale) {
                    dbNum = - dbNum;
                    bNum += dbNum;
                    bCounter = 0;
                } else
                    bCounter++;
                break;
        }
//        System.out.println("Block counter: " + bCounter + ", Qblock number: " + bNum);
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
