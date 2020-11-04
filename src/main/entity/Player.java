package main.entity;

import main.Commons;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.SuperPusheen;
import main.InputHandler;
import main.gfx.Screen;
import main.level.Level;

/** Represents a player as a sprite.
 *  Keeps the image of the sprite and the coordinates of the sprite.
    @author zetcode.com */
public class Player extends Sprite {
 
    private InputHandler input;
    private final int ES = Commons.ENTITY_SIZE;
    public int score; // the player's score
    private SuperPusheen game;
    public int invulnerableTime = 0; // the invulnerability time the player has when he is hit
    private double ds;  // y velocity     
    
    public Player(InputHandler input, Level level, SuperPusheen board) {                
        super(level);
        game = board;
        this.input = input;                
        xS = 4;
        yS = 2;        
        lives = 3;
        
        initPlayer();
    }    
    
    public void initPlayer() {
       
        String playerImg = "src/Pusheen_Right.png";                      
       
        try {
            BufferedImage source = ImageIO.read(new File(playerImg));
//            BufferedImage source = ImageIO.read(Board.class.getResourceAsStream("/Pusheen_Right.png"));
            setImage(source);
        } catch (IOException ex) {
            String msg = String.format("No such file found: %s", ex.getMessage());
            System.out.println(msg);
        }
        
//        var ii = new ImageIcon(playerImg);        
//        width = ii.getImage().getWidth(null);
        
        // Initial coordinates of the player sprite.
        int START_X = Commons.PLAYER_XI;
        setX(START_X);
        
        int START_Y = Commons.PLAYER_YI;
        setY(START_Y);       
        ground = START_Y + height;
        
        xSpeed = 2;
        ySpeed = 2;
        
        dx = 0;
        dy = ySpeed;
        ds = 0;
        score = 0;
    }    
        
    // Positions the player in horizontal direction.
    @Override
    public void tick() {  
        super.tick();
        
        if (invulnerableTime > 0) invulnerableTime--; // if invulnerableTime is above 0, then minus it by 1.
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
                        
        
        dx = 0;
        if (input.left.down) dx = -xSpeed; // if the player presses left then his x acceleration will be -1        
//        if (x < Commons.PLYAER_XMAX && input.right.down) dx = 1;        
        if (input.right.clicked || input.right.down) dx = xSpeed;        
        
        if (y < Commons.GROUND)        grounded = isGrounded();
//        if (y + ES < Commons.BOARD_HEIGHT) topped = (y > ES && checkTopped()) || y <= ES;
        topped = checkTopped();
    
        
        int g = 8;  // gravitational force        
        
        if (grounded)
            ds = 1;
        else if (topped) {
            ds = 1;
        }
        else if (y + ds + height < Commons.BOARD_HEIGHT)
            ds = ds + 0.5;
        
        if (input.jump.clicked && grounded)
             ds = -g;
        
        dy = (int) ds;
        
        if (dy > 0  && y < Commons.GROUND && willBeGrounded()) {
            int yt1 = y + dy + ES;
            int backoff = yt1 - (yt1 >> 4) * 16;
            if (dy > 1 && backoff > 0)
                dy -= backoff;
        }
//        if (dy > 1 && y + dy + ES > ground)
//            dy = ground - y - ES;
        
        
//        int jumpDist = ground - y - height;
//        if (jumpDist >= MAX_JUMP)
//            dy = ySpeed; // Once reached the maximum jump height, fall to the ground (positive y)
//        
//        // Jump
//        if (input.jump.clicked && grounded) dy = - ySpeed;
//                 
//        if (input.jump.down && jumpDist < MAX_JUMP) dy = dy;   
//        else dy = ySpeed;
                
        
        
        // This is to prevent the player from going out of the frame.
//        if (x <= ES)    // left end in the beginning
//            x = ES+1;        
                
        int xScroll = level.getOffset();
        if (x <= xScroll) // left end in the middle of the game
            x = xScroll;
        
        if (x + width + ES >= level.W * Commons.ENTITY_SIZE) // right end of the game
            x = level.W * Commons.ENTITY_SIZE - width - ES - 1;
        
//        if (y <= ES) // top of the game
//            y = ES+1;
        if (y <= 0) // top of the game
            y = 0;                       
//        if (y >= Commons.BOARD_HEIGHT - height - ES) //bottom of the game
//            y = Commons.BOARD_HEIGHT - height - ES -1;
//            health = 0;
        
//        if (y > Commons.GROUND)
//            System.out.println("below the ground");
        
        if (y + dy + height < Commons.BOARD_HEIGHT)
            move(dx, dy);        
        else
            hurt(health);
        
//        System.out.println("dy = " + dy + ", y = " + y);
        
        if (input.attack.clicked) 
            level.add(new Shot(x, y, level));                       
    }       
    
    /** What happens when the player touches an entity. */
    protected void touchedBy(Sprite sprite) {
        if (sprite instanceof Alien) { // if the entity is not a player.
            sprite.touchedBy(this); // calls the touchedBy() method in the entity's class
////            hurt(1);            
        }
    }
    
    @Override
    public void render(Screen screen) {
        int xt = xS;
        // Becomes 1 every other square (8 pixels)
        int flip1 = (walkDist >> 3) & 1; // This will either be a 1 or a 0 depending on the walk distance (Used for walking effect by mirroring the sprite)
        int flip2 = (walkDist >> 3) & 1; // This will either be a 1 or a 0 depending on the walk distance (Used for walking effect by mirroring the sprite)

        if (dir > 1) { // if the direction is larger than 1 (2 is left / 3 is right)...
            flip1 = 0; // flip1 will equal 0.
            flip2 = ((walkDist >> 4) & 1); // This will either be a 1 or a 0 depending on the walk distance (Used for walking effect by mirroring the sprite)
            if (dir == 2) { // if the direction is 2 (left)
                flip1 = 1; // mirror the sprite (because we only have facing right image)
            }
            xt += ((walkDist >> 3) & 1) * 2; // animation based on walk distance (0 is standing still and 2 is moving)            
        }
        
        int sw = screen.getSheet().width;   // width of sprite sheet (256)
        int colNum = sw / Commons.PPS;    // Number of squares in a row (32)    
//        screen.render(x, y, xt + yS * colNum, flip1);
        int PPS = Commons.PPS;
        screen.render(x + PPS * flip1, y, xt + yS * colNum, flip1); // render the top-left part of the sprite         
        screen.render(x - PPS * flip1 + PPS, y, (xt + 1) + yS * colNum, flip1);  // render the top-right part of the sprite
        screen.render(x + PPS * flip1, y + PPS, xt + (yS + 1) * colNum, flip1); // render the bottom-left part of the sprite
        screen.render(x - PPS * flip1 + PPS, y + PPS, xt + 1 + (yS + 1) * colNum, flip1); // render the bottom-right part of the sprite
    }
    
    /** What happens when the player wins */
    public void gameWon() {
        level.player.invulnerableTime = 60 * 5; // sets the invulnerable time to 300
        game.won(); // win the game
    }    
    
    public int getLives() {
        return lives;
    }
    
    public void resetGame() {
        game.resetGame();
    }
}
