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

/**
 * A singleton class serving as the central processor of pattern processing tasks
 * 
 */
public final class PatternProcessor {

    private static PatternProcessor instance = new PatternProcessor();
    private int[][] originalData;  // image data read from file
    private int[][] workingData;   // current data. likely different from original after some processing
    private double aspectRatio = 1.0;  // ratio of image width to height
    private double shrinkScale = 1.0;  // scale factor, size1 of displayData / size1 of workingData    
    private static Pattern currentPattern;  // currently only allow one active pattern
    
    /** no instantiation */
    private PatternProcessor() { }
    
    /** @return the singleton instance of PatternProcessor  */
    public static PatternProcessor getInstance()  { return instance; }
  
    // getters 
    public double  getAspectRatio()     { return aspectRatio;    }
    public double  getShrinkScale()     { return shrinkScale;    }
    public Pattern getCurrentPattern()  { return currentPattern; }

    // setters
    public void setAspectRatio(double ratio)        { aspectRatio = ratio;         }
    public void setshrinkScale(double scale)        { shrinkScale = scale;         }
    public void setCurrentPattern(Pattern pattern)  { createPatternImage(pattern); }
   
    
    /**
     * Recalculate shrinkScale based on new pattern display width
     */
    public synchronized void recalcShrinkScale(double newWidth) {
        shrinkScale = originalData[0].length / newWidth;
    }
    
    
    /**
     * Read an image file and create an a pattern image 
     * @param args  image file path and file attributes if applicable
     */
    public synchronized void createPatternImage(String... args) {

        if(args.length < 1) {
            return;
        }

        workingData = ImageReader.readPattern(args);
        if(workingData == null) {
            return;
        }

        currentPattern = new Pattern(workingData, false);
        createPatternImage(workingData, new File(args[0]).getName()); // add only file name itself
    }
  
    /** 
     * Create an a square rainbow image (no pattern data)
     */
    public synchronized void createPatternImage(int width) {

        if(width < 0) {
            width = 500;
        }
        
        workingData = new int[width][width];
        for(int i=0; i<width; i++) {
            for(int j=0; j<width; j++) {
                workingData[i][j] = i + j; 
            }
        }
        
        createPatternImage(workingData, "");
    }
    
    /**
     * Create a pattern image from a Pattern object
     */
    public synchronized void createPatternImage(Pattern pattern) {

        if(pattern == null) {
            return;
        }
        
        currentPattern = pattern;
        workingData = pattern.getData();         
        createPatternImage(workingData, "");
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
                filename = SystemSettings.getWorkingDir() + File.separator + filename;
            }
            
            if(!new File(filename).exists()) {
                UIMain.getUIMessage().setMessage(filename + " not found");
            }
            else {
                this.createPatternImage(filename);
            }                  
        }
        else if(command.startsWith("resize") || command.startsWith("size")) {
            try {
                int newWidth = Integer.parseInt(command.substring(command.indexOf(' ')).trim());
                UIMain.resizeToWidth(newWidth);   // TODO: not working properly?
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            } 
        }
        else if(command.startsWith("pwd") || command.startsWith("dir")) {
            try {
                UIMain.getUIMessage().setMessage(SystemSettings.getWorkingDir());
            }
            catch(NumberFormatException e) {
                e.printStackTrace();
            } 
        }           
    }

    // create pattern display and colormap control 
    // TODO: maybe we should pass in a Pattern object instead of dataArray
    private synchronized void createPatternImage(int[][] dataArray, String title) {
        originalData = dataArray.clone();
        aspectRatio = dataArray[0].length / dataArray.length;        
        new PatternDisplay(UIMain.getUIPattern(), dataArray, this);
        UIMain.updateSizeInfo();
        UIMain.setTitle(UIMain.getTitle() + " - " + title);    
    }
    
} // class PatternProcessor
