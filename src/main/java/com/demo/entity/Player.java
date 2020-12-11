package main.java.com.demo.entity;

import main.java.com.demo.Commons;

import main.java.com.demo.SuperPusheen;
import main.java.com.demo.InputHandler;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.Level;
import main.java.com.demo.level.tile.FlagTile;
import main.java.com.demo.level.tile.Tile;

/** Represents a player as a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public class Player extends Sprite {
 
    private InputHandler input;
    private SuperPusheen game;
    public int invulnerableTime = 0; // the invulnerability time the player has when he is hit
    public int immortalTime = 0; // the immortal time the player has when it eats the starman.
    private double ds;  // y velocity     
    private boolean crushedAlien, enlarged, fired, immortal;
    private boolean firstTime, reachedEnd, reachedPollBottom, flagReachedBottom, faceLeft, jumped, enteredCastle;
    private int leftCount;    
    private int netDx;
    
    public Player(InputHandler input, Level level, SuperPusheen board) {                
        super(level);        
        this.input = input;                
        game = board; 
        
        lives = 3;        
        scale = 4;
        
        initPlayer();
    }    
    
    public void initPlayer() {
        
        width = height = ES;
        wS = width / PPS;
        hS = height / PPS;
        unit = (int) (Math.log10(width)/Math.log10(2)); // the size of block to be used (4 for 16 px sprite and 3 for 8px sprite)
        aTile = Math.min(Math.pow(2, 4 - unit), 1); // 1 for unit 3, 1 for unit 4, 0.5 for unit 5 (big Pusheen)
        
        xS = 0;
        yS = 10;
        
        // Initial coordinates of the player sprite.
        int START_X = Commons.PLAYER_XI;
        setX(START_X);
        
        int START_Y = Commons.GROUND - height;
        setY(START_Y);       
        ground = START_Y + height;
        
        dir = 3;    // facing right when first created
        
        xSpeed = 2;
//        ySpeed = 2;
        
        dx = 0;
        dy = ySpeed;
        ds = 0;
        score = 0;
        
        crushedAlien = enlarged = fired = immortal = false;   
        reachedEnd = reachedPollBottom = flagReachedBottom = faceLeft = jumped = enteredCastle = false;
        firstTime = true;
        leftCount = 0;        
    }    
        
    // Positions the player in horizontal direction.
    @Override
    public void tick() {  
        super.tick();
        
        if (invulnerableTime > 0) invulnerableTime--; // if invulnerableTime is above 0, then minus it by 1.
        if (immortalTime > 0) { // While Starman is in effect, 
            immortalTime--; // minus it by 1.         
            bNum = (bCounter / scale) % numStage;
            bCounter++;          
        }
//        int xa = 0; // x acceleration
//        int ya = 0; // y acceleration
//        if (input.jump.down) 
//            ya--;
//        else
//            ya++;// if the player presses up then his y acceleration will be -1
//        if (input.left.down) xa--; // if the player presses left then his x acceleration will be -1
//        if (input.right.down) xa++; // if the player presses up right his x acceleration will be 1
//        dx = xa;
//        dy = ya;
                        
        /* x increment. */
        dx = 0;
        if (input.left.down) dx = -xSpeed; // if the player presses left then his x acceleration will be -1 
        if (input.right.clicked || input.right.down) dx = xSpeed;     
        
        /* y increment. */
        int g = 8;  // gravitational force        
        
        if (grounded && y + height == ground)
            ds = 1;
        else if (topped) {
            ds = 1;
        }
        else if (y + height + ds < Commons.BOARD_HEIGHT)
            ds = ds + 0.5;
        
        if (input.jump.clicked && grounded)
            ds = -g;
        if (crushedAlien) { // Slightly jump over the enemy
            ds = -3;
            crushedAlien = false;
        }
        
        dy = (int) ds;
        
        if (dy > 0  && y + height < Commons.GROUND && willBeGrounded()) {
            int yt1 = y + dy + height;
            int backoff = yt1 - (yt1 >> 4) * ES;
            if (dy > 1 && backoff > 0)
                dy -= backoff;
        }
        
        /* Boundary controls. */
        // right end of the screen
        if (x + width >= level.W * ES)
            x = level.W * ES - width;
        
        // top of the screen
        if (y <= 0) 
            y = 0;             
        
        // When falls to the bottom, die immediately and start over.
        if (y > Commons.BOARD_HEIGHT) { 
            lives--;
            resetGame();
        }
        // Don't go beyond the left end of the screen.
        int xScroll = level.getOffset();
        if (x <= xScroll) 
            x = xScroll;        
        
        // When reached the flag
        reachedEnd = x + width >= Commons.X_MAX + ES / 2;     
        if (reachedEnd) {
            if (firstTime) {
                gameWon();
                ((FlagTile)Tile.flag).setScore((Commons.GROUND - y) / ES * 100);
                firstTime = false;
            }
            dx = 0;
            reachedPollBottom = y + height >= Commons.GROUND - ES;
            flagReachedBottom = ((FlagTile)Tile.flag).reachedBottom();
            enteredCastle = x + width>= Commons.X_MAX + 7 * ES;            
            if (!reachedPollBottom) {
                dy = 1;                
            } else if (!flagReachedBottom) {
                dy = 0;// wait                            
            } else if (!faceLeft) {
                if (leftCount == 0) {
                    x += width;
                    dir = 2; // change direction to left
                    game.setPMax(Commons.PLAYER_XMAX + width);
                } else if (leftCount > 10) {
                    faceLeft = true;
                }
                leftCount++;            
            } else if (!jumped){                
                dx = xSpeed;
                ds = -1.5;
                jumped = true;                
            } else if (!enteredCastle) {
                dx = xSpeed;
            } else {
                setVisible(false);
            }
        }
        
        int oldX = x;
        move(dx, dy);
        netDx = x - oldX;
        
        /* Add a shot. */
        if (fired && input.attack.clicked) {
            Shot shot = new Shot(level);
            int dxShot = 0;
            int xShot = 0;
            int yShot = y + (height-shot.height)/2;
            if (dir == 2) { // facing left
                dxShot = -1;
                xShot = x - shot.width;
            } 
            else if (dir == 3) {// facing right 
                dxShot = 1;
                xShot = x + width;
            } 
            shot.setX(xShot);
            shot.setY(yShot);
            shot.setDx(dxShot);
            level.add(shot);                       
        }
    }       
    
    /** What happens when the player touches an entity. */
    @Override
    protected void touchedBy(Sprite sprite) {
        if (sprite instanceof Alien)  {// if the sprite is an enemy
//            sprite.touchedBy(this); // calls the touchedBy() method in the sprite's class            
            if (enlarged) {
                width /= 2;
                height /= 2;
                wS = width / PPS;
                hS = height / PPS;
                unit = (int) (Math.log10(width)/Math.log10(2)); // the size of block to be used (5 for 32 px, 4 for 16 px sprite, and 3 for 8px sprite)
                aTile = Math.min(Math.pow(2, 4 - unit), 1); // 1 for unit 3, 1 for unit 4, 0.5 for unit 5 (big Pusheen)
                yS -= 4;
                enlarged = false;
                level.flower2Mushroom();    // change flowers back to mushrooms.
                if (fired) {
                    yS -= 8;
                    fired = false;
                }
            }
        }
        if (sprite instanceof HiddenSprite && !sprite.removed)
            sprite.touchedBy(this);
    }
    
    @Override
    public void render(Screen screen) {
        int yt = yS;
        
        int flip1 = 0; // flip1 will equal 0.
        if (dir == 2) { // if the direction is 2 (left)
            flip1 = 1; // mirror the sprite (because we only have facing right image)
        }
        // ((walkDist >> 3) & 1) will either be a 1 or a 0 depending on the walk distance (Used for walking effect by mirroring the sprite)
        // Becomes 1 every other square (8 pixels)
        yt += ((walkDist >> 3) & 1) * (height / PPS); // animation based on walk distance (0 is standing still and 2 or 4 is moving)            
        
        
        int sw = screen.getSheet().width;   // width of sprite sheet (256)
        int colNum = sw / PPS;    // Number of squares in a row (32)    
//        screen.render(x + PPS * flip1, y, xS + yt * colNum, flip1); // render the top-left part of the sprite         
//        screen.render(x - PPS * flip1 + PPS, y, (xS + 1) + yt * colNum, flip1);  // render the top-right part of the sprite
//        screen.render(x + PPS * flip1, y + PPS, xS + (yt + 1) * colNum, flip1); // render the bottom-left part of the sprite
//        screen.render(x - PPS * flip1 + PPS, y + PPS, xS + 1 + (yt + 1) * colNum, flip1); // render the bottom-right part of the sprite
        
        int doRender = 1;
        if (invulnerableTime > 0 && !reachedEnd)
            doRender = (invulnerableTime >> 1) & 1;
        
        if (doRender == 1) {
            int xSCur = xS;
            if (immortalTime > 0) // if immortalTime is above 0, 
                xSCur = xS + bNum * wS;  // animation based on walk distance (bNum cycles through 0-3.)   

            boolean mirrorX = (flip1 == 1); // determines if the image should be mirrored horizontally.
            for (int ii = 0; ii < wS; ii++) {
                int xeff = ii; // effective x pixel
                if (mirrorX) xeff = (wS-1) - ii;  // Reverses the pixel for a mirroring effect
                for (int jj = 0; jj < hS; jj++) {
                    screen.render(x + xeff * PPS, y + jj * PPS, (xSCur + ii) + (yt + jj) * colNum, flip1); // render the top-left part of the sprite         
                }
            }
        }        
    }
    
    @Override
    public void hurt(int damage) { // mob hurts this sprite
        if (invulnerableTime > 0 || immortalTime > 0) return; // if hurt time OR invulnerableTime is above 0, then skip the rest of the code.
        
        super.hurt(damage); // Actually change our health
        if (enlarged || fired) invulnerableTime = 100; // invulnerable time is set to 30        
    }
    
    /** What happens when the player wins */
    public void gameWon() {
        invulnerableTime = 60 * 5; // sets the invulnerable time to 300
        game.won(); // win the game
    }    
    
    public int getLives() {
        return lives;
    }
    
    public void resetGame() {
        game.resetGame();
    }
    
    public void setCrushedAlien(boolean crushed) {
        crushedAlien = crushed;
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
            unit = (int) (Math.log10(width)/Math.log10(2)); // the size of block to be used (5 for 32 px, 4 for 16 px sprite, and 3 for 8px sprite)
            aTile = Math.min(Math.pow(2, 4 - unit), 1); // 1 for unit 3, 1 for unit 4, 0.5 for unit 5 (big Pusheen)            
            yS += 4;
            health++;
//            if (grounded)
                y -= ES;
            level.mushroom2Flower();    // change mushrooms to flowers.
        }
        enlarged = true;
    }
    
    public boolean isEnlarged() {
        return enlarged;
    }
    
    public void eatFlower(int score) {
        
        addScore(score);
        if (!fired)
            yS += 8;
        fired = true;        
    }
    
    public void eatStarman(int score) {
        
        addScore(score);
        immortalTime = 500;
        immortal = true; 
    }
    
    public boolean isImortal() {
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
