package main.java.com.demo.screen;

import main.java.com.demo.gfx.Color;
import main.java.com.demo.gfx.Font;
import main.java.com.demo.gfx.Screen;

/**
 * A Menu that can represent either AboutMenu or InstructionsMenu.
 * Only contains text and a black background.
 */
public class ReadMenu extends Menu {

    private Menu parent;      // The parent Menu object to go back to.
    private String[] textList;

    public ReadMenu(String[] textList) {  
        this(null, textList);
    }
    
    public ReadMenu(Menu parent, String[] textList) {        
        this.parent = parent;        
        this.textList = textList;
    }

    /**
     * Update method, TPS (around 60) updates per second.
     */
    @Override
    public void tick() {
        // If the user presses the "attack", "menu", or "pause" key,         
        if (input.attack.clicked || input.menu.clicked || input.pause.clicked) {
            game.setMenu(parent);   // It goes back to the parent menu.
        }
    }

    /**
     * Renders the text on the screen.
     * @param screen The current Screen object displayed.
     */
    @Override
    public void render(Screen screen) {
        screen.clear(0);            // Clears the screen to be a black color.
        
        // Draws Title text.
        Font.draw("About Super Pusheen", screen, 2 * PPS + 4, 1 * PPS,
                Color.get(0, 555, 555, 555)); 
        // Draws texts.         
        int x = 0 * PPS + 4;        
        for (int i = 0; i < textList.length; i++) {
            Font.draw(textList[i], screen, x, (3 + i) * PPS + 2 * i,
                    Color.get(0, 333, 333, 333));
        }
    }
    
    public void setParent(Menu parent) {
        this.parent = parent;
    }
}