package main.java.com.demo.entity;

import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/** Represents a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public class Coin extends HiddenSprite {
        
    // The constructor initiates the x and y coordinates and the visible variable.
    public Coin(int x, int y, Level level) {
        super(x, y, level);
        initCoin();
    }    
    
    private void initCoin() {
        initY = y;
        xS = 0;
        yS = 4;         
        dx = 0;
        dy = -2;        
        
        width = height = ES;
        wS = width / PPS;
        hS = height / PPS;
        
        score = 200;
        scoreStr = "";
        scoreX = 0;
        scoreY = 0;
    }
    
    /** Update method, (Look in the specific entity's class) */
    @Override
    public void tick() {   
                
        if (isActivated) {
            
            if (firstTime) {
                level.player.score += score; // gives the player 100 points of score
                level.player.addCoinCount();
                firstTime = false;
            }
            
            
            if (ds < 0) { // First the coin follows the InteractiveTile's movement
                ds = ds + 0.5;
                y = (int) (y + ds);
            } else {    // After the InteractiveTile reaches the top, this coin continues to move at a constant speed. 
                if (y <= initY) {
                    if (y <= initY - 3 * ES && !reachedTop) {
                        reachedTop = true;
                        dy = -dy;
                    }
                    y += dy;  
                }                
            }        

            if (y >= initY && reachedTop)
                die(); // Make invisible

            bNum = (bCounter / scale) % numStage;
            bCounter++;        
                        
            if (reachedTop && y >= initY - height || !isVisible())            
                scoreStr = Integer.toString(score);
            
            // Update score location        
            if (scoreStr.isEmpty()){
                scoreX = x + ES;
                scoreY = y + ES - PPS;
            } else {    // has died and printing score on the screen during deathTime.
//                scoreX = scoreX + 0.5;
                scoreY = scoreY - 0.5;
                if (scoreY < y - 2 * ES)
                    remove();
            }     
        }
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
}
