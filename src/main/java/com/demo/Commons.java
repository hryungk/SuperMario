package main.java.com.demo;


public interface Commons {
    
    int PPS = 8;    // Pixels per square in the spritesheet.
    int BOARD_WIDTH = 300;
    int BOARD_HEIGHT = 240;    
    
    int ENTITY_SIZE = 16;   // Width, height both is 16 by default.    

    int NUMBER_OF_ALIENS_TO_DESTROY = 16;
    int CHANCE = 5;
    int DELAY = 17;    
    int FONT_Y = 30;
    
    int NUM_GROUND_ROW = 2; // Number of rows of ground blocks
    int GROUND = BOARD_HEIGHT - NUM_GROUND_ROW * ENTITY_SIZE;
    int GROUND_TILE = GROUND / ENTITY_SIZE;
    
    int PLAYER_XI = 42;
    int PLAYER_XMAX = BOARD_WIDTH / 2 - 2 * ENTITY_SIZE;    //(150 - 32 = 118)
   
    int Y96 = BOARD_HEIGHT - 96;  // y-position at 240-96 = 144
    int Y160 = BOARD_HEIGHT - 160; // y-position at 240-160 = 80
    
    int X_MAX = 3168;   // Location of the flag 
    
    double ITV0 = -2.5; // beginning speed of the interactive tiles when hit.
    
    // Ground map
    int[][] GPOS = { // {beginning tile, ending tile}
        {0, 1088}, {1136,1360}, {1424, 2432}, 
        {2480, 3568} 
    };
    
    // Initial positions of aliens.
    int A_GROUND = GROUND - ENTITY_SIZE;
    int[][] APOS = {
        {352, A_GROUND}, {640, A_GROUND}, {816, A_GROUND}, {840, A_GROUND}, 
        {1280, Y160-ENTITY_SIZE}, {1312, Y160-ENTITY_SIZE}, {1552, A_GROUND}, 
        {1576, A_GROUND}, {1824, A_GROUND}, {1848, A_GROUND}, {1984, A_GROUND},
        {2008, A_GROUND}, {2048, A_GROUND}, {2072, A_GROUND}, {2784, A_GROUND}, 
        {2808, A_GROUND}
    };
    // Size and initial (x, y) position of pipes.
    int[][] PPOS = {
        {1, 448, BOARD_HEIGHT-64}, {2, 608,BOARD_HEIGHT-80}, {3, 736, BOARD_HEIGHT-96}, 
        {3, 912, BOARD_HEIGHT-96}, {1, 2608, BOARD_HEIGHT-64}, {1, 2864, BOARD_HEIGHT-64}
    };
    // Ascending blocks
    int[][] BPOS_A = { 
        // {x position of 1st block of base row,  number of blocks in the base row, number of rows}
         {2144, 4, 4}, {2368, 5, 4}, {2896, 9, 8}
     };
    // Descending blocks
    int[][] BPOS_D = {
         {2240, 4, 4}, {2480, 4, 4}, {X_MAX, 1, 1}
     };    
    
    // Initial position of bricks
    int[][] BRPOS = { //{x position, y position, number of bricks in this row}
        {320, Y96, 1}, {352, Y96, 1}, {384, Y96, 1}, {1232, Y96, 1}, {1264, Y96, 1}, 
        {1280, Y160, 8}, {1456, Y160, 3}, {1504, Y96, 1}, {1600, Y96, 2}, 
        {1888, Y96, 1}, {1939, Y160, 3}, {2048, Y160, 1}, {2064, Y96, 2}, 
        {2096, Y160, 1}, {2688, Y96, 2}, {2736, Y96, 1}
    };
    
    // Initial position of question bricks.
    int[][] QBRPOS = { //{x position, y position, number of bricks in this row}
        {256, Y96, 1}, {336, Y96, 1}, {352, Y160, 1}, {368, Y96, 1}, {1248, Y96, 1}, 
        {1504, Y160, 1}, {1696, Y96, 1}, {1744, Y96, 1}, {1744, Y160, 1}, 
        {1792, Y96, 1}, {2064, Y160, 2}, {2720, Y96, 1}
    };  
    
    // Initial position of coins.
    int[][] CPOS = { //{x position, y position}
        {256, Y96}, {352, Y160}, {368, Y96}, {1504, Y96}, {1504, Y160}, 
        {1696, Y96}, {1744, Y96}, {1792, Y96}, {2064, Y160}, {2080, Y160}, {2720, Y96}
    };  
    
    // Initial position of mushrooms.
    int[][] MPOS = { //{x position, y position}
        {336, Y96}, {1248, Y96}, {1744, Y160}
    };  
    
    // Initial position of starman.
    int[][] SPOS = { //{x position, y position}
        {1616, Y96}
    };   
}
