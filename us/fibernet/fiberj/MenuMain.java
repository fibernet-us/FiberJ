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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFrame;

/**
 * A utility class containing the main window's menu data
 */
public final class MenuMain {  
    
    private static final String surName = "main";
    private static final MenuHandlerMainFile fileHandler = new MenuHandlerMainFile();
    private static final MenuHandlerMainImage imageHandler = new MenuHandlerMainImage();
    private static final MenuHandlerMainColormap colormapHandler = new MenuHandlerMainColormap();
    private static final MenuHandlerMainProcess processHandler = new MenuHandlerMainProcess();
    private static final MenuHandlerMainWindow windowHandler = new MenuHandlerMainWindow();
    private static final MenuHandlerMainHelp helpHandler = new MenuHandlerMainHelp();
    
    private static final MenuItem[] MENU_ITEMS = {
      
        new MenuItemComposite(surName,  "File ",    
              new MenuItemLeaf(            "Open",          fileHandler), 
              new MenuItemLeaf(            "Save",          fileHandler), 
              new MenuItemComposite(       "Save As", 
                    new MenuItemLeaf(         "TIF",        fileHandler),
                    new MenuItemLeaf(         "SMV",        fileHandler)),
              new MenuItemLeaf(            "Close",         fileHandler), 
              new MenuItemSeperator(),////////////////////////////////// 
              new MenuItemLeaf(            "Browse",        fileHandler), 
              new MenuItemLeaf(            "Convert",       fileHandler), 
              new MenuItemLeaf(            "Merge",         fileHandler), 
              new MenuItemSeperator(),//////////////////////////////////
              new MenuItemComposite(       "Parameter", 
                    new MenuItemLeaf(         "Load",       fileHandler),
                    new MenuItemLeaf(         "Save",       fileHandler)),
              new MenuItemSeperator(),//////////////////////////////////
              new MenuItemLeaf(            "Exit",          fileHandler)
        ), 

        new MenuItemComposite(surName,  "Image ",   
              new MenuItemLeaf(            "Crop",          imageHandler),
              new MenuItemComposite(       "Flip", 
                    new MenuItemLeaf(         "Horizontal", imageHandler),
                    new MenuItemLeaf(         "Vertical",   imageHandler)),
              new MenuItemComposite(       "Rotate", 
                    new MenuItemLeaf(         "90",         imageHandler),
                    new MenuItemLeaf(         "180",        imageHandler),
                    new MenuItemLeaf(         "270",        imageHandler), 
                    new MenuItemLeaf(         "Custom",     imageHandler)), 
              new MenuItemComposite(       "Resize", 
                    new MenuItemLeaf(         "Actual",     imageHandler),
                    new MenuItemLeaf(         "Fit",        imageHandler),
                    new MenuItemLeaf(         "Custom",     imageHandler)),
              new MenuItemSeperator(),/////////////////////////////////////
              new MenuItemLeaf(            "Repair",        imageHandler),
              new MenuItemLeaf(            "Rescale",       imageHandler)
        ),  

        new MenuItemComposite(surName,  "Colormap ",
              new MenuItemLeaf(            "Customize",     colormapHandler),
              new MenuItemLeaf(            "Load",          colormapHandler), 
              new MenuItemLeaf(            "Save",          colormapHandler)
        ),   

        new MenuItemComposite(surName,  "Process ", 
              new MenuItemLeaf(            "Draw",          processHandler), 
              new MenuItemLeaf(            "Plot",          processHandler), 
              new MenuItemLeaf(            "Filter",        processHandler), 
              new MenuItemLeaf(            "Background",    processHandler),
              new MenuItemLeaf(            "Correction",    processHandler), 
              new MenuItemLeaf(            "Transform",     processHandler)
        ),

        new MenuItemComposite(surName,  "Window ", 
              new MenuItemLeaf(            "Parameter",     windowHandler),  
              new MenuItemLeaf(            "Reflection",    windowHandler),
              new MenuItemLeaf(            "Pixel Viewer",  windowHandler), 
              new MenuItemLeaf(            "Comand Line",   windowHandler),
              new MenuItemLeaf(            "Log",           windowHandler)
        ),
                 
        new MenuItemComposite(surName,  "Help ",    
              new MenuItemLeaf(            "About",         helpHandler), 
              new MenuItemLeaf(            "Resource",      helpHandler)
        )
    };
    
    public MenuMain() {
    }  
     
    public MenuItem[] getMenuItems() {
        return MENU_ITEMS;
    }
    
    
    
    /*
     * Use this to generate code for class MenuHanlderMainDefault. 
     */
    public static void main0(String[] args) {
        JFrame f = new JFrame();
        MenuMain m = new MenuMain();
        MenuBuilder.build(f, m.getMenuItems());
        m.generateDefaultHandlerCode();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }
    
    private void generateDefaultHandlerCode() {
               
        try {
            String fileName = "/home/em/java/FiberJ/MenuHandlerMainDefault.java";           

            String header = "package us.fibernet.fiberj; \n\n" +
                    "/** \n" +
                    " * A default menu handler/adapter for MenuMain, auto generated by \n" +
                    " * MenuMain.generateDefaultHandlerCode(). \n" +
                    " */ \n" +
                    "public class MenuHandlerMainDefault implements MenuHandler { \n\n" +
                    
                    "    public void popupWarning(String what) { \n" +
                    "        javax.swing.JOptionPane.showMessageDialog(null, what + \" - not yet implemented!\"); \n" +
                    "    } \n";
            
            // write new code to file
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            bw.write(header);
            printHandler(bw, MENU_ITEMS);
            bw.write("}\n\n");           
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }        
    }
    
    private void printHandler(BufferedWriter bw, MenuItem... menuItems) throws IOException {
   
        for(MenuItem mi : menuItems) {
            if(mi instanceof MenuItemLeaf) {
                String name = mi.getFullName();
                bw.write("    public void " + name + "()\t\t{ popupWarning(\"" + name + "()\"); } \n");
            }
            else if(mi instanceof MenuItemComposite){
                MenuItemComposite mc = (MenuItemComposite)mi;
                bw.newLine();
                bw.write("    /*\n");
                bw.write("     * " + mc.getFullName() + " handlers\n");
                bw.write("     */\n");
                mc.reset();
                while(mc.hasNext()) {
                    printHandler(bw, mc.next());
                }
            }
        }
    }
    

} // class MenuMain
