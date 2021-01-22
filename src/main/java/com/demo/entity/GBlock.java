package main.java.com.demo.entity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import main.java.com.demo.SuperPusheen;

/**
 * Represents a ground block. Keeps the image and the coordinates of the ground 
 * block.
 *
 * @author HRK
 */
public class GBlock extends RigidEntity {

    /**
     * Constructor. Initiates the x and y coordinates and the visible variable.
     * 
     * @param x x coordinate on the map [pixel]
     * @param y y coordinate on the map [pixel]
     */
    public GBlock(int x, int y) {
        super();
        init(x, y);
    }
    
    private void init(int x, int y) {       

        // Set image.
        try {
            var imgPath = "src/main/resources/ground.png";
            BufferedImage source = ImageIO.read(new File(imgPath));
            setImage(source);
        } catch (IOException ex) {
            Logger.getLogger(SuperPusheen.class.getName()).log(Level.SEVERE, 
                    null, ex);
        }

        // Initial coordinates of the player sprite.
        setX(x);
        setY(y);

        xS = 4;
        yS = 0;
    }
}