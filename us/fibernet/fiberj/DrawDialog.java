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

import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JTabbedPane;
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

    private final int CIRCLE = 0;
    private final int RESOLUTION = 1;
    private final int LAYERLINE = 2;
    private static final long serialVersionUID = 1L;
    private static final int DIALOG_WIDTH = 400;

    private JPanel[] tabPages;
    private JPanel colorPanel;
    private JPanel dataPanel;
    private JPanel cntrlPanel;
    private JLabel colorLabel;
    private Color penColor = Color.BLUE;
    
    private JLabel spinLabel;
    private String[] labelStrings = {"resolution ", "resolution ", "repeat "};
    private JSpinner rSpin;
    private SpinnerNumberModel rModel;
    private ChangeListener rListener;
    
    // data default for circle, resolution, layerline
    private double[] rMin   = { 10,  2,   1   };
    private double[] rMax   = { 900, 200, 500 };
    private double[] rValue = { 100, 10,  70  };
    private double[] rStep  = { 1,   1,   1   };
    
    private JSpinner xSpin, ySpin;
    private SpinnerNumberModel xModel, yModel;
    private double xMin   = 0;
    private double xMax   = 600;
    private double xStart = 300;
    private double yMin   = 0;
    private double yMax   = 600;
    private double yStart = 300;
    private double xyStep = 1;
    
    private static ArrayList<Circle> circleList;
    private static ArrayList<Resolution> resolutionList;
    private static ArrayList<Layerline> layerlineList;
    
    private int curTabIndex = 0;

    
    public DrawDialog(Pattern curPattern) {
        if(curPattern != null) {
            rMin[CIRCLE] = 10;
            rMax[CIRCLE] = curPattern.getMaxDimension();
            rValue[CIRCLE] = 100;
            rStep[CIRCLE] = 1;
            xMin = 0;
            xMax = curPattern.getWidth();
            xStart = curPattern.getCenterX();
            yMin = 0;
            yMax = curPattern.getHeight();
            yStart = curPattern.getCenterY();    
            xyStep = 1;   
        }
        circleList = new ArrayList<Circle>();
        resolutionList = new ArrayList<Resolution>();
        layerlineList = new ArrayList<Layerline>();
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        setTitle("Draw");
        initialize();
    }


    //Initialize the contents of the control panels.
    private void initialize() {

        setLayout(new BorderLayout());
         
        // Create tab pages
        tabPages = new JPanel[3];
        createTabCircle(tabPages[CIRCLE] = new JPanel());
        createTabResolution(tabPages[RESOLUTION] = new JPanel());
        createTabLayerline(tabPages[LAYERLINE] = new JPanel());

        
        // Create tabs
        final JTabbedPane controlTabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        //controlTabs.setAlignmentX(CENTER_ALIGNMENT);
        controlTabs.addTab("Circle", tabPages[CIRCLE]);
        controlTabs.addTab("Resolution", tabPages[RESOLUTION]);
        controlTabs.addTab("Layerline", tabPages[LAYERLINE]);
        controlTabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                curTabIndex = controlTabs.getSelectedIndex();
                showTab(curTabIndex);
            }
        });
        
        add(controlTabs, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }
    
    private void showTab(int index) {
        rSpin.removeChangeListener(rListener);
        tabPages[index].add(colorPanel, BorderLayout.PAGE_START);
        tabPages[index].add(dataPanel, BorderLayout.CENTER);
        tabPages[index].add(cntrlPanel, BorderLayout.PAGE_END);
        spinLabel.setText(labelStrings[index]);
        rModel.setMinimum(rMin[index]);
        rModel.setMaximum(rMax[index]);
        rModel.setValue(rValue[index]);
        rModel.setStepSize(rStep[index]);
        rSpin.setModel(rModel);
        pack();
        rSpin.addChangeListener(rListener);
    }
    
    private void createTabCircle(JPanel parent) {        
        createColorPanel(colorPanel = new JPanel());
        createCntrlPanel(cntrlPanel = new JPanel());
        // createEmptyBorder(top, left, bottom, right)
        colorPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        cntrlPanel.setBorder(BorderFactory.createCompoundBorder(
                               BorderFactory.createLineBorder(Color.gray),
                               BorderFactory.createEmptyBorder(5, 10, 5, 10)));        
        createDataPanel(dataPanel = new JPanel());
        dataPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        parent.setLayout(new BorderLayout());
        parent.add(colorPanel, BorderLayout.PAGE_START);
        parent.add(dataPanel, BorderLayout.CENTER);
        parent.add(cntrlPanel, BorderLayout.PAGE_END);
    }
    
    private void createTabResolution(JPanel parent) {
        parent.setLayout(new BorderLayout());
    }
 
    private void createTabLayerline(JPanel parent) {
        parent.setLayout(new BorderLayout());
    }
    
    /**
     * @return the resolution of the current circle for computing sdd.
     *         assuming the circle is drawn on calibrant ring and
     *         assuming the center is determined correctly.
     *         
     */
    public static double getCurrentCircleRadius() {
        if(!circleList.isEmpty()) {
           return circleList.get(circleList.size() - 1).getR();
        }
        else {
            String err = "calibrant ring is not drawn!";
            JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
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
    private void createDataPanel(JPanel parent) {
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS));
        dataPanel.add(createRadiusPanel());
        dataPanel.add(createCenterPanel());
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
        spinLabel = new JLabel(labelStrings[curTabIndex]);
        
        rModel = new SpinnerNumberModel(rValue[curTabIndex], rMin[curTabIndex], rMax[curTabIndex], rStep[curTabIndex]);  
        rSpin = new JSpinner(rModel);
        //JComponent sfield = rSpin.getEditor();
        //JFormattedTextField tfield = (JFormattedTextField) sfield.getComponent(0);
        //DefaultFormatter formatter = (DefaultFormatter) tfield.getFormatter();
        //formatter.setCommitsOnValidEdit(true);
        
        rListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    double v = Double.parseDouble(rSpin.getValue().toString());
                    rChanged(v);
                }
                catch(NumberFormatException nfe) {
                    String err = "invalud decimal number format. reset.";
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                } 
            }
        };
        rSpin.addChangeListener(rListener);
        
        JLabel stepLabel = new JLabel("   step ");
        JTextField stepText = new JTextField();
        dim = new Dimension(50, 20);
        ((JSpinner.DefaultEditor) rSpin.getEditor()).setPreferredSize(dim);
        stepText.setPreferredSize(dim); 
        stepText.setColumns(4);
        stepText.setText(rStep[curTabIndex] + "");
        stepText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField jt = (JTextField)e.getSource();
                try {
                        double v = Double.parseDouble(jt.getText());
                        rStepChanged(v);
                }
                catch(NumberFormatException nfe) {
                    String err = "invalud decimal number format. reset.";
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                    jt.setText(rStep[curTabIndex] + ""); 
                }    
            }
        });
        
        panel.add(spinLabel);
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
        
        xModel = new SpinnerNumberModel(xStart, xMin, xMax, xyStep); 
        yModel = new SpinnerNumberModel(yStart, yMin, yMax, xyStep); 
        xSpin = new JSpinner(xModel);
        ySpin = new JSpinner(yModel);
              
        xSpin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    xStart = Double.parseDouble(xSpin.getValue().toString());
                    xChanged(xStart);
                }
                catch(NumberFormatException nfe) {
                    String err = "invalud decimal number format. reset.";
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                    xSpin.setValue(xStart+"");
                } 
            }
        });       
        
        ySpin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    yStart = Double.parseDouble(ySpin.getValue().toString());
                    yChanged(yStart);
                }
                catch(NumberFormatException nfe) {
                    String err = "invalud decimal number format. reset.";
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                    ySpin.setValue(yStart+"");
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
    
    // TODO: if there's a circle in circle queue, change its resolution to current r
    //       else do nothing (user need to click draw button to draw)
    private void rChanged(double r) {
        rValue[curTabIndex] = r;
        switch(curTabIndex) {
            case CIRCLE: 
                if(!circleList.isEmpty()) {
                    circleList.get(circleList.size() - 1).setR(r);
                    drawCircles();
                }
                else {
                    Circle c = new Circle(r, xStart, yStart, penColor);
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
    
    private void rStepChanged(double s) {
        rStep[curTabIndex] = s;
        rModel.setStepSize(s);    
    }
    
    
    // TODO: center changed so redrawn all objects
    private void xChanged(double x) {
        switch(curTabIndex) {
            case CIRCLE: 
                if(!circleList.isEmpty()) {
                    for(Circle c : circleList) {
                        c.setX(x);
                    }
                    drawCircles();
                }
                getCurrentPattern().setCenterX(x);
               
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
        switch(curTabIndex) {
            case CIRCLE: 
                if(!circleList.isEmpty()) {
                    for(Circle c : circleList) {
                        c.setY(y);
                    }
                    drawCircles();
                }
                getCurrentPattern().setCenterY(y);
               
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
        switch(curTabIndex) {
            case CIRCLE: 
                Circle c = new Circle(rValue[curTabIndex], xStart, yStart, penColor);
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
        switch(curTabIndex) {
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
        switch(curTabIndex) {
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
        switch(curTabIndex) {
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
    
    // not used
    private void drawResolution(Resolution r) {
        UIMain.getUIPattern().drawResolution(r);
    }
    
    // draw on pattern UI all resolutions in resolutionList
    private void drawResolutions() {
        UIMain.getUIPattern().drawResolutions(resolutionList);
    }
    
    // not used
    private void drawLayerline(Layerline l) {
        UIMain.getUIPattern().drawLayerline(l);
    }
    
    // draw on pattern UI all layerlines in layerlineList
    private void drawLayerlines() {
        UIMain.getUIPattern().drawLayerlines(layerlineList);
    }
    
    /*
     * @return the current Pattern object
     */
    private Pattern getCurrentPattern() {
        return PatternProcessor.getCurrentPattern();
    }
}


