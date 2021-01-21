package main.java.com.demo.level.tile;

import main.java.com.demo.Commons;
import main.java.com.demo.entity.HiddenSprite;
import main.java.com.demo.level.Level;

/**
 * An abstract class for interactive tiles (bricks, question bricks).
 *
 * @author HRK
 */
public abstract class InteractiveTile extends Tile {
    
    protected boolean isHitBottom;  // True when the player hits the bottom of 
                                    // this brick.
    protected boolean removed;      // True if the tile is to be removed.
    protected boolean hitOnce;      // True when the tile is hit once.
    protected boolean firstTime;    // True when hit first time.  
    protected int x, y;             // x/y position [pixel]
    protected int initY;            // Initial(Permanent) y position [pixel]
    protected int xt, yt;           // x/y tile position [tile]
    protected double ds, dsInit;    // Temporary dy

    protected HiddenSprite hs;      // A hidden sprite under the tile, if any.
    protected int numHS;            // Number of hidden sprites under this tile.

    /**
     * Constructor.
     *
     * @param id An integer containing the tile ID
     * @param xt x tile position in the map [tile]
     * @param yt y tile position in the map [tile]
     */
    public InteractiveTile(int id, int xt, int yt) {
        super(id);
        this.xt = xt;
        this.yt = yt;
        init();
    }

    private void init() {

        isHitBottom = removed = hitOnce = firstTime = false;

        x = xt * ES;
        y = yt * ES;
        initY = y; // Permanent y-position 

        ds = 0;
        dsInit = Commons.ITV0;
        
        numHS = 0;
    }

    /**
     * Update method.
     *
     * @param xt x tile position of the current level [tile]
     * @param yt y tile position of the current level [tile]
     * @param level Current level
     */
    @Override
    public void tick(int xt, int yt, Level level) {

        // Update y increment.
        // Right when hit or after hit
        if ((y == initY && ds == dsInit) || y < initY) {
            ds = ds + 0.5;  // Increment dy.
        } else // When reached the initial position
        {
            ds = 0;         // Stop moving.
        }
        // Update y position.
        if ((int) (y + ds) <= initY) { // Right when hit or after hit
            y = (int) (y + ds);
        } else // When reached the initial position
        {
            y = initY;      // Goes back to permanent position.
        }
    }

    /**
     * What happens when you hit the tile (ex: punching a tree)
     */
    public void hurt() {

        isHitBottom = true;
        ds = dsInit;        // Starts moving vertically.
        if (!hitOnce) {     // If it was never hit before
            firstTime = true;
            hitOnce = true;
        }
    }

    /**
     * Set whether this tile is hit on the bottom or not.
     *
     * @param hit A boolean which determines isHitBottom.
     */
    public void setHit(boolean hit) {
        isHitBottom = hit;
    }

    /**
     * Gets whether this tile is hit on the bottom.
     *
     * @return True if it is hit on the bottom.
     */
    public boolean isHitBottom() {
        return isHitBottom;
    }

    /**
     * Gets x position [pixel]
     *
     * @return An integer containing x position
     */
    public int getX() {
        return x;
    }

    /**
     * Gets y position [pixel]
     *
     * @return An integer containing x position
     */
    public int getY() {
        return y;
    }

    /**
     * Assigns hidden sprite behind this tile.
     *
     * @param hs A hidden sprite to be behind this tile.
     */
    public void setHiddenSprite(HiddenSprite hs) {
        this.hs = hs;
    }

    /**
     * Gets hidden sprite behind this tile
     *
     * @return A hidden sprite behind this tile.
     */
    public HiddenSprite getHiddenSprite() {
        return hs;
    }

    /**
     * Gets permanent y position of this tile.
     *
     * @return An integer containing initial (permanent) y position [pixel]
     */
    public int getInitY() {
        return initY;
    }
}
