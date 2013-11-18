/*
 * Copyright Billy Zheng. All rights reserved.
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
import java.awt.geom.Point2D;
import java.util.ArrayList;

import us.fibernet.fiberj.PlotDialog.PlotType;

/**
 * A utility class for extracting pixel data of a plot object.
 * This should be part of Pattern class, but to prevent its size out of control,
 * we moved these data extraction methods into this class.
 *
 */
public class PlotData {

    private static ArrayList<Point2D> plotData = new ArrayList<Point2D>();
    private static Pattern curPattern;
    private static PlotType lastPlotType;
    private static Point pointStart;
    private static Point pointEnd;
        
    /**
     * @return a list of Point2D that contains data from last plot operation
     */
    public static ArrayList<Point2D> getPlotData() {
        switch(lastPlotType) {
            case LINE:
                extractDataLine(pointStart, pointEnd);
                break;
            case RADIUS:
                extractDataRadius(pointEnd);
                break;
            case ARC:
                extractDataArc(pointStart, pointEnd);
                break;
            case CIRCLE:
                extractDataCircle(pointEnd);
                break;
            case SECTOR:
                extractDataSector(pointEnd);
                break;              
            case RECTANGLE:
                extractDataRectAngle(pointStart, pointEnd);
                break;
            default:
                break;
        }

        /*
        for(Point2D p : plotData) {
            System.out.println(p);
        }
        */
        
        return plotData;
    }
    
    public static void setLastPlotData(PlotType t, Point start, Point end) {
        lastPlotType = t;
        pointStart = start;
        pointEnd = end;
    }
    
    // add (x,y) to plotData 
    private static void addPoint(double x, double v) {
        x *= curPattern.getShrinkScale();
        plotData.add(new Point2D.Double(x, v));
    }
    
    // get pixel v(px, py) and add it to plotData (x, v)
    private static void addPoint(double px, double py, double x, boolean doScale) {
        double v = curPattern.getI(px, py);
        if(doScale) {
            x *= curPattern.getShrinkScale();
        }
        plotData.add(new Point2D.Double(x, v));
    }
    
    
    // get aggregated pixel data along X or along Y and add it to plotdata (x, avg(v))
    private static void addPoint(boolean aggregateY, int px, int py, int offset, double x) {
        double v= curPattern.getI(aggregateY, px, py, offset);
        x *= curPattern.getShrinkScale();
        plotData.add(new Point2D.Double(x, v));
    }
    
    
    // get pixel data along a line segment defined by 2 points
    public static void extractDataLine(Point p1, Point p2) {
        setup();
        
        int x1 = p1.x;
        int y1 = p1.y;
        int x2 = p2.x;
        int y2 = p2.y;
        int dx = x2 - x1;
        int dy = y2 - y1;
        int adx = Math.abs(dx);
        int ady = Math.abs(dy);
        
        if(dx == 0) { // vertical line
            int y = dy > 0 ? y1 : y2;
            for(int i=0; i<=ady; i++) {
                addPoint(x1, y+i, i, true);
            }
        }
        else if(dy == 0) { // orizontal line
            int x = dx > 0 ? x1 : x2;
            for(int i=0; i<=adx; i++) {
                addPoint(x+i, y1, i, true);
            }
        }
        // get data along x in 0.5 pix steps
        else if(adx > ady) {  
            double k = (dy + 0.0) / dx;
            double x = x1;
            double y = y1;
            double hdx = 0.5; // x step
            double kdx = k * hdx;
            double dr = Math.sqrt(hdx*hdx + kdx*kdx);
            /*
             *  Xn+1 = Xn + hdx
             *  Yn+1 = Yn + K * (Xn+1 - Xn)  
             *       = Yn + K * hdx 
             *       = Yn + kdx
             */     
            
            for(int i=0; i<=2*adx; i++) {
                double r = dr * i; 
                addPoint(x, y, r, true);
                x += hdx;
                y += kdx;
            }
        }
        // get data along y in 0.5 pix steps
        else {
            double k = (dx + 0.0) / dy;
            double y = y1;
            double x = x1;
            double hdy = 0.5; // y step
            double kdy = k * hdy;
            double dr = Math.sqrt(hdy*hdy + kdy*kdy);
            
            for(int i=0; i<=2*ady; i++) {
                double r = dr * i; 
                addPoint(x, y, r, true);
                y += hdy;
                x += kdy;
            }
        }
    }

    public static void extractDataRadius(Point p) {
        setup();
        int x = (int) Math.round(getCenterPX());
        int y = (int) Math.round(getCenterPY());
        extractDataLine(new Point(x,y), p);
    }
    
    // get pixel data within a rectangle box and aggregate it along the shorter dimension
    public static void extractDataRectAngle(Point p1, Point p2) {
        setup();
        
        int x1 = p1.x;
        int y1 = p1.y;
        int x2 = p2.x;
        int y2 = p2.y;
        int dx = x2 - x1;
        int dy = y2 - y1;
        int adx = Math.abs(dx);
        int ady = Math.abs(dy);
        int x = dx > 0 ? x1 : x2;
        int y = dy > 0 ? y1 : y2;
        
        if(adx > ady) { // aggregate y
            for(int i=0; i<=adx; i++) {
                addPoint(true, x+i, y, ady, x+i);
            }
        }
        else { // aggregate x
            for(int i=0; i<=ady; i++) {
                addPoint(false, x, y+i, adx, y+i);
            }
        }                
    }
    
    // get pixel data on a circle
    public static void extractDataCircle(Point p) {    
        setup();
        
        double x0 = getCenterPX();
        double y0 = getCenterPY();
        double dx = p.x - x0;
        double dy = p.y - y0;
        double r  = Math.sqrt(dx*dx + dy*dy);
        double step = 1.0; // degree
        int np = (int)(360 / step);
        double theta = 0;
        for(int i=0; i<np; i++) {
            theta += step;
            double x = x0 + r * Math.cos(theta);
            double y = y0 + r * Math.sin(theta);
            addPoint(x, y, theta, false);
        }
    }

    // get pixel data on an arc
    // p1 determines the enclosing rectangle, and start angle
    public static void extractDataArc(Point p1, Point p2) {
        setup();
        
        double x0 = getCenterPX();
        double y0 = getCenterPY();
        double dx = p1.x - x0;
        double dy = p1.y - y0;
        double r = Math.sqrt(dx*dx + dy*dy);
        double step = 0.5; // degree
        
        // (360 - ) transfrom our coordinate system to the standard
        double start =  (360 - Math.toDegrees(Math.atan2(dy, dx))); // % 360;
        double end = (360 - Math.toDegrees(Math.atan2(p2.y - y0, p2.x - x0))); // % 360;
        
        int np = (int)(Math.abs(end - start) / step);
        double theta = start % 360;
        double rad = 0;
        double radstep = Math.toRadians(step);
        for(int i=0; i<np; i++) {
            theta += step;
            rad += radstep;
            double x = x0 + r * Math.cos(rad);
            double y = y0 + r * Math.sin(rad);
            addPoint(x, y, theta, false);
        }
    }
        
    // get pixel data in a sector
    // p is at the center of the sector arc
    public static void extractDataSector(Point p) {
        setup();
        
        double angle = PatternProcessor.getCurrentPlotter().getSectorAngle();
        double x0 = getCenterPX();
        double y0 = getCenterPY();
        double dx = p.x - x0;
        double dy = p.y - y0;
        double r0 = Math.sqrt(dx*dx + dy*dy);

        double start = (360 - Math.toDegrees(Math.atan2(dy, dx))) % 360 - (angle / 2);   
        double end = start + angle;     
        double astep = 0.5; // degree
        double rstep = 1.0;
        int nr = (int)(r0 / rstep);
        int np = (int)((end - start) / astep);
        double r = 0;
        double theta = 0;
        
        astep = Math.toRadians(astep);
        for(int i=0; i<nr; i++) {
            r += rstep;
            double sum = 0;
            for(int j=0; j<np; j++) {
                theta += astep;
                double x = x0 + r * Math.cos(theta);
                double y = y0 + r * Math.sin(theta);
                sum += curPattern.getI(x, y);
            }
            addPoint(r, sum/np);
        }
    }
    
    private static double getCenterPX() {
        return curPattern.getCenterX() / curPattern.getShrinkScale();
    }
    
    private static double getCenterPY() {
        return curPattern.getCenterY() / curPattern.getShrinkScale();
    }

    private static void setup() {
        plotData.clear();
        curPattern = PatternProcessor.getCurrentPattern();
    }
 
    
}
