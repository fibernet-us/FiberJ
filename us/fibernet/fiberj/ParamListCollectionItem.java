package us.fibernet.fiberj;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * @ClassName:ParamListCollectionItem
 * @Description:a class to store all parameter list items
 * @author kate.b.wu@gmail.com
 * 
 */
public final class ParamListCollectionItem extends InfoItemCollection implements
		InfoItemGuiCallBack {
	private double pixel = 0.0; // pixel size of pattern image (micrometer)
								// or step size in reciprocal space (1/angstrom)
	private double x0 = 0.0; // x-coordinate, see xCenter in Pattern class
	private double y0 = 0.0; // y-coordinate, see yCenter in Pattern class
	private double tilt = 0.0; // see tile in Pattern class
	private double twist = 0.0; // see twist in Pattern class
	private double repeat = 0.0; // see repeat in Pattern class
	private double wavelength = 0.0; // see wavelen in Pattern class
	private double oY = 0.0; // see betaD in Pattern class
	private double oZ = 0.0; // see gammaD in Pattern class
	private double offset = 0.0; // see offset in Pattern class
	private double calibrant = 0.0; // see dCalibrant in Pattern class
	private boolean isRecip = false; // affect the 'reciprocal' radio button
										// selection, see isRecip in Pattern
										// class

	public ParamListCollectionItem() {

		populateInfoItemList();

	}

	/*
	 * implement InfoItemCollection#populateInfoItemList()
	 */
	@Override
	protected void populateInfoItemList() {
		ParamListItem pix = new ParamListItem("Pixel size", "0", 4, false, this);
		JLabel pixLabel = pix.getLabel();
		InfoItem reciprocal = new InfoItemListRecip("Reciprocal", isRecip,
				pixLabel, this);
		infoItemList = new ArrayList<InfoItem>();
		infoItemList.add(reciprocal);
		infoItemList.add(pix);
		infoItemList.add(new ParamListItem("x0", "0", 4, true, this));
		infoItemList.add(new ParamListItem("y0", "0", 4, true, this));
		infoItemList.add(new ParamListItem("tilt", "0", 4, true, this));
		infoItemList.add(new ParamListItem("twist", "0", 4, true, this));
		infoItemList.add(new ParamListItem("repeat", "0", 4, true, this));
		infoItemList.add(new ParamListItem("wavelength", "0", 4, true, this));
		infoItemList.add(new ParamListItem("oY", "0", 4, true, this));
		infoItemList.add(new ParamListItem("oZ", "0", 4, false, this));
		infoItemList.add(new ParamListItem("offset", "0", 4, false, this));
		infoItemList.add(new ParamListItem("calibrant ring", "0", 4, false,
				this));
	}

	/**
	 * @return the current Pattern object
	 */
	protected Pattern getP() {
		return PatternProcessor.getInstance().getCurrentPattern();
	}

	/*
	 * implement InfoItemGuiCallBack interface, get user input from gui
	 */
	@Override
	public void guiUpdated(String name, String newValue) {

		Pattern currentPattern = getP();
		if (currentPattern == null) {
			return;
		}

		if (name.equalsIgnoreCase("reciprocal")) {
			isRecip = getBooleanValue(newValue);
			currentPattern.setRecip(isRecip);
			return;
		}

		double v = getDoubleValue(newValue);
		if (v < 0) {
			return;
		}
		switch (name.toLowerCase().substring(0, 2)) {
		case "pi":
			pixel = v;
			currentPattern.setPixelSize(v);
			break;
		case "x0":
			x0 = v;
			currentPattern.setxCenter(v);
			break;
		case "y0":
			y0 = v;
			currentPattern.setyCenter(v);
			break;
		case "ti":
			tilt = v;
			currentPattern.setTilt(v);
			break;
		case "tw":
			twist = v;
			currentPattern.setTwist(v);
			break;
		case "wa":
			wavelength = v;
			currentPattern.setWavelen(v);
			break;
		case "oY":
			oY = v;
			currentPattern.setBetaD(v);
			break;
		case "oZ":
			oZ = v;
			currentPattern.setGammaD(v);
			break;
		case "of":
			offset = v;
			currentPattern.setOffset(v);
			break;
		case "ca":
			calibrant = v;
			currentPattern.setdCalibrant(v);
			break;
		case "re":
			repeat = v;
			currentPattern.setRepeat(v);
			break;
		default:
			break;
		}

	}

	/**
	 * update parameter list items value and gui by calling getter of Pattern
	 * class
	 */
	public void updateUI() {
		Pattern currentPattern = getP();
		if (currentPattern == null) {
			return;
		}
		isRecip = currentPattern.isRecip();
		pixel = currentPattern.getPixelSize();
		x0 = currentPattern.getxCenter();
		y0 = currentPattern.getyCenter();
		tilt = currentPattern.getTilt();
		twist = currentPattern.getTwist();
		repeat = currentPattern.getRepeat();
		wavelength = currentPattern.getWavelen();
		oY = currentPattern.getBetaD();
		oZ = currentPattern.getGammaD();
		offset = currentPattern.getOffset();
		calibrant = currentPattern.getdCalibrant();

		// display updated value
		int i = -1;
		((InfoItemListRecip) infoItemList.get(++i)).setGuiValue(isRecip);
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String
				.format("%.4f", pixel));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String
				.format("%.4f", x0));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String
				.format("%.4f", y0));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String
				.format("%.4f", tilt));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String
				.format("%.4f", twist));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String
				.format("%.4f", repeat));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String
				.format("%.4f", wavelength));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String
				.format("%.4f", oY));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String
				.format("%.4f", oZ));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String
				.format("%.4f", offset));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String
				.format("%.4f", calibrant));
	}

	/** parse string to boolean */
	private boolean getBooleanValue(String value) {

		return Boolean.parseBoolean(value);
	}

	/** parse string to double */
	private double getDoubleValue(String value) {

		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * a inner class to hold the name, value of reciprocal and the label, radio
	 * buttons to display it
	 * 
	 */
	class InfoItemListRecip implements InfoItem {
		private JLabel label;
		private String name;
		private boolean value;
		private JRadioButton jrbYes;
		private JRadioButton jrbNo;
		private ButtonGroup jrbGroup = new ButtonGroup();
		private JLabel actionLabel; // the label that changes when click on the
									// radio buttons
		private InfoItemGuiCallBack callback;
		private static final String MICRO_LABLE = "PixelSize(um)";
		private static final String ANGST_LABLE = "Pxicel Size (1/A)";

		InfoItemListRecip(String labelStr, boolean isRecip, JLabel actionLabel,
				InfoItemGuiCallBack callbackObj) {
			label = new JLabel(labelStr);
			name = labelStr;
			value = isRecip;
			this.actionLabel = actionLabel;
			jrbYes = new JRadioButton("Yes", isRecip);
			jrbNo = new JRadioButton("No", !isRecip);
			callback = callbackObj;
			addCallback();
			createGroup();
		}

		/**
		 * add two radio buttons to a group so only one button is selected at a
		 * time
		 */
		private void createGroup() {
			jrbGroup.add(jrbYes);
			jrbGroup.add(jrbNo);
		}

		/**
		 * add action listener to two radio buttons, change the boolean value of
		 * reciprocal
		 */
		private void addCallback() {
			jrbYes.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					actionLabel.setText(ANGST_LABLE);
					value = true;
					callback.guiUpdated(name, "true");

				}
			});
			jrbNo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					actionLabel.setText(MICRO_LABLE);
					value = false;
					callback.guiUpdated(name, "false");
				}
			});
		}

		public boolean getBooleanValue() {
			return value;
		}

		/** update gui according to the boolean value of Reciprocal */
		public void setGuiValue(boolean value) {
			this.value = value;
			jrbYes.setSelected(value);
			jrbNo.setSelected(!value);
		}

		@Override
		public void addTo(JPanel parent) {
			parent.add(label);
			parent.add(jrbYes);
			parent.add(jrbNo);
		}

		/** return the label component */
		public JLabel getJLabel() {
			return label;
		}

		/** return the Yes radio button component */
		public JRadioButton getJrbYes() {

			return jrbYes;
		}

		/** return the No radio button component */
		public JRadioButton getJrbNo() {

			return jrbNo;
		}

	}

}