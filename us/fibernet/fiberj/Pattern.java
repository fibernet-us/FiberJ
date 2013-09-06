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

import javax.swing.JOptionPane;
import static java.lang.Math.asin;
import static java.lang.Math.tan;

/**
 * A class containing all information about a fiber diffraction pattern
 */
public class Pattern {
    
    private static final double EPS = 0.000001;  // values less than this are treated as 0
    
    private String name;        // name or file name for this pattern
    private int[][] data;       // working pattern data: diffracton intensity
    private int[][] data0;      // original pattern data read from file
    private int[][] mask;       // for pixels who are masked   
    private int dataMin;        // min value of intensity data
    private int dataMax;        // max value of intensity data
    private boolean isRecip;    // if this pattern is in reciprocal space

    private double centerX;     // x-coordinate of pattern center (pixel)
    private double centerY;     // y-coordinate of pattern center (pixel)
    private double wavelen;     // radiation wave length (angstrom)
    private double sdd;         // specimen to detector distance in (millimeter)
    //private double sddP;      // normalized sdd (in pixel) // always compute on fly
    private double pixelSize;   // pixel size of pattern image (millimeter)
                                // or step size in reciprocal space (1/angstrom)
    private double twist;       // fiber missetting angle: rotations about x axis (degree) 
    private double tilt;        // fiber missetting angle: rotations about y axis (degree)
    private double repeat;      // fiber repeat distance (angstrom)
    private double betaD;       // detector missetting angle: rotations about y axis (degree)
    private double gammaD;      // detector missetting angle: rotations about z axis (degree)
    private double offset;      // detector offset or bias or background
    private double dCalibrant;  // d-spacing of calibrant (angstrom)
    
    private double aspectRatio = 1.0;  // ratio of image display width to height
    private double shrinkScale = 1.0;  // scale factor, size1 of displayData / size1 of data       

    private PatternDisplay myDisplay;  
    

    /**
     * @param pdata    pattern data, a 2D array of pixels
     * @param pname    pattern name
     * @param isRecip  if this pattern is in reciprocal space
     */
    public Pattern(int[][] pdata, String pname, boolean isRecip) {
        this.name = pname;
        this.isRecip = isRecip;
        setData(pdata);
    }
    
    /**
     * replace this pattern's data array with a new one, set center and create a new mask
     */
    public void setData(int[][] newData) {
        data = newData;
        data0 = data.clone();
        int height = data.length;
        int width = data[0].length;
        centerX = width / 2.0;
        centerY = height / 2.0; 
        mask = new int[height][width];
    }
    
    /**
     * print out all parameters of this pattern
     */
    public String toString() {
        return  "width = "      + getWidth()  + "\n" +  
                "height = "     + getHeight() + "\n" +  
                "centerX = "    + centerX     + "\n" +  
                "centerY = "    + centerY     + "\n" +  
                "pixelSize = "  + pixelSize   + "\n" +  
                "wavelen = "    + wavelen     + "\n" +  
                "sdd = "        + sdd         + "\n" +  
                "sddp = "       + getSddP()   + "\n" +  
                "twist = "      + twist       + "\n" +  
                "tilt = "       + tilt        + "\n" +  
                "repeat = "     + repeat      + "\n" +  
                "betaD = "      + betaD       + "\n" +  
                "gammaD = "     + gammaD      + "\n" +  
                "offset = "     + offset      + "\n" +  
                "dCalibrant = " + dCalibrant;
    }
  
    
    /*------------------------------- getters -------------------------------*/
    
    public String  getName()        { return name;           }
    public int     getWidth()       { return data[0].length; }
    public int     getHeight()      { return data.length;    }
    public int     getDataMin()     { return dataMin;        }
    public int     getDataMax()     { return dataMax;        }
    public int[][] getData()        { return data;           }
    public int[][] getMask()        { return mask;           }
    public double  getCenterX()     { return centerX;        }
    public double  getCenterY()     { return centerY;        }
    public double  getWavelen()     { return wavelen;        }
    public double  getSdd()         { return sdd;            }
    public double  getPixelSize()   { return pixelSize;      }
    public double  getTwist()       { return twist;          }
    public double  getTilt()        { return tilt;           }
    public double  getRepeat()      { return repeat;         }
    public double  getBetaD()       { return betaD;          }
    public double  getGammaD()      { return gammaD;         }
    public double  getOffset()      { return offset;         }
    public double  getdCalibrant()  { return dCalibrant;     } 
    public boolean isRecip()        { return isRecip;        }
    public double  getAspectRatio() { return aspectRatio;    }
    public double  getShrinkScale() { return shrinkScale;    }

    /*---------------------- getters for derivative info --------------------*/
    
    /** get the radius of the pixel at (x, y) relative to pattern center */
    public double getr(int x, int y) {
        double dx = x - centerX;
        double dy = y - centerY;
        return Math.sqrt(dx*dx + dy*dy);
    }  
        
    /** get max dimension of the pattern */
    public double getMaxDimension() {
        int w = getWidth();
        int h = getHeight();
        return Math.sqrt(w*w + h*h);
    }
    
    /** get the intensity value of the pixel at (x, y) */
    public double getI(int x, int y) {
        int x0 = (int)(x / shrinkScale + 0.5);
        int y0 = (int)(y / shrinkScale + 0.5);
        if(x0 < 0 || x0 >= getWidth() || y0 < 0 || y0 >= getHeight()) {
            return 0.0;
        }
        return data[y0][x0];
    }

    /** return normalized sdd (unit is pixel instead of mm as in sdd) */
    public double getSddP() {
        if(pixelSize < EPS) {
            String err = "pixel size not defined!";
            JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }     

        return sdd / pixelSize;
    }
    
    
    /*--------------------------------- setters --------------------------------------*/
    
    public void setCenterX(double d)          { centerX = d;    UIParameter.refresh(); }
    public void setCenterY(double d)          { centerY = d;    UIParameter.refresh(); }
    public void setWavelen(double d)          { wavelen = d;    UIParameter.refresh(); }
    public void setPixelSize(double d)        { pixelSize = d;  UIParameter.refresh(); }
    public void setSdd(double d)              { sdd = d;        UIParameter.refresh(); }
    public void setTwist(double d)            { twist = d;      UIParameter.refresh(); }
    public void setTilt(double d)             { tilt = d;       UIParameter.refresh(); }
    public void setRepeat(double d)           { repeat = d;     UIParameter.refresh(); }
    public void setBetaD(double d)            { betaD = d;      UIParameter.refresh(); }
    public void setGammaD(double d)           { gammaD = d;     UIParameter.refresh(); }
    public void setOffset(double d)           { offset = d;     UIParameter.refresh(); }
    public void setdCalibrant(double d)       { dCalibrant = d; UIParameter.refresh(); }
    public void setRecip(boolean b)           { isRecip = b;    UIParameter.refresh(); }
    public void setMask(int[][] a)            { mask = a;                              }
    public void setDataMin(int i)             { dataMin = i;                           }
    public void setDataMax(int i)             { dataMax = i;                           }
    public void setAspectRatio(double r)      { aspectRatio = r;                       }
    public void setShrinkScale(double s)      { shrinkScale = s;                       }
    public void setDisplay(PatternDisplay d)  { myDisplay = d;                         }
    // TODO: set according to scale factor
    public void setCenterX0(double d)         { centerX = d;    UIParameter.refresh(); }
    public void setCenterY0(double d)         { centerY = d;    UIParameter.refresh(); }
    public void setPixelSize0(double d)       { pixelSize = d;  UIParameter.refresh(); }
    
    /** Recalculate shrinkScale based on new pattern display height  */
    public void recalcShrinkScale(double newHeight) {
        shrinkScale = data.length / newHeight;
    }

    
    /**
     * update reciprocal info
     */
    public void updateReciprocal(boolean verbose) {
        // TODO        
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
     *  transform a point from detector space to reciprocal space (R & L) 
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
    public boolean RL2xy(double RR, double LL, double[] xy)  { 
        return true; // TODO
    }

    /** 
     * transform a point(R & Z) from reciprocal space to detector space 
     */
    public boolean RZ2xy(double R, double Z, double[] xy) { 
        return true; // TODO
    }


} // class Pattern
