package main.java.com.demo.entity;

import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * Represents a flower under an InteractiveTile.
 *
 * @author HRK
 */
public class Flower extends HiddenSprite {

    /**
     * Constructor. Initializes the x and y coordinates and other variables.
     *
     * @param x x coordinate of the hidden sprite [pixel].
     * @param y x coordinate of the hidden sprite [pixel].
     * @param level The Level in which the player currently is.
     */
    public Flower(int x, int y, Level level) {
        super(x, y, level);
        initFlower();
    }

    /**
     * Initialize variables of the flower.
     *
     * @param x x coordinate of the flower [pixel].
     * @param y x coordinate of the flower [pixel].
     */
    private void initFlower() {
        // Variables from Entity class.
        xS = 0;
        yS = 6;

        // Variables from Sprite class.
        dx = 0;
        score = 1000;
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
                    ds = -1;
                }
            // Once done following the tile, move at a constant speed to the top
            } else if (!reachedTop) {
                if (bCounter % ay == 0) {
                    y = (int) (y + ds);
                }
                if (y <= initY - ES) {
                    reachedTop = true;
                }
            } // end if (reaching the top)        
        } // end if(isActivated)         
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

    /**
     * What happens when this entity is touched by another entity.
     *
     * @param sprite The sprite that this sprite is touched by.
     */
    @Override
    protected void touchedBy(Sprite sprite) {
        super.touchedBy(sprite);
        if (sprite instanceof Player && isActivated) {
            if (((Player) sprite).isEnlarged()) {
                ((Player) sprite).eatFlower(score);
            } else {
                ((Player) sprite).eatMushroom(score);
            }
            level.add(new ScoreString(x + 4, y + height / 2, score, level));
            remove();
        }
    }
}
