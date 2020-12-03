package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;
import java.util.List;

/* This class is used for handling your inputs and translating them as booleans */
public class InputHandler extends KeyAdapter {
	
    public List<Key> keys = new ArrayList<Key>(); // List of keys used in the game

    /* Action keys */
    public Key up = new Key();
    public Key down = new Key();
    public Key left = new Key();
    public Key right = new Key();
    public Key jump = new Key();
    public Key attack = new Key();
    public Key menu = new Key();

    public InputHandler() { //Board game) {
//        game.addKeyListener(this); // Adds this to Game.java so it can detect when a key is being pressed.
    }
        
    @Override
    public void keyPressed(KeyEvent ke) {
        toggle(ke, true); // triggered when a key is pressed.
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        toggle(ke, false); // triggered when a key is let go.
    }

    /** This method is used to turn keyboard key presses into actions */
    private void toggle(KeyEvent ke, boolean pressed) {
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD4) left.toggle(pressed); //press keypad 4, moves left
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD6) right.toggle(pressed); //press keypad 6, moves right        
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD8) up.toggle(pressed); //press keypad 8, moves up
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD2) down.toggle(pressed); //press keypad 2, moves down
        if (ke.getKeyCode() == KeyEvent.VK_A) left.toggle(pressed); //press A, moves left
        if (ke.getKeyCode() == KeyEvent.VK_D) right.toggle(pressed); //press D, moves right        
        if (ke.getKeyCode() == KeyEvent.VK_W) up.toggle(pressed); //press W, moves up
        if (ke.getKeyCode() == KeyEvent.VK_S) down.toggle(pressed); //press D, moves down
        if (ke.getKeyCode() == KeyEvent.VK_LEFT) left.toggle(pressed); //press left arrow, moves left
        if (ke.getKeyCode() == KeyEvent.VK_RIGHT) right.toggle(pressed); //press right arrow, moves right
        if (ke.getKeyCode() == KeyEvent.VK_UP) up.toggle(pressed); //press up arrow, moves up
        if (ke.getKeyCode() == KeyEvent.VK_DOWN) down.toggle(pressed); //press down arrow, moves down        
        
        if (ke.getKeyCode() == KeyEvent.VK_SPACE) jump.toggle(pressed); //press space, attack toggled
        
        if (ke.getKeyCode() == KeyEvent.VK_CONTROL) attack.toggle(pressed); //press Ctrl, attack toggled
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD0) attack.toggle(pressed); //press keypad 0, attack toggled
        if (ke.getKeyCode() == KeyEvent.VK_C) attack.toggle(pressed); // press C, attack toggled
        
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) menu.toggle(pressed); //press Enter, attack toggled
        if (ke.getKeyCode() == KeyEvent.VK_X) menu.toggle(pressed); //press X, menu toggled
    }

    
    /** This is used to stop all of the actions when the game is out of focus. */
    public void releaseAll() {
        for (int i = 0; i < keys.size(); i++) {
            keys.get(i).down = false; //turns all the keys down value to false
        }
    }

    public void tick() {
        for (int i = 0; i < keys.size(); i++) {
            keys.get(i).tick(); //Ticks every key to see if it is pressed.
        }
    }

    /****************************************************************************/
    /*                                Class Key                                 */
    /****************************************************************************/
    /** An inner class representing a key. */    
    public class Key {
        public int presses, absorbs; // presses determine how long you held it down, absorbs will determined if you clicked or held it down.
        public boolean down, clicked; // booleans to tell if the player has clicked the button, or held down the button.

        public Key() {
            keys.add(this); //Adds this object to a list of Keys used in the game.
        }

        public void toggle(boolean pressed) {
            if (pressed != down) { 
                down = pressed; //If the key is being pressed, then down is true.
            }
            if (pressed) {
                presses++; //If pressed, then presses value goes up.                
            } else { // if not down, reset 
                presses = 0; 
                absorbs = 0;
            }
            
//            if (this.equals(jump)) 
//                System.out.println("down: " + down + ", presses: " + presses);     
        }

        public void tick() {
            
//            if (this.equals(jump)) {  
//                System.out.println("clicked: " + clicked + ", down: " + down +
//                    ", presses: " + presses + ", absorbs: " + absorbs);    
//            }
            
            if (absorbs < presses) { //if presses are above absorbs
                absorbs++;//increase the absorbs value
                if (presses == 1) clicked = true;//clicked is true only when key is first down.
            } else {
                clicked = false;//else clicked is false
            }                             
        }
    }
}
