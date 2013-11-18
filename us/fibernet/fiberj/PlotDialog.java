/*
 * Copyright Yi Xiao and Billy Zheng. All rights reserved.
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
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;


/**
 * A utility class for plot pattern data
 */
public class PlotDialog extends JDialog {

    public enum PlotType {LINE, RADIUS, ARC, CIRCLE, SECTOR, RECTANGLE};
    private PlotType currentObjectType = PlotType.LINE;    
    
    private static final int PLOT_FRAME_HEIGHT = 500;
    private static final int PLOT_FRAME_WIDTH = 500;
    private static final int OBJECT_PANEL_HEIGHT = 80;
    private static final int BUTTON_PANEL_HEIGHT = 80;
       
    private JLabel graphLabel;
    private JPanel graphPanel;
    private ButtonGroup objectTypeRadioGroup;
    private BufferedImage currentImage;
    private BufferedImage plotCache;
    private int imageWidth;
    private int imageHeight;
    
    // plot parameters
    private ArrayList<Point2D> dataPoints;
    private int originX = 80;
    private int originY = 40;
    private double maxY;
    private double maxX;
    private double minX;
    private double minY;
    private int numDigitX;
    private int numDigitY;

    // plot options
    private double currentSectorWidth = 15; // disorientation in degrees
    private boolean doSectorCorrection = false;
    private boolean xUnitPixel = true;
    private Color penColor = Color.CYAN;

    /**
     * construct a PlotDialog and register it with PatternProcessor
     */
    public PlotDialog() {
        initialize();
        PatternProcessor.setCurrentPlotter(this);
    }

    // clean up
    public void close() {
        this.dispose();
    }
    
    public Color getPenColor() {
        return penColor;
    }

    public double getSectorAngle() {
        return currentSectorWidth;
    }

    public boolean isXunitPixel() {
        return xUnitPixel;
    }

    public boolean isDoSectorCorrection() {
        return doSectorCorrection;
    }
    
    public void setSectorAngle(double d) {
        currentSectorWidth = d;
    }

    public void setPlotData(ArrayList<Point2D> points) {
        dataPoints = points;
    }
    
    public void getPlotData() {
        dataPoints = PlotData.getPlotData();
    }
    
    public static ArrayList<Point2D> getSampleData() {
        double[] bgX = {0.0, 1.0};
        double[] bgY = {0.0, 0.0};

        ArrayList<Point2D> points = new ArrayList<Point2D>();
        for (int i = 0; i < bgX.length; i++) {
            points.add(new Point2D.Double(bgX[i], bgY[i]));
        }

        return points;
    }
    
    public PlotType getPlotType() {
        for (Enumeration<AbstractButton> buttons = objectTypeRadioGroup.getElements(); 
             buttons.hasMoreElements(); ) {
            
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return PlotType.valueOf(button.getText().toUpperCase());
            }
        }

        return null;
    }
    
    
    //
    // Button Plot action
    //
    protected void doPlot(boolean isClear) {
        if(isClear) {
            this.dataPoints = getSampleData();
        }
        else {
            getPlotData();
        }
        
        processPoints();
        
        int w = graphPanel.getWidth();
        int h = graphPanel.getHeight();
        BufferedImage plotImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        paintBackground(plotImage, 244);
        // generate axis x and y
        imageWidth = w - 160;
        imageHeight = h - 120;
        Graphics2D g2 = plotImage.createGraphics();
        Rectangle2D axis = new Rectangle2D.Double(originX, originY, imageWidth,
                imageHeight);
        g2.setColor(Color.BLACK);
        g2.draw(axis);
        // draw markers
        int numXMarker = 6;
        int numYMarker = 6;
        Map<Double, Point2D> xMarkers = new TreeMap<Double, Point2D>();
        Map<Double, Point2D> yMarkers = new TreeMap<Double, Point2D>();
        for (int i = 0; i < 6; i++) {
            xMarkers.put(minX + (maxX - minX) / 5 * i,
                    new Point.Double(originX + i * imageWidth / (numXMarker - 1), originY + imageHeight));
            yMarkers.put(minY + (maxY - minY) / 5 * (5 - i),
                    new Point.Double(originX, originY + i * imageHeight / (numYMarker - 1)));
        }
        drawMarker(g2, xMarkers, yMarkers);
        drawCurve(g2, Color.BLACK, true, originX, originY, imageWidth,
                imageHeight, dataPoints);// data

        graphLabel.setIcon(new ImageIcon(plotImage));
        currentImage = plotImage;
        plotCache = PatternUtil.copyBufferedImage(plotImage);
    }
    
    protected void doType() {
        // TODO Auto-generated method stub
        
    }

    protected void doLog() {
        // TODO Auto-generated method stub
        
    }

    protected void doSave() {
        JFileChooser fc = new JFileChooser();
        int returnValue = fc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            saveCurrentImage(fc.getSelectedFile());
        }
    }
    
    protected void doClear() {
        doPlot(true);
    } 
    
    protected void doOption() {
        optionPopup();        
    }

    protected void doHelp() {
        // TODO Auto-generated method stub
    }
    
    
    private void drawMarker(Graphics2D g2, Map<Double, Point2D> xMarkers, Map<Double, Point2D> yMarkers) {
        int halfMarkerLength = 3;
        // x-axis
        for (Double xNumber : xMarkers.keySet()) {
            Point2D center = xMarkers.get(xNumber);
            g2.draw(new Line2D.Double(center.getX(), center.getY() - halfMarkerLength, 
                                      center.getX(), center.getY() + halfMarkerLength));
            g2.drawString(markerNumberToString(xNumber, true),
                    (int) (center.getX() - 5 * Math.abs(numDigitX)),
                    (int) (center.getY() + 25));
        }
        // y-axis
        for (Double yNumber : yMarkers.keySet()) {
            Point2D center = yMarkers.get(yNumber);
            g2.draw(new Line2D.Double(center.getX() - halfMarkerLength, center.getY(), 
                                      center.getX() + halfMarkerLength, center.getY()));
            g2.drawString(markerNumberToString(yNumber, false),
                    (int) (center.getX() - 30 - 5 * numDigitY), //15 * numDigitY),
                    (int) (center.getY() + 5));
        }
        
    }

    private String markerNumberToString(Double d, boolean isX) {
        if (isX) {
            if (numDigitX < 0) {
                return String.format("%.3f", d);
            } else {
                return String.format("%.1f", d);
            }
        } else {
            if (numDigitY < 0) {
                return String.format("%.1f", d);
            } else {
                return String.format("%.0f", d);
            }
        }
    }

    private void drawCurve(Graphics2D g2, Color color, boolean highlight,
            int oriX, int oriY, int w, int h, ArrayList<Point2D> pts) {

        Point2D previous = null;
        g2.setColor(color);
        for (Point2D p : pts) {
            double x = (p.getX() - minX) / (maxX - minX) * w + oriX;
            double y = (1 - (p.getY() - minY) / (maxY - minY)) * h + oriY;
            Point2D current = new Point2D.Double(x, y);
            if (highlight) {
                for (int i = -1; i <= 1; i++) {
                    g2.draw(new Line2D.Double(current.getX() - 1, current
                            .getY() + i, current.getX() + 1, current.getY() + i));
                }
            }
            if (previous != null) {
                g2.draw(new Line2D.Double(previous, current));
            }
            previous = current;
        }
        g2.dispose();
    }

    private void paintBackground(BufferedImage image, int color) {
        WritableRaster raster = image.getRaster();
        int w = image.getWidth();
        int h = image.getHeight();
        int[] r = null;
        int[] g = null;
        int[] b = null;
        // paint background
        r = new int[w * h];
        g = new int[w * h];
        b = new int[w * h];
        Arrays.fill(r, color);
        Arrays.fill(g, color);
        Arrays.fill(b, color);
        raster.setSamples(0, 0, w, h, 0, r);
        raster.setSamples(0, 0, w, h, 1, g);
        raster.setSamples(0, 0, w, h, 2, b);
    }

    private void processPoints() {
        double localMaxX = Integer.MIN_VALUE;
        double localMaxY = Integer.MIN_VALUE;
        double localMinX = Integer.MAX_VALUE;
        double localMinY = Integer.MAX_VALUE;
        for (Point2D p : dataPoints) {
            if (p.getX() > localMaxX) {
                localMaxX = p.getX();
            }
            if (p.getX() < localMinX) {
                localMinX = p.getX();
            }
            if (p.getY() > localMaxY) {
                localMaxY = p.getY();
            }
            if (p.getY() < localMinY) {
                localMinY = p.getY();
            }
        }
        int factorX = -8;
        int factorY = -8;
        if (localMaxX >= 1) {
            factorX = -1;
            while (localMaxX / Math.pow(10, ++factorX) >= 1) {
                ;
            }
        } else {
            factorX = 1;
            while (localMaxX / Math.pow(10, --factorX) < 1) {
                ;
            }
        }
        if (localMaxY >= 1) {
            factorY = -1;
            while (localMaxY / Math.pow(10, ++factorY) >= 1) {
                ;
            }
        } else {
            factorY = 1;
            while (localMaxY / Math.pow(10, --factorY) < 1) {
                ;
            }
        }

        localMaxX = ((int) (localMaxX / Math.pow(10, factorX - 2) + 1)) * Math.pow(10, factorX - 2);
        localMaxY = ((int) (localMaxY / Math.pow(10, factorY - 2) + 1)) * Math.pow(10, factorY - 2);

        int secLargestDigit = (int) (localMaxX / Math.pow(10, factorX - 2)) % 10;
        maxX = ((secLargestDigit > 5) ? (int) (localMaxX / Math.pow(10, factorX - 1) + 1) * Math.pow(10, factorX - 1)
                                      : ((int) (localMaxX / Math.pow(10, factorX - 1)) + 0.5) * Math.pow(10, factorX - 1));

        secLargestDigit = (int) (localMaxY / Math.pow(10, factorY - 2)) % 10;
        maxY = ((secLargestDigit > 5) ? (int) (localMaxY / Math.pow(10, factorY - 1) + 1) * Math.pow(10, factorY - 1)
                                      : ((int) (localMaxY / Math.pow(10, factorY - 1)) + 0.5) * Math.pow(10, factorY - 1));

        secLargestDigit = (int) (localMinX / Math.pow(10, factorX - 2)) % 10;
        minX = (secLargestDigit > 5) ? ((int) (localMinX / Math.pow(10, factorX - 1)) + 0.5) * Math.pow(10, factorX - 1)
                                     : (int) (localMinX / Math.pow(10, factorX - 1)) * Math.pow(10, factorX - 1);

        secLargestDigit = (int) (localMinY / Math.pow(10, factorY - 2)) % 10;
        minY = (secLargestDigit > 5) ? ((int) (localMinY / Math.pow(10, factorY - 1)) + 0.5) * Math.pow(10, factorY - 1)
                                     : (int) (localMinY / Math.pow(10, factorY - 1)) * Math.pow(10, factorY - 1);

        numDigitX = factorX - 1;
        numDigitY = factorY - 1;
    }


    private void saveCurrentImage(File file) {
        String type = file.getName();
        if (type.lastIndexOf(".") < 0) {
            type = "png";
        } 
        else {
            type = type.substring(type.lastIndexOf(".") + 1);
        }
        
        try {
            ImageIO.write(currentImage, "png", file);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    //-------------------------------------------------------------------------
    //
    // GUI building code
    //
    //-------------------------------------------------------------------------
    
    /*
     * create the contents of the plot plotFrame.
     */
    private void initialize() {
        setSize(PLOT_FRAME_WIDTH, PLOT_FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //setResizable(false);
        setTitle("Data Plot");
        setLocationRelativeTo(null);
        
        //
        // object type panel
        //
        JPanel objectPanel = new JPanel();
        objectPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        objectPanel.setSize(PLOT_FRAME_WIDTH, OBJECT_PANEL_HEIGHT);

        JLabel lblNewLabel = new JLabel("Type: ");
        objectPanel.add(lblNewLabel);

        JRadioButton radioLine = new JRadioButton("line");
        JRadioButton radioRadius = new JRadioButton("radius");
        JRadioButton radioArc = new JRadioButton("arc");
        JRadioButton radioCircle = new JRadioButton("circle");
        JRadioButton radioSector = new JRadioButton("sector");
        JRadioButton radioRectangle = new JRadioButton("rectangle");
 
        radioLine.setSelected(true);
        objectPanel.add(radioLine);
        objectPanel.add(radioRadius);
        objectPanel.add(radioArc);       
        objectPanel.add(radioCircle);     
        objectPanel.add(radioSector);
        objectPanel.add(radioRectangle);   
     
        objectTypeRadioGroup = new ButtonGroup();
        objectTypeRadioGroup.add(radioLine);
        objectTypeRadioGroup.add(radioRadius);
        objectTypeRadioGroup.add(radioArc);
        objectTypeRadioGroup.add(radioCircle);
        objectTypeRadioGroup.add(radioSector);
        objectTypeRadioGroup.add(radioRectangle);
        

        //
        // plot panel
        //
        graphPanel = new JPanel();
        graphPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        graphPanel.setSize(PLOT_FRAME_WIDTH, PLOT_FRAME_HEIGHT - OBJECT_PANEL_HEIGHT - BUTTON_PANEL_HEIGHT);
        
        graphLabel = new JLabel("");
        graphLabel.setSize(graphPanel.getWidth(), graphPanel.getHeight());
        graphPanel.add(graphLabel);
        
        
        //
        // button panel
        //
        JPanel buttonPanel = new JPanel();
        buttonPanel.setSize(PLOT_FRAME_WIDTH, BUTTON_PANEL_HEIGHT);
        buttonPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

        JButton btnPlot = new JButton("Plot");
        btnPlot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doPlot(false);
            }
        });

        /*
        JButton btnType = new JButton("Type");
        btnType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doType();
            }
        });
        */
        
        JButton btnLog = new JButton("Log");
        btnLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doLog();
            }
        });

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doSave();
            }
        });

        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doClear();
            }
        });

        JButton btnOption = new JButton("Option");
        btnOption.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doOption();
            }
        });
        
        JButton btnHelp = new JButton("Help");
        btnHelp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doHelp();
            }
        });

        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(btnPlot);
        //buttonPanel.add(btnType);
        buttonPanel.add(btnLog);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnOption);
        buttonPanel.add(btnHelp);

        add(objectPanel, BorderLayout.PAGE_START);
        add(graphPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_END);
        
        setVisible(true);
        doClear();
    }

    
    // create popup option window
    private void optionPopup() {

        JDialog popup = new JDialog();
        popup.setTitle("Plot Option");
        popup.setLocationRelativeTo(null);
                                       
        // pen color panel      
        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BorderLayout());
        
        JLabel colorLabel = new JLabel("<html><br>pen color.  click to change.<br><br></html>");
        colorLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        colorLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color c = JColorChooser.showDialog(null, "", null);
                if(c != null) {
                    penColor = c;
                    ((JLabel)e.getSource()).setBackground(penColor);
                }
            }
        });
        colorLabel.setForeground(Color.WHITE);
        colorLabel.setOpaque(true);
        colorLabel.setBackground(penColor);
        colorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        colorPanel.add(colorLabel, BorderLayout.CENTER);
        
        // Sector width panel
        JPanel  widthPanel = new JPanel();
        widthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel widthLabel = new JLabel("Sector width (" + "\u00b0" + "): ");
        
        JTextField widthText = new JTextField();
        widthText.setPreferredSize(new Dimension(50, 20));
        widthText.setColumns(4);
        widthText.setText(currentSectorWidth + "");
        widthText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JTextField jt = (JTextField)e.getSource();
                try {
                    currentSectorWidth = Double.parseDouble(jt.getText());
                }
                catch(NumberFormatException nfe) {
                    String err = "invalud decimal number format. reset.";
                    JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
                    jt.setText(currentSectorWidth + "");
                }
            }
        });        

        widthPanel.add(widthLabel);
        widthPanel.add(widthText);
        
        // Sector disorientation correction
        JPanel  corrPanel = new JPanel();
        corrPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel corrLabel = new JLabel("Sector disorientation correction: ");
        
        JRadioButton radioYes = new JRadioButton("yes");
        radioYes.setSelected(doSectorCorrection);
        radioYes.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                doSectorCorrection = true;
            }
        });
        
        JRadioButton radioNo = new JRadioButton("no");
        radioNo.setSelected(!doSectorCorrection);
        radioNo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                doSectorCorrection = false;
            }
        });
        
        ButtonGroup corrRadioGroup = new ButtonGroup();
        corrRadioGroup.add(radioYes);
        corrRadioGroup.add(radioNo);
        
        corrPanel.add(corrLabel);
        corrPanel.add(radioYes);
        corrPanel.add(radioNo);

        // x type panel
        JPanel  xTypePanel = new JPanel();
        xTypePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel xTypeLabel = new JLabel("X type: ");
        
        JRadioButton radioPix = new JRadioButton("pixel");
        radioPix.setSelected(xUnitPixel);
        radioPix.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                xUnitPixel = true;
            }
        });
        
        JRadioButton radioAng = new JRadioButton("1/" + "\u212B");
        radioAng.setSelected(!xUnitPixel);
        radioAng.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                xUnitPixel = false;
            }
        });
        
        ButtonGroup xTypeRadioGroup = new ButtonGroup();
        xTypeRadioGroup.add(radioPix);
        xTypeRadioGroup.add(radioAng);
        
        xTypePanel.add(xTypeLabel);
        xTypePanel.add(radioPix);
        xTypePanel.add(radioAng);
        
        // add borders, top, left, bottom, right
        colorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        widthPanel.setBorder(BorderFactory.createEmptyBorder( 0, 10,  0, 10));
        corrPanel.setBorder (BorderFactory.createEmptyBorder( 0, 10,  0, 10));
        xTypePanel.setBorder(BorderFactory.createEmptyBorder( 0, 10, 15, 10));
        
        // lay out panels on a vbox
        Box vBox = new Box(BoxLayout.Y_AXIS);
        vBox.add(colorPanel);
        vBox.add(widthPanel);
        vBox.add(corrPanel);
        vBox.add(xTypePanel);
        
        popup.add(vBox);
        popup.pack();
        popup.setVisible(true);
    }


}
