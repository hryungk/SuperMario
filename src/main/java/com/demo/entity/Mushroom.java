package main.java.com.demo.entity;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/** Represents a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public class Mushroom extends HiddenSprite {
    
    private boolean doneFollowing;
    public Mushroom(int x, int y, Level level) {
        super(x, y, level);        
        initCoin();
    }    
    
    private void initCoin() {
        initY = y;
        xS = 0;
        yS = 8;         
        dx = 1;
        dy = -1;
        
        width = height = ES;
        wS = width / PPS;
        hS = height / PPS;
        
        score = 1000; 
        
        doneFollowing = false;
    }
    
    /** Update method, (Look in the specific entity's class) */
    public void tick() {   
                
        if (isActivated) {
            bNum = (bCounter / scale) % numStage;
            bCounter++;  
            
            if (!doneFollowing) { // First the mushroom follows the InteractiveTile's movement
                ds = ds + 0.5;
                y = (int) (y + ds);
                if (ds >= 0)
                    doneFollowing = true;
            } else if (!reachedTop) {    // After the InteractiveTile reaches the top, this mushroom continues to move at a constant speed. 
                ds = -1;
                if (bCounter % ay == 0)
                    y = (int) (y + ds);
                if (y <= initY - ES) {
                    reachedTop = true;
                    dy = -dy;
                } 
            } else { // Move normally
                /* Update y position. */
                int oldY = y;
                boolean stopped = !move(dx, dy);     // Updates x and y.

                if (stopped && dx != 0)    // Has met a wall
                    dx = - dx;                                       

                // Update visibility on the screen.
                int offset = level.getOffset();
                if (x <= 0)
                    hurt(health);        
                else if (x+width <= offset && offset + Commons.BOARD_WIDTH <= x)
                    setVisible(false);     

                if (y > Commons.BOARD_HEIGHT)
                    remove();            

                // When falling, add acceleration to y.
                int effDy = y - oldY;
                if (effDy != 0) // actual y displacement
                    dy++;            
                else
                    dy = ySpeed; // By default, there is gravity.

                // Adjust dy when facing a ground tile.
                if (dy > 0  && y + height < Commons.GROUND && willBeGrounded()) {
                    int yt1 = y + dy + ES;
                    int backoff = yt1 - (yt1 >> 4) * 16;
                    if (backoff > 1)
                        dy -= backoff;
                }  
            } // end if (following the InteractiveTile)            
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
                int flip = 0; // dx > 0
                if (dir == 2) // dx < 0
                    flip = 1;

                screen.render(x + PPS * flip, y, xS + yS * colNum, flip); // render the top-left part of the sprite         
                screen.render(x - PPS * flip + PPS, y, (xS + 1) + yS * colNum, flip);  // render the top-right part of the sprite
                screen.render(x + PPS * flip, y + PPS, xS + (yS + 1) * colNum, flip); // render the bottom-left part of the sprite
                screen.render(x - PPS * flip + PPS, y + PPS, xS + 1 + (yS + 1) * colNum, flip); // render the bottom-right part of the sprite
            }               
        }
    }    
    
    @Override
    protected void touchedBy(Sprite sprite) {     
        super.touchedBy(sprite);
        if (sprite instanceof Player && isActivated) {
            ((Player)sprite).eatMushroom(score);
//            scoreStr = Integer.toString(score);
            level.add(new ScoreString(x + 4, y + height/2, score, level));
            remove();
        }
    }
}
