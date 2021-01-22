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
        initSprite();        
    }

    /**
     * Initialize variables.
     */
    private void initSprite() {
        initHealth();
        
        walkDist = 0;
        bCounter = bNum = 0;
        scale = 8;      // Higher the number, slower the transition.
        numStage = 4;   // Number of color schemes
        ay = 2;         // Higher the number, slower the y movement when sprung 
                        // out of the block         
        visible = true;
        dying = false;
        grounded = true;
        topped = isPunchedOnBottom = false;
        
        // Variables below need to be adjusted when creating a new child class.
        lives = 1;
        dx = 0;
        dy = 1;
        ds = dy;
        dir = 2;        
        ySpeed = 1;     // By default, sprites are under gravity               
    }

    /**
     * Update method, (Look in the specific entity's class)
     */
    public void tick() {

        if (health <= 0) {  // If there is no health left
            lives--;        // Reduce life by 1.
            if (this instanceof Player && lives > 0) {  // For the player,
                Player p = (Player) this;
                p.resetGame();  // Restart the game.
                initHealth();   // Initialize the health.
            }
        }

        if (lives <= 0) {   // If no more lives left
            remove();       // Die.
        }
    }

    /**
     * Draws the sprite on the screen
     *
     * @param screen The screen to be displayed on.
     */
    public void render(Screen screen) {
        if (colNum == 0)
            colNum = screen.getSheet().width / PPS;
    }

    /**
     * What happens when this entity is touched by another entity.
     *
     * @param sprite The sprite that this sprite is touched by.
     */
    protected abstract void touchedBy(Sprite sprite);

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
        
//        double xto0 = (x - Math.min(ES, width)) / Math.pow(2, unit); // gets the tile coordinate of the position to the left of the sprite
//        double yto0 = (y - Math.min(ES, height)) / Math.pow(2, unit); // gets the tile coordinate of the position to the top of the sprite
        double xto1 = (x + width) / Math.pow(2, unit); // gets the tile coordinate of the position to the right of the sprite
//        double yto1 = (y + height) / Math.pow(2, unit); // gets the tile coordinate of the position to the bottom of the sprite
//        double xto = x / Math.pow(2, unit);

        double xt0 = ((x + dx) - Math.min(ES, width)) / Math.pow(2, unit); // gets the tile coordinate of the position to the left of the sprite + the horizontal acceleration
//        double yt0 = ((y + dy) - Math.min(ES, height)) / Math.pow(2, unit); // gets the tile coordinate of the position to the top of the sprite + the vertical acceleration
        double xt1 = ((x + dx) + width) / Math.pow(2, unit); // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration
        double yt1 = ((y + dy) + height) / Math.pow(2, unit); // gets the tile coordinate of the position to the bottom of the sprite + the vertical acceleration
        double xt = (x + dx) / Math.pow(2, unit);
        double yt = (y + dy) / Math.pow(2, unit);

        /* Check grounded. */
        if (x <= 0) // When going beyond the left most point of the map
        {
            grounded = !(level.getTile(xto1 - aTile, yt1, unit).mayPass())
                    || ((x % ES != 0) && !(level.getTile(xto1, yt1, unit).mayPass()));
        } else if (x + width >= W_MAP * ES) // When going beyond the right of the screen
        {
            grounded = !(level.getTile(xto1 - aTile, yt1, unit).mayPass());
        } else if (W_MAP * ES - ES <= x + width && x + width < level.getWidth() * ES) // When less than one tile close to the right of the screen
        {
            grounded = !(level.getTile(xto1, yt1, unit).mayPass());
        } else if (y + height > Commons.GROUND) // when falling beyond the ground
        {
            grounded = false;
        } else {

            boolean[] tempBool = new boolean[2];
            int nn = 0;
            for (double xx = xt1; xx > xt; xx -= aTile) {
                Tile t1 = level.getTile(xx - aTile, yt1, unit);
                Tile t2 = level.getTile(xx, yt1, unit);
                boolean temp = !(t1.mayPass()) || ((x % ES != 0) && !(t2.mayPass()));
                tempBool[nn] = temp;
                nn++;
            }
            boolean finalBool = false;
            for (int ii = 0; ii < nn; ii++) {
                finalBool = finalBool || tempBool[ii];
            }
            grounded = finalBool;
        }

        // Find whether the ground is punched
        if (grounded) {
            Tile t1 = level.getTile(xt1 - aTile, yt1, unit);
            Tile t2 = level.getTile(xt1, yt1, unit);
            boolean temp1 = !(t1.mayPass()) && ((t1 instanceof InteractiveTile) && !this.equals(((InteractiveTile) t1).getHiddenSprite()) && ((InteractiveTile) t1).getInitY() != ((InteractiveTile) t1).getY());
            boolean temp2 = ((x % ES != 0) && !(t2.mayPass())) && ((t2 instanceof InteractiveTile) && !this.equals(((InteractiveTile) t2).getHiddenSprite()) && ((InteractiveTile) t2).getInitY() != ((InteractiveTile) t2).getY());
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
//            yto0 = (Math.ceil((y - Math.min(ES, height)) / (double) height));
//            yto1 = (Math.ceil((y + height) / (double) height));
//            yt0 = (Math.ceil((y - Math.min(ES, height) + dy) / (double) height));
            yt1 = (Math.ceil((y + height + dy) / (double) height));
        }
        
        /* Check right stopped. */
        boolean rightStopped;
        if (x + width >= W_MAP * ES) // When going beyond the right of the screen
        {
            rightStopped = true;
        } else if (W_MAP * ES - ES <= x + width && x + width < W_MAP * ES) // When in between one tile to the right of the screen
        {
            rightStopped = false;
        } else if (y + dy > Commons.BOARD_HEIGHT) // When going beyond the bottom most point of the map
        {
            rightStopped = false;
        } else if (y + height > Commons.GROUND) { // when falling beyond the ground, the tile at the most upper-right determins the right stop.
            double ytFall = Commons.GROUND / Math.pow(2, unit);
            rightStopped = !(level.getTile(xt1, ytFall, unit).mayPass());
        } else {
            rightStopped = !(level.getTile(xt1, yt1 - aTile, unit).mayPass())
                    || (((y + dy) % ES != 0) && !(level.getTile(xt1, yt1, unit).mayPass()));
        }

        /* Check topped. */
        double yt_raw = (y - Math.min(ES, height) + dy) / (double) height;
        double yt_ceil = Math.ceil(yt_raw);
        double yt0T = yt_ceil;
        if (unit == 5 && yt_ceil - yt_raw >= 0.5) // When big Pusheen, 0.5 is the unit tile
        {
            yt0T = yt_ceil - 0.5;
        }
        Tile tile = null; // Need to check whether it is a interactive tile
        if (y <= 0) // When going beyond the top most point of the map
        {
            topped = true;
        } else if (0 < y && y <= ES) // When in between one tile to the top of the map
        {
            topped = false;
        } else if (y + dy > Commons.BOARD_HEIGHT) // When going beyond the bottom most point of the map
        {
            topped = false;
        } else if (x < ES) {  // When going beyond the left most point + unit tile of the map
            tile = level.getTile(xt1, yt0T, unit);
            topped = !(tile.mayPass());
        } else if (x + width >= W_MAP * ES) { // When going beyond the right of the screen
            tile = level.getTile(xt1 - aTile, yt0T, unit);
            topped = !(tile.mayPass());
        } else if (W_MAP * ES - ES <= x + width && x + width < W_MAP * ES) { // When in between one tile to the right of the screen
            tile = level.getTile(xto1, yt0T, unit);
            topped = !(tile.mayPass());
        } else {
            boolean[] tempBool = new boolean[2];
            int nn = 0;
            for (double xx = xt1; xx > xt; xx -= aTile) {
                Tile tile1 = level.getTile(xx - aTile, yt0T, unit);
                Tile tile2 = level.getTile(xx, yt0T, unit);
                boolean topped1 = !(tile1.mayPass()); // top-left or top
                boolean topped2 = ((x + dx) % ES != 0) && !(tile2.mayPass()); // top-right                     
                boolean temp = topped1 || topped2;
                tempBool[nn] = temp;
                nn++;

                if (topped1 && topped2) {   // When both top-left and top-right are blocking
                    double xt1T = Math.round((x + dx) / (double) width);  // choose the tile closer to the player.
                    tile = level.getTile(xt1T, yt0T, unit);
                } else if (topped1) {
                    tile = tile1;
                } else if (topped2) {
                    tile = tile2;
                }
            }
            boolean finalBool = false;
            for (int ii = 0; ii < nn; ii++) {
                finalBool = finalBool || tempBool[ii];
            }
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
//            xto0 = Math.ceil((x - Math.min(ES, width)) / (double) width);            
            xt0 = Math.ceil((x - Math.min(ES, width) + dx) / (double) width);
        }
       
        /* Check left stopped. */
        boolean leftStopped;
        if (x <= 0) {// When going beyond the left most point of the map        
            leftStopped = true;
        } else if (0 < x && x <= 0 + ES) {// When in between one tile to the left of the map        
            leftStopped = false;
        } else if (y + dy > Commons.BOARD_HEIGHT) {// When going beyond the bottom most point of the map        
            leftStopped = false;
        } else if (y + height > Commons.GROUND) { // when falling beyond the ground                
            leftStopped = !(level.getTile(xt0, yt, unit).mayPass());
        } else {
            leftStopped = !(level.getTile(xt0, yt1 - aTile, unit).mayPass())
                    || (((y + dy) % ES != 0) && !(level.getTile(xt0, yt1, unit).mayPass()));
        }

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

        List<Sprite> wasInside = level.getEntities(x, y, x + width, y + height); // gets all of the entities that are inside this entity (aka: colliding)
        List<Sprite> isInside = level.getEntities(x + dx, y + dy, x + width + dx, y + height + dy); // gets the entities that this entity will touch.
        for (int i = 0; i < isInside.size(); i++) { // loops through isInside list
            Sprite e = isInside.get(i); // current entity in the list
            if (e == this) {
                continue; // if the entity happens to be this one that is calling this method, then skip to the next entity.
            }
            e.touchedBy(this); // calls the touchedBy(entity) method in that entity's class
        }
        isInside.removeAll(wasInside); // removes all the entities that are in the wasInside from the isInside list.
        for (int i = 0; i < isInside.size(); i++) { // loops through isInside list
            Sprite e = isInside.get(i); // current entity in the list
            if (e == this) {
                continue; // if the entity happens to be this one that is calling this method, then skip to the next entity.            
            }
            if (e.blocks(this)) {
                if (!(!grounded && dy > 0)) {
                    return false;
                } else {// When falling
                    dy = e.y - (y + height);
                    this.dy = dy;
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

        double xto1 = (x + width) / Math.pow(2, unit); // gets the tile coordinate of the position to the right of the sprite
        double yto1 = (y + height) / Math.pow(2, unit); // gets the tile coordinate of the position to the bottom of the sprite

        return !(level.getTile(xto1 - aTile, yto1, unit).mayPass())
                || ((x % ES != 0) && !(level.getTile(xto1, yto1, unit).mayPass()));
    }

    protected boolean willBeGrounded() {

        double xt1 = ((x + dx) + width) / Math.pow(2, unit); // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration        
        double yt1 = ((y + dy) + width) / Math.pow(2, unit); // gets the tile coordinate of the position to the bottom of the sprite + the vertical acceleration

        // if the increment is negative, round up the tile coordinates.
        if (dy < 0) {
            yt1 = (Math.ceil((y + width + dy) / (double) width));
        }

        if (x <= level.getOffset()) // When on the left end, don't check xt1-1
        {
            return !(level.getTile(xt1, yt1, unit).mayPass());
        } 
        else if (x + dx + width >= W_MAP * ES) {
            return !(level.getTile(xt1 - aTile, yt1, unit).mayPass());
        } else if (W_MAP * ES - ES <= x + dx + width && x + dx + width < W_MAP * ES) {
            return !(level.getTile(xt1, yt1, unit).mayPass());
        } else {
            return !(level.getTile(xt1 - aTile, yt1, unit).mayPass())
                    || (((x + dx) % ES != 0) && !(level.getTile(xt1, yt1, unit).mayPass()));
        }
    }

    protected boolean checkTopped() {

        double yt0 = Math.ceil((y + dy - Math.min(ES, height)) / (double) width); // gets the tile coordinate of the position to the top of the sprite + the vertical acceleration
        double xt1 = ((x + dx) + width) / Math.pow(2, unit); // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration


        // When right at the grid, only need to check the neighboring tile
        // When in between, check both tiles that the player spans.        
        if (dx < 0) {
            xt1 = Math.ceil((x + width + dx) / (double) width);
        }

        if (y <= 0) {
            return true;
        } else if (0 < y && y <= ES) {
            return false;
        } else {
            return !(level.getTile(xt1 - aTile, yt0, unit).mayPass())
                    || (((x + dx) % ES != 0) && !(level.getTile(xt1, yt0, unit).mayPass()));
        }
    }

    public void hurt(int damage) { // mob hurts this sprite
        health -= damage; // Actually change our health
    }

    private void initHealth() {
        health = 1;
    }

    public int getHealth() {
        return health;
    }
}
