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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import java.io.IOException;

public class PatternDisplay {

    private UIPattern imagePanel;
    private JLabel imageLabel;
    private BufferedImage currIndexImage;
    private BufferedImage origIndexImage;
    private int[][] imageDataArray;  // original array for images, input pixels may get skipped
    private int[][] imageIndexArray; // converted to color indexes
    private ColormapControl control;
    private PatternProcessor patternProcessor;

    public PatternDisplay(UIPattern panel, int[][] imageData, PatternProcessor processor) {
        imagePanel       = panel;
        patternProcessor = processor;
        imageDataArray   = PatternUtil.shrinkArray(imageData);
        imageIndexArray  = PatternUtil.computerColorIndex(imageDataArray, 256);
        initialize();
    }

    private void initialize() {

        imagePanel.removeAll();
        imagePanel.setLayout(null);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createEmptyBorder());
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mouseHandler(e);              
                imageLabel.setFocusable(true);
                imageLabel.requestFocusInWindow();
            }
        });       
        imageLabel.addKeyListener(new KeyAdapter() {
            //public void keyReleased(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {
               keyHandler(e);
            }
         });
        
        createColormapControlGUI();    
        control.generatePatternImage();
        imagePanel.add(imageLabel);

        // listener for resizing the window
        imagePanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                resizeImagePanel();
            };
        });
          
        imagePanel.updateUI();
    }
    
    // called upon imagePanel resize event, resize according to new width and aspect ratio
    private void resizeImagePanel() {
        int newWidth = imagePanel.getWidth();
        int newHeight = (int)(newWidth / patternProcessor.getAspectRatio());
        imagePanel.setSize(newWidth, newHeight);
        patternProcessor.setShrinkScale(newWidth); 
        reloadImageLabel();
    }

    // refresh the label according to the size of the frame and fit the currIndexImage too.
    private void reloadImageLabel() {
        //System.out.println("new panel size: " + imagePanel.getWidth() + ", " + imagePanel.getHeight());
        imageLabel.setBounds(0, 0, imagePanel.getWidth(), imagePanel.getHeight());
        currIndexImage = PatternUtil.fitImage(imageLabel, origIndexImage);   // currIndexImage size might change
    }
    
    // open the color map control panel
    private void createColormapControlGUI() {
        control = new ColormapControl(imageIndexArray, imageDataArray, this);
    }

    /**
     * Generate an new indexed BufferedImage based one the originalArray and 
     * current colormap. Called when a colormap change occurs
     */
    void generateIndexedImage(int[][] imageArray) {

        try {           
            origIndexImage = PatternUtil.calcBufferedImage(imageArray);           
            //currIndexImage = PatternUtil.copyBufferedImage(origIndexImage); 
            reloadImageLabel();
        } 
        catch (IOException e) {
            System.out.println("Could not generate currIndexImage.");
        }
    }

    public void openColorControl() {
        control.openColorMapControl();
    }

    // track mouse pointer and draw cursor
    private void mouseHandler(MouseEvent e) {
        //added check for MouseEvent.BUTTON1 which is left click
        if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON1) {
            int x = e.getX();
            int y = e.getY();
            drawCrossHair(x,y);
        }
    }
    
    // update cursor location by arrow keys
    private void keyHandler(KeyEvent e) {
        switch(e.getKeyCode()) { 
            case KeyEvent.VK_UP:
            case KeyEvent.VK_KP_UP:
                 SystemSettings.setCursorY(SystemSettings.getCursorY() - 1);
                 drawCrossHair(SystemSettings.getCursorX(), SystemSettings.getCursorY());
                 break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_KP_DOWN:  
                 SystemSettings.setCursorY(SystemSettings.getCursorY() + 1);
                 drawCrossHair(SystemSettings.getCursorX(), SystemSettings.getCursorY());
                 break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_KP_LEFT:
                 SystemSettings.setCursorX(SystemSettings.getCursorX() - 1);
                 drawCrossHair(SystemSettings.getCursorX(), SystemSettings.getCursorY());
                 break;
            case KeyEvent.VK_RIGHT :
            case KeyEvent.VK_KP_RIGHT:
                 SystemSettings.setCursorX(SystemSettings.getCursorX() + 1);
                 drawCrossHair(SystemSettings.getCursorX(), SystemSettings.getCursorY());
                 break;
        }
    }
    
    /*
     * draw a target centered at the current cursor location
     * 
     *     _|_
     *  __|_|_|__
     *    |_|_|
     *      |   
     */
    // TODO: draw on glass pane? maybe faster.
    private void drawCrossHair(int x, int y) {
        final int A = 12;  // half length of cross hair Axis
        final int B = 6;  // half length of cross hair Box      
        BufferedImage drawImage = PatternUtil.copyBufferedImage(currIndexImage);
        Graphics2D g = drawImage.createGraphics();
        g.setColor(Color.WHITE);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.draw(new Line2D.Double(x-A, y,   x+A, y  ));  
        g.draw(new Line2D.Double(x,   y-A, x,   y+A));  
        g.draw(new Line2D.Double(x-B, y-B, x+B, y-B));  
        g.draw(new Line2D.Double(x-B, y-B, x-B, y+B));         
        g.draw(new Line2D.Double(x+B, y+B, x+B, y-B));
        g.draw(new Line2D.Double(x+B, y+B, x-B, y+B));
        g.dispose();
        imageLabel.setIcon(new ImageIcon(drawImage));   
        //System.out.println("x,y: " + x + "," + y);
        SystemSettings.setCursorX(x);
        SystemSettings.setCursorY(y);
        UIMain.cursorUpdate(x, y);
    }
    
}
