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

import java.util.ArrayList;


/**
 * @ClassName:ParamListCollectionItem
 * @Description:a class to store all parameter list items
 * @author kate.b.wu@gmail.com
 * 
 */
public final class ParamListCollectionItem extends InfoItemCollection implements InfoItemGuiCallBack {		

	public ParamListCollectionItem() {
		populateInfoItemList();
	}

	/*
	 * implement InfoItemCollection#populateInfoItemList()
	 */
	@Override
	protected void populateInfoItemList() {
		ParamListItem pixsiz = new ParamListItem("pixel size (μm)", "0", 4, false, this);
		InfoItem reciprocal = new ParamListItemRecip("reciprocal", false, pixsiz.getLabel(), this);
		
		infoItemList = new ArrayList<InfoItem>();
		infoItemList.add(reciprocal);
		infoItemList.add(pixsiz);
		infoItemList.add(new ParamListItem("center x (pix)", "0", 10, true, this));
		infoItemList.add(new ParamListItem("center y (pix)", "0", 10, true, this));
		infoItemList.add(new ParamListItem("tilt (°)",       "0", 10, true, this));
		infoItemList.add(new ParamListItem("twist (°)",      "0", 10, true, this));
		infoItemList.add(new ParamListItem("repeat (Å)",     "0", 10, true, this));
	    infoItemList.add(new ParamListItem("distance (mm)",  "0", 10, true, this));
		infoItemList.add(new ParamListItem("wavelength (Å)", "0", 10, false, this));
		infoItemList.add(new ParamListItem("oY (°)",         "0", 10, true, this));
		infoItemList.add(new ParamListItem("oZ (°)",         "0", 10, true, this));
		infoItemList.add(new ParamListItem("offset",         "0", 10, false, this));
		infoItemList.add(new ParamListItem("calibrant (Å)",  "0", 10, false, this));
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
			currentPattern.setRecip(getBooleanValue(newValue));
			return;
		}

		double v = getDoubleValue(newValue);
		if (v < 0) {
			return;
		}
		
		String s = name.toLowerCase();

		if(s.startsWith("pixel")) {
			currentPattern.setPixelSize(v);
		}
		else if(s.startsWith("center x")) {
			currentPattern.setxCenter(v);
		}
		else if(s.startsWith("center y")) {
			currentPattern.setyCenter(v);
		}
		else if(s.startsWith("tilt")) {
			currentPattern.setTilt(v);
		}
		else if(s.startsWith("twist")) {
			currentPattern.setTwist(v);
		}
	    else if(s.startsWith("distance")) {
	        currentPattern.setSdd(v);
	    }
		else if(s.startsWith("wavelenth")) {
			currentPattern.setWavelen(v);
		}
		else if(s.startsWith("oY")) {
			currentPattern.setBetaD(v);
		}
		else if(s.startsWith("oZ")) {
			currentPattern.setGammaD(v);
		}
		else if(s.startsWith("offset")) {
			currentPattern.setOffset(v);
		}
		else if(s.startsWith("calibrant")) {
			currentPattern.setdCalibrant(v);
		}
		else if(s.startsWith("repeat")) {
			currentPattern.setRepeat(v);
		}

	}

	/**
	 * update parameter gui with values from current Pattern
	 */
	public void updateUI() {
	    
		Pattern cp = getP();
		if (cp == null) {
			return;
		}

		// display updated value
		int i = -1;
		((ParamListItemRecip) infoItemList.get(++i)).setGuiValue(cp.isRecip());
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format("%.4f", cp.getPixelSize()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format("%.4f", cp.getxCenter()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format("%.4f", cp.getyCenter()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format("%.4f", cp.getTilt()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format("%.4f", cp.getTwist()));
	    ((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format("%.4f", cp.getRepeat()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format("%.4f", cp.getSdd()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format("%.4f", cp.getWavelen()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format("%.4f", cp.getBetaD()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format("%.4f", cp.getGammaD()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format("%.4f", cp.getOffset()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format("%.4f", cp.getdCalibrant()));
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


}
