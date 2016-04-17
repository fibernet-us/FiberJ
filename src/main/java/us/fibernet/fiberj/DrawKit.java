/*
 * Copyright Yi Xiao, Billy Zheng and Wen Bian. All rights reserved.
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
 * MERCHANTABILITY AND FITNESS FOR CA PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * CA utility class for drawing cursor and DrawDialog objects on a PatternDisplay
 */
public class DrawKit {

    private final int CA = 12;  // half length of cursor cross Axis
    private final int CB = 6;   // half length of cursor cross Box
    
    private PatternDisplay myDisplay;
    private Pattern myPattern;
    private BufferedImage imageBuffer;
    private Graphics2D g2d;

    
    // no default constructor
    private DrawKit() { }

    /**
     * construct a DrawKit operating on a PatternDisplay
     */
    public DrawKit(PatternDisplay p) {
        myDisplay = p;
        myPattern = p.getPattern();
    }
    
    
    /**
     * draw a target centered at the current cursor location
     *
     *     _|_
     *  __|_|_|__
     *    |_|_|
     *      |
     */
    // TODO: draw on glass pane? maybe faster.
    public void drawCursor(int x, int y) {
        getGraphics();
        
        g2d.setColor(Color.WHITE);
        g2d.draw(new Line2D.Double(x-CA, y,   x+CA, y  ));
        g2d.draw(new Line2D.Double(x,   y-CA, x,   y+CA));
        g2d.draw(new Line2D.Double(x-CB, y-CB, x+CB, y-CB));
        g2d.draw(new Line2D.Double(x-CB, y-CB, x-CB, y+CB));
        g2d.draw(new Line2D.Double(x+CB, y+CB, x+CB, y-CB));
        g2d.draw(new Line2D.Double(x+CB, y+CB, x-CB, y+CB));
        
        updateDisplay();

        SystemSettings.setCursorX(x);
        SystemSettings.setCursorY(y);
        UIMain.cursorUpdate(x, y);
    }
    
    /**
     *  draw a Circle using Graphics2D.drawOval
     */
    // maybe draw it manually for more accuracy
    // this method is not used
    public void drawCircle(Circle c) {
        getGraphics();
        double shinkScale = myPattern.getShrinkScale();
        
        g2d.setColor(c.getColor());
        int x = (int) ((c.getX() - c.getR()) / shinkScale + 0.5);
        int y = (int) ((c.getY() - c.getR()) / shinkScale+ 0.5);
        int w = (int) ((c.getR() * 2.0) / shinkScale+ 0.5);
        g2d.drawOval(x, y, w, w);
        //System.out.println("x,y,w: " + x + ", " + y + ", " + w);
        updateDisplay();
    }

    /**
     *  draw a list of Circles
     */
    public void drawCircles(ArrayList<Circle> cs) {
        getGraphics();
        double shinkScale = myPattern.getShrinkScale();
        
        for(Circle c : cs) {
            g2d.setColor(c.getColor());
            /*
            int x = (int)(c.getX() - c.getR() + 0.5);
            int y = (int)(c.getY() - c.getR() + 0.5);
            int w = (int)(c.getR() * 2.0 + 0.5);
            g2d.drawOval(x, y, w, w);
            */
            double x0 = c.getX();
            double y0 = c.getY();
            double r  = c.getR();
            double thetaStep = c.getThetaStep();
            double theta = 0;
            int NP = (int)(Math.PI * 2 / thetaStep + 1.5);
            for(int i=0; i<NP; i++) {
                theta += thetaStep;
                double x = (x0 + r * Math.cos(theta)) / shinkScale;
                double y = (y0 + r * Math.sin(theta)) / shinkScale;
                g2d.draw(new Line2D.Double(x, y, x, y));
            }
        }
        
        updateDisplay();
    }

    // draw a Resolution
    public void drawResolution(Resolution r) {
    }

    // draw a list of Resolutions
    public void drawResolutions(ArrayList<Resolution> rs) {
        if(!myPattern.hasReciprocal()) {
            return;
        }

        getGraphics();

        for(Resolution r : rs) {
            g2d.setColor(r.getColor());

            ArrayList<Point> points = myPattern.getResolutionPoints(r);
            if(points == null || points.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Resolution not defined.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for(Point p : points) {
                g2d.draw(new Line2D.Double(p, p));
            }
        }
        
        updateDisplay();
    }

    // draw a Layerline
    public void drawLayerline(Layerline l) {
    }

    // draw a list of Layerlines
    public void drawLayerlines(ArrayList<Layerline> lls) {
        if(!myPattern.hasReciprocal()) {
            return;
        }

        getGraphics();

        for(Layerline ll : lls) {

            g2d.setColor(ll.getColor());
            ArrayList<ArrayList<Point>> llPoints = myPattern.getLayerLines(ll.getR());
            if(llPoints == null) {
                JOptionPane.showMessageDialog(null, "Layerlines not defined.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for(ArrayList<Point> points : llPoints) {
                if(points == null || points.isEmpty()) {
                    continue;
                }

                Point pLast = points.get(points.size() - 1);
                for(int i = points.size() - 2; i >= 0; --i) { // process all points on this layerline
                    Point p = points.get(i);
                    g2d.draw(new Line2D.Double(pLast, p));
                    pLast = p;
                }
            }
        }
        
        updateDisplay();
    }
    
    
    private void getGraphics() {
        imageBuffer = myDisplay.getDisplayImageCopy();
        g2d = imageBuffer.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    private void updateDisplay() {
        g2d.dispose();
        myDisplay.setDisplayImage(imageBuffer);       
    }
    
}
