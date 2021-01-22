package main.java.com.demo.entity;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * Represents a sprite hidden under an InteractiveTile. A place holder for
 * hidden sprite.
 *
 * @author HRK
 */
public abstract class HiddenSprite extends Sprite {

    public static int coinID = 0;
    public static int mushroomID = 1;
    public static int starmanID = 2;

    protected boolean reachedTop;   // True when the sprite reaches its upper 
                                    // limit during emerging.    
    protected boolean isActivated;  // The hidden sprite is activated when the 
                                    // player hits the bottom of the interactive
                                    // tile in which this sprite is hidden.
    protected boolean firstTime;    // True when first activated.
    protected boolean doneFollowing;// True if done following the tile.
    protected int initY;            // Initial (Permanent) y position [pixel]

    /**
     * Constructor. Initializes the x and y coordinates and other variables.
     *
     * @param x x coordinate of the hidden sprite [pixel].
     * @param y x coordinate of the hidden sprite [pixel].
     * @param level The Level in which the player currently is.
     */
    public HiddenSprite(int x, int y, Level level) {
        super(level);
        initHiddenSprite(x, y);
    }

    /**
     * Initialize variables of the hidden sprite.
     *
     * @param x x coordinate of the hidden sprite [pixel].
     * @param y x coordinate of the hidden sprite [pixel].
     */
    private void initHiddenSprite(int x, int y) {

        // Variables from Entity class.
        setX(x);
        setY(y);
        wS = hS = 2;
        width = wS * PPS;
        height = hS * PPS;

        // Variables from Sprite class.       
        ds = Commons.ITV0 - 0.5;    // To follow the interactive tile when hit.
        dir = 3;                    // Face right
        ground = y - height;
        unit = (int) (Math.log10(width) / Math.log10(2));   // Sprite unit
        aTile = Math.min(Math.pow(2, 4 - unit), 1);         // Size of one tile

        // Variables from HiddenSprite class.
        reachedTop = isActivated = firstTime = doneFollowing = false;
        initY = y;
        colNum = 0;
    }

    /**
     * Draws the sprite on the screen
     *
     * @param screen The screen to be displayed on.
     */
    @Override
    public void render(Screen screen) {
       super.render(screen);
    }
    
    /**
     * Activate the hidden sprite once the brick encapsulating it is hit by the
     * player.
     */
    public void activate() {
        isActivated = true;
        firstTime = true;
    }

    /**
     * What happens when this entity is touched by another entity.
     *
     * @param sprite The sprite that this sprite is touched by.
     */
    @Override
    protected void touchedBy(Sprite sprite) {
        if (sprite instanceof Player && isActivated) {
            die();
        }
    }
}
