package main.java.com.demo.level;

import java.util.ArrayList;
import java.util.List;

import main.java.com.demo.entity.*;
import main.java.com.demo.Commons;
import main.java.com.demo.gfx.Screen;
import main.java.com.demo.level.levelgen.LevelGen;
import main.java.com.demo.level.tile.FlagTile;
import main.java.com.demo.level.tile.Tile;

public final class Level {

    private final int W, H;     // Width and height of the level's map [tile]

    public byte[] tileIds;      // An array of all the tiles's ID in the map.    
    public Tile[] tiles;        // An array of all the tiles in the map
    // An array of lists of entities in each tile
    public List<Sprite>[] entitiesInTiles; 

    private final int WORLD, STAGE;     // World's level number and stage number
    private final int TIME_LIMIT;       // Maximum time to play this level.

    private final List<Sprite> ENTITIES;    // A list of all the entities       
    private final List<Sprite> ROW_SPRITES; // A list of entities to be rendered    
    private List<Enemy> enemies;
    private List<HiddenSprite> hiddenSprites;
    private final List<ScoreString> SCORES;     // A list of score strings
    private final List<Sprite> BROKEN_BRICKS;   // A list of broken bricks 

    public Player player; 
    public Screen screen;
    private int xScroll;

    private final LevelGen levelGen;
    
    /**
     * Level which the world is contained in.
     * @param w An integer containing width of the level's map [tile]
     * @param h An integer containing height of the level's map [tile]
     * @param level An int array containing the world and the stage number.
     * @param time Maximum game time for this level.
     */
    public Level(int w, int h, int[] level, int time) {

        // Assign values from parameters to instance's variables.
        W = w; 
        H = h;     
        WORLD = level[0];
        STAGE = level[1];
        TIME_LIMIT = time;
        
        // Create a map for the current level.
        levelGen = new LevelGen(w, h);
        tileIds = levelGen.createAndValidateTopMap(); // Create a surface map.
        tiles = levelGen.getTileMap();

        // Initialize entitiesInTiles variable.
        entitiesInTiles = new ArrayList[w * h]; // Creates a List of ArrayLists.
        for (int i = 0; i < w * h; i++) {       // Loops (width * height) times
            // Add an entity list of i^th tile.
            entitiesInTiles[i] = new ArrayList<>(); 
        }

        // Initialize the sprite lists.
        ENTITIES = new ArrayList<>();       // A list of all the entities      
        ROW_SPRITES = new ArrayList<>();    // A list of sprites to be rendered
        SCORES = new ArrayList<>();         // A list of score strings
        BROKEN_BRICKS = new ArrayList<>();  // A list of broken bricks
    }

    /**
     * Update method, TPS (around 60 times) updates (ticks) a second (around
     * every 17 ms)
     *
     * @param screen The current screen showing.
     */
    public void tick(Screen screen) {
        this.screen = screen;

        // Update the tiles.
        tickTiles();

        // Update the sprites.
        // Loop through all the entities inside the entities list.
        for (int i = 0; i < ENTITIES.size(); i++) { 
            Sprite e = ENTITIES.get(i); // Current entity
            int xto = e.x >> 4;         // Entity's x tile coordinate
            int yto = e.y >> 4;         // Entity's y tile coordinate

            if (e instanceof Enemy) {   // If the entity is an Enemy
                // If the screen has reached enemy and it's not activated
                if ((e.getX() <= xScroll + screen.W) &&
                        !((Enemy) e).isActivated()) {     
                    ((Enemy) e).activate();     // Activate the enemy.
                }

                if (((Enemy) e).isActivated()) {// If the enemy is activated
                    e.tick();                   // Update the Enemy.
                }
            } else {            // If other than Enemy
                e.tick();       // Calls the entity's tick() method.
            }

            if (e.removed) {    // If the entity has been removed after update
                ENTITIES.remove(i--);   // Removes the entity from entities list
                removeEntity(xto, yto, e); // Removes the entity from the world.
            } else {            // If the entity isn't removed after udpate
                int xt = e.x >> 4;      // Entity's new x tile coordinate
                int yt = e.y >> 4;      // Entity's new y tile coordinate

                if (xto != xt || yto != yt) {   // If the entity has moved
                    removeEntity(xto, yto, e);  // Removes it from old position.
                    insertEntity(xt, yt, e);    // Adds it at the new position.
                }
            }
        }
    }

    public void tickTiles() {
        for (int xt = 0; xt < W; xt++) {        // Loops through width (tile)
            for (int yt = 0; yt < H; yt++) {    // Loops through height (tile)
                Tile tile = getTile(xt, yt);
                // Only bricks and the flag might go through a change. 
                if (tile.ID == Tile.brickID || tile.ID == Tile.QbrickID || 
                        tile.ID == Tile.flag.ID) {
                    tile.tick(xt, yt, this);    // Updates the tile.
                }
            }
        }
    }

    /**
     * Spawns enemies to the world.
     */
    public void spawn() {

        // Create and add enemies.
        int[][] APOS = Commons.APOS;    // Initial positions of enemies.
        enemies = new ArrayList<>(APOS.length);
        for (int[] p : APOS) {
            Enemy enemy = new Enemy(p[0], p[1], this);
            enemies.add(enemy);
            add(enemy);
        }

        // Create and add hidden sprites.
        hiddenSprites = new ArrayList<>();    
        // Add coins.
        int[][] CPOS = Commons.CPOS;    // Initial position of coins.
        for (int[] a : CPOS) {
            HiddenSprite c = new Coin(a[0], a[1], this);
            add(c);
        }
        // Add starman. 
        int[][] SPOS = Commons.SPOS;    // Initial position of starman.
        for (int[] a : SPOS) {
            HiddenSprite s = new Starman(a[0], a[1], this);
            add(s);
        }
        // Add mushrooms. 
        int[][] MPOS = Commons.MPOS;    // Initial position of mushrooms.
        for (int[] a : MPOS) {
            HiddenSprite m = new Mushroom(a[0], a[1], this);
            add(m);
        }
    }

    /**
     * Renders all the sprites on the screen.
     *
     * @param screen The screen to render.
     * @param xScroll The x offset of the map to display on the screen.
     * @param yScroll The y offset of the map to display on the screen.
     */
    public void renderSprites(Screen screen, int xScroll, int yScroll) {
        this.screen = screen;
        int xsot = xScroll >> 4;        // Horizontal scroll offset [tile]
        xsot = Math.max(0, xsot - 1);
        int ysot = yScroll >> 4;        // Vertical scroll offset [tile]
        int ws = (screen.W + 15) >> 4;  // Width of the screen
        int hs = (screen.H + 15) >> 4;  // Height of the screen

        screen.setOffset(xScroll, yScroll);     // Sets the scroll offsets.        

        // Render Flag tile before the player. 
        FlagTile fTile = (FlagTile) Tile.flag;
        int xtFlag = fTile.getX() >> 4;
        int ytFlag = fTile.getY() >> 4;
        // If the flag is located on the screen
        if (xsot <= xtFlag && xtFlag <= xsot + ws) {
            // Renders the flag tile on the screen.
            fTile.render(screen, this, xtFlag, ytFlag); 
        }
        
        // Render sprites on the screen.
        // Loops through the screen vertically.
        for (int y = ysot; y <= hs + ysot; y++) { 
            // Loops through the screen horizontally.
            for (int x = xsot; x <= ws + xsot; x++) { 
                // If the sprite's x/y positions are out of the map's boundaries
                if (x < 0 || y < 0 || x >= W || y >= H) {
                    continue;       // Skip the rest of this loop.
                }
                // Add all of the sprites in the entitiesInTiles array.
                ROW_SPRITES.addAll(entitiesInTiles[x + y * W]); 
            }
            
            if (ROW_SPRITES.size() > 0) {// If something is added in ROW_SPRITES
                // Loops through the entity list.
                for (int i = 0; i < ROW_SPRITES.size(); i++) { 
                    Sprite sprite = ROW_SPRITES.get(i);
                    if (sprite.isVisible()) {
                        // Renders the sprite on the screen.
                        sprite.render(screen); 
                    }
                }
            }
            ROW_SPRITES.clear();    // Clears the list.
        }
        screen.setOffset(0, 0);     // Resets the offset.
    }
        
    /**
     * Renders all the tiles in the level.
     *
     * @param screen The current Screen displayed.
     * @param xScroll An integer containing x-offset [pixel]
     * @param yScroll An integer containing y-offset [pixel]
     */
    public void renderBackground(Screen screen, int xScroll, int yScroll) {
        this.screen = screen;
        this.xScroll = xScroll;     // Update xScroll variable.
        int xto = xScroll >> 4;     // Horizontal scroll offset [tile].
        int yto = yScroll >> 4;     // Vertical scroll offset [tile].
        int ws = (screen.W + 15) >> 4;          // Width of the screen [tile]
        int hs = (screen.H + 15) >> 4;          // Height of the screen [tile]
        screen.setOffset(xScroll, yScroll);     // Sets the scroll offsets.          
        for (int y = yto; y < hs + yto; y++) {  // Loops through vertically.
            for (int x = xto; x < ws + xto; x++) { // Loops through horizontally
                Tile tile = getTile(x, y);
                // Only bricks might have changed. 
                if (tile.ID == Tile.brickID || tile.ID == Tile.QbrickID) {
                    // Renders the tile on the screen.
                    tile.render(screen, this, x, y); 
                }
            }
        }

        // Render broken brick pieces over the blocks.
        for (int i = 0; i < BROKEN_BRICKS.size(); i++) {// Loops through list.
            Sprite sprite = BROKEN_BRICKS.get(i);
            if (sprite.isVisible()) {
                sprite.render(screen);  // Renders the sprite on the screen.
            }
        }
        screen.setOffset(0, 0);         // Resets the offset.
    }

    /**
     * Sets a tile to the map.
     *
     * @param xt x tile position of the tile t [tile]
     * @param yt y tile position of the tile t [tile]
     * @param t The tile to be assigned at (xt, yt).
     */
    public void setTile(int xt, int yt, Tile t) {
        // If the request position is outside the world boundaries
        if (isOutOfBoundary(xt, yt)) {
            return;     // Stop the method.
        }
        tileIds[xt + yt * W] = t.ID; // Adds the tile ID at (xt, yt) location.
        tiles[xt + yt * W] = t;      // Places the tile at (xt, yt) location.
    }
    
    /**
     * Returns a tile from the map.
     *
     * @param xt x tile position in the current level [tile]
     * @param yt y tile position in the current level [tile]
     * @return A Tile object at position (xt, yt) in the current level
     */
    public Tile getTile(int xt, int yt) {
        // If the tile request is outside the map's boundaries (like xt = -5)
        if (isOutOfBoundary(xt, yt)) {
            return null; // Returns null.
        } 
        // If the tile request is within the map's boundaries
        return tiles[xt + yt * W]; // Returns the tile in that position.
    }

    /**
     * Gets a tile from the map.
     *
     * @param xt x tile position in the current level [tile]
     * @param yt y tile position in the current level [tile]
     * @param unit The unit of tile (8 px for shot, 16 px for sprites, 32 px for
     * big Pusheen)
     * @return A Tile object at position (xt, yt) in the current level
     */
    public Tile getTile(double xt, double yt, int unit) {
        switch (unit) {
            case 4:
                break;
            case 3:     // Unit size is 2^3 = 8 px (shot)
                xt /= 2;
                yt /=2;
                break;                
            case 5:     // Unit size is 2^5 = 32 px (Big Pusheen)
                xt *= 2;
                yt *= 2;
                break;
            default:
                System.out.println("Unit can be either 3, 4, or 5.");
                break;
        }
        
        return getTile((int)xt, (int)yt);
    }

    /**
     * Adds an entity to the level.
     *
     * @param e An entity to be added to the level
     */
    public void add(Sprite e) {
        if (e == null) {
            throw new NullPointerException("Added sprite is null.");
        }

        if (e instanceof Player) {  // If the entity happens to be a player
            player = (Player) e;    // The player object will be this entity.
        }
        e.removed = false;          // Sets the entity's removed value to false.

        ENTITIES.add(e);            // Adds the entity to the entities list.
        if (e instanceof HiddenSprite) {
            hiddenSprites.add((HiddenSprite) e);
        }
        if (e instanceof ScoreString) {
            SCORES.add((ScoreString) e);
        }
        if (e instanceof BrokenBrick) {
            BROKEN_BRICKS.add((BrokenBrick) e);
        }
        int xt = e.x >> 4;          // Gets the x position of the entity.
        int yt = e.y >> 4;          // Gets the y position of the entity.
        insertEntity(xt, yt, e);    // Inserts the entity into the world.
    }
    
    /**
     * Removes a sprite entity.
     *
     * @param e The sprite to be removed.
     */
    public void remove(Sprite e) {
        if (e == null) {
            throw new NullPointerException("Removed sprite is null.");
        }
        ENTITIES.remove(e);     // Removes the entity from the entities list.
        if (e instanceof HiddenSprite) {
            removeHiddenSprite(e.getX(), e.getY());
        }
        int xt = e.x >> 4;      // Gets the x position of the entity.
        int yt = e.y >> 4;      // Gets the y position of the entity.
        removeEntity(xt, yt, e);    // Removes the entity at (xt, yt).
    }
    
    /**
     * Returns the first hidden sprite at (x, y).
     *
     * @param x x-position of the hidden sprite [pixel]
     * @param y y-position of the hidden sprite [pixel]
     * @return The hidden sprite at (x, y), null if none exist at this location.
     */
    public HiddenSprite removeHiddenSprite(int x, int y) {

        boolean found = false;
        int ii = 0;
        while (!found && ii < hiddenSprites.size()) {
            HiddenSprite hs = hiddenSprites.get(ii);
            found = (hs.getX() == x) && (hs.getY() == y);
            if (!found) {
                ii++;
            } else {
                hiddenSprites.remove(ii);
                return hs;
            }
        }
        return null;
    }
    
    /**
     * Inserts an entity to the entitiesInTiles list.
     * @param xt x tile position of the entity e [tile]
     * @param yt y tile position of the entity e [tile]
     * @param e The Sprite entity to be added.
     */
    private void insertEntity(int xt, int yt, Sprite e) {
        // If the entity's position is outside the world
        if (isOutOfBoundary(xt, yt)) {
            return; // Stop the method.
        }
        // Adds the entity to the entitiesInTiles list array.
        entitiesInTiles[xt + yt * W].add(e); 
    }

    /**
     * Removes an entity in the entitiesInTiles list.
     * @param xt x tile position of the entity e [tile]
     * @param yt y tile position of the entity e [tile]
     * @param e The Sprite entity to be removed.
     */
    private void removeEntity(int xt, int yt, Sprite e) {
        // If the entity's position is outside the world
        if (isOutOfBoundary(xt, yt)) {
            return; // Stop the method.
        }
        // Removes the entity to the entitiesInTiles list array.
        entitiesInTiles[xt + yt * W].remove(e); 
    }

    /**
     * Gets all the entities from a square area of 4 points.
     *
     * @param x0 Left boundary of the intersection [pixel]
     * @param y0 Top boundary of the intersection [pixel]
     * @param x1 Right boundary of the intersection [pixel]
     * @param y1 Bottom boundary of the intersection [pixel]
     * @return A list of sprite inside the intersection box.
     */
    public List<Sprite> getEntities(int x0, int y0, int x1, int y1) {
        List<Sprite> result = new ArrayList<>();
        int xt0 = (x0 >> 4) - 1;    // Tile location of x0
        int yt0 = (y0 >> 4) - 1;    // Tile location of y0
        int xt1 = (x1 >> 4) + 1;    // Tile location of x1
        int yt1 = (y1 >> 4) + 1;    // Tile location of y1
        for (int yt = yt0; yt <= yt1; yt++) {       // Loops between y0 and y1.
            for (int xt = xt0; xt <= xt1; xt++) {   // Loops between x0 and x1.
                 // If the entity's position is outside the world
                if (isOutOfBoundary(xt, yt)) {
                    continue;       // Skip the rest of this loop.
                }
                // Get the entity list from the x and y tile position.
                List<Sprite> curEntities = entitiesInTiles[xt + yt * W]; 
                // Loops through all the entities in the entities list.
                for (int i = 0; i < curEntities.size(); i++) { 
                    Sprite e = curEntities.get(i);  // Gets the i^th entity.
                    // If the entity intersects these 4 points
                    if (e.intersects(x0, y0, x1, y1) && !result.contains(e)) {
                        result.add(e);  // Add it to the result list.
                    }
                }
            }
        }
        return result; // Returns the result list.
    }

    /**
     * Swap all the mushrooms to flowers in this level.
     */
    public void mushroom2Flower() {
        // Copy the hiddenSprites list.
        List<HiddenSprite> temp = new ArrayList<>();
        for (HiddenSprite hs : hiddenSprites) {
            temp.add(hs);
        }

        for (HiddenSprite hs : temp) {
            if (hs instanceof Mushroom) {
                remove(hs);
                add(new Flower(hs.getX(), hs.getY(), this));
            }
        }
    }

    /**
     * Swap all the flowers to mushrooms in this level.
     */
    public void flower2Mushroom() {
        // Copy the hiddenSprites list.
        List<HiddenSprite> temp = new ArrayList<>();
        for (HiddenSprite hs : hiddenSprites) {
            temp.add(hs);
        }

        for (HiddenSprite hs : temp) {
            if (hs instanceof Flower) {
                remove(hs);
                add(new Mushroom(hs.getX(), hs.getY(), this));
            }
        }
    }
    
    /** 
     * Boundary check for a given tile coordinate.
     * @param xt x tile position [tile]
     * @param yt y tile position [tile]
     * @return True if (xt, yt) is within the boundary of the map.
     */
    private boolean isOutOfBoundary(int xt, int yt) {
        return (xt < 0 || yt < 0 || xt >= W || yt >= H);
    }
    
    public int getOffset() {
        return xScroll;
    }

    public int getWorld() {
        return WORLD;
    }

    public int getStage() {
        return STAGE;
    }

    public int getTimeLim() {
        return TIME_LIMIT;
    }
    
    public int getWidth() {
        return W;
    }
    
    public int getHeight() {
        return H;
    }
}
