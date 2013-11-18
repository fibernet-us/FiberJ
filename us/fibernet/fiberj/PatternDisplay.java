/*
 * Copyright Yi Xiao and Billy Zheng. All rights reserved.
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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import java.io.IOException;

/**
 * A class to display a pattern as a BufferedImage
 *
 */
public class PatternDisplay {

    private UIPattern imagePanel;
    private JLabel imageLabel;
    private BufferedImage currIndexImage;
    private BufferedImage origIndexImage;
    
    private ColormapControl myColormapControl;
    private Pattern myPattern;
    private PlotKit myPlotKit;
    private DrawKit myDrawKit;
    
    /**
     * Create a PatternDisplay on a UIPanel with a Pattern
     */
    public PatternDisplay(UIPattern panel, Pattern pattern) {
        //imageDataArray = pattern.getData();
        //imageDataArray = PatternUtil.shrinkArray(imageData);
        imagePanel       = panel;
        myPattern        = pattern;
        
        imagePanel.setPattern(this);
        myPattern.setDisplay(this);
        myPlotKit = new PlotKit(this);
        myDrawKit = new DrawKit(this);
        
        imagePanel.removeAll();
        imagePanel.setLayout(null);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createEmptyBorder());
        
        PatternListener mkl = new PatternListener(this);
        imageLabel.addMouseListener(mkl);
        imageLabel.addMouseMotionListener(mkl);
        imageLabel.addKeyListener(mkl);


        myColormapControl = new ColormapControl(this);
        myColormapControl.calculateImageIndex();
        imagePanel.add(imageLabel);

        // listener for resizing the window
        imagePanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                resizeImagePanel();
            };
        });

        imagePanel.updateUI();
    }

    // clean up
    public void close() {
        closeColorControl();
    }
    
    public int getWidth() {
        return imagePanel.getWidth();
    }
    
    public int getHeight() {
        return imagePanel.getHeight();
    }
    
    public Pattern getPattern() {
        return myPattern;
    }
    
    public DrawKit getDrawKit() {
        return myDrawKit;
    }
    
    public PlotKit getPlotKit() {
        return myPlotKit;
    }
    
    public BufferedImage getDisplayImage() {
        return currIndexImage;
    }

    public BufferedImage getDisplayImageCopy() {
        return PatternUtil.copyBufferedImage(currIndexImage);
    }
    
    public void openColorControl() {
        myColormapControl.open();
    }
    
    public void closeColorControl() {
        myColormapControl.close();
        myColormapControl = null;
    }
    
    public boolean isColorControlOpen() {
        return myColormapControl.isOpen();
    }
    
    public void setDisplayImage(BufferedImage image) {
        imageLabel.setIcon(new ImageIcon(image));
    }

    public void plotDraw(Point p1, Point p2) {
        myPlotKit.plotDraw(p1, p2);
    }
    
    
    // called upon imagePanel resize event, resize according to new height and aspect ratio
    private void resizeImagePanel() {
        int newHeight = imagePanel.getHeight();
        int newWidth = (int)(newHeight * myPattern.getAspectRatio());
        imageLabel.setSize(newWidth, newHeight);
        imageLabel.setPreferredSize(new Dimension(newWidth, newHeight));
        myPattern.recalcDisplayScale(newHeight);
        reloadImageLabel();
    }

    // refresh the label according to the size of the frame and fit the currIndexImage too.
    private void reloadImageLabel() {
        //System.out.println("new panel size: " + imagePanel.getWidth() + ", " + imagePanel.getHeight());
        imageLabel.setBounds(0, 0, imagePanel.getWidth(), imagePanel.getHeight());
        currIndexImage = PatternUtil.fitImage(imageLabel, origIndexImage);   // currIndexImage size might change
    }
    
    
    /**
     * Generate an new indexed BufferedImage based one the originalArray and
     * current colormap. Called when a colormap change occurs
     */
    public void generateIndexedImage(int[][] imageArray) {

        try {
            origIndexImage = PatternUtil.calcBufferedImage(imageArray);
            //currIndexImage = PatternUtil.copyBufferedImage(origIndexImage);
            reloadImageLabel();
        }
        catch (IOException e) {
            System.out.println("Could not generate indexed image.");
        }
    }
    

}


