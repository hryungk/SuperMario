package main.java.com.demo.screen;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;

/** 
 * A Menu that can represent either WonMenu or DeadMenu.
 * @author HRK
 */
public class EndMenu extends Menu {

    private final int TPS = Commons.TPS; // Ticks per second.
    private int inputDelay = TPS;   // Time to delay the input of the player, so
                                    // they won't skip the won menu the first 
                                    // second.
    private final String message;
    public EndMenu (String msg) {
        message = msg;
    }

    /**
     * Update Method, TPS (around 60) updates (ticks) per second.
     */
    @Override
    public void tick() {
        if (inputDelay > 0) {
            // If the input delay is above 0 (it starts at TPS ~= 60)
            inputDelay--;   // The inputDelay will decrease by 1. 
        } else if (input.attack.clicked || input.menu.clicked) {
            // Once the delay time is over, 
            game.setMenu(new TitleMenu(game));  // The user can go back to the 
                                            // title menu.
        }
    }

    /**
     * Renders the text on the screen.
     * @param screen The current Screen object displayed.
     */
    @Override
    public void render(Screen screen) {
        int ES = Commons.ENTITY_SIZE;
        int x = 5 * ES;
        int c1 = Color.get(-1, 555, 555, 555);
        int c2 = Color.get(-1, 550, 550, 550);
        Font.draw(message, screen, x, 9 * PPS + 3, c1); // Draws text

        int seconds = game.gameTime / TPS;  // Game time in seconds.
        int minutes = seconds / 60;         // Game time in minutes.
        int hours = minutes / 60;           // Game time in hours.
        minutes %= 60;                      // Leftover minutes.
        seconds %= 60;                      // Leftover seconds.

        // Full text for game time.
        String timeString = minutes + "m "
                + (seconds < 10 ? "0" : "") + seconds + "s";
        if (hours > 0) {                    // If over an hour has passed, 
            timeString = hours + "h " + timeString; // Show hours as well.
        }

        // Draws "Time:" on the frame.
        Font.draw("Time: ", screen, x, 10 * PPS + 6, c1);
        // Draws the time next to "Time:"
        Font.draw(timeString, screen, x + 5 * PPS, 10 * PPS + 6, c2);
        // Draws "Score:" on the frame.
        Font.draw("Score:", screen, x, 11 * PPS + 9, c1);
        // Draws the current score next to "Score:"
        Font.draw("" + game.player.getScore(), screen, x + 6 * PPS,
                11 * PPS + 9, c2);
        // Draws return to title text.
        Font.draw("Press Enter to return to title screen.", screen, 2,
                14 * PPS, Color.get(-1, 333, 333, 333));
    }
}
