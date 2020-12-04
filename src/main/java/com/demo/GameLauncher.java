package main.java.com.demo;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import java.awt.EventQueue;


/** Create a Space Invaders game.
 * Goal of the game is to save the Earth from invasion of evil space invaders.
 * The player controls a cannon.
 * When the player shoots a missile, he can shoot another one only when it hits an
 * alien or the top of the Board.
 * Aliens randomly launch their bombs. Each alien shoots a bomb only after the
 * previous one hits the bottom.
 * @author zetcode.com */
public class GameLauncher extends JFrame {

    public GameLauncher() {
        
        initUI();
    }
    
    private void initUI() {

        SuperPusheen game = new SuperPusheen();
        setTitle(game.NAME); //creates a new window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exits the game when you hit the red "X" on the top right of the window.
        setLayout(new BorderLayout()); //sets the layout of the window
        add(game, BorderLayout.CENTER);  //Adds the game (which is a canvas) to the center of the screen.
        pack(); //contains everything into the preferredSize
        setResizable(false); // A user cannot resize the window.
        setLocationRelativeTo(null); // the window will pop up in the middle of the screen when launched.
        setVisible(true); //the frame will be set to visible.
    }

    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
            var game = new GameLauncher();
            game.setVisible(true);
        });
    }
}
