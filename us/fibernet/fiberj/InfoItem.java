/*
 * Copyright Billy Zheng, Tony Yao and Wen Bian. All rights reserved.
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

import javax.swing.JComponent;
import javax.swing.JTextField;


 /**
 *  InfoItem is an auxiliary class holding an info item's name, type, value, 
 *  and the associated GUI component
 */

enum WidgetType { BUTTON, TFIELD, LABELED_TFIELD };
enum DataType {INT, REAL};

public class InfoItem {
    
    private WidgetType wtype; 
    private DataType dtype;
    private String name;
    private double value;
    private JComponent gui;

    InfoItem(WidgetType wtype, DataType dtype, String name) {
        this.wtype = wtype;
        this.dtype = dtype;
        this.name = name;
        this.gui = null;
    }
    
    WidgetType getWidgetType() { return wtype; }
    String getName()           { return name;  }        
    double getValue()          { return value; }
    JComponent getGui()        { return gui;   }    
    
    void setType(WidgetType t) { wtype = t;    }
    void setName(String s)     { name = s;     }
    void setGui(JComponent j)  { gui = j;      }        
    
    /** 
     * return value in the string form of integer or double
     */
    String getStringValue() {
        if(dtype == DataType.INT) {
            return String.format("%d", (int)(value + 0.5));
        } else {
            return String.format("%.4f", value);
        }
    }

    /**
     * called upon reading input file and parameters
     */
    void setValue(double d) {  
            value = d;
            System.out.println(name + " set to " + d);
            setGuiContent();
    }
    
    /**
     * called upon actionPerformed from gui, i.e., user input
     * return null if input valid, else return an error string
     * 
     * @Attention("We return null for success!")
     * @return an error string
     */
    String setValue(String v) {
        try {
            if(dtype == DataType.INT) {
                value = Integer.parseInt(v);
            } else {
                value = Double.parseDouble(v);
            }    
            System.out.println(name + " set to " + v);
            setGuiContent();  
            return null;
        }
        catch(NumberFormatException e) {
            String err = "Invalud number headerFormat. " + 
                         (dtype == DataType.INT ? "integer" : "real") + " expected. Reset.";
            System.out.println(err);
            setGuiContent();
            return err;
        }                
    }
    
    /** 
     * push value to gui when value has been updated from non-gui code
     * do nothing if the gui is not a text field
     */
    void setGuiContent() {
        if(wtype == WidgetType.TFIELD || wtype == WidgetType.LABELED_TFIELD) {
            ((JTextField)gui).setText(getStringValue());
        }
    }    
    
} // class InfoItem
