package main.java.com.demo.entity;

import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * Represents a coin under an InteractiveTile.
 *
 * @author HRK
 */
public class Coin extends HiddenSprite {

    /**
     * Constructor. Initializes the x and y coordinates and other variables.
     *
     * @param x x coordinate of the hidden sprite [pixel].
     * @param y x coordinate of the hidden sprite [pixel].
     * @param level The Level in which the player currently is.
     */
    public Coin(int x, int y, Level level) {
        super(x, y, level);
        initCoin();
    }

    /**
     * Initialize variables of the coin.
     *
     * @param x x coordinate of the coin [pixel].
     * @param y x coordinate of the coin [pixel].
     */
    private void initCoin() {
        // Variables from Entity class.        
        xS = 0;
        yS = 4;

        // Variables from Sprite class.
        dx = 0;
        dy = -2;
        score = 200;
    }

    /**
     * Update method.
     */
    @Override
    public void tick() {

        if (isActivated) {            
            bNum = (bCounter / scale) % numStage;
            bCounter++;            

            // At first the coin follows the InteractiveTile's movement
            if (!doneFollowing) { 
                ds = ds + 0.5;
                y = (int) (y + ds);
                if (ds >= 0) {
                    doneFollowing = true;                    
                }
            // Once done following the tile, move at a constant speed to the top
            } else if (!reachedTop) {            
                y += dy; 
                if (y <= initY - 3 * ES) {
                    reachedTop = true;
                    dy = -dy;
                }                
            // Once reaching the top, this coin falls at a constant speed.
            } else if (y <= initY) {
                y += dy;
            }

            if (reachedTop && y >= initY - height || !isVisible()) {
                level.add(new ScoreString(x + 4, y + height / 2, score, level));
                remove();
            }
                        
            if (firstTime) {                    // If first time,
                level.player.addScore(score);   // Gives the player points. 
                level.player.addCoinCount();    // Add coint couts.
                firstTime = false;
            }
        }
    }

    /**
     * Draws the sprite on the screen
     *
     * @param screen The screen to be displayed on.
     */
    @Override
    public void render(Screen screen) {
        super.render(screen);
        if (isActivated) {
            if (isVisible()) {     
                // Animation through 4 different colors.
                int xSCur = xS + bNum * wS;                 
                // Loops through all the squares to render them on the screen.
                for (int ys = 0; ys < hS; ys++) {
                    for (int xs = 0; xs < wS; xs++) {
                        screen.render(x + xs * PPS, y + ys * PPS, 
                                (xSCur + xs) + (yS + ys) * colNum, 0); 
                    }
                }
            }
        }
    }
}
