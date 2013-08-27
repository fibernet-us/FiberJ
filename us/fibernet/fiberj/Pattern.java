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

/**
 * A class to contain all information about a fiber diffraction pattern
 */
public class Pattern {
    
    private int[][] data;       // diffracton intensity
    private int[][] mask;       // for pixels who are masked   
    private int width;          // width of this pattern
    private int height;         // height of this pattern
    private int dataMin;        // min value of intensity data
    private int dataMax;        // max value of intensity data
    private boolean isRecip;    // if this pattern is in reciprocal space

    private double xCenter;     // x-coordinate of pattern center (pixel)
    private double yCenter;     // y-coordinate of pattern center (pixel)
    private double wavelen;     // radiation wave length (angstrom)
    private double sdd;         // specimen to detector distance in (millimeter)
    private double sddp;        // normalized sdd (pixel)
    private double pixelSize;   // pixel size of pattern image (micrometer)
                                // or step size in reciprocal space (1/angstrom)
    private double twist;       // fiber missetting angle: rotations about x axis (degree) 
    private double tilt;        // fiber missetting angle: rotations about y axis (degree)
    private double repeat;      // fiber repeat distance (angstrom)
    private double betaD;       // detector missetting angle: rotations about y axis (degree)
    private double gammaD;      // detector missetting angle: rotations about z axis (degree)
    private double offset;      // detector offset or bias or background
    private double dCalibrant;  // d-spacing of calibrant (angstrom)
    
    public Pattern(int[][] data, boolean isRecip) {
        this.data = data;
        this.isRecip = isRecip;
        
        height = data.length;
        width = data[0].length;
        xCenter = width / 2.0;
        yCenter = height / 2.0; 
        mask = new int[height][width];
        
        if(!isRecip) {
            // example data 
            xCenter = 269.50;
            yCenter = 268.34; 
            wavelen = 0.9002;
            pixelSize = 205.2; 
            sdd = 250.0; 
            sddp = sdd * 1000.0 / pixelSize;
            twist = 0.0;        
            tilt = 0.0;         
            repeat = 0.0;              
            betaD = 0.0;              
            gammaD = 0.0;                        
            offset = 0.0;       
            dCalibrant = 3.035;
            System.out.println(this);
        }
        else {
            pixelSize = 0.001;
        }
           
    }
    
    public String toString() {
        return  "width = "      + width     + "\n" +  
                "height = "     + height    + "\n" +  
                "xCenter = "    + xCenter   + "\n" +  
                "yCenter = "    + yCenter   + "\n" +  
                "pixelSize = "  + pixelSize + "\n" +  
                "wavelen = "    + wavelen   + "\n" +  
                "sdd = "        + sdd       + "\n" +  
                "sddp = "       + sddp      + "\n" +  
                "twist = "      + twist     + "\n" +  
                "tilt = "       + tilt      + "\n" +  
                "repeat = "     + repeat    + "\n" +  
                "betaD = "      + betaD     + "\n" +  
                "gammaD = "     + gammaD    + "\n" +  
                "offset = "     + offset    + "\n" +  
                "dCalibrant = " + dCalibrant;
    }
  
    /*--------------------- getters -----------------------*/
    
    public int     getWidth()       { return width;      }
    public int     getHeight()      { return height;     }
    public int     getDataMin()     { return dataMin;    }
    public int     getDataMax()     { return dataMax;    }
    public int[][] getData()        { return data;       }
    public int[][] getMask()        { return mask;       }
    public double  getxCenter()     { return xCenter;    }
    public double  getyCenter()     { return yCenter;    }
    public double  getWavelen()     { return wavelen;    }
    public double  getSdd()         { return sdd;        }
    public double  getSddp()        { return sddp;       }
    public double  getPixelSize()   { return pixelSize;  }
    public double  getTwist()       { return twist;      }
    public double  getTilt()        { return tilt;       }
    public double  getRepeat()      { return repeat;     }
    public double  getBetaD()       { return betaD;      }
    public double  getGammaD()      { return gammaD;     }
    public double  getOffset()      { return offset;     }
    public double  getdCalibrant()  { return dCalibrant; } 
    public boolean isRecip()        { return isRecip;    }

    /**
     *  get max dimension of the pattern 
     */
    public double getMaxDimension() {
        return Math.sqrt(width*width + height*height);
    }
    
    /** get the radius of the pixel at (x, y) relative to pattern center */
    public double getr(int x, int y) {
        double dx = x - xCenter;
        double dy = y - yCenter;
        return Math.sqrt(dx*dx + dy*dy);
    }
    
    /** get the intensity value of the pixel at (x, y) */
    public double getI(int x, int y) {
        int x0 = (int)(x / PatternProcessor.getInstance().getShrinkScale() + 0.5);
        int y0 = (int)(y / PatternProcessor.getInstance().getShrinkScale() + 0.5);
        if(x0 < 0 || x0 >= width || y0 < 0 || y0 >= height) {
            return 0.0;
        }
        return data[y0][x0];
    }
    
    /*--------------------- setters -----------------------*/
    
    public void setMask(int[][] a)       { mask = a;       }
    public void setDataMin(int i)        { dataMin = i;    }
    public void setDataMax(int i)        { dataMax = i;    }
    public void setxCenter(double d)     { xCenter = d;    }
    public void setyCenter(double d)     { yCenter = d;    }
    public void setWavelen(double d)     { wavelen = d;    }
    public void setPixelSize(double d)   { pixelSize = d;  }
    public void setTwist(double d)       { twist = d;      }
    public void setTilt(double d)        { tilt = d;       }
    public void setRepeat(double d)      { repeat = d;     }
    public void setBetaD(double d)       { betaD = d;      }
    public void setGammaD(double d)      { gammaD = d;     }
    public void setOffset(double d)      { offset = d;     }
    public void setdCalibrant(double d)  { dCalibrant = d; }
    public void setRecip(boolean b)      { isRecip = b;    }

    /**
     * set sdd (specimen to detector distance) in millimeter and compute the 
     * normalized distance in pixel
     */
    public void setSdd(double d) {  
        sdd = d;
        sddp = sdd * 1000.0 / pixelSize;
    }
    
    /**
     * replace this pattern's data array with a new one, set dimension and center
     * values, and create a new mask array
     */
    public void setData(int[][] newData) {
        data = newData;
        height = data.length;
        width = data[0].length;
        xCenter = width / 2.0;
        yCenter = height / 2.0; 
        mask = new int[height][width];
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
