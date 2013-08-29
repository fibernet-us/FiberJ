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
import java.io.File;
import java.io.IOException;
import java.lang.Math;

import javax.imageio.ImageIO;

//http://opencv-srf.blogspot.com/2010/09/rotating-images.html

//http://eab.abime.net/coders-general/29492-c-image-rotation-algorithm-problem.html

/**
 * 
 *  Image rotation function
 * 
 */
public class ImageMatrixRotation {

    // Image 2D array rotation and scale
    public static double[][] MatrixRotation(double[][] ImageMatrix,
            double degree, double defaultcolor) {
        double angle = degree * Math.PI / 180;
        double sinma = Math.sin(-angle);
        double cosma = Math.cos(-angle);

        int width = ImageMatrix.length;
        int heigth = ImageMatrix[0].length;

        // in order to improve accuracy, use double instead of int

        double halfwidth = width / 2;
        double halfheigth = heigth / 2;

        // calculate new image's dimension, from four corner point
        double x1 = cosma * (0 - halfwidth) - sinma * (0 - halfheigth)
                + halfwidth;

        double y1 = sinma * (0 - halfwidth) + cosma * (0 - halfheigth)
                + halfheigth;

        double x2 = cosma * (0 - halfwidth) - sinma * (heigth - halfheigth - 1)
                + halfwidth;
        double y2 = sinma * (0 - halfwidth) + cosma * (heigth - halfheigth - 1)
                + halfheigth;

        double x3 = cosma * (width - halfwidth - 1) - sinma * (0 - halfheigth)
                + halfwidth;
        double y3 = sinma * (width - halfwidth - 1) + cosma * (0 - halfheigth)
                + halfheigth;

        double x4 = cosma * (width - halfwidth - 1) - sinma
                * (heigth - halfheigth - 1) + halfwidth;
        double y4 = sinma * (width - halfwidth - 1) + cosma
                * (heigth - halfheigth - 1) + halfheigth;

        double MaxX = Math.max(Math.max(x1, x2), Math.max(x3, x4));
        double MinX = Math.min(Math.min(x1, x2), Math.min(x3, x4));

        double MaxY = Math.max(Math.max(y1, y2), Math.max(y3, y4));
        double MinY = Math.min(Math.min(y1, y2), Math.min(y3, y4));

        /*
         * System.out.println(x1 + " " + y1); System.out.println(x2 + " " + y2);
         * System.out.println(x3 + " " + y3); System.out.println(x4 + " " + y4);
         */

        System.out.println(MaxX + " " + MinX + ", " + MaxY + " " + MinY);

        int newWidth = (int) Math.round(MaxX - MinX + 1);
        int newHeigth = (int) Math.round(MaxY - MinY + 1);

        // calculate scale
        double scale = 1;
        int maxOldlength = Math.max(width, heigth);
        int maxNewlength = Math.max(newWidth, newHeigth);

        if (maxNewlength > maxOldlength) {
            scale = Math.max((double) newWidth / width, (double) newHeigth
                    / heigth);
            // System.out.println(scale);
            // System.out.println(newWidth + " " + width + ", " + newHeigth +
            // " " + heigth);

            newWidth = (int) Math.round(newWidth / scale + 1);
            newHeigth = (int) Math.round(newHeigth / scale + 1);

        }

        // image matrix after rotation
        double[][] newImageMatrix = new double[newWidth][newHeigth];

        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y < newHeigth; y++) {
                newImageMatrix[x][y] = defaultcolor;
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < heigth; y++) {

                double xt = x - halfwidth;
                double yt = y - halfheigth;
                int xs = (int) Math.round(((cosma * xt - sinma * yt)
                        + halfwidth - MinX)
                        / scale);
                int ys = (int) Math.round(((sinma * xt + cosma * yt)
                        + halfheigth - MinY)
                        / scale);
                // System.out.println(xs + " " + ys);
                if (xs >= 0 && xs < newWidth && ys >= 0 && ys < newHeigth) {
                    newImageMatrix[xs][ys] = ImageMatrix[x][y];
                } else {
                    newImageMatrix[xs][ys] = defaultcolor;
                }

            }
        }

        return newImageMatrix;
    }

    // use int[][] for test image, because the output of ImageReader.readTif is
    // int[][]
    public static int[][] MatrixRotation(int[][] ImageMatrix, double degree,
            int defaultcolor) {

        double angle = degree * Math.PI / 180;
        double sinma = Math.sin(-angle);
        double cosma = Math.cos(-angle);

        int width = ImageMatrix.length;
        int heigth = ImageMatrix[0].length;

        // in order to improve accuracy, use double instead of int

        double halfwidth = width / 2;
        double halfheigth = heigth / 2;

        // calculate new image's dimension, from four corner point
        double x1 = cosma * (0 - halfwidth) - sinma * (0 - halfheigth)
                + halfwidth;

        double y1 = sinma * (0 - halfwidth) + cosma * (0 - halfheigth)
                + halfheigth;

        double x2 = cosma * (0 - halfwidth) - sinma * (heigth - halfheigth - 1)
                + halfwidth;
        double y2 = sinma * (0 - halfwidth) + cosma * (heigth - halfheigth - 1)
                + halfheigth;

        double x3 = cosma * (width - halfwidth - 1) - sinma * (0 - halfheigth)
                + halfwidth;
        double y3 = sinma * (width - halfwidth - 1) + cosma * (0 - halfheigth)
                + halfheigth;

        double x4 = cosma * (width - halfwidth - 1) - sinma
                * (heigth - halfheigth - 1) + halfwidth;
        double y4 = sinma * (width - halfwidth - 1) + cosma
                * (heigth - halfheigth - 1) + halfheigth;

        double MaxX = Math.max(Math.max(x1, x2), Math.max(x3, x4));
        double MinX = Math.min(Math.min(x1, x2), Math.min(x3, x4));

        double MaxY = Math.max(Math.max(y1, y2), Math.max(y3, y4));
        double MinY = Math.min(Math.min(y1, y2), Math.min(y3, y4));

        // System.out.println(x1 + " " + y1);
        // System.out.println(x2 + " " + y2);
        // System.out.println(x3 + " " + y3);
        // System.out.println(x4 + " " + y4);

        // System.out.println(MaxX + " " + MinX + ", " + MaxY + " " + MinY);

        int newWidth = (int) Math.round(MaxX - MinX + 1);
        int newHeigth = (int) Math.round(MaxY - MinY + 1);

        // calculate scale
        double scale = 1;
        int maxOldlength = Math.max(width, heigth);
        int maxNewlength = Math.max(newWidth, newHeigth);

        if (maxNewlength > maxOldlength) {
            scale = Math.max((double) newWidth / width, (double) newHeigth
                    / heigth);
            // System.out.println(scale);
            // System.out.println(newWidth + " " + width + ", " + newHeigth +
            // " " + heigth);

            newWidth = (int) Math.round(newWidth / scale + 1);
            newHeigth = (int) Math.round(newHeigth / scale + 1);

        }

        // image matrix after rotation
        int[][] newImageMatrix = new int[newWidth][newHeigth];

        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y < newHeigth; y++) {
                newImageMatrix[x][y] = defaultcolor;
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < heigth; y++) {

                double xt = x - halfwidth;
                double yt = y - halfheigth;
                int xs = (int) Math.round(((cosma * xt - sinma * yt)
                        + halfwidth - MinX)
                        / scale);
                int ys = (int) Math.round(((sinma * xt + cosma * yt)
                        + halfheigth - MinY)
                        / scale);

                // System.out.println(xs + " " + ys);
                if (xs >= 0 && xs < newWidth && ys >= 0 && ys < newHeigth) {
                    newImageMatrix[xs][ys] = ImageMatrix[x][y];
                } else {
                    newImageMatrix[xs][ys] = defaultcolor;
                }

            }
        }

        return newImageMatrix;
    }

    // default pixel is 0
    public static double[][] MatrixRotation(double[][] ImageMatrix,
            double degree) {
        return MatrixRotation(ImageMatrix, degree, 0);
    }

    // save image for test
    public static void displayImage(int[][] imageArray) throws IOException {

        // Dimensions of the image
        int heigth = imageArray.length;

        int width = imageArray[0].length;
        System.out.println(heigth + " " + width);

        // Let's create a BufferedImage for a gray level image.
        BufferedImage im = new BufferedImage(width, heigth,
                BufferedImage.TYPE_BYTE_GRAY);
        // We need its raster to set the pixels' values.
        WritableRaster raster = im.getRaster();
        // Put the pixels on the raster, using values between 0 and 255.
        for (int h = 0; h < heigth; h++)
            for (int w = 0; w < width; w++) {
                // int value = 127 + (int) (128 * Math.sin(w / 32.) * Math
                // .sin(h / 32.)); // Weird sin pattern.

                int value = imageArray[h][w];
                raster.setSample(w, h, 0, value);
            }
        // Store the image using the PNG format.
        ImageIO.write(im, "JPG", new File(
                "/home/em/java/FiberJ/39a.jpg"));

    }

    // test
    public static void main(String[] arg0) {
        double dd = 60;
        String[] testfile = { "/home/em/java/FiberJ/39.tif" };
        // System.out.println(testfile);

        int[][] inputimage = ImageReader.readPatternData(testfile);

        int[][] rotatimage = MatrixRotation(inputimage, dd, 0);

        try {
            displayImage(rotatimage);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
