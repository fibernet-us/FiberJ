/*
 * Copyright Billy Zheng and Wen Bian. All rights reserved.
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
import java.io.File;

import javax.swing.JOptionPane;

/**
 * A central processor for  pattern processing tasks
 *
 */
public final class PatternProcessor {

    private static Pattern currentPattern = null;  // currently only allow one active pattern
    private static PatternDisplay currentDisplay = null; 
    private static PlotDialog currentPlotter = null;

    public static Pattern getCurrentPattern() {
        return currentPattern;
    }

    public static PlotDialog getCurrentPlotter() {
        return currentPlotter;
    }

    public static double getAspectRatio() {
        return currentPattern.getAspectRatio();
    }

    public static double getShrinkScale() {
        return currentPattern.getShrinkScale();
    }


    public static synchronized void setCurrentPlotter(PlotDialog dp) {
        currentPlotter = dp;
    }

    public static synchronized void openColormap() {
        currentDisplay.openColorControl();
    }

    public static synchronized void reset() {
        if(currentPattern != null) {
            currentPattern.close();
            currentPattern = null;
        }

        if(currentDisplay != null) {
            currentDisplay.close();
            currentDisplay = null;
        }

        if(currentPlotter != null) {
            currentPlotter.close();
            currentPlotter = null;
        }
    }

    /**
     * Read an image file and create an a pattern image
     * @param args  image file path and file attributes if applicable
     */
    public static synchronized void createPatternImage(String... args) {
        if(args.length < 1) {
            return;
        }

        reset();
        currentPattern = PatternReader.readPattern(args);
        createPatternImage(currentPattern, null);
    }

    /**
     * Create an a square rainbow image
     */
    public static synchronized void createPatternImage(int width) {
        if(width < 0) {
            width = 600;
        }

        int[][] intensityData = new int[width][width];
        for(int i=0; i<width; i++) {
            for(int j=0; j<width; j++) {
                intensityData[i][j] = i + j;
            }
        }

        reset();
        currentPattern = new Pattern(intensityData, "", false);
        createPatternImage(currentPattern, null);
    }

    /**
     * Create pattern image display for a Pattern
     */
    public static synchronized void createPatternImage(Pattern pattern, Dimension dim) {
        if(pattern != null) {
            PatternDisplay oldDisplay = currentDisplay;
            currentPattern = pattern;

            //if(dim != null) {
            //    UIMain.resizeToHeight(dim.height);
            //}

            currentDisplay = new PatternDisplay(UIMain.getUIPattern(), pattern);
            UIMain.setTitle(UIMain.getTitle() + "  " + pattern.getName());
            UIMain.updateSizeInfo();
            //UIMain.resizeToHeight(currentPattern.getHeight());
            UIParameter.refresh();
            
            pattern.recalcDisplayScale(UIMain.getUIPattern().getHeight());
            if(pattern.getHeight() > UIMain.getAvailableScreenHeight() || pattern.getWidth() != pattern.getHeight()) {
                PatternProcessor.executeCommand("fit");
            }
            else {
                PatternProcessor.executeCommand("actual");
            }
        }
    }


    /**
     * Save current Pattern data or image into a file
     */
    public static synchronized boolean savePatternToFile(String filename) {
        return PatternWriter.writePattern(currentPattern, filename);
    }


    /**
     * Implements interface ComandProcessor to process user command
     *
     */
    // TODO: look up command from CommandTable and call corresponding action
    public synchronized static void executeCommand(String command) {

        command = command.toLowerCase();
        //System.out.println(command);

        if(command.startsWith("open") || command.startsWith("read")) {
            String filename = command.substring(command.indexOf(' ')).trim();
            // check if the file name is valid, if not, try prepend working directory to it
            if(!new File(filename).exists()) {
                filename = SystemSettings.getWorkingDir() + File.separator + filename;
            }

            if(!new File(filename).exists()) {
                UIMain.getUIMessage().setMessage(filename + " not found");
            }
            else {
                createPatternImage(filename);
            }
        }
        else if(command.startsWith("ac")) {   // to display actual image size
            try {
                UIMain.resizeToHeight(currentPattern.getHeight());
                UIMain.setMessage("data w, h: " + currentPattern.getWidth() + ", " + currentPattern.getHeight());
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }
        else if(command.startsWith("f")) {  // to display image to fit to current window
            try {
                UIMain.resizeToFit();
                UIMain.setMessage("display w, h: " + UIMain.getUIPattern().getWidth() + ", " + UIMain.getUIPattern().getHeight());
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }
        else if(command.startsWith("h") || command.startsWith("w")) { // display to height, width
            try {
                int size = Integer.parseInt(command.substring(command.indexOf(' ')).trim());
                if(command.charAt(0) == 'w') {
                    size /= currentPattern.getAspectRatio();
                }
                UIMain.resizeToHeight(size);
                UIMain.setMessage("display w, h: " + UIMain.getUIPattern().getWidth() + ", " + UIMain.getUIPattern().getHeight());
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }
        else if(command.startsWith("o")) {   // scale data to original image size
            try {
                currentPattern.scaleToOriginal();
                UIMain.setMessage("data w, h: " + currentPattern.getWidth() + ", " + currentPattern.getHeight());
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        } 
        else if(command.startsWith("au")) {   // scale data to current window size
            try {
                currentPattern.scaleFit();
                UIMain.setMessage("data w, h: " + currentPattern.getWidth() + ", " + currentPattern.getHeight());
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        } 
        else if(command.startsWith("r")) { // rotate
            try {
                double degree = Double.parseDouble(command.substring(command.indexOf(' ')).trim());
                currentPattern.rotateData(degree);
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }
        else if(command.startsWith("sc")) { // scroll
            try {
                UIMain.scrollPattern();
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }
        else if(command.startsWith("pwd")) {
            try {
                UIMain.getUIMessage().setMessage(SystemSettings.getWorkingDir());
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

} // class PatternProcessor
