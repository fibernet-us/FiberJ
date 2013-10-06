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

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
*  A class holding an InfoItem's name, value, data type and format, JLabel and JTextField
*  for displaying the item, and an InfoItemGuiCallBack object for passing on user input to
*/
public class InfoItemTextField implements InfoItem {

    private JLabel label;
    private JTextField textField;
    boolean editable;
    private int nColumn;  // width of textField
    private String name;
    private String value;
    private String format;
    private DataType dataType;
    private InfoItemGuiCallBack callBack;

    private enum DataType {INT, DOUBLE, STRING};

    /**
     * @param labelStr  the name of this item
     * @param textStr   the value of this this item. the data type and display format of this item
     *                  will be extracted from textStr. e.g.: 0 means value is an integer,
     *                  while 0.00 means value is a double with the length of mantissa being 2
     * @param callBackObj  a InfoItemGuiCallBack object for passing on user input to
     */
    public InfoItemTextField(String labelStr, String textStr, String textFormat, int columns,
                             boolean isEditable, InfoItemGuiCallBack callBackObj) {
        label = new JLabel(labelStr);
        label.setFont(new Font("Arial", Font.BOLD, 12)); // the default font does not show letter "y"!
        textField = new JTextField(textStr);
        name = labelStr;
        value = textStr;
        format = textFormat;
        nColumn = columns;
        editable = isEditable;
        textField.setColumns(nColumn);
        textField.setEditable(editable);
        if(editable) {
            textField.setBackground(new JPanel().getBackground().brighter());
        }
        else {
            textField.setBackground(new JPanel().getBackground());
        }
        callBack = callBackObj;
        addCallback();
        parseDataType();
    }

    /** add label and textField to a JPanel */
    @Override
    public void addTo(JPanel parent) {
        parent.add(label);
        parent.add(textField);
    }

    public JLabel getLabel() {
        return label;
    }

    public JTextField getTextField() {
        return textField;
    }

    /** return value */
    public String getValue() {
        return getFormattedValue();
    }

    /** return value in appropriately formated String form */
    public String getFormattedValue() {

        if(dataType == DataType.INT) {
            return String.format(format, getIntValue());
        }
        else if(dataType == DataType.DOUBLE) {
            return String.format(format, getDoubleValue());
        }
        else {
            return String.format(format, value);
        }
    }

    /** return value as int */
    public int getIntValue() {
        try {
            return Integer.parseInt(value);
        }
        catch(NumberFormatException e) {
            System.out.println("the value of this item can't be converted to an int, return -1");
            return -1;
        }
    }

    /** return value as double */
    public double getDoubleValue() {
        try {
            return Double.parseDouble(value);
        }
        catch(NumberFormatException e) {
            System.out.println("the value of this item can't be converted to a double, return -1");
            return -1;
        }
    }

    public void setLabelFont(Font f) {
        label.setFont(f);
    }

    public void setTextFieldSize(Dimension d) {
        textField.setPreferredSize(d);
        textField.setMaximumSize(d);
        textField.setMinimumSize(d);
    }

    /**
     * verify user input and set value on gui according to desired format
     * @return an error string  @Attention("We return null for success!")
     */
    public String setGuiValue(String input) {
        try {
            if(dataType == DataType.INT) {
                Integer.parseInt(input);
            }
            else if(dataType == DataType.DOUBLE) {
                Double.parseDouble(input);
            }
            value = input;
            textField.setText(getFormattedValue()); // set displayed value according to format
            return null;
        }
        catch(NumberFormatException e) {
            String err = "Invalud number format. " +
                         (dataType == DataType.INT ? "integer" : "double") + " expected. Reset.";
            textField.setText(getFormattedValue()); // reset GUI to display last valid value
            return err;
        }
    }

    /**
     * set value on gui with given input without validation or formatting
     */
    public void setGuiValueNoCheck(String input) {
        value = input;
        textField.setText(input);
    }

    public String toString() {
        return "InfoItemTextField: (" + name + ", " + value + ")";
    }


    // add actionlistener to textField. validate user input. hook up callback.
    private void addCallback() {
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField j = (JTextField)e.getSource();
                String err = setGuiValue(j.getText());
                if(err != null) {
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    callBack.guiUpdated(name, value);
                }
            }
        });
    }

    // get the dataType from format specifier
    private void parseDataType() {
        if(format.matches("%[0-9]*d")) {
            dataType = DataType.INT;
        }
        else if(format.matches("%[0-9]*\\.[0-9]+f")) {
            dataType = DataType.DOUBLE;
        }
        else {
            dataType = DataType.STRING;
        }

        //System.out.println(name + " ==> " + dataType);
    }


} // class InfoItemTextField
