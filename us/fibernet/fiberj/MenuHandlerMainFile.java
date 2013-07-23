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


/**
 * A menu handler for the Main menu bar, File menu
 */
public class MenuHandlerMainFile extends MenuHandlerMain {  
     
    /*
     * File -> Open
     */
    @Override
    public void fileOpen() {
            // fire up a file broswer in current working directory
            JFileChooser fc = new JFileChooser(SystemSettings.getWorkingDir());   
            int response = fc.showOpenDialog(null);      
            if(response == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String filePath = file.getAbsolutePath();
                System.out.println(filePath);
                PatternProcessor.getInstance().createPatternImage(filePath);      
                // user might have changed working directory, update it
                SystemSettings.setWorkingDir(filePath.substring(0,filePath.lastIndexOf(File.separator)));
            } 
            else {
                System.out.println("Open command cancelled");
            }
        }
 
    /*
     * File -> Save
     */
    // TODO
    @Override
    public void fileSave() { }
    
    /*
     * File -> Close
     */
    // TODO
    @Override
    public void fileClose() { }
    /*
     * File -> Exit
     */
    @Override
    public void fileExit() {
        System.exit(0);
    }
}
