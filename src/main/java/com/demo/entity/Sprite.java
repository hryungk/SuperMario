package main.entity;

import java.util.List;
import main.Commons;
import main.gfx.Screen;
import main.level.Level;
import main.level.tile.InteractiveTile;
import main.level.tile.Tile;

/** Represents a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public abstract class Sprite extends Entity {

    private boolean visible;
    private boolean dying;
    protected final int ES = Commons.ENTITY_SIZE; // Default entity size (16 px)
    int dx, dy;
    public int maxHealth = 2; // The maximum amount of health the mob can have
    public int health; // The amount of health we currently have, and set it to the maximum we can have
    protected int walkDist = 0; // How far we've walked currently, incremented after each movement
    protected int dir = 0; // The direction the mob is facing, used in attacking and rendering. 0 is down, 1 is up, 2 is left, 3 is right    
    int numS = 256 / Commons.PPS;    // number of squres in a row in the sprite sheet (32)
    protected boolean grounded, topped; // Whether the sprite is on to a solid tile/touched a solid tile on the head.
    protected int ground;
    public Level level; // the level that the entity is on
    protected int wS, hS; // width and height of tile [squares]
    protected final int MAX_JUMP = Commons.BOARD_HEIGHT - 2 * ES - Commons.Y96; // (64)
    protected int xSpeed, ySpeed;
    protected int lives;
    protected int unit;
    
    // The constructor initiates the x and y coordinates and the visible variable.
    public Sprite(Level level) {
        super();
        this.level = level;  
        initSprite();
    }    
    private void initSprite() {
        visible = true;
        dx = dy = 0;              
        grounded = true;
        topped = false;
        wS = hS = 2;
        initHealth();
        lives = 1;   
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
            }
        }
        
        if (lives <= 0)  // If no more lives left, die.
            remove(); 
        else
            initHealth();        
    }    

    /** Draws the sprite on the screen
     * @param screen The screen to be displayed on. */
    public abstract void render(Screen screen);
    
    
    /** if this entity is touched by another entity (extended by sub-classes)
     * @param sprite The sprite that this sprite is touched by. */
    protected abstract void touchedBy(Sprite sprite);
        
    public void die() { // Kill the mob, called when health drops to 0
        visible = false;
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
//            if (dy < 0) dir = 1; // up
//            if (dy > 0) dir = 0; // down
            
            boolean stopped = true; // stopped value, used for checking if the entity has stopped.
            if (dx != 0 && move2(dx, 0)) stopped = false; // If the horizontal acceleration and the movement was successful then stopped equals false.
            if (dy != 0) {
                if (move2(0, dy))
                    stopped = false; // If the vertical acceleration and the movement was successful then stopped equals false.
                else if (grounded) {
                    ground = y + height;
                }
                
            } 
            if (!stopped && y < Commons.BOARD_HEIGHT) { // if the sprite is able to move then...
                int xt = x >> unit; // the x tile coordinate that the entity is standing on.
                int yt = y >> unit; // the y tile coordinate that the entity is standing on.
                level.getTile(xt, yt, unit).steppedOn(this); // Calls the steppedOn() method in a tile's class. (like sand or lava)
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
        int xto0 = (x - width) >> unit; // gets the tile coordinate of the position to the left of the sprite
        int yto0 = (y - height) >> unit; // gets the tile coordinate of the position to the top of the sprite
        int xto1 = (x + width) >> unit; // gets the tile coordinate of the position to the right of the sprite
        int yto1 = (y + height) >> unit; // gets the tile coordinate of the position to the bottom of the sprite

        int xt0 = ((x + dx) - width) >> unit; // gets the tile coordinate of the position to the left of the sprite + the horizontal acceleration
        int yt0 = ((y + dy) - height) >> unit; // gets the tile coordinate of the position to the top of the sprite + the vertical acceleration
        int xt1 = ((x + dx) + width) >> unit; // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration
        int yt1 = ((y + dy) + height) >> unit; // gets the tile coordinate of the position to the bottom of the sprite + the vertical acceleration
         
        if (x <= 0) 
            grounded = !(level.getTile(xto1-1, yt1, unit).mayPass(xto1-1, yt1, this)) ||
                ((x % width != 0) && !(level.getTile(xto1, yt1, unit).mayPass(xto1, yt1, this)));       
        else if (x + width >= level.W * ES) 
            grounded = !(level.getTile(xto1-1, yt1, unit).mayPass(xto1-1, yt1, this));
        else if (level.W * ES - ES <= x + width && x + width < level.W * ES) 
            grounded = !(level.getTile(xto1, yt1, unit).mayPass(xto1, yt1, this));
        else if (y + height > Commons.GROUND)
            grounded = false;
        else
            grounded = !(level.getTile(xt1-1, yt1, unit).mayPass(xt1-1, yt1, this)) ||
                ((x % width != 0) && !(level.getTile(xt1, yt1, unit).mayPass(xt1, yt1, this)));    
        
        // if the increment is negative, round up the tile coordinates.
        if (dy < 0) {            
            yto0 = (int) (Math.ceil((y - height) / (double)width));
            yto1 = (int) (Math.ceil((y + height) / (double)width));
            yt0 = (int) (Math.ceil((y - height + dy) / (double)width));
            yt1 = (int) (Math.ceil((y + height + dy) / (double)width));
        }
        
        // When right at the grid, only need to check the neighboring tile
        // When in between, check both tiles that the player spans.
//        topped = ((x + dx) % width != 0) && !(level.getTile(xt1, yt0).mayPass(xt1, yt0, this));      
//        if (x > level.getOffset())
//            topped = !(level.getTile(xt1-1, yt0).mayPass(xt1-1, yt0, this)) || topped;          
//        grounded = !(level.getTile(xt1-1, yt1).mayPass(xt1-1, yt1, this)) ||
//                (((x + dx) % width != 0) && !(level.getTile(xt1, yt1).mayPass(xt1, yt1, this)));  

//        grounded = !(level.getTile(xto1-1, yto1).mayPass(xto1-1, yto1, this)) ||
//                ((x % width != 0) && !(level.getTile(xto1, yto1).mayPass(xto1, yto1, this))); 
        
        boolean rightStopped;
        if (x + width >= level.W * ES) rightStopped = true;
        else if (level.W * ES - ES <= x + width && x + width < level.W * ES) rightStopped = false;
        else if (y + dy > Commons.BOARD_HEIGHT) rightStopped = false;
        else if (y + height > Commons.GROUND)
            rightStopped = !(level.getTile(xt1, yt1-1, unit).mayPass(xt1, yt1-1, this));
        else
            rightStopped = !(level.getTile(xt1, yt1-1, unit).mayPass(xt1, yt1-1, this)) || 
                (((y + dy) % width != 0) && !(level.getTile(xt1, yt1, unit).mayPass(xt1, yt1, this)));  
        
        
        int yt0T = (int) (Math.ceil((y - height + dy) / (double)width));
        Tile tile = null;
        if (y <= 0) topped = true;
        else if (0 < y && y <= ES) topped = false;
        else if (y + dy > Commons.BOARD_HEIGHT) topped = false;
        else if (x < ES) {
            tile = level.getTile(xt1, yt0T, unit);
            topped = !(tile.mayPass(xt1, yt0T, this)); 
        } else if (x + width >= level.W * ES) {
            tile = level.getTile(xt1-1, yt0T, unit);
            topped = !(tile.mayPass(xt1-1, yt0T, this));
        } else if (level.W * ES - ES <= x + width && x + width < level.W * ES) {
            tile = level.getTile(xto1, yt0T, unit);
            topped = !(tile.mayPass(xto1, yt0T, this));
        } else {
            Tile tile1 = level.getTile(xt1-1, yt0T, unit);
            Tile tile2 = level.getTile(xt1, yt0T, unit);
            boolean topped1 = !(tile1.mayPass(xt1-1, yt0T, this)); // top-left or top
            boolean topped2 = ((x + dx) % width != 0) && !(tile2.mayPass(xt1, yt0T, this)); // top-right            
            
            if (topped1 && topped2) {   // When both top-left and top-right are blocking
                int xt1T = (int) Math.round((x + dx) / (double)width);  // choose the tile closer to the player.
                tile = level.getTile(xt1T, yt0T, unit);
            }
            else if (topped1) tile = tile1;
            else if (topped2) tile = tile2;
            
            topped = topped1 || topped2;    
        }
        
        if ((tile instanceof InteractiveTile)) {
            InteractiveTile t = (InteractiveTile) tile;
            boolean isHit = t.isHitBottom();
            if (topped && dy < 0) {
//                t.setHit(true);
                
                if (!t.isHitBottom()) {
//                    Coin coin = new Coin(t.getX(), t.getY(), level);
//                    level.add(coin);
                    HiddenSprite hs = level.getHiddenSprite(t.getX(), t.getY());
                    if (hs != null) {
                        hs.activate();
//                        level.player.score += hs.score; // gives the player 100 points of score
//                        level.player.addCoinCount();
                    }
                }
                t.hurt();                
//                System.out.println("Tile's x: " + (xt1-1) + ", Tile's y: " + yt0T);
                
            }
        }
                
        if (dx < 0) {            
            xto0 = (int) Math.ceil((x - width) / (double)width);
            xto1 = (int) Math.ceil((x + width)/ (double)width);
            xt0 = (int) Math.ceil((x - width + dx) / (double)width);
            xt1 = (int) Math.ceil((x + width + dx) / (double)width);
        }
//        if (y <= 0) topped = true;
//        else if (0 < y && y <= ES) topped = false;
//        else topped = !(level.getTile(xt1-1, yt0).mayPass(xt1-1, yt0, this)) ||
//                (((x + dx) % width != 0) && !(level.getTile(xt1, yt0).mayPass(xt1, yt0, this)));    
        
//        boolean leftStopped = !(level.getTile(xt0, yt1-1).mayPass(xt0, yt1-1, this)) || 
//                (((y + dy) % width != 0) && !(level.getTile(xt0, yt1).mayPass(xt0, yt1, this)));        
        

//        boolean leftStopped;
//        if (x <= level.getOffset())
//            leftStopped = true;
//        else if (level.getOffset() < x && x <= level.getOffset() + ES)
//            leftStopped = false;
//        else
//            leftStopped = !(level.getTile(xt0, yt1-1).mayPass(xt0, yt1-1, this)) || 
//                (((y + dy) % width != 0) && !(level.getTile(xt0, yt1).mayPass(xt0, yt1, this)));        
        

        boolean leftStopped = false;  
//        if (this instanceof Player) {
            if (x <= 0) leftStopped = true;
            else if (0 < x && x <= 0 + ES) leftStopped = false;
            else if (y + dy > Commons.BOARD_HEIGHT) leftStopped = false;
            else if (y + height > Commons.GROUND)
                leftStopped = !(level.getTile(xt0, yt1-1, unit).mayPass(xt0, yt1-1, this));            
            else
                leftStopped = !(level.getTile(xt0, yt1-1, unit).mayPass(xt0, yt1-1, this)) || 
                (((y + dy) % width != 0) && !(level.getTile(xt0, yt1, unit).mayPass(xt0, yt1, this)));  
//        }
//        if (this instanceof Alien) {
//            if (x <= 0) leftStopped = true;
//            else if (0 < x && x <= ES) leftStopped = false;    
//            else if (y + dy > Commons.BOARD_HEIGHT) leftStopped = false;
//            else if (y + height > Commons.GROUND)
//                leftStopped = !(level.getTile(xt0, yt1-1).mayPass(xt0, yt1-1, this));
//            else 
//                leftStopped = !(level.getTile(xt0, yt1-1).mayPass(xt0, yt1-1, this)) || 
//                (((y + dy) % width != 0) && !(level.getTile(xt0, yt1).mayPass(xt0, yt1, this)));              
//        }
        
//        if (dx < 0 && level.getOffset() == x) {
//            System.out.print("grounded?: " + grounded + ", topped?: " + topped + ", ");
//            System.out.println("leftStopped?: " + leftStopped + ", rightStopped?: " + rightStopped);
//        }
        
        
        if (grounded && dy > 0) {
            int yground = (((y + dy) + height) >> unit) * width;
            dy = yground - (y + height);
            y += dy;            
            this.dy = 1;
            return false;
        }        
        if (topped && dy < 0) return false;
        if (rightStopped && dx > 0) return false;
        if (leftStopped && dx < 0) return false;
            
//        boolean blocked = false; // determines if the next tile can block you.        
//        for (int yt = yt0; yt <= yt1; yt++) {// cycles through yt0 to yt1
//            for (int xt = xt0; xt <= xt1; xt++) { // cycles through xt0 to xt1        
//                /* If...
//                 * xto0 <= xt <= xto1 AND
//                 * yto0 <= yt <= yto1,
//                 * skip the rest of the code and go to the next cycle */
//                if (xto0 <= xt && xt <= xto1 && yto0 <= yt && yt <= yto1) continue;        
//                
////                level.getTile(xt, yt).bumpedInto(xt, yt, this);  // Calls the bumpedInto function in a tile's class (like cactus)
////                if (dx != 0 && yt == yt1 && level.getTile(xt, yt) != Tile.sky) continue;                 
//                if (!level.getTile(xt, yt).mayPass(xt, yt, this)) { // If the entity cannot pass this block...(always false except sky)                    
//                    if (!grounded && dy > 0) continue;
//                    if (!topped && dy < 0) continue;                    
//                    if (!rightStopped && dx > 0) continue;
//                    if (!leftStopped && dx < 0) continue;
//                    
//                    blocked = true; // blocked value set to true
//                    return false;  // return false
//                }
//            }
//        }
//        if (blocked) return false; // if blocked is equal to true, then return false

//        List<Sprite> wasInside = level.getEntities(x - ES, y - ES, x + ES, y + ES); // gets all of the entities that are inside this entity (aka: colliding)
//        List<Sprite> isInside = level.getEntities(x - ES + dx, y - ES + dy, x + ES + dx, y + ES + dy); // gets the entities that this entity will touch.
        List<Sprite> wasInside = level.getEntities(x, y, x + ES, y + ES); // gets all of the entities that are inside this entity (aka: colliding)
        List<Sprite> isInside = level.getEntities(x + dx, y + dy, x + ES + dx, y + ES + dy); // gets the entities that this entity will touch.
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
                    dy = e.y - y - height;
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
        
        int xto1 = (x + width) >> unit; // gets the tile coordinate of the position to the right of the sprite
        int yto1 = (y + height) >> unit; // gets the tile coordinate of the position to the bottom of the sprite
        
        return !(level.getTile(xto1-1, yto1, unit).mayPass(xto1-1, yto1, this)) ||
                ((x % width != 0) && !(level.getTile(xto1, yto1, unit).mayPass(xto1, yto1, this)));         
    }
    protected boolean willBeGrounded() {        
        
        int xt1 = ((x + dx) + width) >> unit; // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration
        int yt1 = ((y + dy) + width) >> unit; // gets the tile coordinate of the position to the bottom of the sprite + the vertical acceleration
                
        // if the increment is negative, round up the tile coordinates.
        if (dy < 0) {            
            yt1 = (int) (Math.ceil((y + width + dy) / (double)width));
        }
        
        
        if (x <= level.getOffset()) // When on the left end, don't check xt1-1
            return !(level.getTile(xt1, yt1, unit).mayPass(xt1, yt1, this));     
//            return ((x + dx) % width != 0) && !(level.getTile(xt1, yt1).mayPass(xt1, yt1, this));  
        else if (x + dx + width >= level.W * ES) 
            return !(level.getTile(xt1-1, yt1, unit).mayPass(xt1-1, yt1, this));
        else if (level.W * ES - ES <= x + dx + width && x + dx + width < level.W * ES) 
            return !(level.getTile(xt1, yt1, unit).mayPass(xt1, yt1, this));
        else
            return !(level.getTile(xt1-1, yt1, unit).mayPass(xt1-1, yt1, this)) ||
                (((x + dx) % width != 0) && !(level.getTile(xt1, yt1, unit).mayPass(xt1, yt1, this)));     
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
//            return ((x + dx) % width != 0) && !(level.getTile(xt1, yt0).mayPass(xt1, yt0, this));
//        else
//            return !(level.getTile(xt1-1, yt0).mayPass(xt1-1, yt0, this)) || 
//                    (((x + dx) % width != 0) && !(level.getTile(xt1, yt0).mayPass(xt1, yt0, this)));   
        

        int yt0 = ((y + dy) - height) >> unit; // gets the tile coordinate of the position to the top of the sprite + the vertical acceleration
        int xt1 = ((x + dx) + width) >> unit; // gets the tile coordinate of the position to the right of the sprite + the horizontal acceleration
                
        // if the increment is negative, round up the tile coordinates.
//        if (dy < 0) {            
            yt0 = (int) (Math.ceil((y - height + dy) / (double)width));
//        }        
        // When right at the grid, only need to check the neighboring tile
        // When in between, check both tiles that the player spans.        
        if (dx < 0) {            
            xt1 = (int) Math.ceil((x + width + dx) / (double)width);
        }
        
        if (y <= 0) return true;
        else if (0 < y && y <= ES) return false;
        else return !(level.getTile(xt1-1, yt0, unit).mayPass(xt1-1, yt0, this)) ||
                (((x + dx) % width != 0) && !(level.getTile(xt1, yt0, unit).mayPass(xt1, yt0, this))); 
        
        
    }
    
    public void hurt(int damage) { // mob hurts this sprite
        health -= damage; // Actually change our health
    }
    
    private void initHealth() {
        health = 1;
    }
}
