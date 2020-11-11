package main.entity;

import main.Commons;
import main.gfx.Screen;
import main.level.Level;

/** Represents a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public class Mushroom extends HiddenSprite {
    
    private int initY;
    private boolean reachedTop;
    private int bCounter, bNum, scale;
    protected double ds, dsInit;
    
    // The constructor initiates the x and y coordinates and the visible variable.
    public Mushroom(int x, int y, Level level) {
        super(x, y, level);
        this.x = x;
        this.y = y;
        initCoin();
    }    
    
    private void initCoin() {
        initY = y;
        xS = 0;
        yS = 19;         
        dx = 0;
        dy = -2;
        reachedTop = false;
        scale = 4;
        wS = 2;
        hS = 2;        
        dsInit = Commons.ITV0 - 0.5; // -3
        ds = dsInit; 
        score = 1000;
    }
    
    /** Update method, (Look in the specific entity's class) */
    public void tick() {   
                
        if (isActivated) {
            if (ds < 0) { // First the coin follows the InteractiveTile's movement
                ds = ds + 0.5;
                y = (int) (y + ds);
            } else {    // After the InteractiveTile reaches the top, this coin continues to move at a constant speed. 
                if (y <= initY) {
                    if (y <= initY - 3 * ES && !reachedTop) {
                        reachedTop = true;
                        dy = -dy;
                    }
                    y += dy;  
                }
            }        

            if (y >= initY && reachedTop)
                remove();
     
//            System.out.print("Mushroom's y = " + y);
        }
    }    

    /** Draws the sprite on the screen
     * @param screen The screen to be displayed on. */
    public void render(Screen screen) {
                
        if (isActivated) {
            int sw = screen.getSheet().width;   // width of sprite sheet (256)
            int PPS = Commons.PPS;
            int colNum = sw / PPS;    // Number of squares in a row (32)                   

            for (int ys = 0; ys < hS; ys++) {
                for (int xs = 0; xs < wS; xs++) {
                    screen.render(x + xs * PPS, y + ys * PPS, (xS + xs) + (yS + ys) * colNum, 0); // Loops through all the squares to render them all on the screen.                    
                }
            }
        }
    }    
    
    @Override
    protected void touchedBy(Sprite sprite) {
        super.touchedBy(sprite);
        if (isActivated)
            level.player.score += score;
    }
}
