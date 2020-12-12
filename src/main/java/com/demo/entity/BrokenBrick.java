package main.java.com.demo.entity;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/** Represents a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public class BrokenBrick extends Sprite {

    private boolean firstTime; //  when first activated
    // The constructor initiates the x and y coordinates and the visible variable.
    public BrokenBrick(int x, int y, int dx, int ds, Level level) {          
        super(level);
        // Initial coordinates of the player sprite.
        setX(x);        
        setY(y);    
        
        this.dx = dx; 
        this.ds = ds;
        
        initBrokenBrick();        
    }    
    
    private void initBrokenBrick() {
        width = height = ES / 2;
        
        wS = width / PPS;
        hS = height / PPS;
        
        xS = 8;
        yS = 2;
               
        unit = (int) (Math.log10(width)/Math.log10(2)); // the size of block to be used (4 for 16 px sprite and 3 for 8px sprite)
        aTile = Math.min(Math.pow(2, 4 - unit), 1); // 1 for unit 3, 1 for unit 4, 0.5 for unit 5 (big Pusheen)
        
//        scale = 8;  // Higher the number, slower the transition.
//        numStage = 4;   // Number of color schemes
        numStage = 2;
        
        firstTime = true;
    }

    @Override
    public void tick() {
        if (firstTime) { // This is to delay one more tick. 
            firstTime = false;
            return;
        }
        dy = (int) ds;
        if (dy < 4)
            ds += 0.5;     
                
        x += dx;
        y += dy;
        
        // Update visibility on the screen.
        int offset = level.getOffset();
        if (x <= 0)
            remove();        
        else if (x+width <= offset && offset + Commons.BOARD_WIDTH <= x)
            setVisible(false);     

        if (y > Commons.BOARD_HEIGHT)
            remove();      
        
        // Update bNum
        bNum = (bCounter / scale) % numStage;
        bCounter++;
    }
    
    @Override
    public void render(Screen screen) {
        if (isVisible()) {
            int sw = screen.getSheet().width;   // width of sprite sheet (256)
            int colNum = sw / PPS;    // Number of squares in a row (32)

            int xSCur = xS + bNum * wS; // animation based on walk distance (0 is standing still and 2 is moving)          

            for (int ys = 0; ys < hS; ys++) {
                for (int xs = 0; xs < wS; xs++) {
                    screen.render(x + xs * PPS, y + ys * PPS, (xSCur + xs) + (yS + ys) * colNum, 0); // Loops through all the squares to render them all on the screen.                    
                }
            }
        }
    }

    @Override
    protected void touchedBy(Sprite sprite) {}
}
