/*
 * Copyright Yi Xiao. All rights reserved.
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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Pattern display related utilities
 */
public class PatternUtil {

    private static final String colorTableDir = "ColorTables/";
    private static ColorTable colors;

    static {
        try {
            colors = PatternUtil.getColorTable(colorTableDir + "rgb.txt");
        } catch (IOException e) {
            System.out.println("Couldn't find color table file.");
        }
    }

    static void useColorTable(String colorTable) {
        try {
            colors = PatternUtil.getColorTable(colorTableDir + colorTable.toLowerCase() + ".txt");
        } catch (IOException e) {
            System.out.println("Couldn't find color table file.");
        }
    }

    /**
     */
    public static ColorTable getColorTable(String file) throws IOException {

        List<List<Byte>> rgbColorTable = new ArrayList<List<Byte>>();
        for(int i = 0; i < 3; i++) {
            rgbColorTable.add(new ArrayList<Byte>());
        }
        Scanner scan = new Scanner(new File(file));
        while (scan.hasNext()) {
            rgbColorTable.get(0).add((byte) scan.nextInt());
            rgbColorTable.get(1).add((byte) scan.nextInt());
            rgbColorTable.get(2).add((byte) scan.nextInt());
        }

        ColorTable ct = new ColorTable(rgbColorTable.get(0).size());

        byte[] red = ct.getRed();
        byte[] green = ct.getGreen();
        byte[] blue = ct.getBlue();

        for(int i = 0; i < rgbColorTable.get(0).size(); i++) {
            red[i] = rgbColorTable.get(0).get(i);
            green[i] = rgbColorTable.get(1).get(i);
            blue[i] = rgbColorTable.get(2).get(i);
        }

        scan.close();
        return ct;
    }

    /**
     * Display the color table. Used for test
     *
     * @param table
     */
    static void displayColorTable(List<List<Byte>> table) {
        for(List<Byte> row : table) {
            System.out.println(row);
        }
    }

    /**
     * Given a int[][], return a smaller int[][] by skipping some elements in
     * the original int[][]
     */
    static int[][] shrinkArray(int[][] original) {
        int optimalSize = 500;// the pixel number to achieve good image
        int shrinkFactor = Math.max(original.length, original[0].length) / optimalSize;
        int[][] output = new int[original.length / shrinkFactor][original[0].length / shrinkFactor];
        for(int r = 0; r * shrinkFactor < original.length; r++) {
            for(int c = 0; c * shrinkFactor < original.length; c++) {
                output[r][c] = original[r * shrinkFactor][c * shrinkFactor];
            }
        }
        return output;
    }


    /**
     * Compute color index from a data array, i.e., scale data values to range of [0, numColors - 1]
     */
    static int[][] computerColorIndex(int[][] input, int numColors) {

        if(input == null || input.length < 1) {
            return null;
        }

        int[][] output = new int[input.length][input[0].length];

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        // find max and min values of input array
        for(int i = 0; i < output.length; i++) {
            for(int j = 0; j < output[0].length; j++) {
                if(input[i][j] > max) {
                    max = input[i][j];
                }
                else if(input[i][j] < min) {
                    min = input[i][j];
                }
            }
        }

        if(min < 0) { min = 0; } // TODO: warning
        if(max < 0) { max = 0; } // TODO: warning

        double factor = ((double) (max - min)) / numColors  +  1;

        for(int i = 0; i < output.length; i++) {
            for(int j = 0; j < output[0].length; j++) {
                if(input[i][j] < min) {
                    output[i][j] = 0;
                }
                else {
                    output[i][j] = (int) ((input[i][j] - min) / factor);
                }
            }
        }

        return output;
    }


    /**
     * make a deep copy of a BufferedImage, slow
     */
    public static BufferedImage copyBufferedImage0(final BufferedImage src) {
        ColorModel cm = src.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = src.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    /**
     * make a deep copy of a BufferedImage, faster
     */
    public static BufferedImage copyBufferedImage1(final BufferedImage src) {
        BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = copy.createGraphics();
        g.drawImage(src, 0, 0, copy.getWidth(), copy.getHeight(), null);
        g.dispose();
        return copy;
    }

    /**
     * make a deep copy of a BufferedImage, fastest
     */
    public static BufferedImage copyBufferedImage(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] srcbuf = ((DataBufferInt) src.getRaster().getDataBuffer()).getData();
        int[] dstbuf = ((DataBufferInt) copy.getRaster().getDataBuffer()).getData();

        for(int y=0, srcoffs=0, dstoffs=0; y<height; y++, dstoffs+=width, srcoffs+=width ) {
            System.arraycopy(srcbuf, srcoffs , dstbuf, dstoffs, width);
        }
        return copy;
    }


    /**
     * To resize the image to fit in the JLabel, and set it to the JLabel
     *
     * @param label    the JLabel to hold the image
     * @param image    the image to fit in the JLabel
     * @return the resized Image
     */
    static BufferedImage fitImage(JLabel label, BufferedImage image) {

        int newWidth = label.getWidth();
        int newHeight = label.getHeight();

        if(newWidth < 1 || newHeight < 1) {
            return null;
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, newWidth, newHeight, null);
        g.dispose();
        label.setIcon(new ImageIcon(resizedImage));
        return resizedImage;
    }

    /**
     *
     * @param width
     * @param height
     * @return a rainbow image based on the default color table
     */
    static BufferedImage getRainbowImage(int width, int height) {

        int numColors = colors.getRed().length;

        byte[] red = colors.getRed();
        byte[] green = colors.getGreen();
        byte[] blue = colors.getBlue();

        BufferedImage im = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = im.getRaster();

        int unit = width / numColors;

        int[] r = new int[unit * height];
        int[] g = new int[unit * height];
        int[] b = new int[unit * height];

        for(int i = 0; i < width; i += unit) {
            int colorIndex = i / unit;
            Arrays.fill(r, red[colorIndex]);
            Arrays.fill(g, green[colorIndex]);
            Arrays.fill(b, blue[colorIndex]);
            raster.setSamples(i, 0, unit, height, 0, r);
            raster.setSamples(i, 0, unit, height, 1, g);
            raster.setSamples(i, 0, unit, height, 2, b);

        }

        return im;
    }

    /**
     *
     * @return a rainbow image based on the default color table
     */
    static BufferedImage getRainbowImage() {
        // default width = numColors, height = numColors

        int numColors = colors.getRed().length;

        byte[] red = colors.getRed();
        byte[] green = colors.getGreen();
        byte[] blue = colors.getBlue();

        int width = numColors;
        int height = numColors;

        BufferedImage im = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = im.getRaster();

        int unit = width / numColors;

        int[] r = new int[unit * height];
        int[] g = new int[unit * height];
        int[] b = new int[unit * height];

        for(int i = 0; i < width; i += unit) {
            int colorIndex = i / unit;
            Arrays.fill(r, red[colorIndex]);
            Arrays.fill(g, green[colorIndex]);
            Arrays.fill(b, blue[colorIndex]);
            raster.setSamples(i, 0, unit, height, 0, r);
            raster.setSamples(i, 0, unit, height, 1, g);
            raster.setSamples(i, 0, unit, height, 2, b);
        }
        return im;
    }

    /**
     * Given a 2D array, calculate a BufferedImage with default color table
     */
    static BufferedImage calcBufferedImage(int[][] input) throws IOException {

        int unit = 1;// for higher resolution
        int height = input.length * unit;
        int width = input[0].length * unit;

        byte[] red   = colors.getRed();
        byte[] green = colors.getGreen();
        byte[] blue  = colors.getBlue();

        BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = im.getRaster();

        int[] r = new int[unit * unit];
        int[] g = new int[unit * unit];
        int[] b = new int[unit * unit];

        for(int h = 0; h < height; h += unit) {
            for(int w = 0; w < width; w += unit) {

                int color = input[h/unit][w/unit];
                Arrays.fill(r, red[color]);
                Arrays.fill(g, green[color]);
                Arrays.fill(b, blue[color]);
                raster.setSamples(w, h, unit, unit, 0, r);
                raster.setSamples(w, h, unit, unit, 1, g);
                raster.setSamples(w, h, unit, unit, 2, b);
            }
        }

        return im;
    }

    /**
     * GaussianElimination
     *
     * @return coefficients
     */
    static double[] gaussian(double[][] A, double[] b) {
        int N = b.length;
        // double EPSILON = 1e-10;

        for(int p = 0; p < N; p++) {

            // find pivot row and swap
            int max = p;
            for(int i = p + 1; i < N; i++) {
                if(Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }
            double[] temp = A[p];
            A[p] = A[max];
            A[max] = temp;
            double t = b[p];
            b[p] = b[max];
            b[max] = t;

            // singular or nearly singular
            /*
             * if(Math.abs(A[p][p]) <= EPSILON) { throw new
             * RuntimeException("Matrix is singular or nearly singular"); }
             */

            // pivot within A and b
            for(int i = p + 1; i < N; i++) {
                double alpha = A[i][p] / A[p][p];
                b[i] -= alpha * b[p];
                for(int j = p; j < N; j++) {
                    A[i][j] -= alpha * A[p][j];
                }
            }
        }

        // back substitution
        double[] x = new double[N];
        for(int i = N - 1; i >= 0; i--) {
            double sum = 0.0;
            for(int j = i + 1; j < N; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (b[i] - sum) / A[i][i];
        }
        return x;
    }

}
