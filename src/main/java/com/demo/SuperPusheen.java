/* 
    The structure of the codes is derived from Miniventure game. Hence, some of
    the commnetaries are migrated from the original codes 
    (https://github.com/shylor/miniventure.git ).
 */
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

/**
 * Create a Super Pusheen game. This game is a copycat of Super Mario Bros game
 * by Nintendo. But the player is Pusheen the Cat, the internet-famous cat
 * character. (https://pusheen.com ) Goal of the game is to reach the castle at
 * the end of the map. Pusheen can move left and right and jump. When Pusheen
 * eats the flower, she can shoot missiles to defeat enemies.
 *
 * @author Hyunryung Kim
 * @email hryungk@gmail.com
 */
public class SuperPusheen extends JPanel implements Runnable {

    private final int DELAY = Commons.DELAY;// Determines speed of animation. 
    private final int TPS = Commons.TPS;    // Ticks per second.
    private final InputHandler input;       // Handles keyboard input from user.
    private final String NAME = "Super Pusheen"; // Name of the game.

    private Dimension d;    // Dimension of the window.
    private Screen screen;  // Creates the main screen.    
    private Menu menu;      // Current menu
    private Menu aboutMenu, instMenu;   // AboutMenu and InstructionsMenu
    private Level level;    // This is the current level you are on.    
    private List<Entity> grounds, pipes, blocks;
    public Player player;   // The player object of the game.    

    public boolean hasWon;  // True if the player wins the game.
    public int gameTime;    // Main value in the timer used on the dead screen.
    public int timeLeft;    // Time left before the game is over.    
    private boolean inGame; // This stores if the game is running or paused.    
    private int deadTime;   // Paused time when you die before the dead menu 
    // shows up.
    private int wonTimer;   // A timer starting after you win to count until the
    // win menu shows up.

    // image is what is drawn on the screen. source is the map image.
    private BufferedImage image, source;

    private final int B_WIDTH = Commons.BOARD_WIDTH; // Screen width
    private final int B_HEIGHT = Commons.BOARD_HEIGHT; // Screen height
    private final int SCALE = 2;    // Scales the screen up (1,∞) or down (0,1).
    private int[] pixels;           // Pixel data for the image variable.
    private int[][] sourcePixels;   // Pixel data for the map background image.    

    private final int ES = Commons.ENTITY_SIZE; // Default entity size (16 px)
    public byte[] tiles;            // An array of all the tiles in the map.    
    private int numTileX, numTileY; // Number of tiles of the map,x/y direction.
    private int xScroll, yScroll;   // x/y scroll relative to the map.
    private int pMax;               // Maximum x-position of the player 
    private int[] levelNum;         // World and level number.
    private int numCoins;           // Number of coins collected.

    /* Please read below before proceeding. 
        The coordinates in tiles and in pixels are different:
        The map coordinates in tiles are 13x224.
        The map coordinates in pixels are 208x3584.
        This is because each tile is 16x16 pixels big.
        13*16 = 208 and 224*16 = 3584.
        
        Use of big shift operators:
        Bit shift operators are used in this program to easily get the x and y
        coordinates of a tile that an entity is standing on.
        ">>" is a right shift operator. 
        This means it shifts bits to the right (making them smaller).
        For example, x >> 4 is equivalent to x / (2^4). This means it's dividing 
        the x value by 16. (2x2x2x2 = 16)
        Likewise, xt << 4 is equivalent to xt * (2^4). This means it's 
        multiplying the xt tile value by 16. */
    public SuperPusheen() {

        input = new InputHandler();
        try {
            createReadMenu();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        initBoard();
    }
    
    /* Create AboutMenu and InstructionsMenu. */
    private void createReadMenu() throws IOException {
                
        String path = "src/main/resources/AboutMenuText.txt";
        aboutMenu = new ReadMenu(parseStr(path)); // Create AboutMenu.
        path = "src/main/resources/InstMenuText.txt";
        instMenu = new ReadMenu(parseStr(path));  // Create InstructionsMenu.
    }
    
    /* Parse strings from a txt file. */
    private String[] parseStr(String path) throws IOException {
        
        Scanner fileScan = new Scanner(new File(path)); // Read text file.        
        List<String> tempList = new ArrayList<>(); // Temporary list for strings
        
        // Scan the text file line by line.        
        while (fileScan.hasNextLine()) {
            String line = fileScan.nextLine();
            tempList.add(line); // Add each line to the temporary list.
        }
        
        // Convert temporary list into a string list.
        String[] textList = new String[tempList.size()];
        for (int i = 0; i < textList.length; i++)
            textList[i] = tempList.get(i);
        return textList;
    }

    /* Initialize the board. */
    private void initBoard() {
        // Set up the panel board.
        setName(NAME);
        addKeyListener(input);
        setBackground(Color.black);    // For About and Instructions menu
        setFocusable(true);

        d = new Dimension(B_WIDTH * SCALE, B_HEIGHT * SCALE);
        setMinimumSize(d);             // Sets the minimum size of the window.
        setMaximumSize(d);             // Sets the maximum size of the window.
        setPreferredSize(d);           // Sets the preferred size of the window.

        // Set up the background.
        try {
            bgInit();
        } catch (IOException ex) {
            String msg = String.format("No such file found: %s",
                    ex.getMessage());
            System.out.println(msg);
        }

        setMenu(new TitleMenu(this));  // Sets the menu to the title menu.
    }

    /* Initialize background with the map image. */
    private void bgInit() throws IOException {

        // Set up the screen, loads the iconsPusheen.png spritesheet.
        String bgpath = "src/main/resources/iconsPusheen.png";
        screen = new Screen(B_WIDTH, B_HEIGHT, new SpriteSheet(
                ImageIO.read(new File(bgpath))));

        image = new BufferedImage(B_WIDTH, B_HEIGHT,
                BufferedImage.TYPE_INT_RGB);
        /*
        WritableRaster wr = image.getRaster();  // Returns raster object.
        DataBuffer db = wr.getDataBuffer();     // Returns Raster.dataBuffer.
        DataBufferInt dbi = (DataBufferInt) db; // Casts DataBufferInt.
        pixels = dbi.getData();                 // Returns data (int[]) object. 
         */
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        // Load background image.
        String path = "src/main/resources/bg_1-1.png";
        source = ImageIO.read(new File(path));
        sourcePixels = ImageTool.convertTo2D(source);
        numTileX = source.getWidth() / ES;  // 224
        numTileY = source.getHeight() / ES; // 15

        // Extract level numbers from the background image name.
        Scanner scan = new Scanner(path);
        scan.useDelimiter("\\D*");  // Zero or more occurences of any character 
        // other than a digit.
        int ii = 0;
        levelNum = new int[2];
        while (scan.hasNextInt()) {
            levelNum[ii] = scan.nextInt();
            ii++;
        }

        /* Add tile images to the background pixel data. */
        // Add ground.
        int numGRow = Commons.NUM_GBLOCK_ROW;
        int[][] GPOS = Commons.GPOS;
        grounds = new ArrayList<>(GPOS.length);
        for (int yi = numGRow; yi >= 1; yi--) { // For each row of gblocks
            for (int[] a : GPOS) {
                int beg = a[0];
                int end = a[1];
                int y = B_HEIGHT - ES * yi;
                // Loops through the width of the map.
                for (int x = beg; x <= end; x += ES) {
                    GBlock gb = new GBlock(x, y);
                    // Add only where the player might interact.
                    if ((yi == numGRow) || (x == beg && x != 0)
                            || (x == end && x != source.getWidth() - ES)) {
                        grounds.add(gb);
                    }
                    int[][] gBlockPixels = ImageTool.convertTo2D(gb.getImage());
                    // Add the ground block to the background.
                    for (int j = 0; j < gb.getWidth(); j++) {
                        for (int i = 0; i < gb.getHeight(); i++) {
                            int cc = gBlockPixels[i][j];
                            // Add only existing pixels (background value is 0).
                            if (cc != 0) {
                                sourcePixels[i + gb.getY()][j + gb.getX()] = cc;
                            }
                        }
                    }
                }
            }
        }

        // Create and add pipes to the background.
        int[][] PPOS = Commons.PPOS; // Size and initial x/y position of pipes.
        pipes = new ArrayList<>(PPOS.length);
        for (int[] p : PPOS) {
            Pipe pipe = new Pipe(p[0], p[1], p[2]);
            pipes.add(pipe);    // Add the pipe to the pipe array.
            int[][] pipePixels = ImageTool.convertTo2D(pipe.getImage());
            // Add the pipe to the background.
            for (int j = 0; j < pipe.getWidth(); j++) {
                for (int i = 0; i < pipe.getHeight(); i++) {
                    int cc = pipePixels[i][j];
                    // Add only existing pixels (background value is 0).
                    if (cc != 0) {
                        sourcePixels[i + pipe.getY()][j + pipe.getX()] = cc;
                    }
                }
            }
        }

        // Create and add blocks to the background.
        int y1 = (B_HEIGHT - 48);
        /// Ascending blocks
        int[][] BPOS_A = Commons.BPOS_A;
        blocks = new ArrayList<>(BPOS_A.length);
        for (int[] b : BPOS_A) {    // Loops through the BPOS_A list.
            int x0 = b[0];          // x position of 1st block of base row
            int numX = b[1];        // Number of blocks in the base row
            int numY = b[2];        // Number of rows
            for (int yi = 0; yi < numY; yi++) { // Loops through vertical way.
                int xBeg = x0 + yi * ES;
                int y = y1 - yi * ES;
                for (int xi = 0; xi < numX; xi++) {
                    int x = xBeg + xi * ES;
                    Block block = new Block(x, y);
                    // Add only where the player might interact.
                    if (xi == 0 || xi == numX - 1) // if first or last of the row
                    {
                        blocks.add(block);
                    }
                    int[][] pipePixel = ImageTool.convertTo2D(block.getImage());
                    // Add the block to the background.
                    for (int j = 0; j < block.getWidth(); j++) {
                        for (int i = 0; i < block.getHeight(); i++) {
                            sourcePixels[i + block.getY()][j + block.getX()]
                                    = pipePixel[i][j];
                        }
                    }
                }
                numX--;
            }
        }
        /// Descending blocks
        int[][] BPOS_D = Commons.BPOS_D;
        for (int[] b : BPOS_D) {    // Loops through the BPOS_D list.
            int x0 = b[0];          // x position of 1st block of base row
            int numX = b[1];        // Number of blocks in the base row
            int numY = b[2];        // Number of rows
            for (int yi = 0; yi < numY; yi++) { // Loops through vertical way.
                int y = y1 - yi * ES;
                for (int xi = 0; xi < numX; xi++) {
                    int x = x0 + xi * ES;
                    Block block = new Block(x, y);
                    blocks.add(block);
                    int[][] blckPixel = ImageTool.convertTo2D(block.getImage());
                    // Add the block to the background.
                    for (int j = 0; j < block.getWidth(); j++) {
                        for (int i = 0; i < block.getHeight(); i++) {
                            sourcePixels[i + block.getY()][j + block.getX()]
                                    = blckPixel[i][j];
                        }
                    }
                }
                numX--;
            }
        }

        // Add background to the current screen.
        int xOffset = screen.xOffset; // Offset of the screen from y = 0        
        int count = 0;
        for (int i = 0; i < B_HEIGHT; i++) {
            for (int j = xOffset; j < B_WIDTH + xOffset; j++) {
                pixels[count] = sourcePixels[i][j];
                count++;
            }
        }
    }

    /**
     * Create level and sprite objects for the game.
     */
    public void initGame() {
        // Reset all values.
        deadTime = 0;
        wonTimer = 0;
        gameTime = 0;
        hasWon = false;
        numCoins = 0;

        // Create the map.     
        level = new Level(numTileX, numTileY, levelNum, Commons.GAME_TIME);
        tiles = level.tileIds;
        player = new Player(input, level, this);
        player.init();
        level.add(player);

        // Create enemies and hidden sprites.     
        level.spawn();

        // Initialize the screen pixels as the background. 
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

    /**
     * Create level and sprite objects for the game.
     */
    public void resetGame() {
        // Reset all values.
        deadTime = 0;
        wonTimer = 0;
        gameTime = 0;
        hasWon = false;

        // Create the map.
        level = new Level(numTileX, numTileY, levelNum, Commons.GAME_TIME);
        player.level = level;
        player.init();
        level.add(player);

        // Create enemies and hidden sprites.
        level.spawn();
        // If the player is enlarged, change mushrooms to flowers.
        if (player.isEnlarged()) {
            level.mushroom2Flower();
        }

        // Initialize the screen pixels as the background. 
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
        yScroll = 0;

        // Update the screen.
        int count = 0;
        for (int i = 0; i < B_HEIGHT; i++) {
            for (int j = xScroll; j < B_WIDTH + xScroll; j++) {
                screen.pixels[count] = sourcePixels[i][j];
                count++;
            }
        }

        // Render the screen.
        level.renderSprites(screen, xScroll, yScroll);
        level.renderBackground(screen, xScroll, yScroll);
        renderGui();

        // Overwrite the updated screen to the image data.
        for (int y = 0; y < screen.H; y++) {
            for (int x = 0; x < screen.W; x++) {
                // Loops through all the pixels on the screen.
                pixels[x + y * B_WIDTH] = screen.pixels[x + y * screen.W];
            }
        }

        // Draw the screen.
        int ww = B_WIDTH * SCALE;           // Scales the pixels horizontally.
        int hh = B_HEIGHT * SCALE;          // Scales the pixels vertically.
        int xo = (getWidth() - ww) / 2;     // Gets an offset for the image.
        int yo = (getHeight() - hh) / 2;    // Gets an offset for the image.
        g.drawImage(image, xo, yo, ww, hh, null);  // Draws image on the window.

        // Synchronises the painting on systems that buffer graphics events.
        // Without this line, the animation ight not be smooth on Linux.
        Toolkit.getDefaultToolkit().sync();
    }

    /* Renders the GUI on the screen used in the main game. */
    private void renderGui() {
        // Show the stats.
        int PPS = Commons.PPS;
        String[] stringList = {"SCORE", "COINS", "WORLD", "TIME", "LIVES"};
        int[] numList = {player.getScore(), numCoins, level.getWorld(),
            timeLeft, player.getLives()};
        int secLen = B_WIDTH / stringList.length;  // Width of each section. 
        int yGap = 2;   // Horizontal gap between texts.
        for (int i = 0; i < stringList.length; i++) {
            // Draw section names.
            String str = stringList[i];
            int strLen = str.length() * PPS;
            Font.draw(str, screen, (secLen - strLen) / 2 + secLen * i, yGap,
                    main.java.com.demo.gfx.Color.WHITE);
            // Draw corresponding values.
            String numStr = Integer.toString(numList[i]);
            if (i == 2) // Adjust world string.
            {
                numStr += "-" + level.getStage();
            }
            int numStrLen = numStr.length() * PPS;
            Font.draw(numStr, screen, (secLen - numStrLen) / 2 + secLen * i,
                    yGap + PPS + yGap, main.java.com.demo.gfx.Color.WHITE);
        }

        // If there is an active menu, then it will render it.
        if (menu != null) {
            menu.render(screen);
        }
    }

    /**
     * This method is called after our JPanel has been added to the JFrame
     * component. This method is often used for various initialization tasks.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        start();
    }

    /**
     * This method is only called once but have a while loop inside.
     */
    @Override
    public void run() {

        long beforeTime, timeDiff, sleep;
        int frames = 0;
        int ticks = 0;
        long lastTimer = System.currentTimeMillis(); // Current time in ms.
        beforeTime = System.currentTimeMillis();

        while (inGame) {

            ticks++;    // Increases amount of ticks.
            update();   // Updates any changes happened to the game.
            frames++;   // Increases the amount of frames.
            repaint();  // Paints the updated screen.

            /* We want our game to run smoothly, at a constant speed. 
                Therefore, we compute the system time that update() and 
                repaint() take in each loop (timeDiff) because they might take 
                different time at various while cycles. This way we can ensure 
                that each while cycle runs at a constant time. */
            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            // Sleep time should always be positive.
            if (sleep < 0) {
                sleep = 2;
            }

            try {
                // Causes the currently executing thread to sleep (temporarily 
                // cease execution) for "sleep" ms
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                String msg = String.format("Thread interrupted: %s",
                        e.getMessage());
                JOptionPane.showMessageDialog(this, msg, "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            beforeTime = System.currentTimeMillis(); // Update before time.

            // Updates frames and ticks every second.
            if (System.currentTimeMillis() - lastTimer > 1000) {
                lastTimer += 1000; // Adds a second to the timer.
                // Prints out the number of ticks and frames to the console.
//                System.out.println(ticks + " ticks, " + frames + " fps"); 
                frames = 0;         // Resets the frames value.
                ticks = 0;          // Resets the ticks value.
            }
        }
    }

    /* Updates any changes happened to the game. */
    private void update() {

        if (!hasFocus()) {
            // If the player is not focused on the screen, then all the current 
            // inputs will be set to off (well up).
            input.releaseAll();
        } else {
            // If in the middle of playing the game,
            if (!player.removed && !hasWon && menu == null) {
                gameTime++; // Increases time count by 1.
                // This is used for the timer displayed on the GUI.
                timeLeft = level.getTimeLim() - 2 * gameTime / TPS;
            }

            input.tick(); // Calls the tick() method in InputHandler.java            

            if (menu != null) { // If there is an active menu, 
                menu.tick(); // it will call the tick method of that menu.
                level.tickTiles(); // Update tiles even when menu is active.
            } else {            // In the middle of playing the game.                
                // If esc is clicked
                if (input.pause.clicked) {
                    setMenu(new PauseMenu());
                }
                // If the player has been removed
                if (player.removed) {
                    deadTime++;
                    if (deadTime > TPS) { // If a second has passed
                        // Set the menu to the dead menu.
                        setMenu(new EndMenu("Game over :("));
                    }
                }
                // If the player has won the game
                if (player.enteredCastle()) {
                    if (timeLeft > 0) { // Add 50 points for each second left.
                        timeLeft--;
                        player.addScore(50);
                    } else {                       // After done adding scores 
                        if (wonTimer > 0) {        // If the wonTimer is above 0
                            if (--wonTimer == 0) { // If wonTimer hits 0
                                setMenu(new EndMenu("You won! Yay!")); // Actives the win menu.
                            }
                        }
                    }
                } else { // If the player hasn't reached the end
                    if (timeLeft <= 0) // If the time runs out
                    {
                        player.hurt(player.getHealth()); // Player loses a life.
                    }
                }
                level.tick(screen);     // Calls the tick() method in Level.java                
            }
            // Increases the tickCount in Tile.java. Used for QuestionBrick.java
            Tile.tickCount++;
        }
    }

    /**
     * Use this method to switch to another menu.
     *
     * @param menu A Menu object to which you want to switch.
     */
    public void setMenu(Menu menu) {
        this.menu = menu;
        if (menu != null) {
            menu.init(this, input);
        }
    }

    /**
     * This starts the game.
     */
    public void start() {
        inGame = true;
        new Thread(this).start();
    }

    /**
     * This pauses the game.
     */
    public void stop() {
        inGame = false;
    }

    /**
     * This is called when the player has won the game.
     */
    public void won() {
        wonTimer = TPS * 1; // The pause time before the win menu shows up.
        hasWon = true;     // Confirms that the player has indeed, won the game.
    }

    /**
     * Returns number of columns in the sprite sheet.
     *
     * @return An integer containing the number of squares in a row in the
     * sprite sheet.
     */
    public int getColNum() {
        return screen.sheet.width / Commons.PPS;
    }

    /**
     * Add the number of coins acquired.
     */
    public void addCoinCount() {
        numCoins++;
    }

    /**
     * Sets pMax for the screen.
     *
     * @param num The maximum x for player before starting to scroll.
     */
    public void setPMax(int num) {
        pMax = num;
    }

    /**
     * Returns the maximum x position of the player.
     *
     * @return An integer containing the maximum x position on the screen for
     * player before starting to scroll the map.
     */
    public int getPMax() {
        return pMax;
    }
    
    /**
     * Returns AboutMenu.
     * @return A Menu object that represents AboutMenu.
     */
    public Menu getAboutMenu() {
        return aboutMenu;
    }
    
    /**
     * Returns InstMenu.
     * @return A Menu object that represents InstructionsMenu.
     */
    public Menu getInstMenu() {
        return instMenu;
    }
}
