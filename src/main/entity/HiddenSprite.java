package main.entity;

import main.level.Level;

/** Represents a sprite hidden under an InteractiveTile.
 *  A place holder for hidden sprite. */
public abstract class HiddenSprite extends Sprite {

    protected boolean isActivated; // The hidden sprite is activated when the 
                                   // player hits the bottom of the InteractiveTile
                                   //  in which this sprite is hidden.
    protected boolean firstTime; //  when first activated
    protected int score;    // score the player get when interact with the hidden sprite.
    // The constructor initiates the x and y coordinates and the visible variable.
    public HiddenSprite(int x, int y, Level level) {
        super(level);
        this.x = x;
        this.y = y;        
        initHiddenSprite();  
    }    
    
    private void initHiddenSprite() {
        isActivated = false;
    }    
    
    public void activate() {
        isActivated = true;
        firstTime = true;
    }        
    @Override
    protected void touchedBy(Sprite sprite) {
        if (sprite instanceof Player && isActivated)
            remove();
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
