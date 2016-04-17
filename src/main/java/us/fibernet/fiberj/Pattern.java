/*
 * Copyright Wen Bian. All rights reserved.
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

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import static java.lang.Math.asin;
import static java.lang.Math.tan;

/**
 * A class containing all information about a fiber diffraction pattern
 */
public class Pattern {

    private static final double EPS = 0.000001;  // threshold for floating number comparisons

    private String name;        // name or file name for this pattern
    private int[][] data;       // diffracton intensity
    private int[][] data0;      // original data read from file
    private byte[][] mask;      // for pixels who are masked
    private int dataMin;        // min value of intensity data
    private int dataMax;        // max value of intensity data
    private boolean isRecip;    // if this pattern is in reciprocal space

    private double centerX;     // x-coordinate of pattern center (pixel)
    private double centerY;     // y-coordinate of pattern center (pixel)
    private double wavelen;     // radiation wave length (angstrom)
    private double sdd;         // specimen to detector distance in (millimeter)
    //private double sddP;      // normalized sdd (in pixel) // always compute on fly
    private double pixelSize;   // pixel size of pattern image (micrometer)
    // or step size in reciprocal space (1/angstrom)
    private double twist;       // fiber missetting angle: rotations about x axis (degree)
    private double tilt;        // fiber missetting angle: rotations about y axis (degree)
    private double repeat;      // fiber repeat distance (angstrom)
    private double betaD;       // detector missetting angle: rotations about y axis (degree)
    private double gammaD;      // detector missetting angle: rotations about z axis (degree)
    private double offset;      // detector offset or bias or background
    private double dCalibrant;  // d-spacing of calibrant (angstrom)

    private int dataScale = 1;          // data scale factor, data0.height / data.height
    private double displayScale = 1.0;  // display scale factor, data.height / displayData.height;
    private double aspectRatio = 1.0;   // width / height
    private boolean isScaleFit = false;

    private PatternDisplay myDisplay;


    /**
     * @param data     2D array of intensity data 
     * @param name     pattern name
     * @param isRecip  whether this is a reciprocal space pattern
     */
    public Pattern(int[][] data, String name, boolean isRecip) {
        this.data = data;
        this.data0 = data;
        this.name = name;
        this.isRecip = isRecip;
        setAspectRatio((data[0].length + 0.0) / data.length);
        setPatternData(data);
    }

    // TODO: clean up upon user clicking the close button
    public void close() {

    }

    public String toString() {
        return  "width = "      + getWidth()  + "\n" +
                "height = "     + getHeight() + "\n" +
                "centerX = "    + centerX     + "\n" +
                "centerY = "    + centerY     + "\n" +
                "pixelSize = "  + pixelSize   + "\n" +
                "wavelen = "    + wavelen     + "\n" +
                "sdd = "        + sdd         + "\n" +
                "twist = "      + twist       + "\n" +
                "tilt = "       + tilt        + "\n" +
                "repeat = "     + repeat      + "\n" +
                "betaD = "      + betaD       + "\n" +
                "gammaD = "     + gammaD      + "\n" +
                "offset = "     + offset      + "\n" +
                "dCalibrant = " + dCalibrant;
    }


    /*------------------------------- getters -------------------------------*/

    public String  getName()         { return name;           }
    public int     getWidth()        { return data[0].length; }
    public int     getHeight()       { return data.length;    }
    public int     getDataMin()      { return dataMin;        }
    public int     getDataMax()      { return dataMax;        }
    public byte[][] getMask()        { return mask;           }
    public double  getCenterX()      { return centerX;        }
    public double  getCenterY()      { return centerY;        }
    public double  getWavelen()      { return wavelen;        }
    public double  getSdd()          { return sdd;            }
    public double  getDistance()     { return getSdd();       }
    public double  getPixelSize()    { return pixelSize;      }
    public double  getTwist()        { return twist;          }
    public double  getTilt()         { return tilt;           }
    public double  getRepeat()       { return repeat;         }
    public double  getBetaD()        { return betaD;          }
    public double  getGammaD()       { return gammaD;         }
    public double  getOffset()       { return offset;         }
    public double  getdCalibrant()   { return dCalibrant;     }
    public boolean isRecip()         { return isRecip;        }
    public double  getAspectRatio()  { return aspectRatio;    }
    public double  getShrinkScale()  { return displayScale;   }
    public int[][] getData()         { return data;           }


    /*---------------------- getters for derivative info --------------------*/

    public int[][] getDisplayData() { 
        if(isScaleDisplayData()) {
            return PatternUtil.shrinkArray(data);  
        }
        else {
            return data;
        }
    }
    ///////
    private boolean isScaleDisplayData() {
        return Math.abs(displayScale - 1.0) > 0.01;
    }

    
    /** 
     * get max dimension of the pattern. used by DrawDialog 
     */
    public double getMaxDimension() {
        int w = getWidth();
        int h = getHeight();
        return Math.sqrt(w*w + h*h);
    }

    /** 
     * Recalculate displayScale based on new pattern display height  
     *  Do not change parameters, only update coordinates (so x,y no longer integers)
     */
    public void recalcDisplayScale(double newHeight) {
        displayScale = data.length / newHeight;
    }

    
    /** 
     * get the x, y, radius, intensity of the pixel at display (x, y) 
     */
    public double getx(int px)  { 
        return px * displayScale; 
    }
    
    public double gety(int py)  { 
        return py * displayScale; 
    }

    public double getr(int px, int py) {
        double dx = px * displayScale - centerX;
        double dy = py * displayScale - centerY;
        return Math.sqrt(dx*dx + dy*dy);
    }

    public double getI(double px, double py) {
        int x0 = (int)(px * displayScale + 0.5);
        int y0 = (int)(py * displayScale + 0.5);
        if(x0 < 0 || x0 >= getWidth() || y0 < 0 || y0 >= getHeight()) {
            return 0.0;
        }
        return data[y0][x0];
    }

    
    /** 
     * get aggregated pixel data along X or along Y  
     */
    public double getI(boolean aggregatePY, int px, int py, int offset) {
        double sum = 0;
        int x0 = (int)(px * displayScale + 0.5);
        int y0 = (int)(py * displayScale + 0.5);

        if(aggregatePY) {
            for(int i=0; i<offset; i++) {
                y0 = (int)((py + i) * displayScale + 0.5);
                if(x0 < 0 || x0 >= getWidth() || y0 < 0 || y0 >= getHeight()) {
                    continue;
                }
                sum += data[y0][x0];
            }
        }
        else {
            for(int i=0; i<offset; i++) {
                x0 = (int)((px + i) * displayScale + 0.5);
                if(x0 < 0 || x0 >= getWidth() || y0 < 0 || y0 >= getHeight()) {
                    continue;
                }
                sum += data[y0][x0];
            }                 
        }

        return sum / offset;
    }


    /** 
     * return intensity data as an array of bytes 
     */
    public byte[] getDataBytes(int pixSize) {
        int W = getWidth();
        int H = getHeight();
        byte[] bytes;

        if(pixSize == 4) {
            bytes = new byte[W*H*4];
            for(int h=0, p=0; h<H; h++) {
                for(int w=0; w<W; w++) {
                    int i = data[h][w];
                    // no need to & 0xFF as cast only take the last byte
                    bytes[p++] = (byte)(i >> 24);
                    bytes[p++] = (byte)(i >> 16);
                    bytes[p++] = (byte)(i >> 8);
                    bytes[p++] = (byte)(i);
                }
            }
        }
        else {
            bytes = new byte[W*H*2];
            for(int h=0, p=0; h<H; h++) {
                for(int w=0; w<W; w++) {
                    int i = data[h][w];
                    // no need to & 0xFF as cast only take the last byte
                    bytes[p++] = (byte)(i >> 8);
                    bytes[p++] = (byte)(i);
                }
            }            
        }

        return bytes;
    }

    /** return the displayed pattern as a BufferedImage */
    public BufferedImage getDisplayImage() {
        return myDisplay.getDisplayImage();
    }
    
    /** return the DrawKit on myDisplay */
    public DrawKit getDrawKit() {
        return myDisplay.getDrawKit();
    }


    
    /*--------------------------------- setters --------------------------------------*/

    public void setWavelen(double d)          { wavelen = d;    UIParameter.refresh(); }
    public void setSdd(double d)              { sdd = d;        UIParameter.refresh(); }
    public void setTwist(double d)            { twist = d;      UIParameter.refresh(); }
    public void setTilt(double d)             { tilt = d;       UIParameter.refresh(); }
    public void setRepeat(double d)           { repeat = d;     UIParameter.refresh(); }
    public void setBetaD(double d)            { betaD = d;      UIParameter.refresh(); }
    public void setGammaD(double d)           { gammaD = d;     UIParameter.refresh(); }
    public void setOffset(double d)           { offset = d;     UIParameter.refresh(); }
    public void setdCalibrant(double d)       { dCalibrant = d; UIParameter.refresh(); }
    public void setRecip(boolean b)           { isRecip = b;    UIParameter.refresh(); }
    public void setPixelSize(double d)        { pixelSize = d;  UIParameter.refresh(); }

    public void setCenterX(double d)          { centerX = d;    UIParameter.refresh(); }
    public void setCenterY(double d)          { centerY = d;    UIParameter.refresh(); }
    public void setCenterPX(double d)         { centerX = d * displayScale;    UIParameter.refresh(); }
    public void setCenterPY(double d)         { centerY = d * displayScale;    UIParameter.refresh(); }

    public void setMask(byte[][] a)           { mask = a;                              }
    public void setDataMin(int i)             { dataMin = i;                           }
    public void setDataMax(int i)             { dataMax = i;                           }
    public void setAspectRatio(double r)      { aspectRatio = r;                       }
    public void setDisplay(PatternDisplay d)  { myDisplay = d;                         }

 
    /** 
     * when dataScale changes (data rescaling), update parameters affected 
     */
    public void onDataScaling()  { 
        centerX /= dataScale;   
        centerY /= dataScale;    
        pixelSize *= dataScale;  
        UIParameter.refresh(); 
    }


    /**
     * replace this pattern's data array with a new one, set center and create a new mask
     */
    public void setPatternData(int[][] newData) {
        data = newData;
        int height = data.length;
        int width = data[0].length;
        centerX = (width - 1) / 2.0;
        centerY = (height - 1) / 2.0;
        mask = new byte[height][width];
    }

    /** 
     * restore data to fit the screen 
     */
    public void scaleFit() {  
        if(isScaleFit) {
            return;
        }

        isScaleFit = true;
        recalcDisplayScale(data0.length);
        setPatternData(data0);
        PatternProcessor.createPatternImage(this, null);
    }

    /** 
     * restore data to the original size 
     */
    public void scaleToOriginal() {
        if(!isScaleFit) {
            return;
        }

        isScaleFit = false;
        dataScale = 1;
        onDataScaling();
        setPatternData(PatternUtil.copy2dArray(data0));

        PatternProcessor.createPatternImage(this, null);
    }

    /** 
     * rotate the data array by degree 
     */
    public void rotateData(double degree) {
        int[][] newData = ImageRotation.rotate(data, degree, centerX, centerY);
        setPatternData(newData);
        PatternProcessor.createPatternImage(this, null);
    }

    /** 
     * flip data array horizontally 
     */
    public void flipDataHorizontal() {
        if(data != null) {
            int H = data.length;
            int W = data[0].length;

            for(int h=0; h<H; h++) {
                for(int w=0; w<W/2; w++) {
                    int tmp = data[h][w];
                    data[h][w] = data[h][W-1-w];
                    data[h][W-1-w] = tmp;
                }
            }
        }
        PatternProcessor.createPatternImage(this, null);
    }

    /** 
     * flip data array vertically 
     */
    public void flipDataVertical() {
        if(data != null) {
            int H = data.length;
            int W = data[0].length;

            for(int h=0; h<H/2; h++) {
                for(int w=0; w<W; w++) {
                    int tmp = data[h][w];
                    data[h][w] = data[H-1-h][w];
                    data[H-1-h][w] = tmp;
                }
            }
        }
        PatternProcessor.createPatternImage(this, null);
    }


    /**
     *  check if essential reciprocal parameters have been defined
     */
    public boolean isParameterDefined(boolean verbose) {

        boolean isDefined = (pixelSize > EPS && isRecip) ||
                (pixelSize > EPS && sdd > EPS && wavelen > EPS);

        if(!isDefined && verbose) {

            String err = null;

            if(pixelSize < EPS) {
                err = "pixel size not defined!";
            }
            else if(wavelen < EPS) {
                err = "radiation wave length not defined!";
            }
            else if(sdd < EPS) {
                err = "specimen-detector distance not defined!";
            }
            else {
                err = "unknown error";
            }

            JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
        }

        return isDefined;
    }


    /**
     * return if the reciprocal has been calculated
     */
    public boolean hasReciprocal() {
        return false; // TODO
    }


    /**
     * update reciprocal info
     */
    public void updateReciprocal(boolean verbose) {
        // TODO
    }

    /**
     * return a list of pixels whose resolution matches Resolution r
     */
    public ArrayList<Point> getResolutionPoints(Resolution r) {
        return null; // TODO
    }

    /**
     * return layerlines, each as an ArrayList of Points
     */
    public ArrayList<ArrayList<Point>> getLayerLines(double r) {
        return null; // TODO
    }


    /**
     * calculate sdd from calibrant ring
     */
    public void calcSdd(double radius) {
        // TODO
    }


    /**
     * transform a point from detector space to reciprocal space (R & Z)
     */
    public boolean xy2RZ(double x, double y, double[] RZ) {
        return true; // TODO
    }


    /**
     * transform a point from detector space to reciprocal space (R & L)
     */
    public boolean xy2RL(double x, double y, double[] RL) {
        return true; // TODO
    }


    /**
     * transform a point from detector space to reciprocal space (R & Z & incident Angle)
     */
    public boolean xy2RZA(double x, double y, double[] RZA) {
        return true; // TODO
    }


    /**
     * transform a point (R & L) from reciprocal space to detector space
     */
    public boolean RL2xy(double RR, double LL, double[] xy) {
        return true; // TODO
    }


    /**
     * transform a point(R & Z) from reciprocal space to detector space
     */
    public boolean RZ2xy(double R, double Z, double[] xy) {
        return true; // TODO
    }

} // class Pattern
