package main.java.com.demo.screen;

import java.util.ArrayList;
import java.util.List;
import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;

/**
 * Displays the title logo and options.
 * @author HRK
 */
public class OptionMenu extends Menu {

    protected int selected = 0;     // Currently selected option.
    // Available options on the menu.
    protected List<String> OPTIONS = new ArrayList<>(); 
    
    public OptionMenu() {
        super();
    }

    /**
     * Update the menu screen, TPS (around 60) updates per second.
     */
    @Override
    public void tick() {
        // Choosing an option.
        if (input.up.clicked) {     // If the player presses the up key
            selected--;             // Move one up in the option list.
        }
        if (input.down.clicked) {   // If the player presses the down key
            selected++;             // Move one down in the option list.
        }
        int len = OPTIONS.size();   // The size of the list.
        if (selected < 0) {         // If the user goes up on the first option
            selected += len;        // Move it to the last option on the list.
        }
        if (selected >= len) {      // If the user goes down on the last option
            selected -= len;        // Move it to the first option on the list.
        }
    }

    /**
     * Render method, draws the logo and options on the screen.
     * @param screen The current Screen object displayed.
     */
    @Override
    public void render(Screen screen) {

        /* Display the Super Pusheen title logo. */
        int h = 11;         // Height of the logo on the sprite sheet [square]
        int w = 14;         // Width of the logo on the sprite sheet [square]

        int xS = 18;        // x coordinate in the sprite sheet [square]
        int yS = 0;         // y coordinate in the sprite sheet [square]
        int xo = (screen.W - w * PPS) / 2; // x location of the title on screen
        int yo = 3 * PPS;   // y location of the title on screen       
        int lastY = yo;     // Last y location of the displayed object

        // Loops through all the squares to renderFont the title on the screen.
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                lastY = yo + y * PPS;
                int sheetLoc = (xS + x) + (yS + y) * colNum;
                screen.render(xo + x * PPS, lastY, sheetLoc, 0);
            }
        }
        lastY += PPS;       // Update lastY for the next rendering.

        /* Draw text at the bottom of the title logo. */
        String copyright = "©2020 HYUNRYUNGKIM";
        Font.draw(copyright, screen, xo + (w - copyright.length()) / 2 * PPS,
                lastY, Color.get(0, 252, 188, 176));

        /* Display OPTIONS on the screen. */
        // Loops through all the OPTIONS in the list.
        for (int i = 0; i < OPTIONS.size(); i++) {
            lastY += 2 * PPS;
            String msg = OPTIONS.get(i);               // Text of the current option
            int col = Color.get(0, 222, 222, 222); // Color of unselected text
            if (i == selected) {        // If the current option is the selected
                msg = "> " + msg + " <";// Add the cursors to both sides.
                col = Color.get(0, 555, 555, 555); // Change its color.
            }
            // Draw the current option to the screen.
            Font.draw(msg, screen, (screen.W - msg.length() * PPS) / 2, lastY,
                    col);
        }
    }
}
