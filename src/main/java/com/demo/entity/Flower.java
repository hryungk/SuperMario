package main.java.com.demo.entity;

import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/** Represents a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public class Flower extends HiddenSprite {
    
    private boolean doneFollowing;
    // The constructor initiates the x and y coordinates and the visible variable.
    public Flower(int x, int y, Level level) {
        super(x, y, level);
        initCoin();
    }    
    
    private void initCoin() {
        initY = y;
        xS = 0;
        yS = 6;         
        dx = 0;
        
        score = 1000; 
                
        height = width = ES;
        wS = width / PPS;
        hS = height / PPS;
        
        doneFollowing = false;
        
    }
    
    /** Update method, (Look in the specific entity's class) */
    @Override
    public void tick() {                    
        
        if (isActivated) {
            bNum = (bCounter / scale) % numStage;
            bCounter++;    
            
            if (!doneFollowing) { // First the flower follows the InteractiveTile's movement
                ds = ds + 0.5;
                y = (int) (y + ds);
                if (ds >= 0)
                    doneFollowing = true;
            } else if (!reachedTop) { // After the InteractiveTile reaches the top, this flower continues to move at a constant speed. 
                ds = -1;
                if (bCounter % ay == 0)
                    y = (int) (y + ds); 
                if (y <= initY - ES) {
                    reachedTop = true;
                    ds = 1;
                }    
            } // end if (reaching the top)        
        } // end if(isActivated)         
    }    

    /** Draws the sprite on the screen
     * @param screen The screen to be displayed on. */
    @Override
    public void render(Screen screen) {
                
        if (isActivated) {
            if (isVisible()) {
                int sw = screen.getSheet().width;   // width of sprite sheet (256)
                int colNum = sw / PPS;    // Number of squares in a row (32)                   

                int xSCur = xS + bNum * wS; // animation through 4 different colors

                for (int ys = 0; ys < hS; ys++) {
                    for (int xs = 0; xs < wS; xs++) {
                        screen.render(x + xs * PPS, y + ys * PPS, (xSCur + xs) + (yS + ys) * colNum, 0); // Loops through all the squares to renderFont them all on the screen.                    
                    }
                }
            }   
        }
    }

    @Override
    protected void touchedBy(Sprite sprite) {     
        super.touchedBy(sprite);
        if (sprite instanceof Player && isActivated) {
            if (((Player)sprite).isEnlarged())
                ((Player)sprite).eatFlower(score);
            else
                ((Player)sprite).eatMushroom(score);
//            scoreStr = Integer.toString(score);
            level.add(new ScoreString(x + 4, y + height/2, score, level));
            remove();
        }
    }        
}
