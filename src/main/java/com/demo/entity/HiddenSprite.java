package main.java.com.demo.entity;

import main.java.com.demo.Commons;
import main.java.com.demo.level.Level;

/** Represents a sprite hidden under an InteractiveTile.
 *  A place holder for hidden sprite. */
public abstract class HiddenSprite extends Sprite {

    public static int coinID = 0;
    public static int mushroomID = 1;
    public static int starmanID = 2;
    
    protected final int PPS = Commons.PPS;
    protected boolean reachedTop;   // True when the sprite reaches its upper limit during emerging
    protected double ds;    // temporary dy
    protected int initY;  // Initial y
    protected boolean isActivated; // The hidden sprite is activated when the 
                                   // player hits the bottom of the InteractiveTile
                                   //  in which this sprite is hidden.
    protected boolean firstTime; //  when first activated
    
    // The constructor initiates the x and y coordinates and the visible variable.
    public HiddenSprite(int x, int y, Level level) {
        super(level);
        this.x = x;
        this.y = y;        
        initHiddenSprite();  
    }    
    
    private void initHiddenSprite() {
        isActivated = false;
        reachedTop = false;
        ds = Commons.ITV0 - 0.5; // -3   to follow the interactive tile when hit on the bottom.
        wS = 2;
        hS = 2; 
        width = wS * PPS;
        height = hS * PPS;        
        unit = (int) (Math.log10(width)/Math.log10(2)); // the size of block to be used (4 for 16 px sprite and 3 for 8px sprite)
        aTile = Math.min(Math.pow(2, 4 - unit), 1); // 1 for unit 3, 1 for unit 4, 0.5 for unit 5 (big Pusheen)
    }    
    
    public void activate() {
        isActivated = true;
        firstTime = true;
    }
    
    @Override
    protected void touchedBy(Sprite sprite) {
        if (sprite instanceof Player && isActivated)
            die();
    }    
}
