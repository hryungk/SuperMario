package main.java.com.demo.level.tile;

import main.java.com.demo.entity.BrokenBrick;
import main.java.com.demo.entity.Coin;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * A class that represents a brick tile.
 * @author HRK
 */
public class BrickTile extends InteractiveTile {

    private boolean dying; // If this brick is broken by Big Pusheen.

    /**
     * Constructor.
     *
     * @param id An integer containing the tile ID
     * @param xt x tile position in the map [tile]
     * @param yt y tile position in the map [tile]
     */
    public BrickTile(int id, int xt, int yt) {
        super(id, xt, yt);

        xS = 8;
        yS = 0;
        dying = false;
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

        /* This is to delay one more tick so that enemies on the brick will die 
        when punched. */
        if (dying) {
            removed = true;
            level.setTile(xt, yt, sky);
            return;
        }

        super.tick(xt, yt, level);

        if (isHitBottom) {          // If it is hit on the bottom            
            if (hs != null) {       // If there is a hidden sprite (coin)                
                if (firstTime) {    // If it is first hit                     
                    // Generate a random number between 0 and 9.
                    // (since it already has one coin)
                    numHS = (int) (Math.random() * 10);                    
                    firstTime = false;        // First time hit is over.
                }

                // Change it to static question brick after there is no more
                // hidden sprite.
                if (numHS <= 0) { // If there is no more hidden sprites left
                    xS = 10;// x square location of static brick on sprite sheet
                    yS = 2; // y square location of static brick on sprite sheet
                } else {    // If there is still hidden sprites to be withdrawn
                    level.add(new Coin(x, initY, level)); // Add more coin.
                    numHS--; 
                }
            } else { // If there is no hidden sprite
                if (level.player.isEnlarged()) { // If player is Big Pusheen
                    dying = true;   // Crush this brick.
                    // 4 small pieces of bricks fly away around the brick.
                    level.add(new BrokenBrick(x + wS * PPS - PPS / 2, 
                        y - PPS / 2, 1, -4, level));             // Top right
                    level.add(new BrokenBrick(x + wS * PPS - PPS / 2, 
                        y + hS * PPS - PPS / 2, 1, -3, level));  // Bottom right
                    level.add(new BrokenBrick(x - PPS / 2, 
                        y - PPS / 2, -1, -4, level));            // Top left
                    level.add(new BrokenBrick(x - PPS / 2, 
                        y+ hS * PPS - PPS / 2, -1, -3, level));  // Bottom left
                }
            }
            isHitBottom = false;
        }
    }

    /**
     * Render method, used in sub-classes
     *
     * @param xt x tile position of the current level
     * @param yt x tile position of the current level
     * @param level Current level
     * * @param screen Current screen
     */
    @Override
    public void render(int xt, int yt, Level level, Screen screen) {

        int sw = screen.getSheet().width;   // Width of sprite sheet (256)
        int colNum = sw / PPS;              // Number of squares in a row (32)     
        
        // Loops through all the 4 squares to render them on the screen.
        for (int ys = 0; ys < hS; ys++) {
            for (int xs = 0; xs < wS; xs++) {
                screen.render(xt * ES + xs * PPS, y + ys * PPS, 
                        (xS + xs) + (yS + ys) * colNum, 0); 
            }
        }
    }

    @Override
    public void hurt() {
        // If it is not hit yet or has no hidden sprite
        if (!isHitBottom && xS == 8 || hs == null) {
            super.hurt();
        } else {
            ds = 0;
        }
    }
}
