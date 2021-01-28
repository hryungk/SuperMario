package main.java.com.demo.entity;

import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * Whenever the player scores points, a new score string object is created.
 *
 * @author HRK
 */
public class ScoreString extends Sprite {

    private final String SCORE_STR; // Score in string
    private final int YFIN;         // Final location of y
    private double scoreX, scoreY;  // Location of the score string

    public ScoreString(int x, int y, int score, Level level) {
        super(level);

        this.x = x;
        this.y = y;
        this.score = score;
        
        // Initialize variables from Sprite class.
        health = lives = 1;
        
        // Initialize variables for this class.
        SCORE_STR = Integer.toString(score);
        YFIN = y;
        scoreX = x;
        scoreY = y;
    }

    /**
     * Update score string's location.
     */
    @Override
    public void tick() {
        super.tick();
        
        // If the player moves past the maximum screen x-position
        if (level.player.getX() + level.player.width
                >= level.player.getPMax() + level.getOffset()) {
            scoreX += Math.max(0, level.player.getNetDx()); // Move string.
        }
        scoreY -= 0.5;      // String flies away.
        x = (int) scoreX;
        y = (int) scoreY;
        if (y < YFIN - ES){ // If the string moved an entity length,        
//            remove();       // Remove from the screen.
            hurt(health);
        }
    }

    /**
     * Draws the string on the screen.
     *
     * @param screen The screen to be displayed on.
     */
    @Override
    public void render(Screen screen) {
        super.render(screen);
        
        Font.draw(SCORE_STR, screen, x, y, Color.WHITE);
    }

    /**
     * Nothing happens when the score string is touched by another sprite.
     *
     * @param sprite The sprite that this score string is touched by.
     */
    @Override
    protected void touchedBy(Sprite sprite) {
    }
}
