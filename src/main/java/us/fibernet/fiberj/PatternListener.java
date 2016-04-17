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

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JLabel;


/**
 * A utility class for processing all mouse and key events on a PatternDisplay
 */
public class PatternListener implements MouseListener, MouseMotionListener, KeyListener {

    private enum EventMode {CURSOR, PLOT, IMAGE};
    private EventMode currentEventMode = EventMode.PLOT;

    private PatternDisplay myDisplay;
    private DrawKit myDrawKit;
    private Point pointStart = null;
    private Point pointEnd   = null;

    // no default constructor
    private PatternListener() { }

    /**
     * construct a listener on a PatternDisplay
     */
    public PatternListener(PatternDisplay p) {
        myDisplay = p;
        myDrawKit = p.getDrawKit();
    }

    // 
    // MouseListener methods //////////////////////////////////////////////////
    //

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON1) {
            myDrawKit.drawCursor(e.getX(), e.getY());
        }
        JComponent comp = (JLabel)e.getSource();
        comp.setFocusable(true);
        comp.requestFocusInWindow();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pointStart = e.getPoint();
    }       

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }


    // 
    // MouseMotionListener methods ////////////////////////////////////////////
    //

    @Override
    public void mouseDragged(MouseEvent e) {
        pointEnd = e.getPoint();
        myDisplay.plotDraw(pointStart, pointEnd);
    }

    @Override
    public void mouseMoved(MouseEvent e) { }


    //
    // KeyListener methods ////////////////////////////////////////////////////
    //

    @Override
    public void keyReleased(KeyEvent e) {
        moveCursorByKey(e);
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) { }


    
    
    /*
     * update cursor location by arrow keys
     */
    private void moveCursorByKey(KeyEvent e) {
        int curX = SystemSettings.getCursorX();
        int curY = SystemSettings.getCursorY();

        switch(e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_KP_LEFT:
                if(curX > 0) {
                    myDrawKit.drawCursor(--curX, curY);
                }
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_KP_RIGHT:
                if(curX < myDisplay.getWidth() - 1) {
                    myDrawKit.drawCursor(++curX, curY);
                }
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_KP_UP:
                if(curY > 0) {
                    myDrawKit.drawCursor(curX, --curY);
                }
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_KP_DOWN:
                if(curY < myDisplay.getHeight() - 1) {
                    myDrawKit.drawCursor(curX, ++curY);
                }
                break;
            default:
                break;
        }
    }

}
