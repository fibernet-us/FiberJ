/*
 * Copyright Kate Wu. All rights reserved.
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * a class to hold the name, value of reciprocal and the label, radio
 * buttons to display it
 * 
 */
public class ParamListItemRecip implements InfoItem {
    
    private JLabel label;
    private String name;
    private boolean value;
    private JRadioButton jrbYes;
    private JRadioButton jrbNo;
    private ButtonGroup jrbGroup = new ButtonGroup();
    private JLabel actionLabel; // the label that changes when click on the
                                // radio buttons
    private InfoItemGuiCallBack callback;
    private static final String MICRO_LABLE = "pixel size (μm)";
    private static final String ANGST_LABLE = "pixel size (1/Å)";

    ParamListItemRecip(String labelStr, boolean isRecip, JLabel actionLabel,
                      InfoItemGuiCallBack callbackObj) {
        
        label = new JLabel(labelStr);
        name = labelStr;
        value = isRecip;
        this.actionLabel = actionLabel;
        jrbYes = new JRadioButton("yes", isRecip);
        jrbNo = new JRadioButton("no", !isRecip);
        callback = callbackObj;
        if(actionLabel != null) {
            addCallback();
        }
        createGroup();
    }

    public void setActionLabel(JLabel actionLabel) {
        this.actionLabel = actionLabel;
        addCallback();
    }
    
    /**
     * add two radio buttons to a group so only one button is selected at a
     * time
     */
    private void createGroup() {
        jrbGroup.add(jrbYes);
        jrbGroup.add(jrbNo);
    }

    /**
     * add action listener to two radio buttons, change the boolean value of
     * reciprocal
     */
    private void addCallback() {
        jrbYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionLabel.setText(ANGST_LABLE);
                value = true;
                callback.guiUpdated(name, "true");
            }
        });
        jrbNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionLabel.setText(MICRO_LABLE);
                value = false;
                callback.guiUpdated(name, "false");
            }
        });
    }

    public boolean getBooleanValue() {
        return value;
    }

    /** update gui according to the boolean value of Reciprocal */
    public void setGuiValue(boolean value) {
        this.value = value;
        jrbYes.setSelected(value);
        jrbNo.setSelected(!value);
    }

    @Override
    public void addTo(JPanel parent) {
    }

    /** return the label component */
    public JLabel getJLabel() {
        return label;
    }

    /** return the Yes radio button component */
    public JRadioButton getJrbYes() {

        return jrbYes;
    }

    /** return the No radio button component */
    public JRadioButton getJrbNo() {

        return jrbNo;
    }

}
