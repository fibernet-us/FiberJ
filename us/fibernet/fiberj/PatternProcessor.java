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

import java.io.File;

import javax.swing.JOptionPane;

/**
 * A singleton class serving as the central processor of pattern processing tasks
 *
 */
public final class PatternProcessor {

    private static Pattern currentPattern;  // currently only allow one active pattern
    private static PatternDisplay currentDisplay;

    public static Pattern getCurrentPattern()  { return currentPattern;                   }
    public static double  getAspectRatio()     { return currentPattern.getAspectRatio();  }
    public static double  getShrinkScale()     { return currentPattern.getShrinkScale();  }

    /**
     * Read an image file and create an a pattern image
     * @param args  image file path and file attributes if applicable
     */
    public static synchronized void createPatternImage(String... args) {
        if(args.length < 1) {
            return;
        }

        currentPattern = PatternReader.readPattern(args);
        createPatternImage(currentPattern);
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

        currentPattern = new Pattern(intensityData, "", false);
        createPatternImage(currentPattern);
    }

    /**
     * Create pattern image display for a Pattern
     */
    public static synchronized void createPatternImage(Pattern pattern) {
        if(pattern != null) {
            currentDisplay = new PatternDisplay(UIMain.getUIPattern(), pattern);
            UIMain.setTitle(UIMain.getTitle() + "  " + pattern.getName());
            UIMain.updateSizeInfo();
            UIMain.resizeToHeight(currentPattern.getHeight());
            UIParameter.refresh();
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
        else if(command.startsWith("a")) {   // to actual image size
            try {
                UIMain.resizeToHeight(currentPattern.getHeight());
                UIMain.setMessage("width, height: " + currentPattern.getWidth() + ", " + currentPattern.getHeight());
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }
        else if(command.startsWith("f")) {  // to fit image to current window
            try {
                UIMain.resizeToFit();
                UIMain.setMessage("width, height: " + UIMain.getUIPattern().getWidth() + ", " + UIMain.getUIPattern().getHeight());
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            }
        }
        else if(command.startsWith("h") || command.startsWith("w")) { // to height, width
            try {
                int size = Integer.parseInt(command.substring(command.indexOf(' ')).trim());
                if(command.charAt(0) == 'w') {
                    size /= currentPattern.getAspectRatio();
                }
                UIMain.resizeToHeight(size);
                UIMain.setMessage("width, height: " + (int)(size*currentPattern.getAspectRatio()+0.5) + ", " + size);
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
        else if(command.startsWith("s")) { // scroll
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
