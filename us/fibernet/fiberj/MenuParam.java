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
import javax.swing.JMenuBar;

/**
 * A utility class containing the main window's menu data
 */
public final class MenuParam {

    private static final String surName = "param";
    private static final MenuHandlerParamDefault fileHandler = new MenuHandlerParamDefault();
    private static final MenuHandlerParamParameter parameterHandler = new MenuHandlerParamParameter();

    private static final MenuItem[] MENU_ITEMS = {

        new MenuItemComposite(surName,  "Window",
              new MenuItemLeaf(            "Reflection",      fileHandler),
              new MenuItemLeaf(            "Parameter",       fileHandler)
        ),

        new MenuItemComposite(surName,  "Parameter",
              new MenuItemComposite(       "Center from",
                    new MenuItemLeaf(         "Reflection",   parameterHandler),
                    new MenuItemLeaf(         "Cursor",       parameterHandler)),
              new MenuItemComposite(       "Distance from",
                    new MenuItemLeaf(         "Reflection",   parameterHandler),
                    new MenuItemLeaf(         "Ring",         parameterHandler)),
              new MenuItemSeperator(),/////////////////////////////////////
              new MenuItemLeaf(            "Refine Selected", parameterHandler),
              new MenuItemSeperator(),/////////////////////////////////////
              new MenuItemLeaf(            "Load",            parameterHandler),
              new MenuItemLeaf(            "Save",            parameterHandler)
        ),

        new MenuItemComposite(surName,  "Reflection",
              new MenuItemComposite(       "Select",
                    new MenuItemLeaf(         "Box",          fileHandler),
                    new MenuItemLeaf(         "Pointer",      fileHandler)),
              new MenuItemSeperator(),/////////////////////////////////////
              new MenuItemLeaf(            "Index",           fileHandler),
              new MenuItemLeaf(            "Auto Index",      fileHandler),
              new MenuItemSeperator(),/////////////////////////////////////
              new MenuItemLeaf(            "Delete",          fileHandler),
              new MenuItemLeaf(            "Undelete",        fileHandler)
        ),

        new MenuItemComposite(surName,  "Patch",
              new MenuItemLeaf(            "Set Pixel",       fileHandler),
              new MenuItemLeaf(            "Erase Pixel",     fileHandler),
              new MenuItemLeaf(            "Undo Pixel",      fileHandler)
        ),

        new MenuItemComposite(surName,  "Help",
              new MenuItemLeaf(            "Parameter",       fileHandler),
              new MenuItemLeaf(            "Reflection",      fileHandler)
        )
    };

    public MenuParam() {
    }

    public MenuItem[] getMenuItems() {
        return MENU_ITEMS;
    }



    /*
     * Use this to generate code for class MenuHandlerParamDefault
     */
    public static void main0(String[] args) {
        JFrame f = new JFrame();
        MenuParam m = new MenuParam();
        MenuBuilder.build(f, m.getMenuItems());
        m.generateDefaultHandlerCode();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

    private void generateDefaultHandlerCode() {

        try {
            String fileName = "/home/em/java/FiberJ/MenuHandlerParamDefault.java";

            String header = "package us.fibernet.fiberj; \n\n" +
                    "/** \n" +
                    " * A default menu handler/adapter for MenuParam, auto generated by \n" +
                    " * MenuParam.generateDefaultHandlerCode(). \n" +
                    " */ \n" +
                    "public class MenuHandlerParamDefault implements MenuHandler { \n\n" +

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


} // class ParamMain
