/*
 * Copyright Xiao Yi and Wen Bian. All rights reserved.
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

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;

import java.io.IOException;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;

import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageCodec;


@SuppressWarnings("restriction")
public final class ImageReader {

    private static int[][] image;
    
    private ImageReader() {
    }

    /**
     * Parse the pattern file extension and call accordingly a read method
     * Read pattern files that come with attributes not stored in itself
     */
    public static int[][] readPattern(String[] args) {

        if(args == null || args.length < 1) {
            return null;
        }
            
        String fname = args[0];
        
        if(args.length == 1) {
            if (endsWithIgnoreCase(fname, "tif")) {
                return readTif(fname);
            } 
            else if (endsWithIgnoreCase(fname, "plr")) {
                return readPlr(fname);
            } 
        }
        else {
            if (endsWithIgnoreCase(fname, "dat")) {
                int w = 0, h = 0;
                try {
                    w = Integer.parseInt(args[1]);
                    h = Integer.parseInt(args[2]);
                    return readDat(fname, w, h);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("dat image width and height must be specified");
                }
            }
        }

        return null;
    }

    /*
     * Read a raw data image and extract image data into a 2D array
     */
    private static int[][] readDat(String fname, int width, int height) {

        EndianCorrectInputStream input = null;
        image = null;

        try {

            image = new int[height][width];
            input = new EndianCorrectInputStream(new FileInputStream(fname),
                    false);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    image[i][j] = (int) (input.readShortCorrect());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return image;
    }

    /*
     * Read a TIFF image and extract image data into a 2D array
     */
    private static int[][] readTif(String fname) {

        image = null;

        try {
            SeekableStream s = new FileSeekableStream(fname);
            TIFFDecodeParam param = null;
            ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);
            RenderedImage op = dec.decodeAsRenderedImage(0);
            Raster raster = op.getData();
            image = new int[op.getHeight()][op.getWidth()];
            // System.out.println(op.getHeight() + " " + op.getWidth());

            int[] pixelColor = new int[4];
            for (int x = 0; x < op.getWidth(); x++) {
                for (int y = 0; y < op.getHeight(); y++) {
                    raster.getPixel(x, y, pixelColor);
                    image[y][x] = pixelColor[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return image;
    }

    /*
     * Read a PLR image and extract image data into a 2D array
     */
    public static int[][] readPlr(String fname) {
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(fname));
            ImageFilePlr dp = new ImageFilePlr();
            dp.parseData(br);
            br.close();            
            return dp.getImageData();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    /**
     * 
     */
    static int[][] doubleToInt(double[][] array) {
        int[][] output = new int[array.length][array[0].length];
        for (int r = 0; r < array.length; r++) {
            for (int c = 0; c < array[0].length; c++) {
                output[r][c] = (int) array[r][c];
            }
        }
        return output;
    }
    
    /*
     * The following code is copied from:
     * http://www.java2s.com/Tutorial/Java/0040__Data-Type/CheckifaStringendswithaspecifiedsuffix.htm
     */
    
    /*
     * Check if a String ends with a specified suffix, case insensitive.
     */
    private static boolean endsWithIgnoreCase(String str, String suffix) {
        return endsWith(str, suffix, true);
    }

    /*
     * Check if a String ends with a specified suffix
     */
    private static boolean endsWith(String str, String suffix,
            boolean ignoreCase) {

        if (str == null || suffix == null) {
            return (str == null && suffix == null);
        }

        if (suffix.length() > str.length()) {
            return false;
        }

        int strOffset = str.length() - suffix.length();
        return str.regionMatches(ignoreCase, strOffset, suffix, 0,
                suffix.length());
    }

}



/**
 * Input stream provided with NiftiDataset class. Better to use tools.EndianNeutralInputStream, 
 * because that gives an endian-correct result from the standard <code>DataInput</code> methods, 
 * and will also read unsigned types.
 *
 * @author Philip Cook (imported this code)
 * @version $Id$
 */

class EndianCorrectInputStream extends DataInputStream {

    private boolean bigendian = true;

    public EndianCorrectInputStream(String filename, boolean be) throws FileNotFoundException {
        super(new FileInputStream(filename));
        bigendian = be;
    }

    public EndianCorrectInputStream(InputStream is, boolean be) {
        super(is);
        bigendian = be;
    }

    public short readShortCorrect() throws IOException {
        short val;

        val = readShort();
        if (bigendian) {
            return (val);
        } else {
            int byte0 = (int) val & 0xff;
            int byte1 = ((int) val >> 8) & 0xff;
            // swap the byte order
            return (short) ((byte0 << 8) | (byte1));
        }
    }

    public short flipShort(short val) {

        int byte0 = (int) val & 0xff;
        int byte1 = ((int) val >> 8) & 0xff;
        // swap the byte order
        return (short) ((byte0 << 8) | (byte1));
    }

    public int readIntCorrect() throws IOException {
        int val;

        val = readInt();
        if (bigendian) {
            return (val);
        }

        else {
            int byte0 = val & 0xff;
            int byte1 = (val >> 8) & 0xff;
            int byte2 = (val >> 16) & 0xff;
            int byte3 = (val >> 24) & 0xff;
            // swap the byte order
            return (byte0 << 24) | (byte1 << 16) | (byte2 << 8) | byte3;
        }
    }

    // flipInt will flip the byte order of an int
    public int flipInt(int val) {

        int byte0 = val & 0xff;
        int byte1 = (val >> 8) & 0xff;
        int byte2 = (val >> 16) & 0xff;
        int byte3 = (val >> 24) & 0xff;
        // swap the byte order
        return (byte0 << 24) | (byte1 << 16) | (byte2 << 8) | byte3;
    }

    public long readLongCorrect() throws IOException {
        long val;

        val = readLong();
        if (bigendian) {
            return (val);
        }

        else {
            return (flipLong(val));
        }
    }

    // flipLong will flip the byte order of a long
    public long flipLong(long val) {

        long byte0 = val & 0xff;
        long byte1 = (val >> 8) & 0xff;
        long byte2 = (val >> 16) & 0xff;
        long byte3 = (val >> 24) & 0xff;
        long byte4 = (val >> 32) & 0xff;
        long byte5 = (val >> 40) & 0xff;
        long byte6 = (val >> 48) & 0xff;
        long byte7 = (val >> 56) & 0xff;
        // swap the byte order
        return (long) ((byte0 << 56) | (byte1 << 48) | (byte2 << 40)
                | (byte3 << 32) | (byte4 << 24) | (byte5 << 16) | (byte6 << 8) | byte7);
    }

    public float readFloatCorrect() throws IOException {
        float val;

        if (bigendian) {
            val = readFloat();
        }

        else {
            int x = readUnsignedByte();
            x |= ((int) readUnsignedByte()) << 8;
            x |= ((int) readUnsignedByte()) << 16;
            x |= ((int) readUnsignedByte()) << 24;
            val = (float) Float.intBitsToFloat(x);
        }
        return val;
    }

    // flipFloat will flip the byte order of a float
    public float flipFloat(float val) throws IOException {

        int x = Float.floatToIntBits(val);
        int y = flipInt(x);
        return Float.intBitsToFloat(y);
    }

    public double readDoubleCorrect() throws IOException {
        double val;
        if (bigendian) {
            val = readDouble();
        } else {
            long x = readUnsignedByte();
            x |= ((long) readUnsignedByte()) << 8;
            x |= ((long) readUnsignedByte()) << 16;
            x |= ((long) readUnsignedByte()) << 24;
            x |= ((long) readUnsignedByte()) << 32;
            x |= ((long) readUnsignedByte()) << 40;
            x |= ((long) readUnsignedByte()) << 48;
            x |= ((long) readUnsignedByte()) << 56;
            val = Double.longBitsToDouble(x);
        }
        return val;
    }

    // flipDouble will flip the byte order of a double
    public double flipDouble(double val) {

        long x = Double.doubleToLongBits(val);
        long y = flipLong(x);
        return Double.longBitsToDouble(y);
    }

}

