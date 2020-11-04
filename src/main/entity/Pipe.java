package main.entity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import main.SuperPusheen;

/** Represents a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public class Pipe extends RigidEntity {

    // The constructor initiates the x and y coordinates and the visible variable.
    public Pipe(int pipeSize, int x, int y) {          
        super();
        
        var pipeImg = "src/pipe" + pipeSize + ".png";
//        var ii = new ImageIcon(pipeImg);        
//        setImage(ii.getImage());
        
        try {
            BufferedImage source = ImageIO.read(new File(pipeImg));
            setImage(source);
        } catch (IOException ex) {
            Logger.getLogger(SuperPusheen.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
                
        // Initial coordinates of the player sprite.
        setX(x);        
        setY(y);    
        
        xS = 0;
        yS = 0;
    }    
}
