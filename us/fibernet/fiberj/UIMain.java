/*
 * Copyright Wen Bian and Billy Zheng. All rights reserved.
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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

    private static final String FIBERJ_VS = "FiberJ 0.7";
    private static Image myIcon;
    private static JFrame mainFrame;
    private static JScrollPane jScrollPane; // to scroll pattern when its display size is too big
    private static JPanel patternPanel; // to hold uiPattern
    private static JMenuBar  uiMenubar; 
    private static UIInfobar uiInfobar; 
    private static UIPattern uiPattern;
    private static UIMessage uiMessage;
    
    // these parameters might get overwritten by those in ~/.fiberj or from command line
    private static int wPattern = 600; // width of uiPattern
    private static int hPattern = 600; // height of uiPattern     
    private static int hInfobar = 30;  // height of uiInfobar: high enough for info widgets
    private static int hMessage = 30;  // height of uiMessage: high enough for one line of text
    private static int patternPadding = 5;  // padding around uiPattern in patternPanel 
    
    // size of screen that is available for apps. equal to raw screen size minus size of taskbar, sidebar, etc
    private static int availableScreenWidth, availableScreenHeight;    
    
    // the accumulated size of the parts of mainFrame that are not uiPattern.
    // this info is used to maintain pattern's aspect ratio in window resizing event
    private static int nonPatternWidth, nonPatternHeight;
    
    private static boolean isScrollPattern;  // if pattern is in a scrolled pane
    
    private static InfoItemCollectionPixel currentPixelInfo;

    
    /**
     * No instantiation. See init().
     */
    private UIMain() { }  
    
    /**
     * Create application UI and data hooks. Synchronize it to ensure UI creation happen once 
     * and only once. No use of double checking lock which does not work on all JVMs. 
     * 
     * @param x x-coordinate of the main window's startup top-left position
     * @param y y-coordinate of the main window's startup top-left position
     * @param wPatt if > 0 overwrite default wPattern         
     * @param hPatt if > 0 overwrite default hPattern       
     * @param hInfo if > 0 overwrite default hInfobar       
     * @param hMess if > 0 overwrite default hMessage 
     */
    public static synchronized void init(int x, int y, int wPatt, int hPatt, int hInfo, int hMess) {

        if(mainFrame != null) {
            return;
        }

        if(wPatt > 0) { wPattern = wPatt; }    
        if(hPatt > 0) { hPattern = hPatt; }
        if(hInfo > 0) { hInfobar = hInfo; }
        if(hMess > 0) { hMessage = hMess; }

        mainFrame = new JFrame(FIBERJ_VS);
        mainFrame.setLocation(x, y);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        currentPixelInfo = new InfoItemCollectionPixel();

        uiMenubar = MenuBuilder.build(mainFrame, new MenuMain().getMenuItems());
        uiPattern = new UIPattern(mainFrame, wPattern, hPattern);
        uiMessage = new UIMessage(mainFrame, wPattern, hMessage);
        uiInfobar = new UIInfobar(mainFrame, wPattern, hInfobar, currentPixelInfo);

        Border lineBorder = BorderFactory.createLineBorder(Color.gray);
        uiInfobar.setBorder(lineBorder);
        uiPattern.setBorder(lineBorder);
        uiMessage.setBorder(lineBorder);

        // add() is forwarded to getContentPane().add().
        // use BorderLayout to keep height of uiInfobar and uiMessage fixed
        // and let uiPattern take on resizing (BorderLayout.CENTER is greedy)
        mainFrame.setLayout(new BorderLayout()); 
        mainFrame.add(uiInfobar, BorderLayout.PAGE_START);
        mainFrame.add(uiMessage, BorderLayout.PAGE_END);    

        // a hack to allow pattern resize to smaller than the main window and remain fully visible
        // without changing patter display related code - it seems quite messy to do that.
        patternPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, patternPadding, patternPadding));
        patternPanel.add(uiPattern);
        mainFrame.add(patternPanel, BorderLayout.CENTER);

        // listen for window resizing for pattern resizing
        mainFrame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                onMainFrameResize();
            };
        });

        java.net.URL url = ClassLoader.getSystemResource("us/fibernet/fiberj/image/x32.png");
        myIcon = java.awt.Toolkit.getDefaultToolkit().createImage(url);
        mainFrame.setIconImage(myIcon);
        
        mainFrame.pack();
        mainFrame.setVisible(true);
        isScrollPattern = false;
    }

    
    /**
     * UI component getters, used by UI updater, massager, etc.
     */
    public static JMenuBar getUIMenubar()   {  return uiMenubar;  }
    public static UIInfobar getUIInfobar()  {  return uiInfobar;  }
    public static UIPattern getUIPattern()  {  return uiPattern;  }
    public static UIMessage getUIMessage()  {  return uiMessage;  }
    public static Image getMyIcon()         {  return myIcon;     }
    public static int getAvailableScreenWidth()    {  return availableScreenWidth - nonPatternWidth;  };
    public static int getAvailableScreenHeight()   {  return availableScreenHeight - nonPatternHeight; };
    
    /** Get and set the main window title */
    public static String getTitle()            { return FIBERJ_VS; }
    public static void setTitle(String title)  { mainFrame.setTitle(title);   }
    
    /*
     * UI delegation methods. Pass calls from other component to sub UIs.
     */ 
    public static void openColormap()              { uiPattern.openColormap();              }
    public static void setMessage(String message)  { uiMessage.setMessage(message);         }
    public static void cursorUpdate(int x, int y)  { currentPixelInfo.updateLocation(x, y); }


    /** Refresh the main window in case of UI add/remove/update. */
    public static void refresh()  {
        SwingUtilities.updateComponentTreeUI(mainFrame);
        mainFrame.invalidate();
        mainFrame.validate();
        mainFrame.repaint();
    }
    
    /**
     * Remove the scrollbar from pattern display
     */
    public static synchronized void unScrollPattern() {
        if(isScrollPattern) {
            jScrollPane.remove(patternPanel);
            mainFrame.remove(jScrollPane);           
            jScrollPane.setVisible(false);
            mainFrame.add(patternPanel, BorderLayout.CENTER);
            isScrollPattern = false;    
        }       
        refresh();
    }
    
    /**
     * Add a scrollbar to pattern display
     */
    public static synchronized void scrollPattern() {
        if(!isScrollPattern) {
            mainFrame.remove(patternPanel);
            jScrollPane = new JScrollPane(patternPanel);
            jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            mainFrame.add(jScrollPane, BorderLayout.CENTER);
            isScrollPattern = true;     
        }
        refresh();
    }
    
    /**
     * Resize pattern per user command line input. Add or remove scrollbar accordingly.
     */
    public static void resizeToHeight(int height)  {
        double ratio = PatternProcessor.getAspectRatio();       
        if(isZero(ratio)) {  
            return;
        }
        
        int width = (int) (height * ratio + 0.5);
        uiPattern.setPreferredSize(new Dimension(width, height));
        
        if(height + patternPadding + nonPatternHeight > availableScreenHeight) {
            scrollPattern();    
        }
        else {
            unScrollPattern();
            mainFrame.pack();         
        }
    }
    
    /**
     * Resize pattern to fit current window
     */
    public static void resizeToFit()  {
        int height = mainFrame.getHeight() - nonPatternHeight;
        resizeToHeight(height);
    }
    
    /**
     * Maintain the pattern's aspect ratio in window resizing event
     */
    public static void onMainFrameResize() {    
        if(isScrollPattern) {
            return;
        }
        
        double ratio = PatternProcessor.getAspectRatio();
        if(isZero(ratio)) {  
            return;
        }
        
        // reset frame height based on corrected height and width-height ratio
        int hFrame   = mainFrame.getHeight();
        int hPatt = hFrame - nonPatternHeight;
        int wPatt = (int) (hPatt * ratio);
        uiPattern.setPreferredSize(new Dimension(wPatt, hPatt));
        //layout does work properly if we also set the size of patternPanel
        //patternPanel.setPreferredSize(new Dimension(wPatt+patternPadding, hPatt+patternPadding));
        mainFrame.pack();
        setMessage("width, height: " + wPatt + ", " + hPatt);
    }
    
    /** check if a double value is close to zero */
    public static boolean isZero(double d) {
        return Math.abs(d) < 0.000001;  // define an global epsilon if needed
    }
    
    /**
     * Get the accumulated size of the parts of mainFrame that are not uiPattern.
     * This info remains unchanged in window resizing, as uiPattern gets all "resize" as "CENTER".
     */
    public static void updateSizeInfo() {       
        nonPatternWidth  = mainFrame.getWidth() - uiPattern.getWidth(); 
        nonPatternHeight = mainFrame.getHeight() - uiPattern.getHeight();  
    }
    


    
    /**
     * <pre>
     * @param args: in the form of -x value[,value]                        
     *              -c int,int: coordinates (x, y) of the main window's top-left corner at startup
     *              -d int,int: dimension (width, height) of the pattern panel          
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
                    //try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
                    //catch (Exception e) { }    
                    //
                    // screen size
                    //GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                    //screenWidth = gd.getDisplayMode().getWidth();
                    //screenHeight = gd.getDisplayMode().getHeight();
                    
                    // get available screen size
                    Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
                    availableScreenWidth = (int)r.getWidth() - 50;
                    availableScreenHeight = (int)r.getHeight() - 50;

                    SystemSettings.init();
                    UIMain.init(100, 100, 600, 600, 0, 0);
                    PatternProcessor.createPatternImage(600);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

} // Class UIMain
