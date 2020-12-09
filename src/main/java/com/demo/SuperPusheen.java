package main.java.com.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import main.java.com.demo.screen.*;
import main.java.com.demo.entity.*;
import main.java.com.demo.gfx.*;
import main.java.com.demo.level.Level;
import main.java.com.demo.level.tile.Tile;


/** A class including the game logic for Space Invaders game. 
 * @author zetcode.com */
public class SuperPusheen extends JPanel implements Runnable {

    public final String NAME = "Super Pusheen";
    private final int DELAY = Commons.DELAY; // Determines the speed of the animation. 
    
    private Dimension d;     
    private Level level; // This is the current level you are on.    
    private List<Entity> grounds, pipes, blocks;
    public Player player;
    
    private Screen screen; // Creates the main screen
    private InputHandler input;
    private Menu menu;
    
    private int deaths = 0;
    public int gameTime; // Main value in the timer used on the dead screen.
    public int timeLeft;    // Time left before the game is over.
    private int playerDeadTime; // the paused time when you die before the dead menu shows up.
    private int pendingLevelChange; // used to determined if the player should change levels or not.
    private int wonTimer; // the paused time when you win before the win menu shows up.
    public boolean hasWon; // If the player wins this is set to true
    private int tickCount; // Used in the ticking system
   
    private boolean inGame; // This stores if the game is running or paused
    private String message = "Game Over";
    
    private BufferedImage image, source;
    private int[] pixels; 
    private int[][] sourcePixels;
    private final int B_WIDTH = Commons.BOARD_WIDTH;
    private final int B_HEIGHT = Commons.BOARD_HEIGHT;
    private final int SCALE = 2;
       
    private final int ES = Commons.ENTITY_SIZE; // Default entity size (16 px)
    private int numNoG;   // Number of ground blocks in a row.
    public byte[] tiles; // an array of all the tiles in the world.    
    private int numTileX, numTileY;    // Number of tiles in x direction
    private int xScroll, pMax;
    private int yScroll;
    private int[] levelNum = new int[2];
    private int lives, numCoins;  // player's lives, number of coins collected
    
    /* I guess I should explain something real quick. The coordinates between tiles and entities are different.
    * The world coordinates for tiles is 128x128
    * The world coordinates for entities is 2048x2048
    * This is because each tile is 16x16 pixels big
    * 128 x 16 = 2048.
    * When ever you see a ">>", it means that it is a right shift operator. This means it shifts bits to the right (making them smaller)
    * x >> 4 is the equivalent to x / (2^4). Which means it's dividing the X value by 16. (2x2x2x2 = 16)
    * xt << 4 is the equivalent to xt * (2^4). Which means it's multiplying the X tile value by 16.
    * 
    * These bit shift operators are used to easily get the X & Y coordinates of a tile that the entity is standing on. */
    
    public SuperPusheen() {
        
        input = new InputHandler();
        initBoard();        
    }

    private void initBoard() {
        // Set up the panel board
        addKeyListener(input);
        setFocusable(true);
        setBackground(Color.black);
        d = new Dimension(B_WIDTH * SCALE, B_HEIGHT * SCALE); 
        
        setMinimumSize(d); // sets the minimum size of the window
        setMaximumSize(d); // sets the maximum size of the window
        setPreferredSize(d); // sets the preferred size of the window
        
        // Set up the background
        try {
            bgInit();            
        
        } catch (IOException ex) {
            String msg = String.format("No such file found: %s", ex.getMessage());
            System.out.println(msg);
        }
         
        tickCount = 0; 
        initGame();        // will be done in TitleMenu()
        
        setMenu(new TitleMenu(this));  // Sets the menu to the title menu.
    }
        
    private void bgInit() throws IOException{
        
        /* This sets up the screens, loads the icons.png spritesheet. */
        screen = new Screen(B_WIDTH, B_HEIGHT, new SpriteSheet(
                    ImageIO.read(SuperPusheen.class.getResourceAsStream(
                            "/main/resources/iconsPusheen.png"))));  
        
        image = new BufferedImage(B_WIDTH, B_HEIGHT, BufferedImage.TYPE_INT_RGB);
//        WritableRaster wr = image.getRaster();  // Returns raster object.
//        DataBuffer db = wr.getDataBuffer(); // Returns Raster.dataBuffer object.
//        DataBufferInt dbi = (DataBufferInt) db; // Casts DataBufferInt.
//        pixels = dbi.getData(); // Returns data (int[]) object.
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();        
                
        // Load background image.
//        source = ImageIO.read(Board.class.getResourceAsStream("/bg_noObj.png")); 
        String path = "src/main/resources/bg_1-1.png";
        source = ImageIO.read(new File(path));
        sourcePixels = ImageTool.convertTo2D(source);        
        numTileX = source.getWidth() / ES;  // 224
        numTileY = source.getHeight() / ES; // 15
        /* Create a map for the current level. */
//        tiles =  LevelGen.createAndValidateTopMap(numTileX, numTileY); // create a surface map for the level
        
        // Extract level numbers from the background image name
        Scanner scan = new Scanner(path);
        scan.useDelimiter("\\D*"); // zero or more occurences of any character other than a digit
        int ii = 0;
        while (scan.hasNextInt()) {      
            levelNum[ii] = scan.nextInt();;
            ii++;
        }
        
        /* Add ground. */
        int numGRow = Commons.NUM_GROUND_ROW;
        int h = Commons.BOARD_HEIGHT - ES * numGRow; // 2 layers of ground blocks.
        int[][] GPOS = Commons.GPOS;
        grounds = new ArrayList<>(GPOS.length);
        int to = 0;
        int from = 0;
        for (int yi = numGRow; yi >= 1; yi--) { // Loops twice
            for (int[] a : GPOS) {
                int beg = a[0];
                int end = a[1];                
                to = beg;
                numNoG += Math.max((to - from)/ES - 1, 0);
                from = end;
                int y = B_HEIGHT - ES * yi;
                for (int x = beg; x <= end; x+=ES) { // A loop inside a loop that loops through the width of the map.
                    GBlock gb = new GBlock(x, y);
                    if (yi == numGRow || (x == end && x != source.getWidth()-ES) || (x == beg && x != 0)) // Add only where the player might interact.
                        grounds.add(gb);
                    int[][] gBlockPixels = ImageTool.convertTo2D(gb.getImage());
                    // Add the ground block to the background.
                    for (int j = 0; j < gb.getWidth(); j++) {
                        for (int i = 0; i < gb.getHeight(); i++) {
                            int cc = gBlockPixels[i][j];
                            if (cc != 0) sourcePixels[i+gb.getY()][j+gb.getX()] = cc;// Add only existing pixels (background value is 0)
                        }
                    }        
                }
            }
        }
        
        /* Create and add pipes to the background. */  
        int[][] PPOS = Commons.PPOS; // Size and initial (x, y) position of pipes.
        pipes = new ArrayList<>(PPOS.length);
        for (int[] p : PPOS) {
            Pipe pipe = new Pipe(p[0], p[1], p[2]); 
            pipes.add(pipe);    // add the pipe to the pipe array
            int[][] pipePixels = ImageTool.convertTo2D(pipe.getImage());
            // Add the pipe to the background.
            for (int j = 0; j < pipe.getWidth(); j++) {
                for (int i = 0; i < pipe.getHeight(); i++) {
                    int cc = pipePixels[i][j];
                    if (cc != 0) sourcePixels[i+pipe.getY()][j+pipe.getX()] = cc;// Add only existing pixels (background value is 0)
                }
            }            
        }       
        
        /* Create and add blocks to the background. */
        int y1 = (B_HEIGHT-48); 
        // Ascending blocks
        int[][] BPOS_A = Commons.BPOS_A;
        blocks = new ArrayList<>(BPOS_A.length);
        for (int[] b : BPOS_A) { // Loop through the BPOS_A list                
            int x0 = b[0]; // x position of 1st block of base row
            int numX = b[1]; // number of blocks in the base row,
            int numY = b[2]; // number of rows
            for (int yi = 0; yi < numY; yi++) { // Loops through vertical way
                int xBeg = x0 + yi * ES;
                int y = y1 - yi * ES;
                for (int xi = 0; xi < numX; xi++) {
                    int x = xBeg + xi * ES;
                    // If the block is first or last of the row, add to blocks. (they will interact with player)
                    Block block = new Block(x, y);
                    if (xi == 0 || xi == numX-1)
                        blocks.add(block);
                    int[][] pipePixels = ImageTool.convertTo2D(block.getImage());
                    // Add the block to the background.
                    for (int j = 0; j < block.getWidth(); j++) {
                        for (int i = 0; i < block.getHeight(); i++) {                            
                            sourcePixels[i+block.getY()][j+block.getX()] = pipePixels[i][j];
                        }
                    }    
                }
                numX--;
            }
        }                
        // Descending blocks
        int[][] BPOS_D = Commons.BPOS_D;
        for (int[] b : BPOS_D) { // Loop through the BPOS_D list                
            int x0 = b[0]; // x position of 1st block of base row
            int numX = b[1]; // number of blocks in the base row,
            int numY = b[2]; // number of rows
            for (int yi = 0; yi < numY; yi++) { // Loops through vertical way             
                int y = y1 - yi * ES;
                for (int xi = 0; xi < numX; xi++) {
                    int x = x0 + xi * ES;
                    Block block = new Block(x, y);
                    blocks.add(block);
                    int[][] blockPixels = ImageTool.convertTo2D(block.getImage());
                    // Add the block to the background.
                    for (int j = 0; j < block.getWidth(); j++) {
                        for (int i = 0; i < block.getHeight(); i++) {                            
                            sourcePixels[i+block.getY()][j+block.getX()] = blockPixels[i][j];
                        }
                    }    
                }
                numX--;
            }
        }
        
        
        /* Add background to the current screen. */
        int xOffset = screen.xOffset; // offset of the screen from y = 0        
        int count = 0;
        for (int i = 0; i < B_HEIGHT; i++) {
            for (int j = xOffset; j < B_WIDTH+xOffset; j++) {
                pixels[count] = sourcePixels[i][j];                
                count++;
            }
        }        
    }
    
    // Create level and sprite objects for the game.
    public void initGame() {
       
        // Resets all values
        playerDeadTime = 0;
        wonTimer = 0;
        gameTime = 0;
        hasWon = false; 
        numCoins = 0;
        
        level = new Level(numTileX, numTileY, levelNum, 400); // creates the map        
        tiles = level.tileIds;        
        player = new Player(input, level, this);
        player.initPlayer();
        level.add(player);
        
        // Create aliens.        
        level.spawn();                
        
        /* Initialize the screen pixels as the background. */     
        int count = 0;
        for (int i = 0; i < B_HEIGHT; i++) {
            for (int j = 0; j < B_WIDTH; j++) {                
                screen.pixels[count] = sourcePixels[i][j];  
                count++;
            }
        } 
        xScroll = 0;
        yScroll = 0;
        pMax = Commons.PLAYER_XMAX;
    }
    
    // Create level and sprite objects for the game.
    public void resetGame() {
        // Resets all values
        playerDeadTime = 0;
        wonTimer = 0;
        gameTime = 0;
        hasWon = false;  
        
        level = new Level(numTileX, numTileY, levelNum, 400); // creates the map                
        player.level = level;
        player.initPlayer();
        level.add(player);        
        
        // Create aliens and hidden sprites.        
        level.spawn();                
        if (player.isEnlarged())
            level.mushroom2Flower();    // change mushrooms to flowers.
        
        /* Initialize the screen pixels as the background. */     
        int count = 0;
        for (int i = 0; i < B_HEIGHT; i++) {
            for (int j = 0; j < B_WIDTH; j++) {                
                screen.pixels[count] = sourcePixels[i][j];  
                count++;
            }
        } 
        xScroll = 0;
        yScroll = 0;
        pMax = Commons.PLAYER_XMAX;
    }
        

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Update the scroll.
        xScroll = Math.max(xScroll, player.x + ES - pMax);
        xScroll = Math.min(xScroll, source.getWidth() - B_WIDTH);
        int yScroll = 0;        
//        System.out.println("x = " + player.x);
//        System.out.println("xScroll = " + xScroll);
        
        /* Update the screen. */  
        int count = 0;
        for (int i = 0; i < B_HEIGHT; i++) {
            for (int j = xScroll; j < B_WIDTH + xScroll; j++) {
                screen.pixels[count] = sourcePixels[i][j];
                count++;
            }
        }        
        
        level.renderSprites(screen, xScroll, yScroll);
        level.renderBackground(screen, xScroll, yScroll);
        
        
        renderGui(); // calls the renderGui() method.
        
        
        // Overwrite the updated screen to the image data
        for (int y = 0; y < screen.H; y++) {
            for (int x = 0; x < screen.W; x++) {
                //loops through all the pixels on the screen
//                int cc = screen.pixels[x + y * screen.W]; // finds a pixel on the screen.
//                if (cc < 255) pixels[x + y * B_WIDTH] = colors[cc]; // colors the pixel accordingly.
                pixels[x + y * B_WIDTH] = screen.pixels[x + y * screen.W]; // colors the pixel accordingly.
            }
        }
        
        /* Draw the screen. */       
        int ww = B_WIDTH * SCALE; //scales the pixels horizontally so we can see the screen good.
        int hh = B_HEIGHT * SCALE; //scales the pixels vertically so we can see the screen good.
        int xo = (getWidth() - ww) / 2; //gets an offset for the image.
        int yo = (getHeight() - hh) / 2; //gets an offset for the image.
        g.drawImage(image, xo, yo, ww, hh, null); //draws the image on the window
        
        
        // Synchronises the painting on systems that buffer graphics events.
        // Without this line, the animation ight not be smooth on Linux.
        Toolkit.getDefaultToolkit().sync();       
    }

    /* Renders the GUI on the screen used in the main game. */
    private void renderGui() {      
        // Show the stats
        int PPS = Commons.PPS;        
        String[] stringList = {"SCORE", "COINS", "WORLD", "TIME", "LIVES"};
        int[] numList = {player.getScore(), numCoins, level.getWorld(), timeLeft, player.getLives()};
        int secLen = B_WIDTH / stringList.length;  // Width of each section, 300 / 5 = 60 
        int yGap = 2;
        for (int i = 0; i < stringList.length; i++) {
            // Draw section names.
            String str = stringList[i];
            int strLen = str.length() * PPS;
            Font.draw(str, screen, (secLen - strLen)/2 + secLen * i, yGap, main.java.com.demo.gfx.Color.WHITE);
            // Draw corresponding values.
            String numStr = Integer.toString(numList[i]);
            if (i == 2) // Adjust world string.
                numStr += "-" + level.getStage();
            int numStrLen = numStr.length() * PPS;
            Font.draw(numStr, screen, (secLen - numStrLen)/2 + secLen * i, yGap + PPS + yGap, main.java.com.demo.gfx.Color.WHITE);
        }
        
        //if there is an active menu, then it will render it.
        if (menu != null) {
            menu.render(screen);
        }
        
    }
    
    // This method is called after our JPanel has been added to the JFrame component.
    // This method is often used for various initialisation tasks.
    @Override
    public void addNotify() {
        super.addNotify();
        start();
    }        
    
    @Override
    public void run() { // only called once but have a while loop inside.
        
        long beforeTime, timeDiff, sleep;
        int frames = 0;
        int ticks = 0;
        long lastTimer1 = System.currentTimeMillis(); // current time in milliseconds.
        beforeTime = System.currentTimeMillis();
        
        while (inGame) {
            
            ticks++; //increases amount of ticks.
            update();
            frames++; // increases the amount of frames
            repaint();
            
            // We want our game to run smoothly, at a constant speed. 
            // Therefore, we compute the system time that cycle() and repaint()
            // take each loop (timeDiff) because they might take different time at
            // various while cycles. This way we can ensure that each while cycle 
            // runs at a constant time. 
            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;
            
//            System.out.println("sleep: " + sleep + " ms");
            if (sleep < 0)
                sleep = 2;
            
            try {
                // Causes the currently executing thread to sleep (temporarily 
                // cease execution) for "sleep" ms
                Thread.sleep(sleep); 
            } catch (InterruptedException e) {
                
                String msg = String.format("Thread interrupted: %s", e.getMessage());
                
                JOptionPane.showMessageDialog(this, msg, "Error", 
                        JOptionPane.ERROR_MESSAGE);                
            }
            beforeTime = System.currentTimeMillis();
            
            if (System.currentTimeMillis() - lastTimer1 > 1000) { //updates every 1 second
                lastTimer1 += 1000;//adds a second to the timer
//                System.out.println(ticks + " ticks, " + frames + " fps"); // prints out the number of ticks, and the amount of frames to the console.
                frames = 0;// resets the frames value.
                ticks = 0;// resets the ticks value.
            }
        }
    } 
           
    private void update() {    
        
        tickCount++; //increases tickCount by 1, not really used for anything.
        if (!hasFocus()) {
                input.releaseAll(); // If the player is not focused on the screen, then all the current inputs will be set to off (well up).
        } else {
            if (!player.removed && !hasWon && menu == null) {//increases tickCount by 1, this is used for the timer on the death screen.
                gameTime++;
                timeLeft = level.getTimeLim() - 2 * gameTime/60;
            } 
            
            input.tick(); // calls the tick() method in InputHandler.java            
            
            if (menu != null) { 
                    menu.tick(); // If there is an active menu, it will call the tick method of that menu.
                    level.tickTiles();
            } else {
                
                if (input.pause.clicked)
                    setMenu(new PauseMenu());
                
                if (player.removed) {
                    playerDeadTime++;
                    if (playerDeadTime > 60) {
                        setMenu(new DeadMenu()); // If the player has been removed and a second has passed, then set the menu to the dead menu.
                    }
                } 
                
                if (player.enteredCastle()) {
                    if (timeLeft > 0) {
                        timeLeft--;
                        player.addScore(50);
                    } else {
                        if (wonTimer > 0) {                    
                            if (--wonTimer == 0) {
                                setMenu(new WonMenu()); // if the wonTimer is above 0, this will be called and if it hits 0 then it actives the win menu.
                            }
                        }
                    }
                } else {
                    if (timeLeft <= 0)
                        player.hurt(player.health);
                }        
                
                level.tick(screen); // calls the tick() method in Level.java                
            }
            Tile.tickCount++; // increases the tickCount in Tile.java. Used for Water.java and Lava.java.
//            level.tick(screen); // calls the tick() method in Level.java            
        }
    } // end update  
     
    /** Use this method to switch to another menu.
     * @param menu A Menu object to which you want to switch. */
    public void setMenu(Menu menu) {
        this.menu = menu;
        if (menu != null) menu.init(this, input);
    }   
    
    /** This starts the game */
    public void start() {
        inGame = true;
        new Thread(this).start();   
    }
    
    /** This pauses the game */
    public void stop() {
        inGame = false;
    }
        
    /** This is called when the player has won the game */
    public void won() {
        wonTimer = 60 * 1; // the pause time before the win menu shows up.
        hasWon = true; //confirms that the player has indeed, won the game.
    }
    
    public int getNumTileX() {
        return numTileX;
    }
    
    public int getColNum() {
        return screen.sheet.width / Commons.PPS;
    }
    
    public void addCoinCount() {
        numCoins++;
    }
    
    /** Sets pMax for the screen.
     * @param num The maximum x for player before starting to scroll. */
    public void setPMax(int num) {
        pMax = num;
    }
    
    public int getPMax() {
        return pMax;
    }
    
} // end Board
