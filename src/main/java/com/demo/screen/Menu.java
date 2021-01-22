package main.java.com.demo.screen;

import main.java.com.demo.Commons;
import main.java.com.demo.SuperPusheen;
import main.java.com.demo.InputHandler;
import main.java.com.demo.gfx.Screen;

public abstract class Menu {

    protected final int PPS = Commons.PPS; // Size of square in the sprite sheet
    protected int colNum;      // Number of squres in a row in the sprite sheet.
    protected SuperPusheen game;   // Game object used in Menu classes.
    protected InputHandler input;  // Input handler object used in Menu classes.

    /**
     * Initialization step, adds the game & input objects.
     * @param game The game object used in the Menu sub-classes.
     * @param input The input handler used in the Menu sub-classes.
     */
    public void init(SuperPusheen game, InputHandler input) {
        this.input = input;
        this.game = game;
        colNum = game.getColNum();
    }

    /**
     * Update method used in Menu sub-classes,
     * TPS (around 60) updates (ticks) per second.
     */
    public abstract void tick();

    /**
     * Render method used in Menu sub-classes.
     * Draws stuff on the screen.
     * @param screen The current Screen object displayed.
     */
    public abstract void render(Screen screen);
}
