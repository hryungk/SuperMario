package main.java.com.demo.screen;

import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;
//import main.sound.Sound;

public class TitleMenu extends Menu {
    private int selected = 0; // Currently selected option

    private static final String[] OPTIONS = {"Start game", "How to play", "About"}; // Options that are on the main menu, each seperated by a comma.

    public TitleMenu() {
    }

     /** Update method used in menus. */
    @Override
    public void tick() {
        if (input.up.clicked) selected--; // If the player presses the up key, then move up 1 option in the list
        if (input.down.clicked) selected++; // If the player presses the down key, then move down 1 option in the list

        int len = OPTIONS.length; // The size of the list (normally 3 OPTIONS)
        if (selected < 0) selected += len; // If the selected option is less than 0, then move it to the last option of the list.
        if (selected >= len) selected -= len; // If the selected option is more than or equal to the size of the list, then move it back to 0;

        if (input.attack.clicked || input.menu.clicked) { //If either the "Attack" or "Menu" keys are pressed then...
            if (selected == 0) { //If the selection is 0 ("Start game")
//                Sound.test.play(); // Play a sound
                game.initGame(); // Initialize the game
                game.setMenu(null); // Set the menu to null (No active menu)
                System.out.println("=============================Start the game!=============================");
            }
            if (selected == 1) game.setMenu(new InstructionsMenu(this)); //If the selection is 1 ("How to play") then go to the instructions menu.
            if (selected == 2) game.setMenu(new AboutMenu(this)); //If the selection is 2 ("About") then go to the about menu.
        }
    }

    /** Render method used in menus.
     * @param screen The current Screen object displayed. */
    @Override
    public void render(Screen screen) {
//        screen.clear(0);// Clears the screen to a black color.

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
        String copyright = "©1985 NINTENDO";
        Font.draw(copyright, screen, xo+(w-copyright.length())*PPS, lastY, Color.get(0, 252, 188, 176)); // Draw text at the bottom
        
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