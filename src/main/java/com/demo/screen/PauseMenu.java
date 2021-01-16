package main.java.com.demo.screen;

import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;
//import main.sound.Sound;

public class PauseMenu extends Menu {

    private int selected = 0; // Currently selected option.

    private static final String[] OPTIONS = {"Restart game", "Go back to game", 
        "How to play", "About"}; // Available options on the menu

    public PauseMenu() {
    }

    /**
     * Update the menu screen,  TPS (around 60) updates per second.
     */
    @Override
    public void tick() {
        // Choosing an option.
        if (input.up.clicked) { // If the player presses the up key
            selected--;  // Move one up in the option list.
        }
        if (input.down.clicked) { // If the player presses the down key
            selected++;  // Move one down in the option list.
        }
        int len = OPTIONS.length; // The size of the list.
        if (selected < 0) { // If the user goes up on the first option
            selected += len;  // Move it to the last option on the list.
        }
        if (selected >= len) { // If the user goes down on the last option
            selected -= len; // Move it to the first option on the list.
        }
        // If either the "Attack" or "Menu" keys are pressed,
        if (input.attack.clicked || input.menu.clicked) { 
            if (selected == 0) { // If the selection is 0 ("Restart game")
//                Sound.test.play(); // Play a sound.
                game.initGame(); // Initialize the game.
                game.setMenu(null); // Set the menu to null (No active menu).
                System.out.println("=============================Restart the game!=============================");
            }
            if (selected == 1) { // If the selection is 1 ("Go back to game")
                game.setMenu(null);  // Go back to the game.
            }
            if (selected == 2) { // If the selection is 2 ("How to play")
//                game.setMenu(new InstructionsMenu(this)); // Go to the instructions menu.
                Menu tempMenu = game.getInstMenu();
                ((ReadMenu)tempMenu).setParent(this);
                game.setMenu(tempMenu);
            }
            if (selected == 3) { // If the selection is 3 ("About")
//                game.setMenu(new AboutMenu(this));  // Go to the about menu.
                Menu tempMenu = game.getAboutMenu();
                ((ReadMenu)tempMenu).setParent(this);
                game.setMenu(tempMenu);
            }
        }

        if (input.pause.clicked) { // If "Pause" key (esc) is pressed        
            game.setMenu(null);    // Go back to the game.
        }
    }

    /**
     * Render method, draws the logo and options on the screen.
     * @param screen The current Screen object displayed.
     */
    @Override
    public void render(Screen screen) {

        /* This section is used to display the minicraft title */
        int h = 11; // Number of squares in vertical direction (on the spritesheet)
        int w = 14; // Number of squares in horizontal direction (on the spritesheet)     

        int xS = 18; // X tile coordinate in the sprite-sheet
        int yS = 0; // Y tile coordinate in the sprite-sheet
        int xo = (screen.W - w * PPS) / 2; // X location of the title
        int yo = 3 * PPS; // Y location of the title            
        int lastY = yo; // last y location of the displayed object

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                lastY = yo + y * PPS;
                screen.render(xo + x * PPS, lastY, (xS + x) + (yS + y) * COLNUM, 0); // Loops through all the squares to render them all on the screen.                
            }
        }
        lastY += PPS;
        String copyright = "©2020 HYUNRYUNGKIM";
        Font.draw(copyright, screen, xo + (w - copyright.length()) * PPS, lastY, Color.get(0, 252, 188, 176)); // Draw text at the bottom

        /* This section is used to display this OPTIONS on the screen */
        for (int i = 0; i < OPTIONS.length; i++) { // Loops through all the OPTIONS in the list
            lastY += 2 * PPS;
            String msg = OPTIONS[i]; // Text of the current option
            int col = Color.get(0, 222, 222, 222); // Color of unselected text
            if (i == selected) { // If the current option is the option that is selected
                msg = "> " + msg + " <"; // Add the cursors to the sides of the message
                col = Color.get(0, 555, 555, 555); // change the color of the option
            }
            Font.draw(msg, screen, (screen.W - msg.length() * PPS) / 2, lastY, col); // Draw the current option to the screen                             
        }
    }
}
