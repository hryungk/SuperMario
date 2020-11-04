package main.entity;

/** Represents a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public abstract class Brick extends Entity {

    private boolean visible;
    private boolean dying;
    
    int dx, dy;
    
    // The constructor initiates the x and y coordinates and the visible variable.
    public Brick() {
        super();
        visible = true;
        dx = dy = 0;
    }    
    
    /** Update method, (Look in the specific entity's class) */
    public abstract void interact();

    
    public void die() {
        visible = false;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    protected void setVisible(Boolean visible) {
        this.visible = visible;
    }        
    
    public void setDx(int dx) {
        this.dx = dx;
    } 
    
    public int getDx() {
        return dx;
    }
    
    public void setDying (boolean dying) {
        this.dying = dying;
    }
    
    public boolean isDying() {
        return dying;
    }
}
