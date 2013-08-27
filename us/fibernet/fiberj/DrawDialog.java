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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;


/**
 * A JDialog window for drawing operations
 *
 */
public class DrawDialog extends JDialog {

    private enum OBJTYPE { CIRCLE, RESOLUTION, LAYERLINE };
    private OBJTYPE objType;
    private static final long serialVersionUID = 1L;
    private static final int DIALOG_WIDTH = 400;
    private JPanel colorPanel;
    //private JPanel radiusPanel;
    //private JPanel centerPanel;
    private JPanel dataPanel;
    private JPanel cntrlPanel;
    private JLabel colorLabel;
    private Color penColor = Color.BLUE;
    
    private JSpinner rSpin;
    private SpinnerNumberModel rModel;
    private double rMin, rMax, rDefault, rStep;

    private JSpinner xSpin, ySpin;
    private SpinnerNumberModel xModel, yModel;
    private double xMin, xMax, xDefault, yMin, yMax, yDefault, xyStep;
    
    private static ArrayList<Circle> circleList;
    
    public DrawDialog(String drawType, Pattern curPattern) {

        // get related parameters first
        if(curPattern == null) {
            rMin = 10;
            rMax = 900;
            rDefault = 100;
            rStep = 1;
            xMin = 0;
            xMax = 600;
            xDefault = 300;
            yMin = 0;
            yMax = 600;
            yDefault = 300;    
            xyStep = 1;       
        }
        else {
            // TODO
            Pattern cp = getCurrentPattern();
            rMin = 10;
            rMax = cp.getMaxDimension();
            rDefault = 100;
            rStep = 1;
            xMin = 0;
            xMax = cp.getWidth();
            xDefault = cp.getxCenter();
            yMin = 0;
            yMax = cp.getHeight();
            yDefault = cp.getyCenter();    
            xyStep = 1;   
        }
        
        circleList = new ArrayList<Circle>();
        
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


    /*
     * Pen color panel
     */
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
                    penColor = c;
                    colorLabel.setBackground(penColor);
                }
            }
        });         
        colorLabel.setForeground(Color.WHITE);
        colorLabel.setOpaque(true);
        colorLabel.setBackground(penColor);
        colorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        parent.add(colorLabel, BorderLayout.CENTER);
    }
    
    /*
     * Object data panel
     */
    private void createDataPanel(JPanel parent, String drawType) {
        if(drawType.equals("Circle")) {
            objType = OBJTYPE.CIRCLE;
            dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS));
            dataPanel.add(createRadiusPanel());
            dataPanel.add(createCenterPanel());
        }
        else if(drawType.equals("Resolution")) {
        // TODO
            objType = OBJTYPE.RESOLUTION;
        }
        else if(drawType.equals("Layerline")) {
        // TODO
            objType = OBJTYPE.LAYERLINE;
        }
    }
    
    /*
     * Control panel
     */
    private void createCntrlPanel(JPanel parent) {
        Dimension dim = new Dimension(DIALOG_WIDTH, 45);
        parent.setPreferredSize(dim);
        parent.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton btnDraw = new JButton("Draw");
        JButton btnList = new JButton("List");
        JButton btnClear = new JButton("Clear");
        JButton btnClearAll = new JButton("Clear All");
        parent.add(btnDraw);
        parent.add(Box.createHorizontalGlue());
        parent.add(btnList);
        parent.add(Box.createHorizontalGlue());
        parent.add(btnClear);       
        parent.add(Box.createHorizontalGlue());
        parent.add(btnClearAll);
        
        btnDraw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                draw();
            }
        });  

        btnList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listObjects();
            }
        });  
        
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });  
        
        btnClearAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });  
    }

    ///////////////////////////////////////////////////////////////
    
    private JPanel createRadiusPanel() {
        JPanel panel = new JPanel();
        
        Dimension dim = new Dimension(DIALOG_WIDTH, 40);
        panel.setPreferredSize(dim);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel radiusLabel = new JLabel("radius ");
        
        rModel = new SpinnerNumberModel(rDefault, rMin, rMax, rStep);  
        rSpin = new JSpinner(rModel);
        //JComponent sfield = rSpin.getEditor();
        //JFormattedTextField tfield = (JFormattedTextField) sfield.getComponent(0);
        //DefaultFormatter formatter = (DefaultFormatter) tfield.getFormatter();
        //formatter.setCommitsOnValidEdit(true);
        rSpin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    rDefault = Double.parseDouble(rSpin.getValue().toString());
                    rChanged(rDefault);
                }
                catch(NumberFormatException nfe) {
                    String err = "invalud decimal number format. reset.";
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                } 
            }
        });
        
        JLabel stepLabel = new JLabel("   step ");
        JTextField stepText = new JTextField();
        dim = new Dimension(50, 20);
        ((JSpinner.DefaultEditor) rSpin.getEditor()).setPreferredSize(dim);
        stepText.setPreferredSize(dim); 
        stepText.setColumns(4);
        stepText.setText(rStep+"");
        stepText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField jt = (JTextField)e.getSource();
                try {
                        rStep = Double.parseDouble(jt.getText());
                        rModel.setStepSize(rStep);    
                }
                catch(NumberFormatException nfe) {
                    String err = "invalud decimal number format. reset.";
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                    jt.setText(rStep+""); 
                }    
            }
        });
        
        panel.add(radiusLabel);
        panel.add(rSpin);
        panel.add(stepLabel);
        panel.add(stepText);
        
        return panel;
    }

    // TODO: need to connect step value with spinner's step value
    //       also need connect center x and y value to current pattern's
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel();
        
        Dimension dim = new Dimension(DIALOG_WIDTH, 40);
        panel.setPreferredSize(dim);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel xLabel = new JLabel("center   x ");
        JLabel yLabel = new JLabel("   y ");
        
        xModel = new SpinnerNumberModel(xDefault, xMin, xMax, xyStep); 
        yModel = new SpinnerNumberModel(yDefault, yMin, yMax, rStep); 
        xSpin = new JSpinner(xModel);
        ySpin = new JSpinner(yModel);
        
        xSpin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    xDefault = Double.parseDouble(xSpin.getValue().toString());
                    xChanged(xDefault);
                }
                catch(NumberFormatException nfe) {
                    String err = "invalud decimal number format. reset.";
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                    xSpin.setValue(xDefault+"");
                } 
            }
        });       
        
        ySpin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    yDefault = Double.parseDouble(ySpin.getValue().toString());
                    yChanged(yDefault);
                }
                catch(NumberFormatException nfe) {
                    String err = "invalud decimal number format. reset.";
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                    ySpin.setValue(yDefault+"");
                } 
            }
        });               
        
        JLabel sLabel = new JLabel("   step ");
        JTextField sText = new JTextField();
        dim = new Dimension(50, 20);
        ((JSpinner.DefaultEditor) xSpin.getEditor()).setPreferredSize(dim);
        ((JSpinner.DefaultEditor) ySpin.getEditor()).setPreferredSize(dim);
        sText.setPreferredSize(dim); 
        sText.setColumns(4);    
        sText.setText(xyStep+"");
        
        sText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField jt = (JTextField)e.getSource();
                try {
                    xyStep = Double.parseDouble(jt.getText());
                    xModel.setStepSize(xyStep);
                    yModel.setStepSize(xyStep);
                }
                catch(NumberFormatException nfe) {
                    String err = "invalud decimal number format. reset.";
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                    jt.setText(xyStep+""); 
                }    
            }
        });
        
        panel.add(xLabel);
        panel.add(xSpin);
        panel.add(yLabel);
        panel.add(ySpin);
        panel.add(sLabel);
        panel.add(sText);
        
        return panel;
    }
    
    // TODO: if there's a circle in circle queue, change its radius to current r
    //       else do nothing (user need to click draw button to draw)
    private void rChanged(double r) {
        switch(objType) {
            case CIRCLE: 
                if(!circleList.isEmpty()) {
                    circleList.get(circleList.size() - 1).setR(r);
                    drawCircles();
                }
                else {
                    Circle c = new Circle(r, xDefault, yDefault, penColor);
                    circleList.add(c);
                    drawCircles();   
                }
               
                break;
            case RESOLUTION: 
                break;
            case LAYERLINE: 
                break;
            default:
                break;
        }  
    }
    
    // TODO: center changed so redrawn all objects
    private void xChanged(double x) {
        switch(objType) {
            case CIRCLE: 
                if(!circleList.isEmpty()) {
                    for(Circle c : circleList) {
                        c.setX(x);
                    }
                    drawCircles();
                }
                getCurrentPattern().setxCenter(x);
               
                break;
            case RESOLUTION: 
                break;
            case LAYERLINE: 
                break;
            default:
                break;
        }  
    }
    
    // TODO: center changed so redrawn all objects
    private void yChanged(double y) {
        switch(objType) {
            case CIRCLE: 
                if(!circleList.isEmpty()) {
                    for(Circle c : circleList) {
                        c.setY(y);
                    }
                    drawCircles();
                }
                getCurrentPattern().setyCenter(y);
               
                break;
            case RESOLUTION: 
                break;
            case LAYERLINE: 
                break;
            default:
                break;
        }  
    }
    
    
    //////////////////////////////////////////////////////////////////////
    
    private void draw() {
        switch(objType) {
            case CIRCLE: 
                Circle c = new Circle(rDefault, xDefault, yDefault, penColor);
                circleList.add(c);
                drawCircles();                
                break;
            case RESOLUTION: 
                break;
            case LAYERLINE: 
                break;
            default:
                break;
        }       
    }
    
    private void listObjects() {
        switch(objType) {
            case CIRCLE: 
                for(Circle c : circleList) {
                    System.out.println(c);
                }
                break;
            case RESOLUTION: 
                break;
            case LAYERLINE: 
                break;
            default:
                break;
        }  
    }
    
    private void clear() {
        switch(objType) {
            case CIRCLE: 
                if(!circleList.isEmpty()) {
                    circleList.remove(circleList.size() - 1);
                    drawCircles();
                }
                else {
                    String err = "circle list is empty";
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                }
               
                break;
            case RESOLUTION: 
                break;
            case LAYERLINE: 
                break;
            default:
                break;
        }  
    }
    
    private void clearAll() {
        switch(objType) {
            case CIRCLE: 
                if(!circleList.isEmpty()) {
                    circleList.clear();
                    drawCircles();
                }
                else {
                    String err = "circle list is empty";
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case RESOLUTION: 
                break;
            case LAYERLINE: 
                break;
            default:
                break;
        }  
    }
    
    // not used
    private void drawCircle(Circle c) {
        UIMain.getUIPattern().drawCircle(c);
    }
    
    // draw on pattern UI all circles in circleList
    private void drawCircles() {
        UIMain.getUIPattern().drawCircles(circleList);
    }
    
    /*
     * @return the current Pattern object
     */
    private Pattern getCurrentPattern() {
        return PatternProcessor.getInstance().getCurrentPattern();
    }
}


