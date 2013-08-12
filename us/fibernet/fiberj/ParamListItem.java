package us.fibernet.fiberj;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @ClassName:ParamListItem
 * @Description:A class hold the Parameter item name, value, and label, text
 *                field, checkbox to display the item, and callBack to get and
 *                pass user's input
 * @author kate.b.wu@gmail.com
 * 
 */
public class ParamListItem extends InfoItemTextField {

	boolean refinable;
	private JCheckBox ckbox;
	private JLabel label;
	private JTextField textField;
	private String name;
	private String value;
	private int nColumns;
	private InfoItemGuiCallBack callBack;

	/**
	 * @param labelStr
	 *            the name of this item
	 * 
	 * @param textStr
	 *            the value of this item, by default it's double
	 * @param columns
	 *            width of textfield
	 * @param isRefinable
	 *            if yes, then the item has a checkbox
	 * @param callBackObj
	 *            a InfoItemGuiCallBack object for passing on user input to
	 */
	public ParamListItem(String labelStr, String textStr, int columns,
			boolean isRefinable, InfoItemGuiCallBack callBackObj) {
		super(labelStr, textStr, "%.4f", columns, true, callBackObj);
		label = new JLabel(labelStr);
		label.setFont(new Font("Arial", Font.BOLD, 12)); // the default font
		textField = new JTextField(textStr);
		name = labelStr;
		value = textStr;
		nColumns = columns;
		refinable = isRefinable;
		textField.setColumns(nColumns);
		callBack = callBackObj;
		if (refinable) {
			ckbox = new JCheckBox();
		}
		addCallback();

	}

	/** override the method in superclass */
	@Override
	public void addTo(JPanel parent) {
		parent.add(label);
		parent.add(textField);
		if (ckbox != null) {
			parent.add(ckbox);
		}
	}

	/** add action listener to text field and hook up callback */
	private void addCallback() {
		textField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextField j = (JTextField) e.getSource();
				String err = setGuiValue(j.getText());
				if (err != null) {
					JOptionPane.showMessageDialog(null, err, "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					callBack.guiUpdated(name, value);
				}
			}
		});
	}

	/** set the label component */
	public void setLabel(JLabel label) {
		this.label = label;
		name = label.getText();
	}

	/** get the label component */
	public JLabel getLabel() {
		return label;
	}

	/** get the text field component */
	public JTextField getTextField() {
		return textField;
	}

	/** get the checkbox component */
	public JCheckBox getCheckBox() {
		return ckbox;
	}
}
