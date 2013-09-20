/*
 * Copyright Juan Ren. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list
 *   of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer listed in this license in the
 *   documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the copyright holders nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package us.fibernet.fiberj;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 
 * A utility class providing image rotation functions
 * 
 * Ref: http://opencv-srf.blogspot.com/2010/09/rotating-images.html
 * 
 */
public final class ImageRotation {

    private final static int NEAREST_INTERPOLATION = 0;
    private final static int BILINEAR_INTERPOLATION = 1;
    private final static int BG_PIXEL = 0;  // default background pixel value
    private final static int HALF_W = -1;   // indicate x0 is at half width
    private final static int HALF_H = -1;   // indicate y0 is at half height

    private ImageRotation() {}

    /** 
     * rotate a double[][] with default center (x, y), default background pixel value and  
     * default interpolation method
     */
    public static double[][] rotate(double[][] array2d, double degree) {
        return rotate(array2d, degree, HALF_W, HALF_H, BG_PIXEL, NEAREST_INTERPOLATION);
    }

    /** 
     * rotate a double[][] with given center (x, y), default background pixel value and  
     * default interpolation method
     */
    public static double[][] rotate(double[][] array2d, double degree, double x0, double y0) {
        return rotate(array2d, degree, x0, y0, BG_PIXEL, NEAREST_INTERPOLATION);
    }

    /** 
     * rotate an int[][] with default center (x, y), default background pixel value and  
     * default interpolation method
     */
    public static int[][] rotate(int[][] array2d, double degree) {
        return rotate(array2d, degree, HALF_W, HALF_H, BG_PIXEL, NEAREST_INTERPOLATION);
    }

    /** 
     * rotate an int[][] with given center (x, y), default background pixel value and  
     * default interpolation method
     */
    public static int[][] rotate(int[][] array2d, double degree, double x0, double y0) {
        return rotate(array2d, degree, x0, y0, BG_PIXEL, NEAREST_INTERPOLATION);
    }

    /** 
     * rotate an int[][] with given center (x, y), background pixel value and interpolation method
     */
    public static int[][] rotate(int[][] array2d, double degree, double x0, double y0, 
                                                  int bgPixel, int interpolation) {

        // copy int[][] elements to a double[][]
        int H = array2d.length;
        int W = array2d[0].length;
        double[][] matrix = new double[H][W];
        
        for(int h = 0; h < H; h++) {
            for(int w = 0; w < W; w++) {
                matrix[h][w] = array2d[h][w];
            }
        }

        // call rotate on double[][]
        double[][] rotArray2d = rotate(matrix, degree, x0, y0, bgPixel, interpolation);

        // copy double[][] back to an int[][]
        H = rotArray2d.length;
        W = rotArray2d[0].length;
        int[][] newArray2d = new int[H][W];
        
        for(int h = 0; h < H; h++) {
            for(int w = 0; w < W; w++) {
                newArray2d[h][w] = (int) Math.round(rotArray2d[h][w]);
            }
        }

        return newArray2d;
    }


    /**
     * @param array2d
     * @param degree
     * @param bgPixel
     * @param x0
     * @param y0
     * @param interpolation
     * @return
     * 
     * x' = cosa * (x - x0) - sina * (y - y0)  + x0
     * y' = sina * (x - x0) + cosa * (y - y0)  + y0
     */
    public static double[][] rotate(double[][] array2d, double degree, double x0, double y0,
                                                        double bgPixel, int interpolation) {

        double angle = degree * Math.PI / 180;
        double sina = Math.sin(angle);
        double cosa = Math.cos(angle);

        int H = array2d.length;
        int W = array2d[0].length;

        if(x0 == HALF_W)  {  x0 = (W - 1) / 2;  }   // not W/2!
        if(y0 == HALF_H)  {  y0 = (H - 1) / 2;  }
        
        
        // calculate new image's dimension, from four corner points 
        
        double dx, dy, x1, y1, x2, y2, x3, y3, x4, y4;
        
        // (0, 0) 
        dx = 0 - x0;
        dy = 0 - y0;
        x1 = cosa * dx - sina * dy + x0;
        y1 = sina * dx + cosa * dy + y0;

        // (W-1, 0)
        dx = W - 1 - x0;
        dy = 0 - y0;
        x2 = cosa * dx - sina * dy + x0;
        y2 = sina * dx + cosa * dy + y0;

        // (0, H-1)
        dx = 0 - x0;
        dy = H - 1 - y0;
        x3 = cosa * dx - sina * dy + x0;
        y3 = sina * dx + cosa * dy + y0;

        // (W-1, H-1)
        dx = W - 1 - x0;
        dy = H - 1 - y0;
        x4 = cosa * dx - sina * dy + x0;
        y4 = sina * dx + cosa * dy + y0;

        double xMax = Math.max(Math.max(x1, x2), Math.max(x3, x4));
        double xMin = Math.min(Math.min(x1, x2), Math.min(x3, x4));

        double yMax = Math.max(Math.max(y1, y2), Math.max(y3, y4));
        double yMin = Math.min(Math.min(y1, y2), Math.min(y3, y4));

        // calculate new width, height using max and min x and y
        //
        // e.g., xMin = 2.9, xMax = 5.7, then width = 5: 2 3 4 5 6
        //              2.2         5.7                  2 3 4 5 6
        //              2.0         5.7                  2 3 4 5 6       
        int newW = (int) Math.round(xMax - xMin + 1);
        int newH = (int) Math.round(yMax - yMin + 1);
        

        //int newW = (int)(xMax) - (int)xMin + 2;
        //int newH = (int)(yMax) - (int)yMin + 2;
        
        // if Max falls exactly on an int, then --W/H
        // e.g., xMin = 2.2, xMax = 5.0, then width = 4: 2 3 4 5
        //              2.0         5.0                  2 3 4 5
        //if(Math.abs(xMax - Math.round(xMax)) < 0.00001) {
        //    --newW;
        //}

        //if(Math.abs(yMax - Math.round(yMax)) < 0.000001) {
        //    --newH;
        //}
        
        System.out.println("max x, y: " + xMax + ", " + yMax);
        System.out.println("min x, y: " + xMin + ", " + yMin);
        System.out.println("new w, h: " + newW + ", " + newH);
        
        // image array to hold rotated data
        double[][] newArray2d = new double[newH][newW];

        for(int h = 0; h < newH; h++) {
            for(int w = 0; w < newW; w++) {
                newArray2d[h][w] = bgPixel;
            }
        }

        // rotate pixel by pixel, and do interpolation
        for(int y = 0; y < newH; y++) {
            for(int x = 0; x < newW; x++) {

                double xt = x - x0 + xMin;
                double yt = y - y0 + yMin;

                double xs =  cosa * xt + sina * yt + x0;
                double ys = -sina * xt + cosa * yt + y0;

                int xx = (int) Math.round(xs);
                int yy = (int) Math.round(ys);

                if(interpolation == NEAREST_INTERPOLATION) { 
                    if(xx >= 0 && yy >= 0 && xx < W && yy < H) {
                        newArray2d[y][x] = array2d[yy][xx];
                    }
                } 
                else if(interpolation == BILINEAR_INTERPOLATION) {
                    int xx1 = xx - 1;
                    int yy1 = yy - 1;
                    int xx2 = xx;
                    int yy2 = yy;

                    if(xs > xx) {
                        xx1 = xx;
                        xx2 = xx + 1;
                    }

                    if(ys > yy) {
                        yy1 = yy;
                        yy2 = yy + 1;
                    }

                    if(xx1 >= 0 && yy1 >= 0 && xx2 < W && yy2 < H) {
                        double Q11 = array2d[yy1][xx1];
                        double Q12 = array2d[yy1][xx2];
                        double Q21 = array2d[yy2][xx1];
                        double Q22 = array2d[yy2][xx2];
                        newArray2d[y][x] = bilinearinterpolation(xx1, xx2, xs, yy1, yy2, ys, Q11, Q12, Q21, Q22);;
                    } 
                    else if(xx >= 0 && yy >= 0 && xx < W && yy < H) {
                        newArray2d[y][x] = array2d[yy][xx];
                    }
                }

            }
        }

        return newArray2d;
    }

    // bilinear interpolation
    // http://supercomputingblog.com/graphics/coding-bilinear-interpolation/
    private static double bilinearinterpolation(int x1, int x2, double x, int y1, int y2, double y,
                                                double Q11, double Q12, double Q21, double Q22) {
        
        double R1 = ((x2 - x) / (x2 - x1)) * Q11 + ((x - x1) / (x2 - x1)) * Q21;
        double R2 = ((x2 - x) / (x2 - x1)) * Q12 + ((x - x1) / (x2 - x1)) * Q22;
        double P = ((y2 - y) / (y2 - y1)) * R1 + ((y - y1) / (y2 - y1)) * R2;
        return P;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    
    // for test purpose
    private static void writeImage(int[][] imageArray) throws IOException {
        int H = imageArray.length;
        int W = imageArray[0].length;
        BufferedImage im = new BufferedImage(W, H, BufferedImage.TYPE_BYTE_GRAY);

        WritableRaster raster = im.getRaster();
        for(int h = 0; h < H; h++) {
            for(int w = 0; w < W; w++) {
                raster.setSample(w, h, 0, imageArray[h][w]);
            }
        }
        ImageIO.write(im, "JPG", new File("./39_color.jpg"));
    }

    public static void main0(String[] arg0) {     
        String[] testfile = { "./39_color.tif" };
        // System.out.println(testfile);

        int[][] inputimage = PatternReader.readPatternData(testfile);
        int[][] rotatimage = rotate(inputimage, 150);

        try {
            writeImage(rotatimage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
