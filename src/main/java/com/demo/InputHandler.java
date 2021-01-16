package main.java.com.demo;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;
import java.util.List;

/* A class that handles keyboard inputs and translates them as booleans. */
public class InputHandler extends KeyAdapter {
	
    public List<Key> keys = new ArrayList<>(); // List of keys used in the game.

    // Action keys.
    public Key up = new Key();
    public Key down = new Key();
    public Key left = new Key();
    public Key right = new Key();
    public Key jump = new Key();
    public Key attack = new Key();
    public Key menu = new Key();
    public Key pause = new Key();

        
    @Override
    /** Triggered when a key is pressed. */
    public void keyPressed(KeyEvent ke) {
        toggle(ke, true); 
    }

    @Override
    /** Triggered when a key is let go. */
    public void keyReleased(KeyEvent ke) {
        toggle(ke, false);
    }

    /* Turns keyboard key presses into actions. */
    private void toggle(KeyEvent ke, boolean pressed) {
        // Numpads system
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD4) // If numpad 4 is pressed,            
            left.toggle(pressed);                   // left Key is toggled.
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD6) right.toggle(pressed);   
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD8) up.toggle(pressed); 
        if (ke.getKeyCode() == KeyEvent.VK_NUMPAD2) down.toggle(pressed); 
        // ADWS system
        if (ke.getKeyCode() == KeyEvent.VK_A) left.toggle(pressed); 
        if (ke.getKeyCode() == KeyEvent.VK_D) right.toggle(pressed); 
        if (ke.getKeyCode() == KeyEvent.VK_W) up.toggle(pressed); 
        if (ke.getKeyCode() == KeyEvent.VK_S) down.toggle(pressed);
        // Arrow keys
        if (ke.getKeyCode() == KeyEvent.VK_LEFT) left.toggle(pressed); 
        if (ke.getKeyCode() == KeyEvent.VK_RIGHT) right.toggle(pressed); 
        if (ke.getKeyCode() == KeyEvent.VK_UP) up.toggle(pressed); 
        if (ke.getKeyCode() == KeyEvent.VK_DOWN) down.toggle(pressed);
        
        if (ke.getKeyCode() == KeyEvent.VK_SPACE)   // If space bar is pressed,
            jump.toggle(pressed);                   // jump Key is toggled.
        
        if (ke.getKeyCode() == KeyEvent.VK_CONTROL) // if ctrl is pressed,
            attack.toggle(pressed);                 // attack Key is toggled.
        
        if (ke.getKeyCode() == KeyEvent.VK_ENTER)   // if enter is pressed,
            menu.toggle(pressed);                   // menu Key is toggled.
        
        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE)  // if esc is pressed,
            pause.toggle(pressed);                  // pause Key is toggled.
    }

    
    /** Stops all of the actions when the game is out of focus. */
    public void releaseAll() {
        for (int i = 0; i < keys.size(); i++) {
            keys.get(i).down = false; // Turns all the keys' down value to false
        }
    }

    /** Ticks every key to see if it is pressed. */
    public void tick() {
        for (int i = 0; i < keys.size(); i++) {
            keys.get(i).tick();
        }
    }

    /**************************************************************************/
    /*                                Class Key                               */
    /**************************************************************************/
    /** An inner class representing a key. */    
    public class Key {
        public int presses, absorbs; // presses: How long you held it down, 
                                     // absorbs: If you clicked or held it down.
        public boolean down, clicked;// Tells if the player has held down or 
                                     // clicked the button.

        public Key() {
            presses = absorbs = 0;
            down = clicked = false;
            keys.add(this);          // Adds this Key to a list of Keys in use.
        }

        /** Updates variables when the key is toggled.
         * @param pressed True if the key is toggled. */
        public void toggle(boolean pressed) {
            if (pressed != down) {   // If the key is being pressed,
                down = pressed;      // then down is true.
            }
            if (pressed) {           // If pressed, 
                presses++;           // then presses value goes up.                
            } else {                 // If not pressed down, reset counts.
                presses = 0; 
                absorbs = 0;
            }  
        }
        
        /** Updates the key status. */
        public void tick() {            
            if (absorbs < presses) { // If presses are greater than absorbs
                absorbs++;           // Increase the absorbs value.
                if (presses == 1)    // Only if key is first down,
                    clicked = true;  // clicked is true.
            } else {                 // Else, clicked is false.
                clicked = false;
            }                             
        }
    }
}
