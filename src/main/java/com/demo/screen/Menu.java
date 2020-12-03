package main.screen;

import main.Commons;
import java.util.List;

import main.SuperPusheen;
import main.InputHandler;
import main.gfx.Screen;

public abstract class Menu {
    protected SuperPusheen game; // game object used in the menu classes.
    protected InputHandler input; // input handler object used in the menu classes.
    protected final int PPS = Commons.PPS; // Size of the tile in the sprite sheet (pixels per tile).
    protected int COLNUM;    // Number of tiles in x direction in the sprite sheet.

    /** Initialization step, adds the game & input objects.
     * @param game The game object used in the menu classes.
     * @param input The input handler used in the menu classes. */
    public void init(SuperPusheen game, InputHandler input) {
        this.input = input;
        this.game = game; 
        COLNUM = game.getColNum();
    }

    /** Update method used in menus. */
    public abstract void tick();

    /** Render method used in menus
     * @param screen The current Screen object displayed. */
    public abstract void render(Screen screen);    
}
