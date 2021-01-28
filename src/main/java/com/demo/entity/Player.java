package main.java.com.demo.entity;

import main.java.com.demo.Commons;

import main.java.com.demo.SuperPusheen;
import main.java.com.demo.InputHandler;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;
import main.java.com.demo.level.tile.FlagTile;
import main.java.com.demo.level.tile.Tile;

/**
 * Represents a player as a sprite.
 *
 * @author HRK
 */
public class Player extends Sprite {

    private final InputHandler input;
    private final SuperPusheen game;
    public int invulnerableTime;    // Time for when the player is hurt by enemy
    public int immortalTime;        // Time for when the player eats the starman    
    private int leftCount;          // Counter during facing left
    private int netDx;              // The effective dx
    private int deathTime;          // Counts ticks after death.
    private boolean crushedEnemy, enlarged, fired, immortal;
    // Booleans for ending movement.
    private boolean firstTime, reachedEnd, reachedPollBottom, flagReachedBottom, 
                    faceLeft, jumped, enteredCastle;    

    public Player(InputHandler input, Level level, SuperPusheen board) {
        super(level);
        this.input = input;
        game = board;

        lives = 3;
        scale = 4;

        initPlayer();
    }
    
    public void initPlayer() {        
        super.initSprite();
        
        // Initialize variables from Entity class.
        width = height = ES;
        setX(Commons.PLAYER_XI);
        int START_Y = Commons.GROUND - height;
        setY(START_Y);        
        xS = 0;
        yS = 10;

        // Initialize variables from Sprite class.
        dx = 0;
        dy = ySpeed;
        ds = 0;
        dir = 3;    // Face right when first created.

        ground = Commons.GROUND;
        wS = width / PPS;
        hS = height / PPS;
        xSpeed = 2;
        score = 0;
        unit = (int) (Math.log10(width) / Math.log10(2));
        aTile = Math.min(Math.pow(2, 4 - unit), 1); 
        
        // Initialize variables for this class.
        invulnerableTime = immortalTime = 0;
        leftCount = netDx = deathTime = 0;
        crushedEnemy = enlarged = fired = immortal = false;
        firstTime = true;
        reachedEnd = reachedPollBottom = flagReachedBottom = faceLeft = jumped 
                = enteredCastle = false;
        
    }    
    
    /** 
     * What happens when the player touches a sprite
     * 
     * @param sprite The sprite that this sprite is touched by. 
     */
    @Override
    protected void touchedBy(Sprite sprite) {
        // If the player is touched by an enemy
        if (sprite instanceof Enemy) {
            // Call the enemy's touchedBy only when player doesn't move.
            if (dx == 0) {
                sprite.touchedBy(this); 
            }
        }
        // If the player is touched by a hidden sprite
        if (sprite instanceof HiddenSprite && !sprite.removed) {
            sprite.touchedBy(this); // Call the hidden sprite's touchedBy.
        }
    }

    /**
     * Update method.
     */
    @Override
    public void tick() {        
        // When fell to the bottom
        if (deathTime != 0) {       
            if (deathTime < 40) {   // If deathTime is not over
                deathTime++;        // Keep counting.
            } else if (deathTime == 40) {   // If deathTime is over
                lives--;                    // Decrease life by 1.
                if (lives <= 0) {   // If no more lives left
                    remove();       // Die. Game over.
                } else {            // If there are lives left
                    resetGame();    // Restart the game.
                }
            }
        } 
        // If still playing
        else {    
            super.tick();

            if (invulnerableTime > 0) { // If player is in invulrnerable state
                invulnerableTime--;     // Keep counting down the timer.
            }
            if (immortalTime > 0) { // While Starman is in effect, 
                immortalTime--;     // Keep counting down the timer.
                bNum = (bCounter / scale) % numStage; // Index for animation
                bCounter++;         // Increment animation counter.
            }

            if (dying) { // If dying, not playable
                dx = 0; // Stop moving horizontally.
                netDx = 0;
                dy = (int) ds;
                if (dy < 5) {   // Accelerate until g-force becomes 5.
                    ds += 0.3;
                }                
                y += dy;
            } else { // If not dying, still playable
                // Update x increment.
                dx = 0;
                if (input.left.down) {  // If the user presses left
                    dx = -xSpeed;       // Player's x acceleration will be -2.
                }                
                if (input.right.clicked || 
                        input.right.down) { // If the user presses left
                    dx = xSpeed;            // Player's x acceleration will be 2 
                }

                // Update y increment.
                // If the player is grounded
                if (grounded && y + height == ground) {
                    ds = ySpeed;        // Default g-force
                } // Once topped to a tile 
                else if (topped) {    
                    ds = ySpeed;        // Start falling.
                } // If going beyond the bottom end of the screen
                else if (y + height + ds < Commons.BOARD_HEIGHT) {
                    ds = ds + 0.5;      // Accelerate vertically.
                }

                // If the user clicked jump key
                if (input.jump.clicked && grounded) { 
                    ds = -8;            // Move upward
                }
                // If jumped on top of the enemy
                if (crushedEnemy) {     
                    ds = -3;            // Slightly jump over the enemy
                    crushedEnemy = false;
                }

                dy = (int) ds;          // Update dy

                // If falling on the ground in the next tick
                if (dy > 0 && y + height < Commons.GROUND && willBeGrounded()) {
                    int yt1 = y + dy + height;
                    int backoff = yt1 - (yt1 >> 4) * ES;
                    if (dy > 1 && backoff > 0) {
                        dy -= backoff;  // Adjust dy so that it doesn't go over
                    }                   // the ground.
                }

                // Ending of the game: When reached the flag. 
                reachedEnd = x + width >= Commons.X_MAX + ES / 2;
                if (reachedEnd) {
                    if (firstTime) {    // Only when first reached the end.
                        gameWon();
                        int flagScore = (Commons.GROUND - y) / ES * 100;
                        ((FlagTile) Tile.flag).setScore(flagScore);
                        firstTime = false;
                    }
                    dx = 0;
                    reachedPollBottom = y + height >= Commons.GROUND - ES;
                    flagReachedBottom = ((FlagTile) Tile.flag).reachedBottom();
                    enteredCastle = x + width >= Commons.X_MAX + 7 * ES;
                    if (!reachedPollBottom) {
                        dy = ySpeed;    // Keep going down to the poll bottom.
                    } else if (!flagReachedBottom) {
                        dy = 0;         // Wait until flag reaches the bottom.                         
                    } else if (!faceLeft) {
                        if (leftCount == 0) {
                            x += width;
                            dir = 2;    // Face left to go over the flag.
                            game.setPMax(Commons.PLAYER_XMAX + width);
                        } else if (leftCount > 10) {
                            faceLeft = true;
                        }
                        leftCount++;
                    } else if (!jumped) {
                        dx = xSpeed;    // Move to the right.
                        ds = -1.5;      // Jump slightly out of the platform.
                        jumped = true;
                    } else if (!enteredCastle) {
                        dx = xSpeed;    // Move to the right to the castle.
                    } else {            // Once entered the castle
                        setVisible(false);  // Set invisible.
                    }
                }

                // Make a movement.
                int oldX = x;
                move(dx, dy);       // Updates x and y.
                netDx = x - oldX;   // Updates net dx.
            }

            /* Boundary controls. */
            // Don't go beyond the left end of the screen.
            int offset = level.getOffset();
            if (x <= offset) {
                x = offset;
            }
            // Right end of the screen
            if (x + width >= level.getWidth() * ES) {
                x = level.getWidth() * ES - width;
            }
            // Top of the screen
            if (y <= 0) {
                y = 0;
            }
            // When falls to the bottom, start death timer.
            if (y > Commons.BOARD_HEIGHT) {
                deathTime++;
            }

            /* Add a shot. */
            if (fired && input.attack.clicked) {
                Shot shot = new Shot(level);
                int dxShot = 0;
                int xShot = 0;
                int yShot = y + (height - shot.height) / 2;
                if (dir == 2) {     // If facing left
                    dxShot = -1;    // Shot moves to the left.
                    xShot = x - shot.width;
                } else if (dir == 3) {  // If facing right 
                    dxShot = 1;         // Shot moves to the right.
                    xShot = x + width;
                }
                shot.setX(xShot);
                shot.setY(yShot);
                shot.setDx(dxShot);
                level.add(shot);
            }
        }
    }

    /**
     * Draws the sprite on the screen.
     *
     * @param screen The screen to be displayed on.
     */
    @Override
    public void render(Screen screen) {
        super.render(screen);
                
        int flip1 = 0; 
        if (dir == 2) { // If facing left
            flip1 = 1;  // Mirror the sprite (because we only have facing right
        }               // image)
        // Animation based on walking distance.
        // ((walkDist >> 3) & 1) will either be a 1 or a 0 depending on walkDist
        // Becomes 1 every other square (8 pixels)
        int ySCur = yS + ((walkDist >> 3) & 1) * (height / PPS); 
 
        int doRender = 1;
        // If in invulnerable state during the game
        if (invulnerableTime > 0 && !reachedEnd) {
            doRender = (invulnerableTime >> 1) & 1; // Blink every two ticks.
        }

        if (doRender == 1) {
            int xSCur = xS;
            if (immortalTime > 0) { // If in immortal state            
                xSCur = xS + bNum * wS;  // Animation based on bNum.
            }
            // Determines if the image should be mirrored horizontally.
            boolean mirrorX = (flip1 == 1); 
            // Loop through each square to render.
            for (int ii = 0; ii < wS; ii++) {
                int xeff = ii; // Effective x pixel
                if (mirrorX) {
                    xeff = (wS - 1) - ii;  // Reverses the pixel for mirroring 
                }
                for (int jj = 0; jj < hS; jj++) {
                    screen.render(x + xeff * PPS, y + jj * PPS, 
                            (xSCur + ii) + (ySCur + jj) * colNum, flip1);  
                }
            }
        }
    }

    /**
     * The sprite is hurt by another sprite.
     * 
     * @param damage An integer containing damage to this sprite.
     */
    @Override
    public void hurt(int damage) { 
        // If in immortal OR invulnerable state,
        if (invulnerableTime > 0 || immortalTime > 0) {
            return;  // Skip the rest of the code.
        }
        
        if (enlarged || fired) { // If enlarged or have eaten flower
            invulnerableTime = Commons.TPS * 2; // Set invulnerable time to 2s.
        } else {            // If in original state
            setDying(true); // Dies.
            ds = -5;        // Jumps to the air.
        }
    }

    /**
     * What happens when the player wins.
     */
    public void gameWon() {
        invulnerableTime = Commons.TPS * 5; // Sets the invulnerable time to 5s.
        game.won();                         // Set the game won.
    }

    public int getLives() {
        return lives;
    }

    public void resetGame() {
        game.resetGame();
    }

    public void setCrushedEnemy(boolean crushed) {
        crushedEnemy = crushed;
    }

    public void addCoinCount() {
        game.addCoinCount();
    }

    public void eatMushroom(int score) {
        addScore(score);
        if (!enlarged) {
            width *= 2;
            height *= 2;
            wS = width / PPS;
            hS = height / PPS;
            unit = (int) (Math.log10(width) / Math.log10(2)); 
            aTile = Math.min(Math.pow(2, 4 - unit), 1); 
            yS += 4;
            health++;
            y -= ES;
            level.mushroom2Flower();    // Change mushrooms to flowers.
        }
        enlarged = true;
    }

    public boolean isEnlarged() {
        return enlarged;
    }

    public void setEnlarged(boolean TF) {
        enlarged = TF;
    }

    public void eatFlower(int score) {

        addScore(score);
        if (!fired) {
            yS += 8;
        }
        fired = true;
    }

    public boolean isFired() {
        return fired;
    }

    public void setFired(boolean TF) {
        fired = TF;
    }

    public void eatStarman(int score) {

        addScore(score);
        immortalTime = Commons.TPS * 9;
        immortal = true;
    }

    public boolean isImmortal() {
        return immortal;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int num) {
        score += num;
    }

    public boolean reachedEnd() {
        return reachedEnd;
    }

    public boolean enteredCastle() {
        return enteredCastle;
    }

    public int getPMax() {
        return game.getPMax();
    }

    public int getNetDx() {
        return netDx;
    }
}
