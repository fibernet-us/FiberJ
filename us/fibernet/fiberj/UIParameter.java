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

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * A UI class for parameter and reflection operations
 * 
 */
public final class UIParameter {

	private static final String title = "Reflection-Parameter";
	private static JFrame paramFrame;
	private static JPanel paramPanel;
	private static JPanel reflxPanel;
	private static ParamListCollectionItem paramListCollectionItems;

	/**
	 * No instantiation
	 */
	private UIParameter() {
	};

	/**
	 * create Parameter UI and make sure it happens only once.
	 */
	public static synchronized void init() {

		if (paramFrame != null) {
	        paramFrame.pack();
	        paramFrame.setVisible(true);
			return;
		}

		paramFrame = new JFrame(title);
		paramFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		paramFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                paramFrame.setVisible(false);
            }
        });

		paramListCollectionItems = new ParamListCollectionItem();

		MenuBuilder.build(paramFrame, new MenuParam().getMenuItems());

		reflxPanel = new JPanel();
		paramPanel = new JPanel();
		createGroups(paramPanel, paramListCollectionItems);

		Border lineBorder = BorderFactory.createLineBorder(Color.gray);
		reflxPanel.setBorder(lineBorder);
        paramPanel.setBorder(lineBorder);
        
		Box hBox = Box.createHorizontalBox();
		hBox.add(reflxPanel);
		hBox.add(paramPanel);
		paramFrame.add(hBox);

		paramFrame.pack();
		paramFrame.setVisible(true);

	}

	
    /*
     * build paramPanel with GroupsLayout
     */
    private static void createGroups(JPanel panel, ParamListCollectionItem infoItems) {

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // create horizontal sequential group
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
     
        // create parallel group to hold the JCheckboxes
        GroupLayout.ParallelGroup h1 = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
        GroupLayout.ParallelGroup h2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.ParallelGroup h3 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

        for(InfoItem i : infoItems) {
            
            if(i instanceof ParamListItem) {
                ParamListItem item = (ParamListItem) i;
                h1 = h1.addComponent(item.getLabel());
                h2 = h2.addComponent(item.getTextField());

                // create vertical parallel group to hold each item components
                GroupLayout.ParallelGroup vp = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
                vp.addComponent(item.getLabel()).addComponent(
                        item.getTextField());

                // if item has checkbox, add the component to horizontal and
                // vertical parallel group respectively
                if (item.getCheckBox() != null) {
                    h3 = h3.addComponent(item.getCheckBox());
                    vp.addComponent(item.getCheckBox());
                }

                vGroup.addGroup(vp);
            }
            else {
                ParamListItemRecip item = (ParamListItemRecip) i;
                h1 = h1.addComponent(item.getJLabel());

                // create group to hold two radio buttons
                GroupLayout.SequentialGroup h2H = layout.createSequentialGroup();

                h2H.addComponent(item.getJrbYes()).addComponent(item.getJrbNo());
                h2 = h2.addGroup(h2H);

                vGroup.addGap(15);
                // the first vertical parallel group to hold reciprocal components
                vGroup.addGroup((layout.createParallelGroup(GroupLayout.Alignment.BASELINE))
                        .addComponent(item.getJLabel())
                        .addComponent(item.getJrbYes())
                        .addComponent(item.getJrbNo()));
                
                vGroup.addGap(10);
            } 

        } // end of for loop

        hGroup.addGroup(h1);
        hGroup.addGroup(h2);
        hGroup.addGroup(h3);
        vGroup.addGap(20);
        
        layout.setHorizontalGroup(hGroup);
        layout.setVerticalGroup(vGroup);
    }
    
    
} // class UIParameter

