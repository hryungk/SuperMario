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

    public final int W, H; // width and height of the level

    public byte[] tileIds; // an array of all the tiles in the world.    
    public Tile[] tiles;
    public List<Sprite>[] entitiesInTiles; // An array of each entity in each tile in the world

    private int world, stage; // depth level of the level
    private final int TIME_LIMIT; 
    public List<Alien> aliens; 
    public List<HiddenSprite> hiddenSprites; 
    
    public List<Sprite> entities = new ArrayList<>(); // A list of all the entities in the world        
    private List<Sprite> rowSprites = new ArrayList<>(); // A list of entities to be rendered
    public List<ScoreString> scores = new ArrayList<>(); // A list of score strings
    
    public Player player; // the player object
    public Screen screen;
    private int xScroll;


    @SuppressWarnings("unchecked") // @SuppressWarnings ignores the warnings (yellow underline) in this method.
    /** Level which the world is contained in */
    public Level(int w, int h, int[] level, int time) {
        
        /* Assign values from parameters to instance variables. */
        W = w; // assigns the width [tiles]
        H = h; // assigns the height [tiles]
        world = level[0]; // assigns the depth variable                       
        stage = level[1];
        TIME_LIMIT = time;
                
        /* Create a map for the current level. */
        tileIds =  LevelGen.createAndValidateTopMap(w, h); // create a surface map for the level   
        tiles = LevelGen.tileMap;
        
        /* Initializes entitiesInTiles variable. */
        entitiesInTiles = new ArrayList[w * h]; // Creates a new arrayList with the size of width * height.
        for (int i = 0; i < w * h; i++) { // Loops (width * height) times
            entitiesInTiles[i] = new ArrayList<>(); // Adds a entity list in that tile.
        }
    }        
    
    /** Update method, updates (ticks) 60 times a second (around every 17 ms)
     * @param screen The current screen showing. */
    public void tick(Screen screen) {
        this.screen = screen;
        
        // tick the tiles
        tickTiles();
        
        // tick the sprites
        for (int i = 0; i < entities.size(); i++) { // Loops through all the entities inside the entities list
            Sprite e = entities.get(i); // the current entity
            int xto = e.x >> 4; // gets the entity's x tile coordinate
            int yto = e.y >> 4; // gets the entity's y tile coordinate

            
            if (e instanceof Alien) { // if the entity is an alien
                if (e.getX() <= xScroll + screen.W && !((Alien) e).isActivated()) // tick only when the screen reaches it.
                    ((Alien) e).activate();
                    
                if (((Alien) e).isActivated())
                    e.tick();                
            }
            else
                e.tick();// calls the entity's tick() method.
            

            if (e.removed) { // if the entity's removed value is true...
                entities.remove(i--); // removes the entity from the entities list and makes the list smaller.
                removeEntity(xto, yto, e); // Removes the entity from the world
            } else { // if the entity's removed value is false...
                int xt = e.x >> 4; // gets the entity's x coordinate
                int yt = e.y >> 4; // gets the entity's y coordinate

                if (xto != xt || yto != yt) { // If xto and xt, & yto and yt don't match... 
                    removeEntity(xto, yto, e); // remove the entity from xto & yto position 
                    insertEntity(xt, yt, e); // adds the entity at the xt & yt position
                }
            }
        }
    }
    
    public void tickTiles() {        
        for (int xt = 0; xt < W; xt++) { // Loops width
            for (int yt = 0; yt < H; yt++) { // Loops height
                Tile tile = getTile(xt, yt);
                if (tile.ID == Tile.brickID || tile.ID == Tile.QbrickID || tile.ID == Tile.flag.ID) {// only bricks might disappear. 
                    tile.tick(xt, yt, this); // updates the tile at that location.
                }
            }
        }
    }
    
    /** Spawns aliens in the world.  */
    public void spawn() {        
        
        /* Create and add aliens. */
        int[][] APOS = Commons.APOS; // Initial positions of aliens.
        aliens = new ArrayList<>(APOS.length);
        for (int[] p : APOS) {
            Alien alien = new Alien(p[0], p[1], this);
            aliens.add(alien);
            add(alien);
        }                
        
        /* Add hidden sprites. */
        hiddenSprites = new ArrayList<>();
        
        // Add coins.
        int[][] CPOS = Commons.CPOS;
        for (int[] a : CPOS) {
            HiddenSprite c = new Coin(a[0], a[1], this);   
//            hiddenSprites.add(c);
            add(c);
        }
        
        // Add starman. 
        int[][] SPOS = Commons.SPOS;
        for (int[] a : SPOS) {
            HiddenSprite s = new Starman(a[0], a[1], this);   
//            hiddenSprites.add(s);
            add(s);
        }
        
        // Add mushrooms. 
        int[][] MPOS = Commons.MPOS;
        for (int[] a : MPOS) {
            HiddenSprite m = new Mushroom(a[0], a[1], this);   
//            hiddenSprites.add(m);
            add(m);
        }
    }
        
    /** This method renders all the tiles in the game
     * @param screen The current Screen displayed.
     * @param xScroll  x-offset [pixels]
     * @param yScroll  y-offset [pixels] */
    public void renderBackground(Screen screen, int xScroll, int yScroll) {
        this.screen = screen;
        this.xScroll = xScroll;
        int xto = xScroll >> 4; // the game's horizontal scroll offset [tile].
        int yto = yScroll >> 4; // the game's vertical scroll offset [tile].
        int ws = (screen.W + 15) >> 4; // width of the screen being rendered [tile]
        int hs = (screen.H + 15) >> 4; // height of the screen being rendered [tile]
        screen.setOffset(xScroll, yScroll); // sets the scroll offsets.          
        for (int y = yto; y < hs + yto; y++) { // loops through the vertical positions
            for (int x = xto; x < ws + xto; x++) { // loops through the horizontal positions
                Tile tile = getTile(x, y);
                if (tile.ID == Tile.brickID || tile.ID == Tile.QbrickID) // only bricks might disappear. 
                    tile.render(screen, this, x, y); // renders the tile on the screen
            }
        }
        screen.setOffset(0, 0); // resets the offset.     
    }

    /** Renders all the entity sprites on the screen */
    public void renderSprites(Screen screen, int xScroll, int yScroll) {
        this.screen = screen;
        int xto = xScroll >> 4; // the game's horizontal scroll offset [tiles].
        xto = Math.max(0, xto-1);
        int yto = yScroll >> 4; // the game's vertical scroll offset [tiles].
        int ws = (screen.W + 15) >> 4; // width of the screen being rendered
        int hs = (screen.H + 15) >> 4; // height of the screen being rendered

        screen.setOffset(xScroll, yScroll); // sets the scroll offsets.
        
        
        /* Render Flag tile before the player. */
        int xtFlag = ((FlagTile) Tile.flag).getX() >> 4;
        int ytFlag = ((FlagTile) Tile.flag).getY() >> 4;
        if (xto <= xtFlag && xtFlag <= ws + xto)
            Tile.flag.render(screen, this, xtFlag, ytFlag); // renders the tile on the screen
    
        /* Render sprites including player. */
        for (int y = yto; y <= hs + yto; y++) { // loops through the vertical positions
                for (int x = xto; x <= ws + xto; x++) { // loops through the horizontal positions
                    if (x < 0 || y < 0 || x >= W || y >= H) continue; // If the x & y positions of the sprites are within the map's boundaries
                    rowSprites.addAll(entitiesInTiles[x + y * W]); // adds all of the sprites in the entitiesInTiles array.                        
                }
                if (rowSprites.size() > 0) { // If the rowSprites list size is larger than 0...
                    sortAndRender(screen, rowSprites); // sorts and renders the sprites on the screen
                }
                rowSprites.clear(); // clears the list
        }
        screen.setOffset(0, 0); // resets the offset.
    }
    
    /** Sorts and renders sprites from an entity list */
    private void sortAndRender(Screen screen, List<Sprite> list) {
        this.screen = screen;
        for (int i = 0; i < list.size(); i++) { // loops through the entity list
            Sprite sprite = list.get(i);
            if (sprite.isVisible())
                sprite.render(screen); // renders the sprite on the screen
        }
    }
    
    /** Gets a tile from the world.
     * @param x x position in the current level
     * @param y y position in the current level
     * @return A Tile object at position (x, y) in the current level */
    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= W || y >= H) return null; // If the tile request is outside the world's boundaries (like x = -5), then returns a rock.
//        return Tile.tiles[tileIds[x + y * W]]; // Returns the tile that is at the position
        return tiles[x + y * W];
    }
    
    /** Gets a tile from the world.
     * @param x x position in the current level
     * @param y y position in the current level
     * @param unit The unit of tile (8 px for shot, 16 px for sprites, 32 px for big pusheen)
     * @return A Tile object at position (x, y) in the current level */
    public Tile getTile(double x, double y, int unit) {
        switch(unit) {
            case 4:
                if (x < 0 || y < 0 || x >= W || y >= H) return null; // If the tile request is outside the world's boundaries (like x = -5), then returns a rock.
//                return Tile.tiles[tileIds[x + y * W]]; // Returns the tile that is at the position
                return tiles[(int)x + (int)y * W]; // Returns the tile that is at the position                  
            case 3:
                if (x < 0 || y < 0 || x/2 >= W || y/2 >= H) return null; // If the tile request is outside the world's boundaries (like x = -5), then returns a rock.                
//                return Tile.tiles[tileIds[x/2 + (y/2) * W]]; // Returns the tile that is at the position                
                return tiles[(int)(x/2) + (int)(y/2) * W];
            case 5:
                if (x < 0 || y < 0 || 2*x >= W || 2*y >= H) return null; // If the tile request is outside the world's boundaries (like x = -5), then returns a rock.                
//                return Tile.tiles[tileIds[2*x + (2*y) * W]]; // Returns the tile that is at the position                
                return tiles[(int)(2*x) + (int)(2*y) * W];
        }
        return null;
    }

    /** Sets a tile to the world.
     * @param xt x tile position of the tile t [tile]
     * @param yt y tile position of the tile t [tile]
     * @param t The tile to be assigned at (xt, yt). */
    public void setTile(int xt, int yt, Tile t) {
            if (xt < 0 || yt < 0 || xt >= W || yt >= H) return; // If the tile request position is outside the world boundaries (like x = -1337), then stop the method.
            tileIds[xt + yt * W] = t.ID; // Places the tile at the x & y location
            tiles[xt + yt * W] = t;
    }    
    
    /** Returns the first hidden sprite at (x, y).
     * @param x x-position of the hidden sprite [pixel]
     * @param y y-position of the hidden sprite [pixel]
     * @return  The hidden sprite at (x, y), null if none exist at this location. */
    public HiddenSprite removeHiddenSprite(int x, int y) {
        
        boolean found = false;
        int ii = 0;
        while (!found && ii < hiddenSprites.size()) {
            HiddenSprite hs = hiddenSprites.get(ii);
            found = (hs.getX() == x) && (hs.getY() == y);
            if (!found)
                ii++;
            else {
                hiddenSprites.remove(ii);
                return hs;
            }
        }
        return null;
    }

    /** Adds an entity to the level
     * @param e An entity to add to the level */
    public void add(Sprite e) {
        
        if (e instanceof Player) { // if the entity happens to be a player
            player = (Player) e; // the player object will be this entity
        }
        e.removed = false; // sets the entity's removed value to false
        entities.add(e); // adds the entity to the entities list
        if (e instanceof HiddenSprite)
            hiddenSprites.add((HiddenSprite)e);
        if (e instanceof ScoreString)
            scores.add((ScoreString)e);
//        entity.init(this); // Initializes the entity
        int xt = e.x >> 4; // gets the x position of the entity
        int yt = e.y >> 4; // gets the y position of the entity
        insertEntity(xt, yt, e); // inserts the entity into the world
    }
    
    /** Removes a entity */
    public void remove(Sprite e) { 
        entities.remove(e); // removes the entity from the entities list
        int xt = e.x >> 4; // gets the x position of the entity
        int yt = e.y >> 4; // gets the y position of the entity
        removeEntity(xt, yt, e); // removes the entity based on the x & y position.
    }    
    
    /** Inserts an entity to the entitiesInTiles list */
    private void insertEntity(int xt, int yt, Sprite e) {
        if (xt < 0 || yt < 0 || xt >= W || yt >= H) return; // if the entity's position is outside the world, then stop the method.
        entitiesInTiles[xt + yt * W].add(e); // adds the entity to the entitiesInTiles list array.
    }    

    /** Removes an entity in the entitiesInTiles list */
    private void removeEntity(int xt, int yt, Sprite e) {
        if (xt < 0 || yt < 0 || xt >= W || yt >= H) return; // if the entity's position is outside the world, then stop the method.
        entitiesInTiles[xt + yt * W].remove(e); // removes the entity to the entitiesInTiles list array.
    }
    
    /** Gets all the entities from a square area of 4 points. */
    public List<Sprite> getEntities(int x0, int y0, int x1, int y1) {
        List<Sprite> result = new ArrayList<>(); // result list of entities
        int xt0 = (x0 >> 4) - 1; // tile location of x0
        int yt0 = (y0 >> 4) - 1; // tile location of y0
        int xt1 = (x1 >> 4) + 1; // tile location of x1
        int yt1 = (y1 >> 4) + 1; // tile location of y1
        for (int y = yt0; y <= yt1; y++) { // Loops through the difference between y0 and y1
            for (int x = xt0; x <= xt1; x++) { // Loops through the difference between x0 & x1
                if (x < 0 || y < 0 || x >= W || y >= H) continue; // if the x & y position is outside the world, then skip the rest of this loop.
                List<Sprite> curEntities = entitiesInTiles[x + y * W]; // gets the entity from the x & y position
                for (int i = 0; i < curEntities.size(); i++) { // Loops through all the entities in the entities list
                    Sprite e = curEntities.get(i); // gets the current entity                    
                    if (e.intersects(x0, y0, x1, y1) && !result.contains(e)) result.add(e); // if the entity intersects these 4 points, then add it to the result list.
                }
            }
        }
        return result; // returns the result list of entities
    }
    
    /** Swap all the mushrooms to flowers in this level. */
    public void mushroom2Flower() {
        // Copy the hiddenSprites list.
        List<HiddenSprite> temp = new ArrayList<>();
        for (HiddenSprite hs : hiddenSprites) {
            temp.add(hs);
        }
        
        for (HiddenSprite hs : temp) {
            if (hs instanceof Mushroom) {
                remove(hs);
                removeHiddenSprite(hs.getX(), hs.getY());
                add(new Flower(hs.getX(), hs.getY(), this));                
            }
        }        
    }
    
    /** Swap all the flowers to mushrooms in this level. */
    public void flower2Mushroom() {
        // Copy the hiddenSprites list.
        List<HiddenSprite> temp = new ArrayList<>();
        for (HiddenSprite hs : hiddenSprites) {
            temp.add(hs);
        }
        
        for (HiddenSprite hs : temp) {
            if (hs instanceof Flower) {                
                remove(hs);
                removeHiddenSprite(hs.getX(), hs.getY());
                add(new Mushroom(hs.getX(), hs.getY(), this));
            }
        }        
    }
    
    public int getOffset() {
        return xScroll;
    }
    
    public int getWorld() {
        return world;
    }
    
    public int getStage() {
        return stage;
    }
    
    public int getTimeLim() {
        return TIME_LIMIT;
    }
}