package main.java.com.demo;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import java.awt.EventQueue;

/** Launches the Super Pusheen game. 
 * @author Hyunryung Kim
 * @email hryungk@gmail.com  
 */
public class GameLauncher extends JFrame {

    public GameLauncher() {
        
        initUI();
    }
    
    private void initUI() {

        SuperPusheen game = new SuperPusheen();// Instantiates the game.
        setLayout(new BorderLayout());         // Sets the layout of the window.        
        // Add the game (which is a panel) to the center of the screen.
        add(game, BorderLayout.CENTER);  
        pack();                    // Packs everything into the preferredSize.
        setVisible(true);          // The frame will be set to visible.
        setResizable(false);       // A user cannot resize the window.
        setTitle(game.getName());  // Sets the window with the game's name.
        // The window will pop up in the middle of the screen when launched.
        setLocationRelativeTo(null); 
        // Exit the game when you hit the red X on the top of the window.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
    }

    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
            var gameLauncher = new GameLauncher();
            gameLauncher.setVisible(true);
        });
    }
}
