/*
 * Copyright Billy Zheng. All rights reserved.
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * A Jdialog window for drawing operations
 *
 */
public class DrawDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final int DIALOG_WIDTH = 400;
    JPanel colorPanel;
    JPanel radiusPanel;
    JPanel centerPanel;
    JPanel dataPanel;
    JPanel cntrlPanel;
    JLabel colorLabel;
    Color curColor = Color.BLUE;

    public DrawDialog(String drawType) {

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        setTitle("Draw " + drawType);
        
        colorPanel = new JPanel();
        dataPanel = new JPanel();
        cntrlPanel = new JPanel();
              
        // createEmptyBorder(top, left, bottom, right)
        colorPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        dataPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        cntrlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        
        createColorPanel(colorPanel);
        createDataPanel(dataPanel, drawType);
        createCntrlPanel(cntrlPanel);
        
        add(colorPanel);
        add(dataPanel);
        add(cntrlPanel);

        pack();
        setVisible(true);
    }


    private void createColorPanel(JPanel parent) {
        Dimension dim = new Dimension(DIALOG_WIDTH, 50);
        parent.setPreferredSize(dim);
        parent.setLayout(new BorderLayout());
        colorLabel = new JLabel("pen color.  click to change."); 
        colorLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        colorLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color c = JColorChooser.showDialog(null, "", null);
                if(c != null) {
                    curColor = c;
                    colorLabel.setBackground(curColor);
                }
            }
        });         
        colorLabel.setForeground(Color.WHITE);
        colorLabel.setOpaque(true);
        colorLabel.setBackground(curColor);
        colorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        parent.add(colorLabel, BorderLayout.CENTER);
    }
    
    private void createDataPanel(JPanel parent, String drawType) {
        if(drawType.equals("Circle")) {
            dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS));
            dataPanel.add(createRadiusPanel());
            dataPanel.add(createCenterPanel());
        }
        else if(drawType.equals("Resolution")) {
        // TODO
        }
        else if(drawType.equals("Layerline")) {
        // TODO
        }
    }
    
    private void createCntrlPanel(JPanel parent) {
        Dimension dim = new Dimension(DIALOG_WIDTH, 45);
        parent.setPreferredSize(dim);
        parent.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton btnDraw = new JButton("Draw");
        JButton btnClear = new JButton("Clear");
        JButton btnClose = new JButton("CLear All");
        parent.add(btnDraw);
        parent.add(Box.createHorizontalGlue());
        parent.add(btnClear);       
        parent.add(Box.createHorizontalGlue());
        parent.add(btnClose);
    }

    ///////////////////////////////////////////////////////////////
    
    // TODO: need to connect step value with spinner's step value
    //       also need connect center x and y value to current pattern's
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel();
        
        Dimension dim = new Dimension(DIALOG_WIDTH, 40);
        panel.setPreferredSize(dim);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel xLabel = new JLabel("center   x ");
        JLabel yLabel = new JLabel("   y ");
        JSpinner xSpin = new JSpinner();
        JSpinner ySpin = new JSpinner();
        JLabel sLabel = new JLabel("   step ");
        JTextField sText = new JTextField();
        dim = new Dimension(40, 20);
        ((JSpinner.DefaultEditor) xSpin.getEditor()).setPreferredSize(dim);
        ((JSpinner.DefaultEditor) ySpin.getEditor()).setPreferredSize(dim);
        sText.setPreferredSize(dim); 
        sText.setColumns(4);     
        panel.add(xLabel);
        panel.add(xSpin);
        panel.add(yLabel);
        panel.add(ySpin);
        panel.add(sLabel);
        panel.add(sText);
        
        return panel;
    }

    // TODO: need to connect step value with spinner's step value
    private JPanel createRadiusPanel() {
        JPanel panel = new JPanel();
        
        Dimension dim = new Dimension(DIALOG_WIDTH, 40);
        panel.setPreferredSize(dim);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel radiusLabel = new JLabel("radius ");
        JSpinner spinner = new JSpinner();
        JComponent field = ((JSpinner.DefaultEditor) spinner.getEditor());
        JLabel stepLabel = new JLabel("   step ");
        JTextField stepText = new JTextField();
        dim = new Dimension(40, 20);
        field.setPreferredSize(dim);
        stepText.setPreferredSize(dim); 
        stepText.setColumns(4);
        panel.add(radiusLabel);
        panel.add(spinner);
        panel.add(stepLabel);
        panel.add(stepText);
        
        return panel;
    }


}
