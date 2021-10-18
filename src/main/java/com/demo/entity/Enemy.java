package main.java.com.demo.entity;

import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;

/**
 * Represents an enemy as a sprite.
 *
 * @author HRK
 */
public class Enemy extends Sprite {
    
    private int counter;    // Tick counter. This is to slow down enemy.
    private int deathTime;  // Counts ticks after death
    private int curDx, initY;    
    private boolean activated, crushed, isShot, jumping;    
    
    public Enemy(int x, int y, Level level) {                
        super(level);
        initEnemy(x, y);        
    }    
    
    private void initEnemy(int x, int y) {
        // Initialize variables from Entity class.
        setX(x);
        setY(y); 
        width = height = ES;
        xS = 12;
        yS = 0;
        
        // Initialize variables from Sprite class.
        xSpeed = 1; 
        dx = -xSpeed;
        dy = ySpeed;
        ds = 1;
        
        ground = y + height;
        wS = width / PPS;
        hS = height / PPS;
        score = 100;     
        unit = (int) (Math.log10(width)/Math.log10(2)); 
        aTile = Math.min(Math.pow(2, 4 - unit), 1); 
        
        // Initialize variables for this class.
        counter = deathTime = 0;
        curDx = dx; 
        initY = y;
        activated = crushed = isShot = jumping = false;        
    }           
    
    /** 
     * What happens when the enemy touches a sprite.
     * 
     * @param sprite The sprite that this sprite is touched by. 
     */    
    @Override
    protected void touchedBy(Sprite sprite) {
        if (sprite instanceof Player && !sprite.isDying()) { // if this enemy touches the player
            Player p = (Player) sprite;            
            boolean isOverTop = p.y + p.height <= y;
            boolean willCrossTop = p.y + p.height + p.dy >= y;
            boolean isXInRange = p.x + p.width > x && x + width > p.x;
            if (isOverTop && willCrossTop && isXInRange && !crushed) { // player jumps onto the enemy
                xS += 2;
                crushed = true;
                dx = 0;
                p.addScore(score); // gives the player 100 points of score
                level.add(new ScoreString(x, y - height, score, level));
                p.setCrushedEnemy(true);
            } else if (!crushed && !isShot) {   // regular encounter
                if (p.isImmortal()) { // when player is immortal (ate starman)
                    setShot();
                } else {    
                    sprite.hurt(1); // hurts the player, damage is based on lvl.
//                    p.touchedBy(this);                    
                    if (p.isEnlarged()) {                        
                        p.width /= 2;
                        p.height /= 2;
                        p.wS = p.width / PPS;
                        p.hS = p.height / PPS;
                        p.unit = (int) (Math.log10(p.width)/Math.log10(2)); // the size of block to be used (5 for 32 px, 4 for 16 px sprite, and 3 for 8px sprite)
                        p.aTile = Math.min(Math.pow(2, 4 - p.unit), 1); // 1 for unit 3, 1 for unit 4, 0.5 for unit 5 (big Pusheen)
                        p.yS -= 4;
                        p.setEnlarged(false);
                        level.flower2Mushroom();    // change flowers back to mushrooms.
                        if (p.isFired()) {
                            p.yS -= 8;
                            p.setFired(false);
                        }
                    } 
                }
            }
        }
        
        if (sprite instanceof Enemy) {
            if (!((Enemy) sprite).isCrushed()) {
                if (dx != 0) {
                    dx = -dx;
                    curDx = dx; 
                    sprite.dx = -sprite.dx;
                    ((Enemy) sprite).curDx = sprite.dx;
                }          
            }
        }
    }
    
    /**
     * Update method.
     */
    @Override
    public void tick() {
        super.tick();     
        
        // If it was crushed by the Player
        if (crushed) { 
            if (deathTime == 20)    // When death time is over
                hurt(health);       // Remove.
            else
                deathTime++;        // Increment death timer.            
        } // If it was shot by the Shot 
        else if (isShot) {
            dy = (int) ds;
            if (dy < 4)             // Accelerate until reaching g-force of 4.
                ds += 0.5;     

            x += dx;                // Keep moving.
            y += dy;            
            
        }  // Otherwise, move normally.
        else {
            // When punched from below, die.            
            if (grounded && isPunchedOnBottom) {     
                initY = y;
                jumping = true;
                setShot();                
            }
            
            // Update dy. 
            if (jumping) {           // While jumping from punched on the bottom
                dy = (int) ds;
                ds += 0.5;              // Accelerate vertical speed.
                if (y + dy >= initY)    // When falls back to original position
                    jumping = false;    // Jumping state is over.
            } else if (!grounded)   // While falling
                dy++;                   // Accelerate faster 
            else                    // Otherwise, grounded
                dy = ySpeed;            // By default, there is gravity.
            
            // Update dx. 
            // To slow down enemy, move every other tick.
            if (counter % 2 == 0)   // In every other tick
                dx = curDx;         // dx has a value.
            else  {
                dx = 0;
            }
            counter++;              // Increment tick counter.
    
            // If falling on the ground in the next tick
            if (dy > 0  && y + height < Commons.GROUND && willBeGrounded()) {
                int yt1 = y + dy + ES;
                int backoff = yt1 - (yt1 >> 4) * 16;
                if (backoff > 1)
                    dy -= backoff;  // Adjust dy so that it doesn't go over
            }                       // the ground.
            
            // Make a movement.
            boolean stopped = !move(dx, dy);     // Updates x and y.
                        
            if (stopped && dx != 0) {   // Has met a wall
                dx = - dx;      // Change direction to the opposite. 
                curDx = dx;     // Update currnet dx accordingly.                
            }            
        }
        // Update visibility on the screen.
        updateVisibility();        
    }  
    
    /**
     * Draws the sprite on the screen.
     *
     * @param screen The screen to be displayed on.
     */
    @Override
    public void render(Screen screen) {     
        super.render(screen);
        
        // Animation based on walking distance.
        // ((walkDist >> 2) & 1) will either be a 1 or a 0 depending on walkDist
        // Becomes 1 every half square (4 pixels)
        int flip1 = (walkDist >> 2) & 1;
        
        if (isVisible()) {
            // If crushed into half the height
            if (crushed) {      
                screen.render(x, y + PPS, 
                        xS + yS * colNum, 0);           // Top-left 
                screen.render(x + PPS, y + PPS,
                        (xS + 1) + yS * colNum, 0);     // Top-right 
            } // If is shot (upside down)
            else if (isShot) {
                flip1 = 0;
                screen.render(x + PPS * flip1, y + PPS, 
                        xS + yS * colNum, 2);           // Top-left        
                screen.render(x - PPS * flip1 + PPS, y + PPS, 
                        (xS + 1) + yS * colNum, 2);     // Top-right 
                screen.render(x + PPS * flip1, y, 
                        xS + (yS + 1) * colNum, 2);     // Bottom-left 
                screen.render(x - PPS * flip1 + PPS, y,
                        xS + 1 + (yS + 1) * colNum, 2); // Bottom-right 
            } // If normal state 
            else {           
                screen.render(x + PPS * flip1, y, 
                        xS + yS * colNum, flip1);       // Top-left 
                screen.render(x - PPS * flip1 + PPS, y,
                        (xS + 1) + yS * colNum, flip1); // Top-right 
                screen.render(x + PPS * flip1, y + PPS, 
                        xS + (yS + 1) * colNum, flip1); // Bottom-left 
                screen.render(x - PPS * flip1 + PPS, y + PPS, 
                        xS + 1 + (yS + 1) * colNum, flip1); // Bottom-right 
            }
        }               
    }    
    
    public void activate() {
        activated = true;
    }
    
    public boolean isActivated() {
        return activated;
    }
    
//    @Override
//    public boolean blocks(Entity e) {
//        return crushed;
//    }   
    
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
        int offset = level.getOffset();
        // If going beyond the left end of the map
        if (x <= 0)
            hurt(health);       // Die.
        // If going outside of the screen
        else if (x+width <= offset || offset + Commons.BOARD_WIDTH <= x)
            setVisible(false);  // Set invisible
        // Otherwise
        else
            setVisible(true);   // Set visible.
        // If going down beyond the screen
        if (y > Commons.BOARD_HEIGHT)
            hurt(health);              
    }
}
