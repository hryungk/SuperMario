package main.java.com.demo.entity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import main.java.com.demo.SuperPusheen;

/**
 * Represents a pipe. Keeps the image and the coordinates of the pipe.
 *
 * @author HRK
 */
public class Pipe extends RigidEntity {

   /**
     * Constructor.Initiates the x and y coordinates and the visible variable.
     *      
     * @param x x coordinate on the map [pixel]
     * @param y y coordinate on the map [pixel]
     * @param pipeSize An integer containing the pipe size.
     */
    public Pipe(int x, int y, int pipeSize) {          
        super();        
        init(x, y, pipeSize);
    }    
    
    private void init (int x, int y, int pipeSize) {
        
        // Set image.
        try {
            var imgPath = "src/main/resources/pipe" + pipeSize + ".png";
            BufferedImage source = ImageIO.read(new File(imgPath));
            setImage(source);
        } catch (IOException ex) {
            Logger.getLogger(SuperPusheen.class.getName()).log(Level.SEVERE,
                     null, ex);
        }
                
        // Initial coordinates of the player sprite.
        setX(x);        
        setY(y);    
        
        xS = 0;
        yS = 0;
    }
}
