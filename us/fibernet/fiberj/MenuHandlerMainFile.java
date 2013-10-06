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

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
 * A menu handler for the Main menu bar, File menu
 */
public class MenuHandlerMainFile extends MenuHandlerMainDefault {

    /*
     * Main Menu -> File -> Open
     */
    @Override
    public void mainFileOpen() {
        // fire up a file browser in current working directory
        JFileChooser fc = new JFileChooser(SystemSettings.getWorkingDir());
        int response = fc.showOpenDialog(null);
        if(response == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String filePath = file.getAbsolutePath();
            System.out.println(filePath);
            PatternProcessor.createPatternImage(filePath);
            // user might have changed working directory, update it
            SystemSettings.setWorkingDir(filePath.substring(0,filePath.lastIndexOf(File.separator)));
        }
        else {
            System.out.println("Open command cancelled");
        }
    }

    /*
     * Main Menu -> File -> Close
     */
    public void mainFileClose() {
        // TODO: clean up resources
        PatternProcessor.createPatternImage(600);
    }


    /*
     * Main Menu -> Save As -> SMV
     */
    public void mainFileSaveAsSMV() {
        saveImageFile("smv");
    }

    /*
     * Main Menu -> Save As -> PNG
     */
    public void mainFileSaveAsPNG() {
        saveImageFile("png");
    }

    /*
     * Main Menu -> Save As -> JPG
     */
    public void mainFileSaveAsJPG() {
        saveImageFile("jpg", "jpeg");
    }

    /*
     * Main Menu -> File -> Exit
     */
    @Override
    public void mainFileExit() {
        System.exit(0);
    }


    /*\*************************************************************************
     *
     * TODO section
     *
     */

    /*
     * Main Menu -> File -> Save
     */
    public void mainFileSave() {
        super.mainFileSave();
    }

    /*
     * Main Menu -> File -> Save As -> TIF
     */
    public void mainFileSaveAsTIF() {
        super.mainFileSaveAsTIF();
    }

    /*
     * Main Menu -> File -> Browse
     */
    public void mainFileBrowse() {
        super.mainFileBrowse();
    }

    /*
     * Main Menu -> File -> Convert
     */
    public void mainFileConvert() {
        super.mainFileConvert();
    }

    /*
     * Main Menu -> File -> Merge
     */
    public void mainFileMerge() {
        super.mainFileMerge();
    }


    /*
     * mainFileParameter handlers
     */
    public void mainFileParameterLoad() {
        super.mainFileParameterLoad();
    }

    public void mainFileParameterSave() {
        super.mainFileParameterSave();
    }



    /*\*************************************************************************
     *
     * private method section
     *
     */

    // A helper method used by all Save As method
    private void saveImageFile(String... type) {

        // fire up a file browser in current working directory
        JFileChooser fc = new JFileChooser(SystemSettings.getWorkingDir());

        // loop until user enters a value file name to write to, or user cancel the dialog
        while(true) {
            int response = fc.showOpenDialog(null);

            if(response == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String filePath = file.getAbsolutePath();

                // deal with cases where the file exists
                if(new File(filePath).exists()) {
                    int answer = JOptionPane.showConfirmDialog(null,  // center window
                            filePath + " exists. Do you want to overwrite it?", // message
                            "Danger!",  // title
                            JOptionPane.YES_NO_OPTION); // option list

                    if(answer == JOptionPane.NO_OPTION) {
                        continue;
                    }
                }

                // update working directory
                SystemSettings.setWorkingDir(filePath.substring(0,filePath.lastIndexOf(File.separator)));
                System.out.println(filePath);

                // check file extension to see if it conforms to given type
                String ext = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();

                boolean validExt = false;
                for(String t : type) {
                    if(t.equals(ext)) {
                        validExt = true;
                        break;
                    }
                }

                if(!validExt) {
                    String err = ext + " is not a valid extension for a " + type[0] + " file!";
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                if(!PatternProcessor.savePatternToFile(filePath)) {
                    JOptionPane.showMessageDialog(null, "Save failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            else {
                //System.out.println("Open command cancelled");
            }

            break;
        }
    }


} // class MenuHandlerMainFile

