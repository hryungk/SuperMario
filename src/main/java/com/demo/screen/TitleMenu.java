package main.java.com.demo.screen;

import java.util.Arrays;
import main.java.com.demo.SuperPusheen;
//import main.sound.Sound;

public class TitleMenu extends OptionMenu {

    public TitleMenu(SuperPusheen game) {
        super();
        // Fill up OPTIONS.
        String[] temp = {"Start game", "How to play", "About"};
        OPTIONS.addAll(Arrays.asList(temp));
        
        // Initialize the game.
        game.initGame(); 
    }

    /**
     * Update method used in menus.
     */
    @Override
    public void tick() {        
        super.tick();
        // If either the "Attack" or "Menu" keys are pressed,
        if (input.attack.clicked || input.menu.clicked) {
            if (selected == 0) {    // If the selection is 0 ("Start game")
//                Sound.test.play();// Play a sound                
                game.setMenu(null); // Set the menu to null (No active menu)
                System.out.println("=============================Start the "
                        + "game!=============================");
            }
            if (selected == 1) {    // If the selection is 1 ("How to play")
                Menu tempMenu = game.getInstMenu();
                ((ReadMenu) tempMenu).setParent(this);
                game.setMenu(tempMenu); // Go to the instructions menu.
            }
            if (selected == 2) {    // If the selection is 2 ("About")
                Menu tempMenu = game.getAboutMenu();
                ((ReadMenu) tempMenu).setParent(this);
                game.setMenu(tempMenu); // Go to the about menu.
            }
        }
    }
}
