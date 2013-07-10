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

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;


/**
 * <pre>
 * The FIBERJ main application window consists of 4 areas (besides window title):
 *   _____________
 *  |______1______|
 *  |______2______|
 *  |             |
 *  |      3      | 
 *  |             |
 *  |_____________|
 *  |______4______|
 * 
 * 1: menu bar, 2: info bar, 3: pattern display, 4: message/command pane 
 * </pre>
 */
public final class UIMain {

    private static final String FIBERJ_VS = "FiberJ 0.3";
    private static JFrame mainFrame;
    private static UIMenubar uiMenubar;  
    private static UIInfobar uiInfobar; 
    private static UIPattern uiPattern;
    private static UIMessage uiMessage;
    
    // these parameters might get overwritten by those in ~/.fiberj or from command line
    private static int wMainwin = 600; // width of mainFrame
    private static int hMainwin = 600; // height of mainFrame     
    private static int hInfobar = 30;  // height of uiInfobar: high enough for info widgets
    private static int hMessage = 30;  // height of uiPattern: high enough for one line of text
    
    // the accumulated size of the parts of mainFrame that are not uiPattern.
    // this info is used to maintain pattern's aspect ratio in window resizing event
    private static int nonPatternWidth, nonPatternHeight;
    
    private static PatternProcessor patternProcessor;
    private static InfoItemCollectionPixel currentPixelInfo;

    /**
     * No instantiation. See init().
     */
    private UIMain() { }  
    
    /**
     * UI component getters, used by UI updater, massager, etc.
     */
    public static UIMenubar getUIMenubar()  {  return uiMenubar;  }
    public static UIInfobar getUIInfobar()  {  return uiInfobar;  }
    public static UIPattern getUIPattern()  {  return uiPattern;  }
    public static UIMessage getUIMessage()  {  return uiMessage;  }
    
    /**
     * UI delegation methods. Pass calls from other component to sub UIs.
     */
    public static void openColormap() {
        uiPattern.openColormap();
    }
    
    /** update the x- and y- coordinate of current pixel at cursor */
    public static void cursorUpdate(int x, int y) {
        currentPixelInfo.updateLocation(x, y);
    }
    
    /**
     * Get the default the main window title
     */
    public static String getTitle() {  
        return FIBERJ_VS;  
    }
    
    /**
     * Set the main window title, for displaying pattern name, size, etc.
     */
    public static void setTitle(String title) {  
        mainFrame.setTitle(title);  
    }
    
    /**
     * Refresh the main window in case of UI add/remove/update.
     */
    public static void refresh()  {
        SwingUtilities.updateComponentTreeUI(mainFrame);
        mainFrame.invalidate();
        mainFrame.validate();
        mainFrame.repaint();
    }
    
    /**
     * Refresh the main window in case of UI add/remove/update.
     */
    public static void resizeToWidth(int width)  {
        double ratio = patternProcessor.getAspectRatio();
        
        if(isZero(ratio)) {  
            return;
        }
        
        int wFrame   = width + nonPatternWidth;
        int hFrame   = (int) (width / ratio) + nonPatternHeight;
        mainFrame.setSize(wFrame, hFrame);
    }
    
    /**
     * Get the accumulated size of the parts of mainFrame that are not uiPattern.
     * This info remains unchanged in window resizing, as uiPattern gets all "resize" as "CENTER".
     */
    public static void updateSizeInfo() {       
        nonPatternWidth  = mainFrame.getWidth() - uiPattern.getWidth(); 
        nonPatternHeight = mainFrame.getHeight() - uiPattern.getHeight(); 
        //System.out.println("   pattern width, height: " + uiPattern.getWidth() + ", " + uiPattern.getHeight());
        //System.out.println("nonPattern width, height: " + nonPatternWidth + ", " + nonPatternHeight);     
    }
    
    public static boolean isZero(double d) {
        return Math.abs(d) < 0.000001;  // define an global epsilon if needed
    }
    
    /**
     * Maintain the pattern's aspect ratio in window resizing event
     */
    public static void onFrameResize() {        
        double ratio = patternProcessor.getAspectRatio();
        
        if(isZero(ratio)) {  
            return;
        }
        
        // reset frame height based on corrected width
        int wFrame   = mainFrame.getWidth();
        int wPattern = wFrame - nonPatternWidth;
        int hPattern = (int) (wPattern / ratio);
        int hFrame   = hPattern + nonPatternHeight;
        mainFrame.setSize(wFrame, hFrame);
    }
    
    /**
     * Create application UI and data hooks. Synchronize it to ensure UI creation happen once 
     * and only once. No use of double checking lock which does not work on all JVMs. 
     * 
     * @param x x-coordinate of the main window's startup top-left position
     * @param y y-coordinate of the main window's startup top-left position
     * @param wMain if > 0 overwrite default wMainwin         
     * @param hMain if > 0 overwrite default hMainwin       
     * @param hInfo if > 0 overwrite default hInfobar       
     * @param hMess if > 0 overwrite default hMessage 
     */
    public static synchronized void init(int x, int y, int wMain, int hMain, int hInfo, int hMess) {
        
        if(mainFrame != null) {
            return;
        }

        if(wMain > 0) { wMainwin = wMain; }    
        if(hMain > 0) { hMainwin = hMain; }
        if(hInfo > 0) { hInfobar = hInfo; }
        if(hMess > 0) { hMessage = hMess; }
        
        mainFrame = new JFrame(FIBERJ_VS);
        mainFrame.setBounds(x, y, wMainwin, hMainwin);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        patternProcessor = PatternProcessor.getInstance();
        currentPixelInfo = new InfoItemCollectionPixel();
        
        uiMenubar = new UIMenubar(mainFrame, new MenuDataMain());
        uiInfobar = new UIInfobar(mainFrame, hMainwin, hInfobar);
        uiInfobar.addInfoItemCollection(currentPixelInfo);
        uiPattern = new UIPattern(mainFrame, hMainwin, 0);
        uiMessage = new UIMessage(mainFrame, hMainwin, hMessage, patternProcessor);

        Border lineBorder = BorderFactory.createLineBorder(Color.gray);
        uiInfobar.setBorder(lineBorder);
        uiPattern.setBorder(lineBorder);
        uiMessage.setBorder(lineBorder);

        // add() is forwarded to getContentPane().add().
        // use BorderLayout to keep height of uiInfobar and uiMessage fixed
        // and let uiPattern take on resizing (BorderLayout.CENTER is greedy)
        mainFrame.add(uiInfobar, BorderLayout.PAGE_START);
        mainFrame.add(uiPattern, BorderLayout.CENTER);
        mainFrame.add(uiMessage, BorderLayout.PAGE_END);    
        
        // listen for window resizing for pattern resizing
        mainFrame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
               onFrameResize();
            };
        });
        
        mainFrame.setVisible(true);
    }


    /**
     * <pre>
     * @param args: in the form of -x value[,value]                        
     *              -c int,int: coordinates (x, y) of the main window's top-left corner at startup
     *              -d int,int: dimension (width, height) of the main window at startup          
     *              -i int    : info bar height, just large enough to show info widgets
     *              -m int    : message pane height, just large enough to show one line of text
     *              -p string : pattern image file name 
     * </pre>
     */
    /*
     *  TODO: parse command line argument & configuration file (~/.fiberj) 
     *       with command line given higher precedence over configuration
     */       
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SystemSettings.init();
                    UIMain.init(100, 100, 600, 600, 0, 0);
                    patternProcessor.createRainbowImage(600);
                    
                    //patternProcessor.getInputImage("2048.tif");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

} // Class UIMain
