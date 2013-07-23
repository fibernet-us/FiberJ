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
    
    private int width, height;     // width and height of the pattern
    private int dataMin, dataMax;  // min and max value of intensity data
    private int[][] data;          // diffracton intensity
    private int[][] mask;          // for pixels who are masked   
    private boolean isRecip;       // if a pattern is reciprocal

    private double fiber_twist;        /* fiber missetting angles: */
    private double fiber_tilt;         /* -rotations about x and y axes */
    private double fiber_repeat;       /* fiber repeat distance */
    private double wave_length;        /* radiation wave length */
    private double rwave_length;       /* 1/wave_length */
    private double SF_distance;        /* specimen to film distance */
    private double nSF_distance;       /* normalize SF_distance */
    private double fcenter_x, fcenter_y;   /* center of the film */
    private double betaD;              /* detector missetting angles: */
    private double gammaD;             /* -rotations about y and z axies */
    private double raster_unit;        /* pixel size of detector (or film) */
    private double detector_fog;       /* */
    private double calibrant_d;
    /*
    double twist;       // fiber missetting angle: rotations about x axis 
    double tilt;        // fiber missetting angle: rotations about y axis 
    double repeat;      // fiber repeat distance 
    double waveLength;  // radiation wave length in Angstrom
    double sdd;         // specimen to detector distance 
    double xCenter;     // x-coordinate of pattern center
    double yCenter;     // y-coordinate of pattern center
    double betaD;       // detector missetting angle: rotations about y axis
    double gammaD;      // detector missetting angle: rotations about z axis 
    double pixelSize;   // pixel size of detector (or film) 
    double detectorFog; // 
    double dCalibrant;  // d-spacing of the calibrant, in Angstrom
    */
    
    public Pattern(int[][] dataArrary, boolean isReciprocal) {
        data = dataArrary;
        isRecip = isReciprocal;
    }

    /** get the radius of the pixel at (x, y) relative to pattern center */
    double getr(int x, int y) {
        double dx = x - fcenter_x;
        double dy = y - fcenter_y;
        return Math.sqrt(dx*dx + dy*dy);
    }
    
    /** get the intensity value of the pixel at (x, y) */
    double getI(int x, int y) {
        int x0 = (int)(x / PatternProcessor.getInstance().getShrinkScale() + 0.5);
        int y0 = (int)(y / PatternProcessor.getInstance().getShrinkScale() + 0.5);
        if(x0 < 0 || x0 >= width || y0 < 0 || y0 >= height) {
            return 0.0;
        }
        return data[y0][x0];
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDataMin() {
        return dataMin;
    }

    public int getDataMax() {
        return dataMax;
    }

    public int[][] getData() {
        return data;
    }

    public int[][] getMask() {
        return mask;
    }

    public double getFiber_twist() {
        return fiber_twist;
    }

    public double getFiber_tilt() {
        return fiber_tilt;
    }

    public double getFiber_repeat() {
        return fiber_repeat;
    }

    public double getWave_length() {
        return wave_length;
    }

    public double getRwave_length() {
        return rwave_length;
    }

    public double getSF_distance() {
        return SF_distance;
    }

    public double getnSF_distance() {
        return nSF_distance;
    }

    public double getFcenter_x() {
        return fcenter_x;
    }

    public double getFcenter_y() {
        return fcenter_y;
    }

    public double getBetaD() {
        return betaD;
    }

    public double getGammaD() {
        return gammaD;
    }

    public double getRaster_unit() {
        return raster_unit;
    }

    public double getDetector_fog() {
        return detector_fog;
    }

    public double getCalibrant_d() {
        return calibrant_d;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setDataMin(int dataMin) {
        this.dataMin = dataMin;
    }

    public void setDataMax(int dataMax) {
        this.dataMax = dataMax;
    }

    public void setData(int[][] data) {
        this.data = data;
    }

    public void setMask(int[][] mask) {
        this.mask = mask;
    }

    /*------------------------------- setters -------------------------------*/
    
    public void setFiber_twist(double fiber_twist) {
        this.fiber_twist = fiber_twist;
    }

    public void setFiber_tilt(double fiber_tilt) {
        this.fiber_tilt = fiber_tilt;
    }

    public void setFiber_repeat(double fiber_repeat) {
        this.fiber_repeat = fiber_repeat;
    }

    public void setWave_length(double wave_length) {
        this.wave_length = wave_length;
    }

    public void setRwave_length(double rwave_length) {
        this.rwave_length = rwave_length;
    }

    public void setSF_distance(double sF_distance) {
        SF_distance = sF_distance;
    }

    public void setnSF_distance(double nSF_distance) {
        this.nSF_distance = nSF_distance;
    }

    public void setFcenter_x(double fcenter_x) {
        this.fcenter_x = fcenter_x;
    }

    public void setFcenter_y(double fcenter_y) {
        this.fcenter_y = fcenter_y;
    }

    public void setBetaD(double betaD) {
        this.betaD = betaD;
    }

    public void setGammaD(double gammaD) {
        this.gammaD = gammaD;
    }

    public void setRaster_unit(double raster_unit) {
        this.raster_unit = raster_unit;
    }

    public void setDetector_fog(double detector_fog) {
        this.detector_fog = detector_fog;
    }

    public void setCalibrant_d(double calibrant_d) {
        this.calibrant_d = calibrant_d;
    }

} // class Pattern
