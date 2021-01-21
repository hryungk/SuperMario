/* 
    This code is directly imported from Miniventure game. 
    (https://github.com/shylor/miniventure.git ).
 */
package main.java.com.demo.gfx;

public class Color {

    public static final int WHITE = get(0, 255, 255, 255);

    /* To explain this class, you have to know what a int (integer) is in Java 
        and how Bit-Shifting works.I made a small post, so go here if you don't 
        already know: http://minicraftforums.com/viewtopic.php?f=9&t=2256 */
    /* Note: this class still confuses me a bit, lol. -David */
    
    /**
     * This returns an integer with 4 rgb color values.
     * @param a An integer containing alpha value.
     * @param r An integer containing red value.
     * @param g An integer containing green value.
     * @param b An integer containing blue value.
     * @return An integer containing a RGBA color.
     */
    public static int get(int a, int r, int g, int b) {
//        int argb = 0;
//        argb += get(b) << 24;// Gets blue and shifts bits 24 times to the left.
//        argb += get(g) << 16;// Gets green and shifts bits 16 times to the left.
//        argb += get(r) << 8; // Gets red and shifts bits 8 times to the left.
//        argb += get(a);      // Gets alpha value.
//        return argb;
        
        int argb = 0;
        argb += (((int) a & 0xff) << 24);   // alpha
        argb += ((int) b & 0xff);           // blue
        argb += (((int) g & 0xff) << 8);    // green
        argb += (((int) r & 0xff) << 16);   // red
        return argb;
    }

    /**
     * Gets the color based off an integer.
     * @param d An integer containing a RGB color value.
     * @return An integer containing a RGB color.
     */
    public static int get(int d) {
        if (d < 0) {        // If d is smaller than 0
            return 255;     // Return 255.
        }
        int r = d / 100 % 10;   // Red value is the remainder of (d/100) / 10.
        int g = d / 10 % 10;    // Green value is the remainder of (d/10) / 10.
        int b = d % 10;         // Blue value is the remainder of d / 10.
        return r * 36 + g * 6 + b;

        // Why do we need all this math to get the colors? I don't even know.
        // -David
    }
}
