package main.java.com.demo.entity;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/** Represents a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public class Starman extends HiddenSprite {
    
    private int bCounter, bNum, scale;
    private boolean doneFollowing, leftBlock;
    private int y0Count;
    // The constructor initiates the x and y coordinates and the visible variable.
    public Starman(int x, int y, Level level) {
        super(x, y, level);
        initCoin();
    }    
    
    private void initCoin() {
        initY = y;
        xS = 8;
        yS = 4;         
        dx = 1;
        scale = 4;
        height = width = ES;
        wS = width / PPS;
        hS = height / PPS;
        
        score = 1000; 
        scoreStr = "";
        scoreX = 0;
        scoreY = 0;
        
        doneFollowing = false;
        leftBlock = false;
        y0Count = 0;
    }
    
    /** Update method, (Look in the specific entity's class) */
    @Override
    public void tick() {   
                
        if (isActivated) {
            if (!doneFollowing) { // First the coin follows the InteractiveTile's movement
                ds = ds + 0.5;
                y = (int) (y + ds);
                if (ds >= 0)
                    doneFollowing = true;
            } else if (!reachedTop) { // After the InteractiveTile reaches the top, this coin continues to move at a constant speed. 
                ds = -0.5;
                y = (int) (y + ds); 
                if (y <= initY - ES) {
                    reachedTop = true;
                    ds = 1;
                }    
            } else if (!leftBlock) {                
                move(dx, dy);     // Updates x and y.
                if (!grounded)
                    leftBlock = true;
            } else { // Move normally
                                
                boolean stopped = !move(dx, dy);     // Updates x and y.

                if (stopped && dx != 0)    // Has met a wall
                    dx = - dx;                                       

                /* Update visibility on the screen. */
                int offset = level.getOffset();
                if (x <= 0)
                    remove();        
                else if (x+width <= offset || offset + Commons.BOARD_WIDTH <= x)
                    remove();     

                if (y > Commons.BOARD_HEIGHT)
                    remove();     
                
                /* Update y position. */
                double g = 3.125;  // gravitational force        

                if (grounded)
                    ds = -g;
                else if (topped || y0Count >= (int) (1.0/0.125 / 2)) { // This is so that 0 velocity doesn't occur for a long time.
                    ds = 1;
                    y0Count = 0;    // Reset the count for the next leap
                }
                else if (y + height + ds < Commons.BOARD_HEIGHT)
                    ds = ds + 0.125;                

                dy = (int) ds;
                
                if (dy == 0) y0Count++; // This is so that 0 velocity doesn't occur for a long time.

                // Adjust dy when facing a ground tile.
                if (dy > 0  && y + height < Commons.GROUND && willBeGrounded()) {
                    int yt1 = y + dy + ES;
                    int backoff = yt1 - (yt1 >> unit) * ES;
                    if (dy > 1 && backoff > 0)
                        dy -= backoff;
                }
            } // end if (reaching the top)
        bNum = (bCounter / scale) % 4;
        bCounter++;  
        
        // Update score location        
            if (scoreStr.isEmpty()){
                scoreX = x + width;
                scoreY = y + height - PPS;
                yFin = y + height - PPS;
            } else {    // has died and printing score on the screen during deathTime.
                scoreY = scoreY - 0.5;
                if (scoreY < yFin - 2 * height)
                    remove();
            }     
        } // end if(isActivated)         
    }    

    /** Draws the sprite on the screen
     * @param screen The screen to be displayed on. */
    public void render(Screen screen) {
                
        if (isActivated) {
            if (isVisible()) {
                int sw = screen.getSheet().width;   // width of sprite sheet (256)
                int colNum = sw / PPS;    // Number of squares in a row (32)                   

                int xSCur = xS + bNum * wS; // animation based on walk distance (bNum cycles through 0-3.)                  

                for (int jj = 0; jj < hS; jj++) {
                    for (int ii = 0; ii < wS; ii++) {
                        screen.render(x + ii * PPS, y + jj * PPS, (xSCur + ii) + (yS + jj) * colNum, 0); // Loops through all the squares to render them all on the screen.                    
                    }
                }
            }
            
            // Render score location once died
            if (!scoreStr.isEmpty()){
                Font.draw(scoreStr, screen, (int)scoreX, (int)scoreY, Color.WHITE);
            }   
        }
    }
   
    @Override
    protected void touchedBy(Sprite sprite) {     
        super.touchedBy(sprite);
        if (sprite instanceof Player && isActivated && firstTime) {
            ((Player)sprite).eatStarman(score);
            scoreStr = Integer.toString(score);
            firstTime = false;            
        }
    }
}
