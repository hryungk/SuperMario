package main.java.com.demo;

/** Stores some constants used across the game. */
public interface Commons {
    
    int GAME_TIME = 350;    
    int DELAY = 17;         // Ticks the game every 17 ms.
    int TPS = 1000 / DELAY; // Ticks per second.
    
    int PPS = 8;            // Pixels per square in the spritesheet.
    int ENTITY_SIZE = 16;   // Default Width & height of tiles/entities [px]. 
    
    int BOARD_WIDTH = 300;
    int GROUND = 208;       // Height of the overground of the map.
    int GROUND_TILE = GROUND / ENTITY_SIZE;
    int NUM_GBLOCK_ROW = 3; // Number of rows of ground blocks.
    int BOARD_HEIGHT = GROUND + NUM_GBLOCK_ROW * ENTITY_SIZE;        
    
    int PLAYER_XI = 42;     // Initial x-position of the player.
    // Player's maximum x-position in the screen. Once the player reaches this 
    // point, the map moves forward.
    int PLAYER_XMAX = BOARD_WIDTH / 2 - 2 * ENTITY_SIZE;    //(150 - 32 = 118)
    int X_MAX = 3168;       // Location of the flag.
    
    int Y64 = GROUND - 64;  // y-position at 240-96 = 144
    int Y128 = GROUND - 128;// y-position at 240-160 = 80
    
    double ITV0 = -2.5;     // Beginning speed of the interactive tiles when hit
    
    /* Tile locations for the map. */
    // Ground tile
    int[][] GPOS = {   // {beginning tile, ending tile}
        {0, 1088}, {1136,1360}, {1424, 2432}, 
        {2480, 3568} 
    };
    
    // Pipes
    int[][] PPOS = {   // {size, x position, y position}
        {1, 448, GROUND - 32}, {2, 608,GROUND - 48}, {3, 736, GROUND -64}, 
        {3, 912, GROUND - 64}, {1, 2608, GROUND - 32}, {1, 2864, GROUND - 32}
    };
    
    // Ascending blocks
    int[][] BPOS_A = { // {x position of 1st block of base row,  number of
                       //  blocks in the base row, number of rows}
         {2144, 4, 4}, {2368, 5, 4}, {2896, 9, 8}
     };
    // Descending blocks
    int[][] BPOS_D = {
         {2240, 4, 4}, {2480, 4, 4}, {X_MAX, 1, 1}
     };    
    
    // Initial position of bricks
    int[][] BRPOS = {  // {x position, y position, number of bricks in this row}
        {320, Y64, 1}, {352, Y64, 1}, {384, Y64, 1}, {1232, Y64, 1},
        {1264, Y64, 1}, {1280, Y128, 8}, {1456, Y128, 3}, {1504, Y64, 1},
        {1600, Y64, 2}, {1888, Y64, 1}, {1939, Y128, 3}, {2048, Y128, 1}, 
        {2064, Y64, 2}, {2096, Y128, 1}, {2688, Y64, 2}, {2736, Y64, 1}
    };
    
    // Initial position of question bricks.
    int[][] QBRPOS = { // {x position, y position, number of bricks in this row}
        {256, Y64, 1}, {336, Y64, 1}, {352, Y128, 1}, {368, Y64, 1}, 
        {1248, Y64, 1}, {1504, Y128, 1}, {1696, Y64, 1}, {1744, Y64, 1}, 
        {1744, Y128, 1}, {1792, Y64, 1}, {2064, Y128, 2}, {2720, Y64, 1}
    };  
        
    /* Sprites' initial locations for the map. */
    // Goomba
    int A_GROUND = GROUND - ENTITY_SIZE;
    int[][] APOS = {   // {initial x position, initial y position}
        {352, A_GROUND}, {640, A_GROUND}, {816, A_GROUND}, {840, A_GROUND}, 
        {1280, Y128-ENTITY_SIZE}, {1312, Y128-ENTITY_SIZE}, {1552, A_GROUND}, 
        {1576, A_GROUND}, {1824, A_GROUND}, {1848, A_GROUND}, {1984, A_GROUND},
        {2008, A_GROUND}, {2048, A_GROUND}, {2072, A_GROUND}, {2784, A_GROUND}, 
        {2808, A_GROUND}
    };
    
    // Coins
    int[][] CPOS = {   // {initial x position, initial y position}
        {256, Y64}, {352, Y128}, {368, Y64}, {1504, Y64}, {1504, Y128}, 
        {1696, Y64}, {1744, Y64}, {1792, Y64}, {2064, Y128}, {2080, Y128},
        {2720, Y64}
    };  
    
    // Mushrooms
    int[][] MPOS = {   // {initial x position, initial y position}
        {336, Y64}, {1248, Y64}, {1744, Y128}
    };  
    
    // Starman
    int[][] SPOS = {   // {initial x position, initial y position}
        {1616, Y64}
    };   
}
