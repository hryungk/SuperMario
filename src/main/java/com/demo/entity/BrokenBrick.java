package main.java.com.demo.entity;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * Represents a broken brick as a sprite.
 *
 * @author HRK
 */
public class BrokenBrick extends Sprite {

    private boolean firstTime; //  When first activated
    
    public BrokenBrick(int x, int y, int dx, int ds, Level level) {          
        super(level);          
        
        this.dx = dx; 
        this.ds = ds;
        
        initBrokenBrick(x, y);        
    }    
    
    private void initBrokenBrick(int x, int y) {
        // Initialize variables from Entity class.
        setX(x);        
        setY(y);  
        width = height = ES / 2;        
        xS = 8;
        yS = 2;
               
        // Initialize variables from Sprite class.
        wS = width / PPS;
        hS = height / PPS;
        
        unit = (int) (Math.log10(width)/Math.log10(2)); 
        aTile = Math.min(Math.pow(2, 4 - unit), 1); 
        
        numStage = 2;
        health = lives = 1;
        
        // Initialize variables for this class.
        firstTime = true;
    }

    /**
     * Update method.
     */
    @Override
    public void tick() {
        super.tick();
        
        if (firstTime) { // This is to delay one more tick. 
            firstTime = false;
            return;
        }
        dy = (int) ds;
        if (dy < 4)     // Accelerate until reaching g-force of 4.
            ds += 0.5;     
        // Keep moving.
        x += dx;
        y += dy;
        
        // Update visibility on the screen.
        int offset = level.getOffset();
        // If going beyond the left end of the map
        if (x <= 0)
            hurt(health);       // Die.
        // If going outside of the screen
        else if (x+width <= offset || offset + Commons.BOARD_WIDTH <= x)
            setVisible(false);  // Set invisible
        // Otherwise
        else
            setVisible(true);   // Set visible.
        // If going down beyond the screen
        if (y > Commons.BOARD_HEIGHT)
            hurt(health);    
        
        // Update bNum
        bNum = (bCounter / scale) % numStage;
        bCounter++;
    }
    
    /**
     * Draws the sprite on the screen.
     *
     * @param screen The screen to be displayed on.
     */
    @Override
    public void render(Screen screen) {
        super.render(screen);
        
        if (isVisible()) {
            // Animation based on bNum.
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

    /**
     * Nothing happens when the broken brick is touched by another sprite.
     *
     * @param sprite The sprite that this broken brick is touched by.
     */
    @Override
    protected void touchedBy(Sprite sprite) {
    }
}
