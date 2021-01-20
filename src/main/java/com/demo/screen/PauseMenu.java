package main.java.com.demo.screen;

import java.util.Arrays;
//import main.sound.Sound;

public class PauseMenu extends OptionMenu {// Currently selected option.

    public PauseMenu() {        
        // Fill up OPTIONS.
        String[] temp = {"Go back to game", "How to play", "About", 
        "Restart game"};
        OPTIONS.addAll(Arrays.asList(temp));
    }

    /**
     * Update the menu screen,  TPS (around 60) updates per second.
     */
    @Override
    public void tick() {
        super.tick();
        // If either the "Attack" or "Menu" keys are pressed,
        if (input.attack.clicked || input.menu.clicked) {             
            if (selected == 0) {    // If the selection is 0 ("Go back to game")
                game.setMenu(null); // Go back to the game.
            }
            if (selected == 1) {    // If the selection is 1 ("How to play")
                Menu tempMenu = game.getInstMenu();
                ((ReadMenu)tempMenu).setParent(this);
                game.setMenu(tempMenu); // Go to the instructions menu.
            }
            if (selected == 2) {    // If the selection is 2 ("About")
                Menu tempMenu = game.getAboutMenu();
                ((ReadMenu)tempMenu).setParent(this);
                game.setMenu(tempMenu); // Go to the about menu.
            }
            if (selected == 3) {    // If the selection is 3 ("Restart game")
//                Sound.test.play();// Play a sound.
                game.initGame();    // Initialize the game.
                game.setMenu(null); // Set the menu to null (No active menu).
                System.out.println("=============================Restart the" +
                        " game!=============================");
            }
        }
        // If "Pause" key (esc) is pressed 
        if (input.pause.clicked) {       
            game.setMenu(null);     // Go back to the game.
        }
    }
}