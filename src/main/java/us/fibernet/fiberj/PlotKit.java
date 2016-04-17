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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

import us.fibernet.fiberj.PlotDialog.PlotType;

/**
 * A utility class for drawing PlotDialog objects on a PatternDisplay
 * and extracting plot data from those objects for plotting
 */
public class PlotKit {

    private PatternDisplay myDisplay;
    private Pattern myPattern;

    // no default constructor
    private PlotKit() { }

    public PlotKit(PatternDisplay p) {
        myDisplay = p;
        myPattern = p.getPattern();
    }

    public void plotDraw(Point p1, Point p2) {
        //
        // get a copy of display image and create a g2d on it
        //
        PlotDialog curPlotter = PatternProcessor.getCurrentPlotter();
        if(curPlotter == null) {
            return;
        }
        
        Color color = curPlotter.getPenColor();
        BufferedImage drawImage = myDisplay.getDisplayImageCopy();
        Graphics2D g = drawImage.createGraphics();
        g.setColor(color);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // use g to draw on the image
        PlotType type = curPlotter.getPlotType();
        switch(type) {
            case LINE:
                plotLine(p1, p2, g);
                break;
            case RADIUS:
                plotRadius(p2, g);
                break;
            case ARC:
                plotArc(p1, p2, g);
                break;
            case CIRCLE:
                plotCircle(p2, g);
                break;
            case SECTOR:
                plotSector(p2, g);
                break;              
            case RECTANGLE:
                plotRectangle(p1, p2, g);
                break;
            default:
                break;
        }      

        // release g and update display image
        g.dispose();
        myDisplay.setDisplayImage(drawImage);
        PlotData.setLastPlotData(type, p1, p2);
    }

    private void plotLine(Point p1, Point p2, Graphics2D g) {
        g.drawLine(p1.x,  p1.y,  p2.x,  p2.y);
    }

    private void plotRadius(Point p, Graphics2D g) {
        int x = (int)(getPatternCenterX() + 0.5);
        int y = (int)(getPatternCenterY() + 0.5);
        g.drawLine(x,  y,  p.x,  p.y);
    }

    // TODO: can't draw across left x-axis (180 degree line)
    private void plotArc(Point p1, Point p2, Graphics2D g) {
        // p1 determines the enclosing rectangle, and start angle
        double x0 = getPatternCenterX();
        double y0 = getPatternCenterY();
        double dx = p1.x - x0;
        double dy = p1.y - y0;
        double r = Math.sqrt(dx*dx + dy*dy);
        int x = (int)(x0 - r + 0.5);
        int y = (int)(y0 - r + 0.5);
        int w = (int)(2*r + 0.5);
        
        // (360 - ) transfrom our coordinate system to the standard
        double start =  360 - Math.toDegrees(Math.atan2(dy, dx));
        double end = 360 - Math.toDegrees(Math.atan2(p2.y - y0, p2.x - x0));
        
        // draw clockwise
        double extent = (end - start); // % 360;       
        Arc2D arc = new Arc2D.Double(x, y, w, w, start, extent, Arc2D.OPEN);
        if (arc != null) {
            g.draw(arc);
        }
    }

    private void plotCircle(Point p, Graphics2D g) {
        double x0 = getPatternCenterX();
        double y0 = getPatternCenterY();
        double dx = p.x - x0;
        double dy = p.y - y0;
        double r = Math.sqrt(dx*dx + dy*dy);
        int x = (int)(x0 - r + 0.5);
        int y = (int)(y0 - r + 0.5);
        int w = (int)(2*r + 0.5);
        g.drawOval(x, y, w, w);
    }

    // draw a sector centered at a Point 
    private void plotSector(Point p, Graphics2D g) {
        double x0 = getPatternCenterX();
        double y0 = getPatternCenterY();
        double dx = p.x - x0;
        double dy = p.y - y0;
        double r = Math.sqrt(dx*dx + dy*dy);
        int x = (int)(x0 - r + 0.5);
        int y = (int)(y0 - r + 0.5);
        int w = (int)(2*r + 0.5);

        double extent = PatternProcessor.getCurrentPlotter().getSectorAngle(); 
        double start = (360 - Math.toDegrees(Math.atan2(dy, dx))) % 360 - (extent / 2);       
        Arc2D arc = new Arc2D.Double(x, y, w, w, start, extent, Arc2D.PIE);

        if (arc != null) {
            g.draw(arc);
        }
    }

    // TODO: currently can only draw from top-left to bottom-right 
    private void plotRectangle(Point p1, Point p2, Graphics2D g) {
        g.drawRect(p1.x, p1.y, p2.x - p1.x,  p2.y - p1.y);
    }

    private double getPatternCenterX() {
        return myPattern.getCenterX() / myPattern.getShrinkScale();
    }

    private double getPatternCenterY() {
        return myPattern.getCenterY() / myPattern.getShrinkScale();
    }
    
}
