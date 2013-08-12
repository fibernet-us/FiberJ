/**
 * 
 */
package us.fibernet.fiberj;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import us.fibernet.fiberj.ParamListCollectionItem.InfoItemListRecip;

/**
 * @ClassName:UIParamList
 * @Description:the UI class to display the parameter items
 * @author kate.b.wu@gmail.com
 * 
 */
@SuppressWarnings("serial")
public class UIParamList extends JPanel {

	private JFrame parentFrame;

	/**
	 * create a GroupLayout JPanel with given dimension and parent JFrame
	 * 
	 */
	public UIParamList(JFrame parent, int width, int height,
			InfoItem... paramListItems) {
		parentFrame = parent;
		Dimension dim = new Dimension(width, height);
		setPreferredSize(dim);
		setMinimumSize(dim);

		createGroups(this, paramListItems);
	}

	/**
	 * create horizontal and vertical layouts for the GroupsLayout
	 */
	private void createGroups(JPanel parent, InfoItem... infoItems) {

		GroupLayout layout = new GroupLayout(parent);
		parent.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		// create horizontal sequential group
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		// create parallel group to hold the JLabels
		GroupLayout.ParallelGroup h1 = layout
				.createParallelGroup(GroupLayout.Alignment.BASELINE);

		// create parallel group to hold the JTextFields
		GroupLayout.ParallelGroup h2 = layout
				.createParallelGroup(GroupLayout.Alignment.LEADING);

		// create parallel group to hold the JCheckboxes
		GroupLayout.ParallelGroup h3 = layout
				.createParallelGroup(GroupLayout.Alignment.CENTER);

		// create vertical sequential group
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		for (InfoItem i : infoItems) {
			if (i instanceof InfoItemListRecip) {
				InfoItemListRecip item = (InfoItemListRecip) i;
				h1.addComponent(item.getJLabel());

				// create group to hold two radio buttons
				GroupLayout.SequentialGroup h2H = layout
						.createSequentialGroup();

				h2H.addComponent(item.getJrbYes())
						.addComponent(item.getJrbNo());

				h2.addGroup(h2H);

				// the first vertical parallel group to hold reciprocal
				// components
				vGroup.addGroup((layout
						.createParallelGroup(GroupLayout.Alignment.BASELINE))
						.addComponent(item.getJLabel())
						.addComponent(item.getJrbYes())
						.addComponent(item.getJrbNo()));

			} else if (i instanceof ParamListItem) {
				ParamListItem item = (ParamListItem) i;
				h1.addComponent(item.getLabel());
				h2.addComponent(item.getTextField());

				// create vertical parallel group to hold each item components
				GroupLayout.ParallelGroup vp = layout
						.createParallelGroup(GroupLayout.Alignment.BASELINE);
				vp.addComponent(item.getLabel()).addComponent(
						item.getTextField());

				// if item has checkbox, add the component to horizontal and
				// vertical parallel group respectively
				if (item.getCheckBox() != null) {
					h3.addComponent(item.getCheckBox());
					vp.addComponent(item.getCheckBox());
				}

				vGroup.addGroup(vp);
			}// end of else if

			hGroup.addGroup(h1);
			hGroup.addGroup(h2);
			hGroup.addGroup(h3);

			layout.setHorizontalGroup(hGroup);
			layout.setVerticalGroup(vGroup);

		}// end of for loop

	}

}
