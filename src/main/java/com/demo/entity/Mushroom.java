package main.java.com.demo.entity;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * Represents a mushroom under an InteractiveTile.
 *
 * @author HRK
 */
public class Mushroom extends HiddenSprite {

    private boolean jumping;

    /**
     * Constructor. Initializes the x and y coordinates and other variables.
     *
     * @param x x coordinate of the hidden sprite [pixel].
     * @param y x coordinate of the hidden sprite [pixel].
     * @param level The Level in which the player currently is.
     */
    public Mushroom(int x, int y, Level level) {
        super(x, y, level);
        initMushroom();
    }

    /**
     * Initialize variables of the mushroom.
     *
     * @param x x coordinate of the mushroom [pixel].
     * @param y x coordinate of the mushroom [pixel].
     */
    private void initMushroom() {
        // Variables from Entity class.
        xS = 0;
        yS = 8;

        // Variables from Sprite class.        
        dx = 1;
        dy = -1;
        score = 1000;
        jumping = false;
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
                    dy = -dy;
                }
            // Once reaching the top, move normally.
            } else {
                // When punched from below, jump.            
                if (grounded && isPunchedOnBottom) {
                    ds = -4;
                    initY = y;
                    jumping = true;
                }

                // Update dy.
                if (jumping) {              // While jumping
                    dy = (int) ds;
                    ds += 0.5;              // Accelerate.
                    if (y + dy >= initY) {  // Once come back to the ground
                        jumping = false;
                    }
                } else if (!grounded) {     // If flying in the air
                    dy++;                   // Accelerate.
                } else {                    // When grounded,
                    dy = ySpeed;            // By default, there is gravity.
                }
                
                // Adjust dy when facing a ground tile.
                if (dy > 0 && y + height < Commons.GROUND && willBeGrounded()) {
                    int yt1 = y + dy + ES;
                    int backoff = yt1 - (yt1 >> unit) * ES;
                    if (backoff > 1) {
                        dy -= backoff;
                    }
                }
                                
                boolean stopped = !move(dx, dy);     // Updates x and y.

                if (stopped && dx != 0)   // If it has met a wall                
                    dx = -dx;             // Change x direction to the opposite.
                
                // Update visibility on the screen.
                // In x-direction
                int offset = level.getOffset();
                if (x <= 0)           // Once it reaches the left end of the map
                    remove();         // Remove it from the level.
                else if (x + width <= offset || // Once it's out of screen
                        offset + Commons.BOARD_WIDTH <= x) 
                    setVisible(false);          // Make invisible.                
                // In y-direction
                if (y > Commons.BOARD_HEIGHT)   // If if falls to the bottom
                    remove();                   // Remove it from the level.                             
            }   
        }
    }

    /**
     * Draws the sprite on the screen.
     *
     * @param screen The screen to be displayed on.
     */
    @Override
    public void render(Screen screen) {
        super.render(screen);
        
        if (isActivated) {
            if (isVisible()) {                
                int flip = 0;               // dx > 0
                if (dir == 2) {             // dx < 0                
                    flip = 1;
                }

                // Render the sprite.
                screen.render(x + PPS * flip, y, xS + yS * colNum, 
                        flip);                              // Top-left part
                screen.render(x - PPS * flip + PPS, y, (xS + 1) + yS * colNum, 
                        flip);                              // Top-right part           
                screen.render(x + PPS * flip, y + PPS, xS + (yS + 1) * colNum, 
                        flip);                              // Bottom-left part     
                screen.render(x - PPS * flip + PPS, y + PPS, 
                        xS + 1 + (yS + 1) * colNum, flip);  // Bottom-right part
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
            ((Player) sprite).eatMushroom(score);
            level.add(new ScoreString(x + 4, y + height / 2, score, level));
            remove();
        }
    }
}
