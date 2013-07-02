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

import java.io.File;

/**
 * A singleton class serving as the central processor of pattern processing tasks
 * 
 */
public final class PatternProcessor {

    private static PatternProcessor patternProcessor = new PatternProcessor();
    private static int[][] originalData;  // image data read from file
    private static int[][] workingData;   // current data. likely different from original after some processing
    private static double aspectRatio = 1.0;    // ratio of image width to height
    private static double shrinkScale = 1.0;    // scale factor, size1 of displayData / size1 of workingData
    
    private static PatternDisplay patternDisplay;   // where image will be displayed
    
    /**
     * No instantiation
     */
    private PatternProcessor() { }
    
    /**
     * @return the singleton instance of PatternProcessor
     */
    public static PatternProcessor getInstance() { return patternProcessor; }
    
    public double getAspectRatio() { return aspectRatio; }
    public double getShrinkScale() { return shrinkScale; }
    
    /**
     * Read an image file and display the pattern
     * 
     * @param args image file name and file attributes if applicable
     */
    public synchronized void getInputImage(String... args) {
        
        if(args.length < 1) {
            return;
        }
        
        originalData = ImageReader.readPattern(args);
        if(originalData == null) {
            return;
        }
        
        workingData = originalData;
        aspectRatio = originalData[0].length / originalData.length;
        shrinkScale = 1.0;

        if (workingData != null) {
            UIMain.updateSizeInfo();
            patternDisplay = new PatternDisplay(UIMain.getUIPattern(), workingData, this);
            UIMain.getUIPattern().setPattern(patternDisplay);
        }
        
        UIMain.setTitle(UIMain.getTitle() + " - " + new File(args[0]).getName());
    }

    /**
     * Create a rainbow image
     */
    public synchronized void createRainbowImage(int width) {

        if(width < 0) {
            width = 500;
        }
        
        originalData = new int[width][width];
        for(int i=0; i<width; i++) {
            for(int j=0; j<width; j++) {
                originalData[i][j] = i + j; 
            }
        }
        
        workingData = originalData;
        aspectRatio = 1.0;
        shrinkScale = 1.0;

        patternDisplay = new PatternDisplay(UIMain.getUIPattern(), workingData, this);
        UIMain.getUIPattern().setPattern(patternDisplay);
        
        UIMain.setTitle(UIMain.getTitle());
        UIMain.updateSizeInfo();
    }
    
    public synchronized void setPatternDisplaySize(int width) {
        UIMain.resizeToWidth(width);
    }
    
    /*
     * TODO: update image width/height on title bar?
     */
    public synchronized void setShrinkScale(double newWidth) {
        shrinkScale = originalData[0].length / newWidth;
        //System.out.println("old height: " + originalData.length + ", new width: " + newWidth);
    }

    /**
     * Implements interface ComandProcessor to process user command
     * 
     */
    // TODO: look up command from CommandTable and call corresponding action
    public synchronized void executeCommand(String command) {
        
        //System.out.println(command);
        // a simple example
        if(command.startsWith("open") || command.startsWith("read")) {         
            String filename = command.substring(command.indexOf(' ')).trim();
            // check if the file name is valid, if not, try prepend working directory to it
            if(!new File(filename).exists()) {
                filename = Parameter.getWorkingDir() + File.separator + filename;
            }
            
            if(!new File(filename).exists()) {
                UIMain.getUIMessage().setMessage(filename + " not found");
            }
            else {
                this.getInputImage(filename);
            }                  
        }
        else if(command.startsWith("resize") || command.startsWith("size")) {
            try {
                int newWidth = Integer.parseInt(command.substring(command.indexOf(' ')).trim());
                setPatternDisplaySize(newWidth);   // TODO: not working
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            } 
        }
        else if(command.startsWith("pwd") || command.startsWith("dir")) {
            try {
                UIMain.getUIMessage().setMessage(Parameter.getWorkingDir());
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            } 
        }           
    }
}

