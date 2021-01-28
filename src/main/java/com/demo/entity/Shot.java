package main.java.com.demo.entity;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * Represents a shot when Pusheen eats flower.
 *
 * @author HRK
 */
public class Shot extends Sprite {

    public Shot(Level level) {
        super(level);
        initShot();
    }

    /**
     * Initiate variables of the shot object.
     */
    private void initShot() {
        // Variables from Entity class.
        xS = 10;
        yS = 2;
        width = height = ES / 2;
        // Variables from Sprite class.  
        wS = width / PPS;
        hS = height / PPS;
        dy = 0;
        unit = (int) (Math.log10(width) / Math.log10(2));
        aTile = Math.min(Math.pow(2, 4 - unit), 1);
    }

    /**
     * Update method.
     */
    @Override
    public void tick() {
        super.tick();

        boolean stopped = false;

        int offset = level.getOffset();
        if (x <= 0) { // Once it reaches the left end of the map        
            remove();       // Remove it from the level.
        } else if (offset < x + width
                && // Once it's inside the screen  
                x < offset + Commons.BOARD_WIDTH) {
            stopped = !move(dx, dy);    // Move.
        } else {
            remove();
        }

        if (stopped) {
            remove();
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
        
        // Loops through all the squares to render them on the screen.                    
        for (int ys = 0; ys < hS; ys++) {
            for (int xs = 0; xs < wS; xs++) {
                screen.render(x + xs * PPS, y + ys * PPS, 
                        (xS + xs) + (yS + ys) * colNum, 0); 
            }
        }
    }

    /**
     * What happens when the shot is touched by another sprite.
     *
     * @param sprite The sprite that this sprite is touched by.
     */
    @Override
    protected void touchedBy(Sprite sprite) {
        if (sprite instanceof Enemy) {
            hurt(health);   // Hurt the shot.
            if (!((Enemy) sprite).isShot()) {
                ((Enemy) sprite).setShot();
            }
        }
    }
}
