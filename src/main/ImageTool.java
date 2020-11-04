/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 *
 * @author HRK
 */
public class ImageTool {
    
    // From https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
    public static int[] convertTo1D(BufferedImage image) {

      final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
      final int width = image.getWidth();
      final int height = image.getHeight();
      final boolean hasAlphaChannel = image.getAlphaRaster() != null;

      int[] result = new int[height*width];
      int counter = 0;
      if (hasAlphaChannel) {
         final int pixelLength = 4;
         
         for (int pixel = 0, row = 0, col = 0; pixel + 3 < pixels.length; pixel += pixelLength) {
            int argb = 0;
            argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
            argb += ((int) pixels[pixel + 1] & 0xff); // blue
            argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
            argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
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
            argb += -16777216; // 255 alpha
            argb += ((int) pixels[pixel] & 0xff); // blue
            argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
            argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
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
    
    // From https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
    public static int[][] convertTo2D(BufferedImage image) {

      final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
      final int width = image.getWidth();
      final int height = image.getHeight();
      final boolean hasAlphaChannel = image.getAlphaRaster() != null;

      int[][] result = new int[height][width];
      if (hasAlphaChannel) {
         final int pixelLength = 4;
         for (int pixel = 0, row = 0, col = 0; pixel + 3 < pixels.length; pixel += pixelLength) {
            int argb = 0;
            argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
            argb += ((int) pixels[pixel + 1] & 0xff); // blue
            argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
            argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
            result[row][col] = argb;
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
            argb += -16777216; // 255 alpha
            argb += ((int) pixels[pixel] & 0xff); // blue
            argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
            argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
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
