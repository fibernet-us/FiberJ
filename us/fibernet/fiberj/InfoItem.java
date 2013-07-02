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
