package main.java.com.demo.screen;

import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;

public class AboutMenu extends Menu {
	private Menu parent; // Creates a parent object to go back to

	/** The about menu is a read menu about what the game was made for. Only contains text and a black background */
	public AboutMenu(Menu parent) {
		this.parent = parent; // The parent Menu that it will go back to.
	}

	/** The update method. 60 updates per second. */
	public void tick() {
		if (input.attack.clicked || input.menu.clicked) {
			game.setMenu(parent); // If the user presses the "Attack" or "Menu" button, it will go back to the parent menu.
		}
	}

	/** Renders the text on the screen */
	public void render(Screen screen) {
		screen.clear(0); // clears the screen to be a black color.

		/* Font.draw Parameters: Font.draw(String text, Screen screen, int x, int y, int color) */
		
		Font.draw("About Super Pusheen", screen, 2 * 8 + 4, 1 * 8, Color.get(0, 555, 555, 555)); //draws Title text
		Font.draw("Super Pusheen was made by Hyunryung", screen, 0 * 8 + 4, 3 * 8, Color.get(0, 333, 333, 333)); // draws text
		Font.draw("Kim for a fun personal project in", screen, 0 * 8 + 4, 4 * 8 + 2, Color.get(0, 333, 333, 333)); // draws text
		Font.draw("2020. It is a copycat of the ever", screen, 0 * 8 + 4, 5 * 8 + 4, Color.get(0, 333, 333, 333)); // draws text
		Font.draw("so famous Super Mario Bros. by", screen, 0 * 8 + 4, 6 * 8 + 6, Color.get(0, 333, 333, 333)); // draws text		
                Font.draw("©Nintendo.", screen, 0 * 8 + 4, 7 * 8 + 8, Color.get(0, 333, 333, 333)); // draws text		
	}
}
