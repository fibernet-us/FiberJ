/**
 * A JTextField that automatically resizes its text
 * 
 * Modified from example code at http://www.nextpoint.se/?p=304
 * 
 */

package us.fibernet.fiberj;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class AutoResizeJTextField extends JTextField implements ComponentListener {

    float iw, ih, sw, sh, weight;
    boolean first;

    public AutoResizeJTextField(String text) {
        super(text);
        init();
    }
    
    public AutoResizeJTextField() {
        init();
    }

    public void componentResized(ComponentEvent e) {
        if (first) {
            iw = getWidth();
            ih = getHeight();
            first = false;
        }

        sw = (getWidth() / iw) * weight;
        if ((int)sw == 0) {
            sw = 1f;
        }
        sh = (getHeight() / ih) * weight;
        if ((int)sh == 0) {
            sh = 1f;
        }
        
        AffineTransform trans = AffineTransform.getScaleInstance(sw, sh);
        setFont(getFont().deriveFont(trans));
    }

    public void componentShown(ComponentEvent e) { }

    public void componentHidden(ComponentEvent e) { }

    public void componentMoved(ComponentEvent e) { }
    
    private void init() {
        first = true;
        weight = 0.8f;
        setHorizontalAlignment(JTextField.CENTER);
        addComponentListener(this);  
    }
}
