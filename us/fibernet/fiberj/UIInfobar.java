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

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A JPanel for displaying pattern related information.
 * Currently items include 
 */
@SuppressWarnings("serial")
public class UIInfobar extends JPanel {

    private JFrame parentFrame;
      
    /** 
     * create a FlowLayout JPanel with given dimension on a parent frame 
     */
    public UIInfobar(JFrame parent, int width, int height, InfoItem... infoItems) {
        parentFrame = parent;
        Dimension dim = new Dimension(width, height);
        setPreferredSize(dim); 
        setMinimumSize(dim);  
        setLayout(new FlowLayout(FlowLayout.LEFT)); 
        for(InfoItem i : infoItems) {
            addInfoItem(i);
        }
    }
 
    /**
     * add an InfoItem (implementing addTo(JPanel)) to UIInfobar
     */
    public void addInfoItem(InfoItem infoItem) {
        infoItem.addTo(this);
        this.add(Box.createHorizontalStrut(1)); // add spacing between InfoItems
    }
        
} // class UIInfobar
