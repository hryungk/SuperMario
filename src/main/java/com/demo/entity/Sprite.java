package main.java.com.demo.entity;

import java.util.List;
import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;
import main.java.com.demo.level.tile.InteractiveTile;
import main.java.com.demo.level.tile.Tile;

/**
 * Represents a sprite.
 *
 * @author HRK
 */
public abstract class Sprite extends Entity {
    
    protected final int ES = Commons.ENTITY_SIZE; // Default entity size (16 px)
    protected final int PPS = Commons.PPS;        // Pixels per square (8)
    protected final int MAX_JUMP = Commons.BOARD_HEIGHT - 2 * ES - Commons.Y64;
    private final int W_MAP;                      // Width of the level's map
    
    public Level level;         // Level that the entity is on        
    protected int colNum;      // Number of squres in a row in the sprite sheet
            
    public int maxHealth = 2;   // Maximum amount of health the sprite can have
    protected int health;       // Current health
    
    protected int walkDist;     // How far the sprite has walked
    protected int bCounter, bNum, scale, numStage, ay; // For color animation   
    private boolean visible;    // True if visible in the level.
    protected boolean dying;    // True if dying.
    protected boolean grounded; // If the sprite is on to a solid tile
    protected boolean topped;   // If the sprite touches a tile on the head
    private boolean leftStopped, rightStopped; // If stopped by a tile on side.
    protected boolean isPunchedOnBottom;    // If the tile on which the sprite 
                                            // stands is punched from the bottom    
    
    // Variables below need to be adjusted when creating a new child class.
    protected int lives;    // Number of lives the sprite has
    protected int dx, dy;   // Increment of position
    protected double ds;    // Temporary dy    
    protected int dir;      // The direction the sprite is facing, 
                            // 0 is down, 1 is up, 2 is left, 3 is right   
        
    // Variables below need to be defined in children classes. 
    protected int ground;   // y posiiton of the ground [pixel]
    protected int wS, hS;   // Width and height of tile [squares]    
    protected int xSpeed, ySpeed; // Default dx and dy    
    protected int score;    // Score the player gets interacting with others              
    protected int unit;     // Size unit of sprite to be used (log2(size))
    protected double aTile; // Size of a tile, 
                            // 1 for unit 3 and 4, 0.5 for unit 5 (big Pusheen)
    
    /**
     * Constructor.
     * 
     * @param level The level of the game the sprite is currently on.
     */
    public Sprite(Level level) {
        super();
        this.level = level;
        W_MAP = level.getWidth();   
        colNum = 0;
        init();        
    }

    /**
     * Initialize variables.
     */
    private void init() {
        initSprite();
        
        scale = 8;      // Higher the number, slower the transition.
        numStage = 4;   // Number of color schemes
        ay = 2;         // Higher the number, slower the y movement when sprung 
                        // out of the block        
                
        // Variables below need to be adjusted when creating a new child class.
        lives = 1;
        dx = 0;
        dy = 1;
        ds = dy;
        dir = 2;        // Face left by default.
        ySpeed = 1;     // By default, sprites are under gravity               
    }
    
    /**
     * Initialize variables for reset.
     */
    public void initSprite() {
        initHealth();
        
        walkDist = 0;
        bCounter = bNum = 0;
        
        visible = true;
        dying = false;
        grounded = true;
        topped = isPunchedOnBottom = false;
    }
    

    /**
     * What happens when this sprite is touched by another sprite.
     *
     * @param sprite The sprite that this sprite is touched by.
     */
    protected abstract void touchedBy(Sprite sprite);
    
    /**
     * Update method, (Look in the specific entity's class)
     */
    public void tick() {
        if (health <= 0) {      // If there is no health left
            lives--;            // Reduce life by 1.
            if (this instanceof Player && lives > 0) {  // For the player,
                Player p = (Player) this;
                p.resetGame();  // Restart the game.
                initHealth();   // Initialize the health.
            }
        }

        if (lives <= 0) {       // If no more lives left
            remove();           // Die.
        }
    }

    /**
     * Draws the sprite on the screen.
     *
     * @param screen The screen to be displayed on.
     */
    public void render(Screen screen) {
        if (colNum == 0)
            colNum = screen.getSheet().width / PPS;
    }

    /**
     * Make the sprite invisible from the map.
     */
    public void die() { 
        setVisible(false);
    }

    /**
     * Checks if the sprite is visible.
     * 
     * @return True if the sprite is visible on the level.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets visibility of the sprite.
     * 
     * @param visible A boolean containing visibility of the sprite.
     */
    protected void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Sets increment in x direction.
     * 
     * @param dx An integer containing increment in x direction.
     */
    public void setDx(int dx) {
        this.dx = dx;
    }

    /**
     * Gets increment in x direction.
     * 
     * @return An integer containing increment in x direction.
     */
    public int getDx() {
        return dx;
    }

    /**
     * Sets dying status of the sprite.
     * 
     * @param dying True if the sprite is dying.
     */
    public void setDying(boolean dying) {
        this.dying = dying;
    }

    /**
     * Checks if the sprite is dying.
     * 
     * @return True if the sprite is dying.
     */
    public boolean isDying() {
        return dying;
    }

    /**
     * Moves an entity with horizontal acceleration, and vertical acceleration.
     *
     * @param dx The increment in x direction [pixel]
     * @param dy The increment in y direction [pixel]
     * @return True if this sprite can move to (x + dx, y + dy).
     */
    public boolean move(int dx, int dy) {        
        if (dx != 0 || dy != 0) {       // If there is an acceleration
            if (dx < 0) {               // If dx is negative
                dir = 2;                // Set the sprite's direction left.
            } else if (dx > 0) {        // If dx is positive
                dir = 3;                // Set the sprite's direction left.
            }

            boolean stopped = true; // Used for checking if the sprite's stopped
            // If there is x acceleration
            if (dx != 0) {              
                if (move2(dx, 0)) {     // If the sprite moves in x direction
                    stopped = false;    // It is not stopped 
                }
            }
            // If there is y acceleration
            if (dy != 0) {              
                if (move2(0, dy)) {     // If the sprite moves in y direction
                    stopped = false;    // It is not stopped. 
                } else if (grounded) {  // If not moving in y and grounded
                    ground = y + height;// Update ground to current level.
                }
            }
            // If the sprite moves && is within the vertical range of screen
            if (!stopped && y < Commons.BOARD_HEIGHT) { 
                if (dx != 0) {          // If there is x acceleration
                    walkDist++;         // Increment our walking counter.
                }
            }
            return !stopped;            // Returns the opposite of stopped.
        }
        return false;                   // If there is no acceleration, no move.
    }

    /**
     * Second part to the move method (moves in one direction at a time)
     *
     * @param dx The increment in x direction [pixel]
     * @param dy The increment in y direction [pixel]
     * @return True if this sprite can move to (x + dx, y + dy).
     */
    protected boolean move2(int dx, int dy) {
        // If the x and y acceleration are BOTH NOT 0, then throw an error.
        if (dx != 0 && dy != 0) {
            throw new IllegalArgumentException("Move2 can only move along one"
                    + " axis at a time!");
        }
        
        // Get tile coordinate of the position relative to the sprite.
//        double xto0 = (x - Math.min(ES, width)) / Math.pow(2, unit); // Left 
//        double yto0 = (y - Math.min(ES, height)) / Math.pow(2, unit); // Top 
        double xto1 = (x + width) / Math.pow(2, unit); // Right 
//        double yto1 = (y + height) / Math.pow(2, unit); // Bottom 
//        double xto = x / Math.pow(2, unit);  // Right at the sprite

        // Get tile coordinate of the position relative to the sprite + acc.
        double xt0 = ((x + dx) - Math.min(ES, width)) / Math.pow(2, unit); // L
//        double yt0 = ((y + dy) - Math.min(ES, height)) / Math.pow(2, unit);//T
        double xt1 = ((x + dx) + width) / Math.pow(2, unit); // R
        double yt1 = ((y + dy) + height) / Math.pow(2, unit); // B
        double xt = (x + dx) / Math.pow(2, unit); // Right at the sprite, x
        double yt = (y + dy) / Math.pow(2, unit); // Right at the sprite, y

        
        // Check grounded.
        // When going beyond the left end of the map
        if (x <= 0) {
            grounded = !(level.getTile(xto1 - aTile, yt1, unit).mayPass())|| 
                ((x % ES != 0) && !(level.getTile(xto1, yt1, unit).mayPass()));
         // When going beyond the right end of the screen
        } else if (x + width >= W_MAP * ES) {        
            grounded = !(level.getTile(xto1 - aTile, yt1, unit).mayPass());
        // When less than one tile close to the right end of the screen    
        } else if (W_MAP * ES - ES <= x + width && 
                x + width < level.getWidth() * ES) {         
            grounded = !(level.getTile(xto1, yt1, unit).mayPass());
        // When falling beyond the ground
        } else if (y + height > Commons.GROUND) {         
            grounded = false;
        } else {
            boolean[] tempBool = new boolean[2];
            int nn = 0;
            for (double xx = xt1; xx > xt; xx -= aTile) {
                Tile t1 = level.getTile(xx - aTile, yt1, unit);
                Tile t2 = level.getTile(xx, yt1, unit);
                boolean temp = !(t1.mayPass()) || 
                        ((x % ES != 0) && !(t2.mayPass()));
                tempBool[nn] = temp;
                nn++;
            }
            boolean finalBool = false;
            for (int ii = 0; ii < nn; ii++) {
                finalBool = finalBool || tempBool[ii];
            }
            grounded = finalBool;
        }

        // Find whether the ground is punched.
        if (grounded) {
            Tile tL = level.getTile(xt1 - aTile, yt1, unit);    // Left tile
            Tile tR = level.getTile(xt1, yt1, unit);            // Right tile
            boolean temp1 = false;
            if (tL instanceof InteractiveTile) {
                InteractiveTile tL1 = (InteractiveTile) tL;
                temp1 = !(tL1.mayPass()) 
                        && !this.equals(tL1.getHiddenSprite()) 
                        && (tL1.getInitY() != tL1.getY());
            }
            boolean temp2 = false;
            if (tR instanceof InteractiveTile) {
                InteractiveTile tR1 = (InteractiveTile) tR;
                temp2 = ((x % ES != 0) && !(tR1.mayPass())) 
                        && !this.equals(tR1.getHiddenSprite()) 
                        && (tR1.getInitY() != tR1.getY());
            }            
//            boolean temp1 = !(t1.mayPass()) && 
//            ((t1 instanceof InteractiveTile) && 
//            !this.equals(((InteractiveTile) t1).getHiddenSprite()) && 
//            ((InteractiveTile) t1).getInitY() != ((InteractiveTile) t1).getY());
//            boolean temp2 = ((x % ES != 0) && !(t2.mayPass())) && 
//            ((t2 instanceof InteractiveTile) && 
//            !this.equals(((InteractiveTile) t2).getHiddenSprite()) && 
//            ((InteractiveTile) t2).getInitY() != ((InteractiveTile) t2).getY());
            isPunchedOnBottom = temp1 || temp2;

            if (dx != 0) {
                if (temp1) {        // If puhcned on the bottom left
                    dir = 3;        // Face right
                    this.dx = Math.abs(dx); // Move to right
                } else if (temp2) { // If puhcned on the bottom right
                    dir = 2;        // Face left
                    this.dx = -Math.abs(dx); // Move to left
                }
            }
        }

        // If the increment is negative, round up the tile coordinates.
        if (dy < 0) {
//            yto0 = (Math.ceil((y - Math.min(ES, height)) / (double) height));
//            yto1 = (Math.ceil((y + height) / (double) height));
//            yt0 = (Math.ceil((y - Math.min(ES, height) + dy) / (double) height));
            yt1 = (Math.ceil((y + height + dy) / (double) height));
        }
        
        
        // Check right stopped. 
//        boolean rightStopped;
        // When going beyond the right end of the screen
        if (x + width >= W_MAP * ES) {
            rightStopped = true;
        // When in between one tile to the right end of the screen
        } else if (W_MAP * ES - ES <= x + width && x + width < W_MAP * ES) {
            rightStopped = false;
        // When going beyond the bottom end of the map
        } else if (y + dy > Commons.BOARD_HEIGHT) {
            rightStopped = false;
        // When falling beyond the ground
        } else if (y + height > Commons.GROUND) { 
            // The tile at the most upper-right determines the right stop.
            double ytFall = Commons.GROUND / Math.pow(2, unit);
            rightStopped = !(level.getTile(xto1, ytFall, unit).mayPass());
        } else {
            rightStopped = !(level.getTile(xto1, yt1 - aTile, unit).mayPass())|| 
                    (((y + dy) % ES != 0) && 
                    !(level.getTile(xto1, yt1, unit).mayPass()));
        }

        
        // Check topped. 
        double yt_raw = (y - Math.min(ES, height) + dy) / (double) height;
        double yt_ceil = Math.ceil(yt_raw);
        double yt0T = yt_ceil;
        // When big Pusheen, 0.5 is the unit tile.
        if (unit == 5 && yt_ceil - yt_raw >= 0.5) {
            yt0T = yt_ceil - 0.5;
        }
        Tile tile = null; // Need to check whether it is a interactive tile.
        // When going beyond the top end of the map
        if (y <= 0) {
            topped = true;
        // When in between one tile to the top of the map
        } else if (0 < y && y <= ES) {
            topped = false;
        // When going beyond the bottom end of the map
        } else if (y + dy > Commons.BOARD_HEIGHT) {
            topped = false;
        // When going beyond the left end + unit tile of the map
        } else if (x < ES) {  
            tile = level.getTile(xt1, yt0T, unit);
            topped = !(tile.mayPass());
        // When going beyond the right end of the screen
        } else if (x + width >= W_MAP * ES) { 
            tile = level.getTile(xt1 - aTile, yt0T, unit);
            topped = !(tile.mayPass());
        // When in between one tile to the right end of the screen
        } else if (W_MAP * ES - ES <= x + width && x + width < W_MAP * ES) { 
            tile = level.getTile(xto1, yt0T, unit);
            topped = !(tile.mayPass());
        } else {
            boolean[] tempBool = new boolean[2];
            int nn = 0;
            for (double xx = xt1; xx > xt; xx -= aTile) {
                Tile tL = level.getTile(xx - aTile, yt0T, unit); // Top(-left) 
                Tile tR = level.getTile(xx, yt0T, unit); // Top-right tile
                boolean toppedL = !(tL.mayPass()); 
                boolean toppedR = ((x + dx) % ES != 0) && !(tR.mayPass()); 
                tempBool[nn] = toppedL || toppedR;
                nn++;

                // When both top-left and top-right are blocking
                if (toppedL && toppedR) {   
                    // Choose the tile closer to the player.
                    double xt1T = Math.round((x + dx) / (double) width);  
                    tile = level.getTile(xt1T, yt0T, unit);
                } else if (toppedL) {
                    tile = tL;
                } else if (toppedR) {
                    tile = tR;
                }
            }
            boolean finalBool = false;
            for (int ii = 0; ii < nn; ii++) {
                finalBool = finalBool || tempBool[ii];
            }
            topped = finalBool;
        }

        // When the player is hitting an interactive tile
        if (tile instanceof InteractiveTile && this instanceof Player) {
            InteractiveTile t = (InteractiveTile) tile;
            if (topped && dy < 0) {
                if (!t.isHitBottom()) {
                    // Return a hidden sprite under this tile, if any.
                    HiddenSprite hs = 
                            level.removeHiddenSprite(t.getX(), t.getY());
                    if (hs != null) {
                        hs.activate();         // Activate the hidden sprite.
                        t.setHiddenSprite(hs); // Add hidden sprite to the tile.
                    }
                }
                t.hurt();
            }
        }

        if (dx < 0) {
//            xto0 = Math.ceil((x - Math.min(ES, width)) / (double) width);            
//            xt0 = Math.ceil((x + dx - Math.min(ES, width)) / (double) width);
            xt0 =(x + dx - Math.min(ES, width)) / (double) width + aTile;
        }
       
        
        // Check left stopped.
//        boolean leftStopped;
        // When going beyond the left end of the map 
        if (x <= 0) {       
            leftStopped = true;
        // When in between one tile to the left end of the map
        } else if (0 < x  && x  <= 0 + ES) {        
            leftStopped = false;
        // When going beyond the bottom end of the map
        } else if (y + dy > Commons.BOARD_HEIGHT) {        
            leftStopped = false;
        // when falling beyond the ground  
        } else if (y + height > Commons.GROUND) {               
            leftStopped = !(level.getTile(xt0, yt, unit).mayPass());
        } else {
            leftStopped = !(level.getTile(xt0, yt1 - aTile, unit).mayPass()) || 
                    (((y + dy) % ES != 0) && 
                    !(level.getTile(xt0, yt1, unit).mayPass()));
        }

        
        // Determine move or not.
        if (grounded && dy > 0) {
            int yground = (((y + dy) + height) >> 4) * ES;
            dy = yground - (y + height);
            y += dy;
            this.dy = 1;
            return false;
        }
        if (topped && dy < 0) {
            return false;
        }
        if (rightStopped && dx > 0) {
            return false;
        }
        if (leftStopped && dx < 0) {
            return false;
        }

        
        // Gets all of the entities that are inside this entity.
        List<Sprite> wasInside = level.getEntities(x, y, x + width, y + height); 
        // Gets the entities that this entity will touch.
        List<Sprite> isInside = level.getEntities(x + dx, y + dy, 
                x + width + dx, y + height + dy); 
        // Loops through isInside list.
        for (int i = 0; i < isInside.size(); i++) { 
            Sprite e = isInside.get(i); // Current entity in the list
            if (e == this) {            // If the current sprite is this object
                continue;               // Skip to the next entity.
            }
            e.touchedBy(this);
        }
        // Removes all the sprites in the wasInside from the isInside list.
        isInside.removeAll(wasInside); 
        // Loops through isInside list (only new sprites).
        for (int i = 0; i < isInside.size(); i++) { 
            Sprite e = isInside.get(i); // Current entity in the list
            if (e == this) {            // If the current sprite is this object
                continue;               // Skip to the next entity.
            }
            if (e.blocks(this)) {
                if (!grounded && dy > 0) {      // When falling
                    dy = e.y - (y + height);    // Adjust dy
                    this.dy = dy;
                } else {                // When not falling
                    return false;       // Not gonna move.
                }
            }         
        }

        x += dx;        // Moves horizontally based on the x acceleration
        y += dy;        // Moves vertically based on the y acceleration
        return true;    // If it went through all the requirement, then move!
    }

    /**
     * Extended in Sprite.java.
     *
     * @param e The entity that wants to interact with this entity.
     * @return True if this entity blocks e, false if this entity lets e pass
     * it.
     */
    @Override
    public boolean blocks(Entity e) {
        return false;
    }

    /**
     * Check whether the sprite will be grounded with the acceleration.
     * @return True if the sprite will be grounded 
     */
    protected boolean willBeGrounded() {

        // Get tile coordinate of the position relative to the sprite + acc.
        double xt1 = ((x + dx) + width) / Math.pow(2, unit); // Right
        double yt1 = ((y + dy) + height) / Math.pow(2, unit); // Bottom 

        if (dx < 0 && leftStopped) {
            xt1 =(x + dx + width) / (double) width + aTile;
        }
        // If the increment is negative, round up the tile coordinates.
        if (dy < 0) {
            yt1 = (Math.ceil((y + height + dy) / (double) height));
        }

        boolean temp1 = !(level.getTile(xt1 - aTile, yt1, unit).mayPass());
        boolean temp2 = !(level.getTile(xt1, yt1, unit).mayPass());
        // When beyond the left end of the screen, don't check xt1-1
        if (x <= level.getOffset()) {
            return temp2;
        // When beyond the right end of the map, don't check xt1
        } else if (x + dx + width >= W_MAP * ES) {
            return temp1;
        // When one tile away from the right end of the map, don't check xt1-1
        } else if (W_MAP * ES - ES <= x + dx + width && 
                x + dx + width < W_MAP * ES) {
            return temp2;
        } else if (rightStopped) { // If there is a wall on right, check below.
            return temp1;
        } else {
            return temp1 || (((x + dx) % ES != 0) && temp2);
        }
    }

    /**
     * The sprite is hurt by another sprite.
     * 
     * @param damage An integer containing damage to this sprite.
     */
    public void hurt(int damage) { 
        health -= damage; 
    }

    /**
     * Initialize health to 1.
     */
    private void initHealth() {
        health = 1;
    }

    /**
     * Returns current health of the sprite.
     * 
     * @return An integer containing the health of the sprite.
     */
    public int getHealth() {
        return health;
    }
}
