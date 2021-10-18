package main.java.com.demo.level.tile;

import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * A class that represents a question brick tile.
 *
 * @author HRK
 */
public class QuestionBrickTile extends InteractiveTile {

    public static int tickCount = 0; // A global tickCount to update colors.
    private final int SCALE = 7;
    private int bNum, bCounter, dbNum;

    /**
     * Constructor.
     *
     * @param id An integer containing the Tile ID
     * @param xt An integer containing x tile position in the map [tile]
     * @param yt An integer containing y tile position in the map [tile]
     */
    public QuestionBrickTile(int id, int xt, int yt) {
        super(id, xt, yt);

        xS = 4;
        yS = 2;

        bNum = 0;
        bCounter = tickCount;
        dbNum = 1;
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
        super.tick(xt, yt, level);
        if (!isHitBottom) {
            switch (bNum) {
                case 0:     // Bright 
                    dbNum = Math.abs(dbNum);
                    if (bCounter > 2 * SCALE) {
                        bNum += dbNum;
                        bCounter = 0;
                    } else {
                        bCounter++;
                    }
                    break;
                case 1:     // Intermediate
                    if (bCounter > SCALE) {
                        bNum += dbNum;
                        bCounter = 0;
                    } else {
                        bCounter++;
                    }
                    break;
                case 2:     // Dark
                    if (bCounter > SCALE) {
                        dbNum = -dbNum;
                        bNum += dbNum;
                        bCounter = 0;
                    } else {
                        bCounter++;
                    }
                    break;
            }
        }
    }

    /**
     * Render method, used in sub-classes.
     *
     * @param xt x tile position of the current level [tile]
     * @param yt x tile position of the current level [tile]
     * @param level Current level
     * @param screen Current screen
     */
    @Override
    public void render(int xt, int yt, Level level, Screen screen) {

        int sw = screen.getSheet().width;   // Width of sprite sheet (256)
        int colNum = sw / PPS;              // Number of squares in a row (32)
        int xSCur = xS + bNum * wS;         // Animation based on bNum      
        // Loops through all the squares to render them on the screen.
        for (int ys = 0; ys < hS; ys++) {
            for (int xs = 0; xs < wS; xs++) {
                screen.render(xt * ES + xs * PPS, y + ys * PPS, 
                        (xSCur + xs) + (yS + ys) * colNum, 0); 
            }
        }
    }

    /**
     * What happens when you hit the tile (ex: punching a tree)
     */
    @Override
    public void hurt() {
        if (!isHitBottom) {
            super.hurt();
        } else {
            ds = 0;
        }
        bNum = 3;
    }
}
