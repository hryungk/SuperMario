package main.java.com.demo.entity;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/** Represents an alien as a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public class Alien extends Sprite {
    
    private int counter;
    private int curDx, initY;
    private boolean activated, crushed, jumping;
    private boolean isShot;
    private int deathTime;  // Counts ticks after death
    private double ds;
    
    public Alien(int x, int y, Level level) {                
        super(level);
        initAlien(x, y);        
    }    
    
    private void initAlien(int x, int y) {
        
        xS = 4;
        yS = 2;
        width = height = ES;
        wS = width / PPS;
        hS = height / PPS;
        
        setX(x);
        setY(y);    
        ground = y+height;
        
        xSpeed = 1;
        
        dx = -xSpeed;
        dy = ySpeed;
        ds = 1;
        
        counter = 0;
        curDx = dx;
        
        activated = crushed = isShot = jumping = false;
        deathTime = 0;
        score = 100;
        
        unit = (int) (Math.log10(width)/Math.log10(2)); // the size of block to be used (4 for 16 px sprite and 3 for 8px sprite)
        aTile = Math.min(Math.pow(2, 4 - unit), 1); // 1 for unit 3, 1 for unit 4, 0.5 for unit 5 (big Pusheen)
    }    
        
    // Positions the alien in horizontal direction.
    @Override
    public void tick() {
        super.tick();     
                
        if (deathTime == 20) {    // Remove after 20 ticks.
            remove();
        } else if ((crushed) && !removed)   // increase tick when attacked but not removed
            deathTime++;
        else if (isShot) {
            dy = (int) ds;
            if (dy < 4)
                ds += 0.5;     

            x += dx;
            y += dy;
            
            // Update visibility on the screen.
            updateVisibility();     
            
        } else {  // unaffected and moves               
            // When punched from below, die.            
            if (grounded && isPunchedOnBottom) {     
                initY = y;
                jumping = true;
                setShot();                
            }
            
            /* Update y position. */
            if (jumping) {
                dy = (int) ds;
                ds += 0.5;
                if (y + dy >= initY)
                    jumping = false;
            } else if (!grounded)
                dy++;            
            else
                dy = ySpeed; // By default, there is gravity.
            
            /* Update x position. */
            // Moves in x direction every other tick. This is to slow down alien.
            if (counter % 2 == 0) 
                dx = curDx;
            else  {
                dx = 0;
            }
            counter++;      
    
            // Adjust dy when facing a ground tile.
            if (dy > 0  && y + height < Commons.GROUND && willBeGrounded()) {
                int yt1 = y + dy + ES;
                int backoff = yt1 - (yt1 >> 4) * 16;
                if (backoff > 1)
                    dy -= backoff;
            }            
            
            /* Update y position. */
            boolean stopped = !move(dx, dy);     // Updates x and y.
                        
            if (stopped && dx != 0) {   // Has met a wall
                dx = - dx;
                curDx = dx;                    
            }
            
            // Update visibility on the screen.
            updateVisibility();
        }        
    }  
    
    /** What happens when the player touches an entity.
     * @param sprite The sprite that this sprite is touched by. */    
    @Override
    protected void touchedBy(Sprite sprite) {
        if (sprite instanceof Player) { // if the entity touches the player
            boolean isOverTop = sprite.y + sprite.height <= y;
            boolean willCrossTop = sprite.y + sprite.height + sprite.dy >= y;
            boolean isXInRange = sprite.x + sprite.width > x && x + width > sprite.x;
            if (isOverTop && willCrossTop && isXInRange && !crushed) { // player jumps onto the enemy
                xS += 2;
                crushed = true;
                dx = 0;
                ((Player)sprite).addScore(score); // gives the player 100 points of score
                level.add(new ScoreString(x, y - height, score, level));
                ((Player) sprite).setCrushedAlien(true);
            } else if (!crushed && !isShot) {   // regular encounter
                if (((Player) sprite).isImortal()) { // when player is immortal (ate starman)
                    setShot();
                } else {    
                    sprite.hurt(1); // hurts the player, damage is based on lvl.
                    sprite.touchedBy(this);
                }
            }            
        }
        
        if (sprite instanceof Alien) {
            if (!((Alien) sprite).isCrushed()) {
                if (dx != 0) {
                    dx = -dx;
                    curDx = dx; 
                    sprite.dx = -sprite.dx;
                    ((Alien) sprite).curDx = sprite.dx;
                }          
            }
        }
    }

    @Override
    public void render(Screen screen) {        
        // Becomes 1 every other square (8 pixels)
        int flip1 = (walkDist >> 2) & 1; // This will either be a 1 or a 0 depending on the walk distance (Used for walking effect by mirroring the sprite)
                       
        int sw = screen.getSheet().width;   // width of sprite sheet (256)
        int colNum = sw / PPS;    // Number of squares in a row (32)    
//        screen.render(x, y, xS + yS * colNum, flip1); // draws the top-left tile
        
        if (isVisible()) {
            if (crushed) {  // crushed into half the height
                screen.render(x, y + PPS, xS + yS * colNum, 0); // render the top-left part of the sprite         
                screen.render(x + PPS, y + PPS, (xS + 1) + yS * colNum, 0);  // render the top-right part of the sprite
            } else if (isShot) {    // Upside down
                flip1 = 0;
                screen.render(x + PPS * flip1, y + PPS, xS + yS * colNum, 2); // render the top-left part of the sprite         
                screen.render(x - PPS * flip1 + PPS, y + PPS, (xS + 1) + yS * colNum, 2);  // render the top-right part of the sprite
                screen.render(x + PPS * flip1, y, xS + (yS + 1) * colNum, 2); // render the bottom-left part of the sprite
                screen.render(x - PPS * flip1 + PPS, y, xS + 1 + (yS + 1) * colNum, 2); // render the bottom-right part of the sprite        
            } else { // Normal state
                screen.render(x + PPS * flip1, y, xS + yS * colNum, flip1); // render the top-left part of the sprite         
                screen.render(x - PPS * flip1 + PPS, y, (xS + 1) + yS * colNum, flip1);  // render the top-right part of the sprite
                screen.render(x + PPS * flip1, y + PPS, xS + (yS + 1) * colNum, flip1); // render the bottom-left part of the sprite
                screen.render(x - PPS * flip1 + PPS, y + PPS, xS + 1 + (yS + 1) * colNum, flip1); // render the bottom-right part of the sprite        
            }
        }               
    }    
    
    public void activate() {
        activated = true;
    }
    
    public boolean isActivated() {
        return activated;
    }
    
    @Override
    public boolean blocks(Sprite e) {
//        if (crushed) return true;
//        else 
            return false;
    }   
    
    public boolean isCrushed() {
        return crushed;
    }
    
    public boolean isShot() {
        return isShot;
    }
    
    public void setShot() {
        isShot = true;
        dx = 0;
        ds = -3;
        level.player.addScore(score);
        level.add(new ScoreString(x, y - height, score, level));
    }
    
    private void updateVisibility() {
        // Update visibility on the screen.
        int offset = level.getOffset();
        if (x <= 0)
            remove();   //hurt(health);     
        else if (x+width <= offset && offset + Commons.BOARD_WIDTH <= x)
            setVisible(false);   
        else
            setVisible(true);   

        if (y > Commons.BOARD_HEIGHT)
            remove();              
    }
}
