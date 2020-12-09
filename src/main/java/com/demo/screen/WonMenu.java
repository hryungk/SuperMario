package main.java.com.demo.screen;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;

public class WonMenu extends Menu {
	private int inputDelay = 60; // variable to delay the input of the player, so they won't skip the won menu the first second.

	/* WonMenu & DeadMenu are very similar... scratch that, the exact same class with text changes. */
	
	public WonMenu() {
	}
	
	/** Update Method, 60 updates (ticks) per second */
        @Override
	public void tick() {
            if (inputDelay > 0) //If the input delay is above 0 (it starts at 60) then...
                inputDelay--; // the inputDelay will minus by 1. 
            else if (input.attack.clicked || input.menu.clicked) {
                game.setMenu(new TitleMenu(game)); //If the delay is equal or lower than 0, then the person can go back to the title menu.
            }
	}
	
    /** Render method, draws stuff on the screen. */
    public void render(Screen screen) {
        int ES = Commons.ENTITY_SIZE;
        
        Font.draw("You won! Yay!", screen, 8 * ES, 4 * PPS, Color.get(-1, 555, 555, 555)); // Draws text

        int seconds = game.gameTime / 60; // The current amount of seconds in the game.
        int minutes = seconds / 60; // The current amount of minutes in the game.
        int hours = minutes / 60; // The current amount of hours in the game.
        minutes %= 60; // fixes the number of minutes in the game. Without this, 1h 24min would look like: 1h 84min.
        seconds %= 60; // fixes the number of seconds in the game. Without this, 2min 35sec would look like: 2min 155sec.

        String timeString = ""; //Full text of time.
        if (hours > 0) {
            timeString = hours + "h" + (minutes < 10 ? "0" : "") + minutes + "m"; // If over an hour has passed, then it will show hours and minutes.
        } else {
            timeString = minutes + "m " + (seconds < 10 ? "0" : "") + seconds + "s"; // If under an hour has passed, then it will show minutes and seconds.
        }
        Font.draw("Time:", screen, 8 * ES, 5 * PPS, Color.get(-1, 555, 555, 555)); // Draws "Time:" on the frame
        Font.draw(timeString, screen, 8 * ES + 5 * PPS, 5 * PPS, Color.get(-1, 550, 550, 550)); // Draws the current time next to "Time:"
        Font.draw("Score:", screen, 8 * ES, 6 * PPS, Color.get(-1, 555, 555, 555)); // Draws "Score:" on the frame
        Font.draw("" + game.player.getScore(), screen, 8 * ES + 6 * PPS, 6 * PPS, Color.get(-1, 550, 550, 550)); // Draws the current score next to "Score:"
        Font.draw("Press C to return to title screen.", screen, 8 * ES, 8 * PPS, Color.get(-1, 333, 333, 333)); //Draws text
    }
}
