package main.java.com.demo.entity;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/** Represents a shot as a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
 *  The shot is triggered with the Space key.
    @author zetcode.com */
public class Shot extends Sprite {
    
    private final int ES = Commons.ENTITY_SIZE;
    
    public Shot( Level level) {      
        super(level);
        initShot();
        
    }       
    
    // (x, y) is the position of the player.
    private void initShot() {
       
        width = height = ES / 2;
        wS = width / PPS;
        hS = height / PPS;
//        // Initial coordinates of the shot sprite.          
//        setX(x + ES);        
//        setY(y + (ES-height)/2);       
//        
//        this.dx = dx; 
        
        xS = 8;
        yS = 2;
        
        dy = 0;        
        unit = (int) (Math.log10(width)/Math.log10(2)); // the size of block to be used (4 for 16 px sprite and 3 for 8px sprite)
        aTile = Math.min(Math.pow(2, 4 - unit), 1); // 1 for unit 3, 1 for unit 4, 0.5 for unit 5 (big Pusheen)
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
            else if (offset < x+width && x < offset + Commons.BOARD_WIDTH)
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
            if (!((Alien) sprite).isShot()) {                
                ((Alien) sprite).setShot();
                sprite.dx = 0;
                level.player.score += ((Alien) sprite).score; // gives the player 1000 points of score
                ((Alien) sprite).scoreStr = Integer.toString(((Alien) sprite).score);
            }
        }
    }
    
    @Override
    public void render(Screen screen) {
                
        int sw = screen.getSheet().width;   // width of sprite sheet (256)
        int colNum = sw / Commons.PPS;    // Number of squares in a row (32)    
//        screen.render(x, y, xS + yS * colNum, 0); // draws the top-left tile
        for (int ys = 0; ys < hS; ys++) {
            for (int xs = 0; xs < wS; xs++) {
                screen.render(x + xs * PPS, y + ys * PPS, (xS + xs) + (yS + ys) * colNum, 0); // Loops through all the squares to render them all on the screen.                    
            }
        }
    }
}
