/*
 * Copyright Wen Bian and Billy Zheng. All rights reserved.
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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A UI class for displaying program messages and taking user commands
 * and pass the commands to an PatternProcessor.
 * 
 * Either of the two text fields can be used as command line. When one 
 * is used as command line, the other will be used for message output.
 */
@SuppressWarnings("serial")
public class UIMessage extends JPanel {

    private JFrame parentFrame;  // might be useful down the road
    private JTextField[] textFields;   // two text fields, one for message, one for command
    private int messageFieldNumber = 0; // id of the textField used to display messages
    PatternProcessor patternProcessor; // to which to pass user commands
    
    public UIMessage(JFrame parent, int width, int height, PatternProcessor processor) {     
        parentFrame = parent;
        patternProcessor = processor;
        Dimension dim = new Dimension(width, height);      
        setPreferredSize(dim); 
        setMinimumSize(dim); 
        setMaximumSize(dim);
        setLayout(new GridLayout(1,2)); 
        
        textFields = new JTextField[2];
        for(int i=0; i<2; i++) {
            textFields[i] = new JTextField();
            textFields[i].setName(i + ""); 
            textFields[i].setBackground(this.getBackground().brighter());     
            textFields[i].setEditable(true);
            
            // add actionLister to take user command and process it
            textFields[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JTextField jt = (JTextField)e.getSource();
                    char id = jt.getName().charAt(0); // first char is jt's id, '0' or '1'
                    messageFieldNumber = '1' - id;  // use the other field for message. '1' should be next to '0'...
                    processCommand(jt.getText());
                }
            });
            add(textFields[i]);
        }
    }
    
    /**
     * Show message or action result on the available textfield 
     */
    public void setMessage(String messsage) {
        textFields[messageFieldNumber].setText(messsage);
    }
    
    /**
     * Get the command string from the command line text field
     */
    public String getCommand() {
        return textFields[1 - messageFieldNumber].getText();
    }
    
    /**
     * Pass a user command String to PatternProcessor
     */
    public void processCommand(String command) {
        if(command != null) {
            patternProcessor.executeCommand(command);
        }
    }
    
} // class UIMessage

