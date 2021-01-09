package main.java.com.demo.screen;

import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;

public class InstructionsMenu extends Menu {
	private Menu parent; // Creates a parent object to go back to

	/** The about menu is a read menu about what you have to do in the game. Only contains text and a black background */
	public InstructionsMenu(Menu parent) {
		this.parent = parent; // The parent Menu that it will go back to.
	}

	public void tick() {
		if (input.attack.clicked || input.menu.clicked) {
			game.setMenu(parent);  // If the user presses the "Attack" or "Menu" button, it will go back to the parent menu.
		}
	}

	/** Renders the text on the screen */
	public void render(Screen screen) {
		screen.clear(0); // clears the screen to be a black color.
		
		/* Font.draw Parameters: Font.draw(String text, Screen screen, int x, int y, int color) */

		Font.draw("HOW TO PLAY", screen, 4 * 8 + 4, 1 * 8, Color.get(0, 555, 555, 555)); //draws Title text
		Font.draw("Move your character with the left ", screen, 0 * 8 + 4, 3 * 8, Color.get(0, 333, 333, 333)); // draws text
		Font.draw("and right arrow keys. ", screen, 0 * 8 + 4, 4 * 8 + 2, Color.get(0, 333, 333, 333)); // draws text
		Font.draw("Press space bar to jump.", screen, 0 * 8 + 4, 5 * 8 + 4, Color.get(0, 333, 333, 333)); // draws text
		Font.draw("Once you eat the fire flower,", screen, 0 * 8 + 4, 6 * 8 + 6, Color.get(0, 333, 333, 333)); // draws text
                Font.draw("press ctrl to shoot fire.", screen, 0 * 8 + 4, 7 * 8 + 6, Color.get(0, 333, 333, 333)); // draws text
		Font.draw("Reach the castle to win the game!", screen, 0 * 8 + 4, 8 * 8 + 8, Color.get(0, 333, 333, 333)); // draws text		
	}
}
