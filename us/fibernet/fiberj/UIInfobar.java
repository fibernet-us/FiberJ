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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.util.HashMap;
import java.util.Map;

/**
 * A JPanel for displaying pattern related information
 */
@SuppressWarnings("serial")
public class UIInfobar extends JPanel {

    private JFrame parentFrame;
    
    // column width is likely the width of the widest char in the default font.
    // 4 columns should be wide enough for 6 digits.
    // TODO: give user an option to change this?
    private static int TEXT_COLUMNS = 4; 
    
    // infobar items are InfoItem objects
    private static InfoItem[] infoList = { 
        new InfoItem(WidgetType.LABELED_TFIELD, DataType.INT,  "x"),
        new InfoItem(WidgetType.LABELED_TFIELD, DataType.INT,  "y"),
        new InfoItem(WidgetType.LABELED_TFIELD, DataType.REAL, "r"), 
        new InfoItem(WidgetType.LABELED_TFIELD, DataType.REAL, "I"),
        new InfoItem(WidgetType.LABELED_TFIELD, DataType.REAL, "d"), 
        new InfoItem(WidgetType.LABELED_TFIELD, DataType.REAL, "D"),
        new InfoItem(WidgetType.LABELED_TFIELD, DataType.REAL, "R"), 
        new InfoItem(WidgetType.LABELED_TFIELD, DataType.REAL, "Z")  
    };
    
    // maybe used by other module to look up and set InfoItems
    private Map<String, InfoItem> infoTable;   
    
    /**
     * set up dimension attribute and create infobar UI
     */
    public UIInfobar(JFrame parent, int width, int height) {
        parentFrame = parent;
        Dimension dim = new Dimension(width, height);
        setPreferredSize(dim); 
        setMinimumSize(dim);  
        infoTable = new HashMap<String, InfoItem>();
        initialize();
    }

    // add a list of InfoItems to the panel, on one row
    private void initialize() {
        setLayout(new FlowLayout(FlowLayout.LEFT));      
        for (int i = 0; i < infoList.length; i++) {
            addInfoItem(infoList[i]);
        }
    }

    // Append an InfoItem to the right end
    private void addInfoItem(InfoItem infoItem) {
        
        switch (infoItem.getWidgetType()) {
            case BUTTON:
                JButton jb = new JButton(infoItem.getName());
                add(jb);
                break;
                //
            case LABELED_TFIELD:
                JLabel jl = new JLabel(infoItem.getName());
                add(jl);
                //
                // fall through. label always followed by its text field
                //
            case TFIELD:
                JTextField jt = new JTextField();
                jt.setName(infoItem.getName());     
                jt.setBackground(this.getBackground().brighter());
                // jt.setMinimumSize(new Dimension(WIDGET_WIDTH, WIDGET_HEIGHT));
                jt.setColumns(TEXT_COLUMNS);
                jt.setText(infoItem.getStringValue());
                jt.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JTextField j = (JTextField)e.getSource();
                        InfoItem datum = infoTable.get(j.getName());
                        if(datum != null) {
                            String err = datum.setValue(j.getText());
                            if(err != null) {
                                JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });

                infoItem.setGui(jt);
                infoTable.put(infoItem.getName(), infoItem); 
                add(jt);
                break;
                //
            default:
                break;
        }
        
        add(Box.createHorizontalStrut(1)); // add spacing between InfoItems
    }

} // class UIInfobar
