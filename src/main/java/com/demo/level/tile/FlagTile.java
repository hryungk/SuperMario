package main.java.com.demo.level.tile;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * A class that represents a flag tile.
 * @author HRK
 */
public class FlagTile extends Tile {

    protected int score;    // Score the player gets from interacting with flag
    protected int scoreX, scoreY, yFin; // x/y location of the score string, 
                                        // final location of y
    protected String scoreStr;          // Score in string    
    private int x, y;                   // x/y location of flag
    private boolean reachedBottom;      // True if flag has reached the bottom.

    public FlagTile(int id) {
        super(id);

        initFlagTile();
    }

    private void initFlagTile() {
        xS = 12;
        yS = 2;

        x = Commons.X_MAX - ES / 2;
        y = Commons.GROUND - ES - 127 - ES;
        reachedBottom = false;

        scoreX = x + ES + 8;
        scoreY = y + 127;
        yFin = y;
        scoreStr = "";
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
        reachedBottom = (y >= Commons.GROUND - ES - ES);
        if (level.player.reachedEnd() && !reachedBottom) {
            y++;

            // Update score location.
            if (scoreY > yFin) {
                scoreY--;
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
        // Loops through all the squares to render them on the screen.                    
        for (int ys = 0; ys < hS; ys++) {
            for (int xs = 0; xs < wS; xs++) {
                screen.render(x + xs * PPS, y + ys * PPS,
                        (xS + xs) + (yS + ys) * colNum, 0);
            }
        }

        // Render score location once the player reaches the end.
        if (level.player.reachedEnd()) {
            Font.draw(scoreStr, screen, scoreX, scoreY, Color.WHITE);
        }
    }

    /**
     * Gets x position,
     *
     * @return An integer containing x position of the flag [pixel]
     */
    public int getX() {
        return x;
    }

    /**
     * Gets y position,
     *
     * @return An integer containing y position of the flag [pixel]
     */
    public int getY() {
        return y;
    }

    /**
     * Returns whether the flag has reached the bottom.
     *
     * @return True if the flag has reached bottom, not otherwise.
     */
    public boolean reachedBottom() {
        return reachedBottom;
    }

    /**
     * Assigns score.
     *
     * @param num An integer containing the score.
     */
    public void setScore(int num) {
        score = num;
        scoreStr = Integer.toString(num);
    }
}
