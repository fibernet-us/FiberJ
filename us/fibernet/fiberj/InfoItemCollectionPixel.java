/*
 * Copyright Billy Zheng and Wen Bian. All rights reserved.
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
import java.util.Iterator;

import javax.swing.Box;

/**
 * A class to store all info related to a pixel on a pattern. 
 * Used to display the info of the pixel at cursor on UIInfobar.
 */
public final class InfoItemCollectionPixel extends InfoItemCollection implements InfoItemGuiCallBack {

    private int x = 0;         // x-coordinate
    private int y = 0;         // y-coordinate
    private double r = 0.0;    // radius
    private double I = 0.0;    // intensity
    private double d = 0.0;    // d-spacing
    private double D = 0.0;    // 1/d
    private double R = 0.0;    // reciprocal R
    private double Z = 0.0;    // reciprocal Z
    
    public InfoItemCollectionPixel() {
        populateInfoItemList();
    }

    /** implement InfoItemCollection's populateInfoItemList */
    @Override
    protected void populateInfoItemList() {
        infoItemList = new ArrayList<InfoItem>();
        infoItemList.add(new InfoItemTextField("x", "0",      this));
        infoItemList.add(new InfoItemTextField("y", "0",      this));
        infoItemList.add(new InfoItemTextField("r", "0.00",   this));
        infoItemList.add(new InfoItemTextField("I", "0.00",   this));
        infoItemList.add(new InfoItemTextField("d", "0.0000", this));
        infoItemList.add(new InfoItemTextField("D", "0.0000", this));
        infoItemList.add(new InfoItemTextField("R", "0.0000", this));
        infoItemList.add(new InfoItemTextField("Z", "0.0000", this));
    }
    
    /** implement InfoItemGuiCallBack's update(). obtain user input from gui */
    @Override
    public void updateFromGui(String name, String newValue) {      
        int iv = getIntValue(newValue);
        double dv = getDoubleValue(newValue); 
        if(iv < 0 || dv < 0) {
            return;
        }      
        switch(name.charAt(0)) {
            case 'x': x = iv; break;
            case 'y': y = iv; break;
            case 'r': r = dv; break;
            case 'I': I = dv; break;
            case 'd': d = dv; break;
            case 'D': D = dv; break;
            case 'R': R = dv; break;
            case 'Z': Z = dv; break;
            default:  break;
        }
    }
    
    
    /** update the x- and y- coordinate, and other info accordingly */
    public void updateLocation(int x, int y) {
        this.x = x;
        this.y = y;
        updateAll();  // update all info and update GUI
    }
    
    public String toString() {
        return    "x=" + x + ", y=" + y + ", r=" + r + ", I=" + I +
                ", d=" + d + ", D=" + D + ", R=" + R + ", Z=" + Z;
    }

    // compute all data from x and y, and update gui
    private void updateAll() {
        // TODO: compute other values
        
        // display new values
        int i = -1;
        ((InfoItemTextField) infoItemList.get(++i)).setGuiValue(String.format("%d", x));
        ((InfoItemTextField) infoItemList.get(++i)).setGuiValue(String.format("%d", y));
        ((InfoItemTextField) infoItemList.get(++i)).setGuiValue(String.format("%.2f", r));
        ((InfoItemTextField) infoItemList.get(++i)).setGuiValue(String.format("%.2f", I));
        ((InfoItemTextField) infoItemList.get(++i)).setGuiValue(String.format("%.4f", d));
        ((InfoItemTextField) infoItemList.get(++i)).setGuiValue(String.format("%.4f", D));
        ((InfoItemTextField) infoItemList.get(++i)).setGuiValue(String.format("%.4f", R));
        ((InfoItemTextField) infoItemList.get(++i)).setGuiValue(String.format("%.4f", Z));
    }
    
    // return value as int
    private int getIntValue(String value) { 
        try {
            return Integer.parseInt(value);
        }
        catch(NumberFormatException e) {
            return -1;
        } 
    }

    //return value as double 
    private double getDoubleValue(String value) { 
        try {
            return Double.parseDouble(value);
        }
        catch(NumberFormatException e) {
            return -1;
        } 
    }

} // class InfoItemCollectionPixel
