package main.java.com.demo.entity;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * Represents a starman under an InteractiveTile.
 *
 * @author HRK
 */
public class Starman extends HiddenSprite {

    // True if the starman left the tile it was spawn from.
    private boolean leftBlock;
    // This is used so that 0 velocity doesn't occur for a long time.
    private int dy0Count;

    /**
     * Constructor. Initializes the x and y coordinates and other variables.
     *
     * @param x x coordinate of the hidden sprite [pixel].
     * @param y x coordinate of the hidden sprite [pixel].
     * @param level The Level in which the player currently is.
     */
    public Starman(int x, int y, Level level) {
        super(x, y, level);
        initStarman();
    }

    /**
     * Initialize variables of the Starman.
     *
     * @param x x coordinate of the Starman [pixel].
     * @param y x coordinate of the Starman [pixel].
     */
    private void initStarman() {
        // Variables from Entity class.
        xS = 8;
        yS = 4;

        // Variables from Sprite class.    
        dx = 1;
        score = 1000;

        leftBlock = false;
        dy0Count = 0;
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
                    ds = 1;
                }
            // Once reaching the top, move to the right till leaving the tile.
            } else if (!leftBlock) {
                move(dx, dy);       // Updates x and y.
                if (!grounded) // Once leaving the tile, it's in the air.
                {
                    leftBlock = true;
                }
            // Once leaving the tile, move normally, bouncing.
            } else {
                // Update dy. 
                double g = 3.125;   // Gravitational force        

                // If grounded
                if (grounded) {
                    ds = -g;        // By default, there is gravity.
                } // If topped by a block or dy=0 time reached its limit
                else if (topped || dy0Count >= (int) (1.0 / 0.125 / 2)) {
                    ds = 1;         // Start falling to the ground.
                    dy0Count = 0;   // Reset the count for the next leap.
                } // If falling in the air
                else if (y + height + ds < Commons.BOARD_HEIGHT) {
                    ds += 0.125;    // Accelrate.
                }
                dy = (int) ds;

                // This is so that 0 y-velocity doesn't occur for a long time.
                if (dy == 0) {
                    dy0Count++;
                }

                // Adjust dy when facing a ground tile.
                if (dy > 0 && y + height < Commons.GROUND && willBeGrounded()) {
                    int yt1 = y + dy + ES;
                    int backoff = yt1 - (yt1 >> unit) * ES;
                    if (dy > 1 && backoff > 0) {
                        dy -= backoff;
                    }
                }

                boolean stopped = !move(dx, dy);     // Updates x and y.

                if (stopped && dx != 0) { // If it has met a wall                
                    dx = -dx;             // Change x direction to the opposite.                          
                }
                // Update visibility on the screen.
                // In x-direction
                int offset = level.getOffset();
                if (x <= 0) {         // Once it reaches the left end of the map                
                    remove();         // Remove it from the level.
                } else if (x + width <= offset||    // Once it's out of screen                        
                        offset + Commons.BOARD_WIDTH <= x) {
                    remove();         // Remove it from the level.
                }                
                // In y-direction
                if (y > Commons.BOARD_HEIGHT) {     // If if falls to the bottom                
                    remove();                       // Remove it from the level.
                }
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

    /**
     * What happens when this entity is touched by another entity.
     *
     * @param sprite The sprite that this sprite is touched by.
     */
    @Override
    protected void touchedBy(Sprite sprite) {
        super.touchedBy(sprite);
        if (sprite instanceof Player && isActivated) {
            ((Player) sprite).eatStarman(score);
            level.add(new ScoreString(x, y - height, score, level));
            remove();
        }
    }
}
