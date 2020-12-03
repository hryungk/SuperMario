package main.entity;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import main.SuperPusheen;
import main.InputHandler;
import main.Commons;
import main.gfx.Screen;
import main.level.Level;

/** Represents a shot as a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
 *  The shot is triggered with the Space key.
    @author zetcode.com */
public class Shot extends Sprite {
    
    private final int ES = Commons.ENTITY_SIZE;
    
    public Shot( Level level) {      
        super(level);
        wS = hS = 1;
        initShot();
        xS = 8;
        yS = 2;
    }       
    
    // (x, y) is the position of the player.
    private void initShot() {
       
        String shotImg = "src/Retro-Fire-Ball-icon_8px.png";        
       
        try {
//            BufferedImage source = ImageIO.read(new File(shotImg));
                BufferedImage source = ImageIO.read(SuperPusheen.class.getResourceAsStream("/Retro-Fire-Ball-icon_8px.png"));
            setImage(source);
        } catch (IOException ex) {
           String msg = String.format("No such file found: %s", ex.getMessage());
            System.out.println(msg);
        }      
        
//        // Initial coordinates of the shot sprite.          
//        setX(x + ES);        
//        setY(y + (ES-height)/2);       
//        
//        this.dx = dx; 
        dy = 0;
        
        unit = (int) (Math.log10(width)/Math.log10(2)); // the size of block to be used (4 for 16 px sprite and 3 for 8px sprite)
    }    
    
@Override
    public void tick() {
        super.tick();
        
        
//        if (isVisible()) {
//
//            List<Alien> aliens = level.aliens;   
//            for (Alien alien : aliens) {
//
//                // When alien and shot collide, alien's dying flag is set and 
//                // shot dies.
//                if (alien.isVisible() && isVisible()) {
//                    int alienX = alien.getX();// - level.screen.xOffset;
//                    int alienY = alien.getY();
//                    if (intersects(alienX + 4, alienY, alienX + ES, alienY + ES)) {
//                        alien.setDying(true);
////                        deaths++;
//                        die();
//                    }
//                }
//            }

//            if ((player.x >= Commons.PLYAER_XMAX && input.right.down) &&  
//                (screen.xOffset + B_WIDTH < source.getWidth())) // within the map range
//                setDx(0);
//            else
//                setDx(1);

            boolean stopped = false;
            // Update shot's position
//            if (x > level.getOffset() + Commons.BOARD_WIDTH) 
//                remove();
//            else 
//                stopped = !move(dx, dy);
            
            int offset = level.getOffset();
            if (x <= 0)
                remove();
            else if (offset < x+width && x < offset + Commons.BOARD_WIDTH )
                stopped = !move(dx, dy);
            else
                remove();
            
            
            if (stopped)
                remove();            
//        }        
    }
    
    /** What happens when the player touches an entity */    
    protected void touchedBy(Sprite sprite) {
        if (sprite instanceof Alien) { // if the entity touches the player
//            sprite.touchedBy(this); // calls the touchedBy() method in the entity's class
//            hurt(1); // hurts the player, damage is based on lvl.
//            sprite.setDying(true);            
//            setDying(true);

            hurt(health); // hurt the shot
            if (!((Alien) sprite).shot) {                
                ((Alien) sprite).shot = true;
                sprite.dx = 0;
                level.player.score += Commons.SPE; // gives the player 1000 points of score
                ((Alien) sprite).score = Integer.toString(Commons.SPE);
            }
        }
    }
    
    @Override
    public void render(Screen screen) {
                
        int sw = screen.getSheet().width;   // width of sprite sheet (256)
        int colNum = sw / Commons.PPS;    // Number of squares in a row (32)    
//        screen.render(x, y, xS + yS * colNum, 0); // draws the top-left tile
        int PPS = Commons.PPS;
        for (int ys = 0; ys < hS; ys++) {
            for (int xs = 0; xs < wS; xs++) {
                screen.render(x + xs * PPS, y + ys * PPS, (xS + xs) + (yS + ys) * colNum, 0); // Loops through all the squares to render them all on the screen.                    
            }
        }
    }
}
