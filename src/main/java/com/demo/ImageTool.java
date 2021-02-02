/*
 * This code is obtained from 
 * https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
 */
package main.java.com.demo;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Extracts pixels from an Image object.
 */
public class ImageTool {

    /**
     * Converts an Image to 1D pixel array.
     *
     * @param image A BufferedImage object to extract pixels.
     * @return A 1D array of pixel data of the image.
     */
    public static int[] convertTo1D(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer())
                .getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[] result = new int[height * width];
        int counter = 0;
        if (hasAlphaChannel) {
            final int pixelLength = 4;

            for (int pixel = 0, row = 0, col = 0; pixel + 3 < pixels.length;
                    pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24);       // alpha
                argb += ((int) pixels[pixel + 1] & 0xff);           // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8);    // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16);   // red
                counter = row * width + col;
                result[counter] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel + 2 < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216;                                  // 255 alpha
                argb += ((int) pixels[pixel] & 0xff);               // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8);    // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16);   // red
                counter = row * width + col;
                result[counter] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }
        return result;
    }

    /**
     * Converts an Image to 2D pixel array.
     *
     * @param image A BufferedImage object to extract pixels.
     * @return A 2D array of pixel data of the image.
     */
    public static int[][] convertTo2D(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer())
                .getData();
        final int width = image.getWidth();
        final int height = image.getHeight()
                + Commons.NUM_GBLOCK_ROW * Commons.ENTITY_SIZE; // Add gblock height
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[][] result = new int[height][width];

        // Make the empty area sky blue.
        for (int i = image.getHeight(); i < height; i++) {
            for (int j = 0; j < width; j++) {
                result[i][j] = 0x5C94FC;
            }
        }
        
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel + 3 < pixels.length;
                    pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24);       // alpha
                argb += ((int) pixels[pixel + 1] & 0xff);           // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8);    // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16);   // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel + 2 < pixels.length;
                    pixel += pixelLength) {
                int argb = 0;
                argb += -16777216;                                  // 255 alpha
                argb += ((int) pixels[pixel] & 0xff);               // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8);    // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16);   // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }
        return result;
    }
}
