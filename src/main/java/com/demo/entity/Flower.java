package main.java.com.demo.entity;

import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/** Represents a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public class Flower extends HiddenSprite {
    
    private int bCounter, bNum, scale;
    private boolean doneFollowing, leftBlock;
    private int y0Count;
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
        scoreStr = "";
        scoreX = 0;
        scoreY = 0;
        
        scale = 4;
        height = width = ES;
        wS = width / PPS;
        hS = height / PPS;
        
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
    @Override
    public void render(Screen screen) {
                
        if (isActivated) {
            if (isVisible()) {
                int sw = screen.getSheet().width;   // width of sprite sheet (256)
                int colNum = sw / PPS;    // Number of squares in a row (32)                   

                int xSCur = xS + bNum * wS; // animation based on walk distance (0 is standing still and 2 is moving)                  

                for (int ys = 0; ys < hS; ys++) {
                    for (int xs = 0; xs < wS; xs++) {
                        screen.render(x + xs * PPS, y + ys * PPS, (xSCur + xs) + (yS + ys) * colNum, 0); // Loops through all the squares to render them all on the screen.                    
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
            ((Player)sprite).eatFlower(score);
            scoreStr = Integer.toString(score);
            firstTime = false;
        }
    }        
}
