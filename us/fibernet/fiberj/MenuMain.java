package us.fibernet.fiberj;

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

//package us.fibernet.fiberj;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A utility class containing the main window's menu data
 */
public final class MenuMain {  
    
    private static final String surName = "main";
    private static final MenuHandlerMainFile fileHandler = new MenuHandlerMainFile();
    private static final MenuHandlerMainImage imageHandler = new MenuHandlerMainImage();
    private static final MenuHandlerMainColormap colormapHandler = new MenuHandlerMainColormap();
    private static final MenuHandlerMainDraw drawHandler = new MenuHandlerMainDraw();
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
                    new MenuItemLeaf(         "Custom",     imageHandler)),
              new MenuItemSeperator(),/////////////////////////////////////
              new MenuItemLeaf(            "Repair",        imageHandler),
              new MenuItemLeaf(            "Rescale",       imageHandler)
        ),  

        new MenuItemComposite(surName,  "Colormap ",
              new MenuItemLeaf(            "Load",          colormapHandler), 
              new MenuItemLeaf(            "Save",          colormapHandler), 
              new MenuItemLeaf(            "Customize",     colormapHandler)
        ),   

        new MenuItemComposite(surName,  "Draw ",    
              new MenuItemLeaf(            "Circle",        drawHandler), 
              new MenuItemLeaf(            "Resolution",    drawHandler), 
              new MenuItemLeaf(            "Layerline",     drawHandler), 
              new MenuItemSeperator(),/////////////////////////////////////
              new MenuItemLeaf(            "Mask",          drawHandler), 
              new MenuItemSeperator(),/////////////////////////////////////
              new MenuItemLeaf(            "Refresh",       drawHandler), 
              new MenuItemLeaf(            "Clear",         drawHandler), 
              new MenuItemLeaf(            "Clear All",     drawHandler)
        ), 

        new MenuItemComposite(surName,  "Process ", 
              new MenuItemLeaf(            "Background",    processHandler),
              new MenuItemLeaf(            "Correction",    processHandler), 
              new MenuItemLeaf(            "Filter",        processHandler), 
              new MenuItemLeaf(            "Plot",          processHandler), 
              new MenuItemLeaf(            "Transform",     processHandler)
        ),

        new MenuItemComposite(surName,  "Window ", 
              new MenuItemLeaf(            "Parameter",     windowHandler),  
              new MenuItemLeaf(            "Coordinates",   windowHandler),
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
    public void generateDefaultHandlerCode() {
               
        try {
            String fileName = "/home/em/java/FiberJ/Menu/MenuHandlerMainDefault.java";
            ArrayList<String> lines = new ArrayList<String>();
            String line;
            
            // read in header part of the file
            BufferedReader br = new BufferedReader(new FileReader(fileName));       
            while ((line = br.readLine()) != null) {
                if(line.contains("class MenuHandlerMainDefault")) { 
                    break;
                }
                lines.add(line);
            }
            br.close();
            
            // write new code to file
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            for(String s : lines) {
                bw.write(s);
                bw.newLine();
            }
            
            bw.write("public class MenuHandlerMainDefault implements MenuHandler {");
            bw.newLine();
            bw.newLine();
            bw.write("\tpublic void popupWarning(String what) {");
            bw.newLine();
            bw.write("\t    javax.swing.JOptionPane.showMessageDialog(null, what + \" - not yet implemented!\");");
            bw.newLine();
            bw.write("\t}");          
            bw.newLine();
            
            printHandler(bw, MENU_ITEMS);
            bw.write("}");
            bw.newLine();    
            
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
                bw.write("\tpublic void " + name + "()\t\t{ popupWarning(\"" + name + "()\"); }");
                bw.newLine();
            }
            else if(mi instanceof MenuItemComposite){
                MenuItemComposite mc = (MenuItemComposite)mi;
                bw.newLine();
                bw.write("\t/*");
                bw.newLine();
                bw.write("\t * " + mc.getFullName() + " handlers");
                bw.newLine();
                bw.write("\t */");
                bw.newLine();
                mc.reset();
                while(mc.hasNext()) {
                    printHandler(bw, mc.next());
                }
            }
        }
    }
    

} // class MenuMain
