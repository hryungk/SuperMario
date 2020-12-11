package main.java.com.demo.entity;

import java.util.List;
import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;
import main.java.com.demo.level.tile.InteractiveTile;
import main.java.com.demo.level.tile.Tile;

/** Represents a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public abstract class Sprite extends Entity {

    public Level level; // the level that the entity is on
    public int maxHealth = 2; // The maximum amount of health the mob can have
    public int health; // The amount of health we currently have, and set it to the maximum we can have
    private boolean visible;
    private boolean dying;
    protected final int ES = Commons.ENTITY_SIZE; // Default entity size (16 px)
    protected final int PPS = Commons.PPS;  // Pixels per square (8)
    protected final int MAX_JUMP = Commons.BOARD_HEIGHT - 2 * ES - Commons.Y96; // (64)
    protected int numS = 256 / PPS;    // number of squres in a row in the sprite sheet (32)
    protected int dx, dy;
    protected int walkDist = 0; // How far we've walked currently, incremented after each movement
    protected int dir = 0; // The direction the mob is facing, used in attacking and rendering. 0 is down, 1 is up, 2 is left, 3 is right   
    protected boolean grounded, topped; // Whether the sprite is on to a solid tile/touched a solid tile on the head.
    protected int ground;    
    protected int wS, hS; // width and height of tile [squares]    
    protected int xSpeed, ySpeed;
    protected int lives;
    protected int unit;
    protected double aTile;
    
    protected int score;    // score the player get when interact with the hidden sprite.
        
    protected int bCounter, bNum, scale, numStage, ay;    // for color change animation
    
    protected boolean isPunchedOnBottom;

// The constructor initiates the x and y coordinates and the visible variable.
    public Sprite(Level level) {
        super();
        this.level = level;  
        initSprite();
    }    
    private void initSprite() {
        visible = true;
        dx = 0;
        dy = 1;
        grounded = true;
        topped = false;
        initHealth();
        lives = 1;   
        
        ySpeed = 1; // By default, sprites are under gravity
        
        scale = 8;  // Higher the number, slower the transition.
        numStage = 4;   // Number of color schemes
        ay = 2; // higher the number, slower the y movement when sprung out of the block
        isPunchedOnBottom = false;
    }
    
    /** Update method, (Look in the specific entity's class) */
    public void tick() {
        
        if (isDying())
            die();   // Set visible false.
        
        if (health <= 0) {// Check if health is at a death-causing level (less than or equal to 0)
            lives--;      
            if (this instanceof Player && lives > 0) {
                Player p = (Player) this;
                p.resetGame();
                initHealth();   
            }
        }
        
        if (lives <= 0)  // If no more lives left, die.
            remove(); 
//        else
//            initHealth();   
    }    

    /** Draws the sprite on the screen
     * @param screen The screen to be displayed on. */
    public abstract void render(Screen screen);
    
    
    /** if this entity is touched by another entity (extended by sub-classes)
     * @param sprite The sprite that this sprite is touched by. */
    protected abstract void touchedBy(Sprite sprite);
        
    public void die() { // Kill the mob, called when health drops to 0
        setVisible(false);
//        remove();// Remove the mob, with the method inherited from Entity
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    protected void setVisible(Boolean visible) {
        this.visible = visible;
    }        
    
    public void setDx(int dx) {
        this.dx = dx;
    } 
    
    public int getDx() {
        return dx;
    }
    
    public void setDying (boolean dying) {
        this.dying = dying;
    }
    
    public boolean isDying() {
        return dying;
    }
    
    /** Moves an entity with horizontal acceleration, and vertical acceleration
     * @param dx The increment in x direction [pixel]
     * @param dy The increment in y direction [pixel]
     * @return True if this sprite can move to (x+dx, y + dy).  */
    public boolean move(int dx, int dy) {
        
        if (dx != 0 || dy != 0) { // If the horizontal acceleration OR vertical acceleration does NOT equal 0 then...
            
            
            if (dx < 0) dir = 2; // Set the mob's direction based on movement: left
            if (dx > 0) dir = 3; // right
//            if (dir == 2) dx = -Math.abs(dx);
//            if (dir == 3) dx = Math.abs(dx);
            
            boolean stopped = true; // stopped value, used for checking if the entity has stopped.
//            if (dx != 0 && move2(dx, 0)) stopped = false; // If the horizontal acceleration and the movement was successful then stopped equals false.
            if (dx != 0) 
                if (move2(dx, 0))
                    stopped = false;
            if (dy != 0) {
                if (move2(0, dy))
                    stopped = false; // If the vertical acceleration and the movement was successful then stopped equals false.
                else if (grounded) {
                    ground = y + height;
                }
                
            } 
            if (!stopped && y < Commons.BOARD_HEIGHT) { // if the sprite is able to move then...
                int xTile = x >> unit; // the x tile coordinate that the entity is standing on.
                int yTile = y >> unit; // the y tile coordinate that the entity is standing on.
                level.getTile(xTile, yTile, unit).steppedOn(this); // Calls the steppedOn() method in a tile's class. (like sand or lava)
                if (dx != 0) walkDist++; // Increment our walking/movement counter
            }
            return !stopped; // returns the opposite of stopped
        }
        return true; // returns true
    }
    /** Second part to the move method (moves in one direction at a time)
     * @param dx The increment in x direction [pixel]
     * @param dy The increment in y direction [pixel]
     * @return True if this sprite can move to (x+dx, y + dy). */
    protected boolean move2(int dx, int dy) {
        /* If the x acceleration and y acceleration are BOTH NOT 0, then throw an error */
        if (dx != 0 && dy != 0) throw new IllegalArgumentException("Move2 can only move along one axis at a time!");

        /* Note: I was tired when typing this part, please excuse grammar quirks in the writing. (Or just re-write it to make it more sensible, lol) */            
//        int xto0 = (x - Math.min(ES, width)) >> unit; // gets the tile coordinate of the position to the left of the sprite
//        int yto0 = (y - Math.min(ES, height)) >> unit; // gets the tile coordinate of the position to the top of the sprite
//        int xto1 = (x + width) >> unit; // gets the tile coordinate of the position to the right of the sprite
//        int yto1 = (y + height) >> unit; // gets the tile coordinate of the position to the bottom of the sprite
        double xto0 = (x - Math.min(ES, width)) / Math.pow(2,unit); // gets the tile coordinate of the position to the left of the sprite
        double yto0 = (y - Math.min(ES, height)) / Math.pow(2,unit); // gets the tile coordinate of the position to the top of the sprite
        double xto1 = (x + width) / Math.pow(2,unit); // gets the tile coordinate of the position to the right of the sprite
        double yto1 = (y + height) / Math.pow(2,unit); // gets the tile coordinate of the position to the bottom of the sprite
        double xto = x / Math.pow(2,unit);

//        int xt0 = ((x + dx) - width) >> unit; // gets the tile coordinate of the position to the left of the sprite + the horizontal acceleration
//        int yt0 = ((y + dy) - height) >> unit; // gets the tile coordinate of the position to the top of the sprite + the vertical acceleration
//        int xt1 = ((x + dx) + width) >> unit; // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration
//        int yt1 = ((y + dy) + height) >> unit; // gets the tile coordinate of the position to the bottom of the sprite + the vertical acceleration
        double xt0 = ((x + dx) - Math.min(ES, width)) / Math.pow(2,unit); // gets the tile coordinate of the position to the left of the sprite + the horizontal acceleration
        double yt0 = ((y + dy) - Math.min(ES, height)) / Math.pow(2,unit); // gets the tile coordinate of the position to the top of the sprite + the vertical acceleration
        double xt1 = ((x + dx) + width) / Math.pow(2,unit); // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration
        double yt1 = ((y + dy) + height) / Math.pow(2,unit); // gets the tile coordinate of the position to the bottom of the sprite + the vertical acceleration
        double xt = (x + dx) / Math.pow(2,unit);
        double yt = (y + dy) / Math.pow(2,unit);
        
        /* Check grounded. */        
        if (x <= 0)  // When going beyond the left most point of the map
            grounded = !(level.getTile(xto1-aTile, yt1, unit).mayPass()) ||
                ((x % ES != 0) && !(level.getTile(xto1, yt1, unit).mayPass()));       
        else if (x + width >= level.W * ES) // When going beyond the right of the screen
            grounded = !(level.getTile(xto1-aTile, yt1, unit).mayPass());
        else if (level.W * ES - ES <= x + width && x + width < level.W * ES) // When less than one tile close to the right of the screen
            grounded = !(level.getTile(xto1, yt1, unit).mayPass());
        else if (y + height > Commons.GROUND) // when falling beyond the ground
            grounded = false;
        else {
//            grounded = !(level.getTile(xt1-aTile, yt1, unit).mayPass()) ||
//                ((x % ES != 0) && !(level.getTile(xt1, yt1, unit).mayPass()));    
            boolean[] tempBool = new boolean[2];
//            boolean[] tempBool2 = new boolean[2];
            int nn = 0;
            for (double xx = xt1; xx > xt; xx -= aTile){
                Tile t1 = level.getTile(xx-aTile, yt1, unit);
                Tile t2 = level.getTile(xx, yt1, unit);
                boolean temp = !(t1.mayPass()) || ((x % ES != 0) && !(t2.mayPass()));    
                tempBool[nn] = temp;
                
//                // Find whether the ground is punched
//                boolean temp2 = ((t1 instanceof InteractiveTile) && ((InteractiveTile)t1).isHitBottom()) ||
//                                ((t2 instanceof InteractiveTile) && ((InteractiveTile)t2).isHitBottom());
//                tempBool2[nn] = temp && temp2;
                
                nn++;
            }
            boolean finalBool = false;
//            boolean finalBool2 = false;
            for (int ii = 0; ii < nn; ii++) {
                finalBool = finalBool || tempBool[ii];
//                finalBool2 = finalBool2 || tempBool2[ii];
            }
            grounded = finalBool;
//            isPunchedOnBottom = finalBool2;
        }
        
        // Find whether the ground is punched
        if (grounded) {  
            Tile t1 = level.getTile(xt1-aTile, yt1, unit);
            Tile t2 = level.getTile(xt1, yt1, unit);
            boolean temp1 = !(t1.mayPass()) && ((t1 instanceof InteractiveTile) && !this.equals(((InteractiveTile)t1).getHiddenSprite()) && ((InteractiveTile)t1).getInitY() != ((InteractiveTile)t1).getY());
            boolean temp2 = ((x % ES != 0) && !(t2.mayPass())) && ((t2 instanceof InteractiveTile) && !this.equals(((InteractiveTile)t2).getHiddenSprite()) && ((InteractiveTile)t2).getInitY() != ((InteractiveTile)t2).getY());
            isPunchedOnBottom = temp1 || temp2;            
            
            if (dx != 0) {
                if (temp1) {
                    dir = 3;
                    this.dx = Math.abs(dx);
                } else if (temp2) {
                    dir = 2;
                    this.dx = -Math.abs(dx);
                }        
            }        
        }
                
        // if the increment is negative, round up the tile coordinates.
        if (dy < 0) {            
            yto0 = (Math.ceil((y - Math.min(ES, height)) / (double)height));
            yto1 = (Math.ceil((y + height) / (double)height));
            yt0 = (Math.ceil((y - Math.min(ES, height) + dy) / (double)height));
            yt1 = (Math.ceil((y + height + dy) / (double)height));
        }
        
        // When right at the grid, only need to check the neighboring tile
        // When in between, check both tiles that the player spans.
//        topped = ((x + dx) % ES != 0) && !(level.getTile(xt1, yt0).mayPass(xt1, yt0, this));      
//        if (x > level.getOffset())
//            topped = !(level.getTile(xt1-1, yt0).mayPass(xt1-1, yt0, this)) || topped;          
//        grounded = !(level.getTile(xt1-1, yt1).mayPass(xt1-1, yt1, this)) ||
//                (((x + dx) % ES != 0) && !(level.getTile(xt1, yt1).mayPass(xt1, yt1, this)));  

//        grounded = !(level.getTile(xto1-1, yto1).mayPass(xto1-1, yto1, this)) ||
//                ((x % ES != 0) && !(level.getTile(xto1, yto1).mayPass(xto1, yto1, this))); 
        

        /* Check right stopped. */
        boolean rightStopped;
        if (x + width >= level.W * ES) // When going beyond the right of the screen
            rightStopped = true; 
        else if (level.W * ES - ES <= x + width && x + width < level.W * ES) // When in between one tile to the right of the screen
            rightStopped = false;
        else if (y + dy > Commons.BOARD_HEIGHT) // When going beyond the bottom most point of the map
            rightStopped = false;
        else if (y + height > Commons.GROUND) { // when falling beyond the ground, the tile at the most upper-right determins the right stop.
//            int ytFall = Commons.GROUND >> unit;
                double ytFall = Commons.GROUND / Math.pow(2,unit);
//            rightStopped = !(level.getTile(xt1, yt1-1, unit).mayPass(xt1, yt1-1, this));
            rightStopped = !(level.getTile(xt1, ytFall, unit).mayPass());
        } else
            rightStopped = !(level.getTile(xt1, yt1-aTile, unit).mayPass()) || 
                (((y + dy) % ES != 0) && !(level.getTile(xt1, yt1, unit).mayPass()));  
                
        
        /* Check topped. */
        double yt_raw = (y - Math.min(ES, height) + dy) / (double)height;
        double yt_ceil = Math.ceil(yt_raw);
        double yt0T = yt_ceil;
        if (unit == 5 && yt_ceil - yt_raw >= 0.5)   // When big Pusheen, 0.5 is the unit tile
            yt0T = yt_ceil - 0.5;
//        double yt0T = (Math.ceil((y - Math.min(ES, height) + dy) / (double)height));
        Tile tile = null; // Need to check whether it is a interactive tile
        if (y <= 0)  // When going beyond the top most point of the map
            topped = true; 
        else if (0 < y && y <= ES)  // When in between one tile to the top of the map
            topped = false; 
        else if (y + dy > Commons.BOARD_HEIGHT)  // When going beyond the bottom most point of the map
            topped = false;
        else if (x < ES) {  // When going beyond the left most point + unit tile of the map
            tile = level.getTile(xt1, yt0T, unit);
            topped = !(tile.mayPass()); 
        } else if (x + width >= level.W * ES) { // When going beyond the right of the screen
            tile = level.getTile(xt1-aTile, yt0T, unit);
            topped = !(tile.mayPass());
        } else if (level.W * ES - ES <= x + width && x + width < level.W * ES) { // When in between one tile to the right of the screen
            tile = level.getTile(xto1, yt0T, unit);
            topped = !(tile.mayPass());
        } else {             
//            Tile tile1 = level.getTile(xt1-aTile, yt0T, unit);
//            Tile tile2 = level.getTile(xt1, yt0T, unit);
//            boolean topped1 = !(tile1.mayPass()); // top-left or top
//            boolean topped2 = ((x + dx) % ES != 0) && !(tile2.mayPass()); // top-right            
//            
//            if (topped1 && topped2) {   // When both top-left and top-right are blocking
//                double xt1T = Math.round((x + dx) / (double)width);  // choose the tile closer to the player.
//                tile = level.getTile(xt1T, yt0T, unit);
//            }
//            else if (topped1) tile = tile1;
//            else if (topped2) tile = tile2;
//            
//            topped = topped1 || topped2;                

            boolean[] tempBool = new boolean[2];
            int nn = 0;
            for (double xx = xt1; xx > xt; xx -= aTile){
                Tile tile1 = level.getTile(xx-aTile, yt0T, unit);
                Tile tile2 = level.getTile(xx, yt0T, unit);
                boolean topped1 = !(tile1.mayPass()); // top-left or top
                boolean topped2 = ((x + dx) % ES != 0) && !(tile2.mayPass()); // top-right                     
                boolean temp = topped1 || topped2;    
                tempBool[nn] = temp;
                nn++;
                
                if (topped1 && topped2) {   // When both top-left and top-right are blocking
                    double xt1T = Math.round((x + dx) / (double)width);  // choose the tile closer to the player.
                    tile = level.getTile(xt1T, yt0T, unit);
                }
                else if (topped1) tile = tile1;
                else if (topped2) tile = tile2;
            }
            boolean finalBool = false;
            for (int ii = 0; ii < nn; ii++)
                finalBool = finalBool || tempBool[ii];
            topped = finalBool;
        }
        
        if (tile instanceof InteractiveTile && this instanceof Player) {
            InteractiveTile t = (InteractiveTile) tile;
            if (topped && dy < 0) {
                if (!t.isHitBottom()) {
                    HiddenSprite hs = level.removeHiddenSprite(t.getX(), t.getY());
                    if (hs != null) {
                        hs.activate();
                        t.setHiddenSprite(hs);
                    }
                }
                t.hurt();                                
            }
        }
                
        if (dx < 0) {            
            xto0 = Math.ceil((x - Math.min(ES, width)) / (double)width);
            xto1 = Math.ceil((x + width)/ (double)width);
            xt0 = Math.ceil((x - Math.min(ES, width) + dx) / (double)width);
            xt1 = Math.ceil((x + width + dx) / (double)width);
        }
//        if (y <= 0) topped = true;
//        else if (0 < y && y <= ES) topped = false;
//        else topped = !(level.getTile(xt1-1, yt0).mayPass(xt1-1, yt0, this)) ||
//                (((x + dx) % ES != 0) && !(level.getTile(xt1, yt0).mayPass(xt1, yt0, this)));    
        
//        boolean leftStopped = !(level.getTile(xt0, yt1-1).mayPass(xt0, yt1-1, this)) || 
//                (((y + dy) % ES != 0) && !(level.getTile(xt0, yt1).mayPass(xt0, yt1, this)));        
        

//        boolean leftStopped;
//        if (x <= level.getOffset())
//            leftStopped = true;
//        else if (level.getOffset() < x && x <= level.getOffset() + ES)
//            leftStopped = false;
//        else
//            leftStopped = !(level.getTile(xt0, yt1-1).mayPass(xt0, yt1-1, this)) || 
//                (((y + dy) % ES != 0) && !(level.getTile(xt0, yt1).mayPass(xt0, yt1, this)));        
        

        /* Check left stopped. */
        boolean leftStopped = false;  
        if (x <= 0) // When going beyond the left most point of the map
            leftStopped = true;
        else if (0 < x && x <= 0 + ES)  // When in between one tile to the left of the map
            leftStopped = false;
        else if (y + dy > Commons.BOARD_HEIGHT)  // When going beyond the bottom most point of the map
            leftStopped = false;
        else if (y + height > Commons.GROUND) // when falling beyond the ground
//            leftStopped = !(level.getTile(xt0, yt1-aTile, unit).mayPass());        
            leftStopped = !(level.getTile(xt0, yt, unit).mayPass());        
        else
            leftStopped = !(level.getTile(xt0, yt1-aTile, unit).mayPass()) || 
            (((y + dy) % ES != 0) && !(level.getTile(xt0, yt1, unit).mayPass()));              
        
        
        if (grounded && dy > 0) {
            int yground = (((y + dy) + height) >> 4) * ES;
//            int yground = (int)((y + height) / Math.pow(2,unit) * height + ((dy >> 4) * ES));
            dy = yground - (y + height);
            y += dy;            
            this.dy = 1;
            return false;
        }        
        if (topped && dy < 0) return false;
        if (rightStopped && dx > 0) return false;
        if (leftStopped && dx < 0) return false;            

//        List<Sprite> wasInside = level.getEntities(x - ES, y - ES, x + ES, y + ES); // gets all of the entities that are inside this entity (aka: colliding)
//        List<Sprite> isInside = level.getEntities(x - ES + dx, y - ES + dy, x + ES + dx, y + ES + dy); // gets the entities that this entity will touch.
        List<Sprite> wasInside = level.getEntities(x, y, x + width, y + height); // gets all of the entities that are inside this entity (aka: colliding)
        List<Sprite> isInside = level.getEntities(x + dx, y + dy, x + width + dx, y + height + dy); // gets the entities that this entity will touch.
        for (int i = 0; i < isInside.size(); i++) { // loops through isInside list
            Sprite e = isInside.get(i); // current entity in the list
            if (e == this) continue; // if the entity happens to be this one that is calling this method, then skip to the next entity.
            e.touchedBy(this); // calls the touchedBy(entity) method in that entity's class
        }
        isInside.removeAll(wasInside); // removes all the entities that are in the wasInside from the isInside list.
        for (int i = 0; i < isInside.size(); i++) { // loops through isInside list
            Sprite e = isInside.get(i); // current entity in the list
            if (e == this) continue; // if the entity happens to be this one that is calling this method, then skip to the next entity.            
            if (e.blocks(this)) {
                if (!(!grounded && dy > 0)){   
                    return false;
                }
                else {// When falling
                    dy = e.y - (y + height);
                    this.dy = dy;
//                    ground = y + dy + height;
//                    grounded = true;
                }
            } // if the entity can block this entity then... return false.            
        }

        x += dx; // moves horizontally based on the x acceleration
        y += dy; // moves vertically based on the y acceleration
        return true; // return true
    }
    
    public boolean blocks(Sprite e) {
        return false;
    }    
    
    protected boolean isGrounded() {        
        
//        int xto1 = (x + width) >> unit; // gets the tile coordinate of the position to the right of the sprite
//        int yto1 = (y + height) >> unit; // gets the tile coordinate of the position to the bottom of the sprite
        double xto1 = (x + width) / Math.pow(2,unit); // gets the tile coordinate of the position to the right of the sprite
        double yto1 = (y + height) / Math.pow(2,unit); // gets the tile coordinate of the position to the bottom of the sprite
        
        return !(level.getTile(xto1-aTile, yto1, unit).mayPass()) ||
                ((x % ES != 0) && !(level.getTile(xto1, yto1, unit).mayPass()));         
    }
    protected boolean willBeGrounded() {        
        
//        int xt1 = ((x + dx) + width) >> unit; // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration
//        int yt1 = ((y + dy) + width) >> unit; // gets the tile coordinate of the position to the bottom of the sprite + the vertical acceleration
        double xt1 = ((x + dx) + width) / Math.pow(2,unit); // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration        
        double yt1 = ((y + dy) + width) / Math.pow(2,unit); // gets the tile coordinate of the position to the bottom of the sprite + the vertical acceleration
                
        // if the increment is negative, round up the tile coordinates.
        if (dy < 0) {            
            yt1 = (Math.ceil((y + width + dy) / (double)width));
        }
        
        
        if (x <= level.getOffset()) // When on the left end, don't check xt1-1
            return !(level.getTile(xt1, yt1, unit).mayPass());     
//            return ((x + dx) % ES != 0) && !(level.getTile(xt1, yt1).mayPass(xt1, yt1, this));  
        else if (x + dx + width >= level.W * ES) 
            return !(level.getTile(xt1-aTile, yt1, unit).mayPass());
        else if (level.W * ES - ES <= x + dx + width && x + dx + width < level.W * ES) 
            return !(level.getTile(xt1, yt1, unit).mayPass());
        else
            return !(level.getTile(xt1-aTile, yt1, unit).mayPass()) ||
                (((x + dx) % ES != 0) && !(level.getTile(xt1, yt1, unit).mayPass()));     
    }
    
    protected boolean checkTopped() {        
        
//        int yt0;// = ((y + dy) - ES) >> unit; // gets the tile coordinate of the position to the top of the sprite + the vertical acceleration
//        int xt1 = ((x + dx) + ES) >> unit; // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration
//                   
//        yt0 = (int) (Math.ceil((y - ES + dy) / (double)width));
//        
//        // When right at the grid, only need to check the neighboring tile
//        // When in between, check both tiles that the player spans.
//        if (x <= level.getOffset()) // When on the left end, don't check xt1-1
//            return ((x + dx) % ES != 0) && !(level.getTile(xt1, yt0).mayPass(xt1, yt0, this));
//        else
//            return !(level.getTile(xt1-1, yt0).mayPass(xt1-1, yt0, this)) || 
//                    (((x + dx) % ES != 0) && !(level.getTile(xt1, yt0).mayPass(xt1, yt0, this)));   
        

//        int yt0 = ((y + dy) - height) >> unit; // gets the tile coordinate of the position to the top of the sprite + the vertical acceleration
//        int xt1 = ((x + dx) + width) >> unit; // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration
        double yt0 = ((y + dy) - Math.min(ES, height)) / Math.pow(2,unit); // gets the tile coordinate of the position to the top of the sprite + the vertical acceleration
        double xt1 = ((x + dx) + width) / Math.pow(2,unit); // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration
                
        // if the increment is negative, round up the tile coordinates.
//        if (dy < 0) {            
            yt0 =  (Math.ceil((y - Math.min(ES, height) + dy) / (double)width));
//        }        
        // When right at the grid, only need to check the neighboring tile
        // When in between, check both tiles that the player spans.        
        if (dx < 0) {            
            xt1 = Math.ceil((x + width + dx) / (double)width);
        }
        
        if (y <= 0) return true;
        else if (0 < y && y <= ES) return false;
        else return !(level.getTile(xt1-aTile, yt0, unit).mayPass()) ||
                (((x + dx) % ES != 0) && !(level.getTile(xt1, yt0, unit).mayPass())); 
    }
    
    public void hurt(int damage) { // mob hurts this sprite
        health -= damage; // Actually change our health
    }
    
    private void initHealth() {
        health = 1;
    }
}
