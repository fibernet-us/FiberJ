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

    final static String[][] PARAM_DEF = {
            { "pixel size (μm)",  "0",  "%.4f",    "editable" }, 
            { "center x (pix)",   "0",  "%.4f",    "editable" },
            { "center y (pix)",   "0",  "%.4f",    "editable" },
            { "tilt (°)",         "0",  "%.4f",    "editable" },
            { "twist (°)",        "0",  "%.4f",    "editable" },
            { "repeat (Å)",       "0",  "%.4f",    "editable" },
            { "distance (mm)",    "0",  "%.4f",    "editable" },
            { "wavelength (Å)",   "0",  "%.4f",  "uneditable" },
            { "oY (°)",           "0",  "%.4f",    "editable" },
            { "oZ (°)",           "0",  "%.4f",    "editable" },
            { "offset",           "0",  "%.4f",  "uneditable" },
            { "calibrant (Å)",    "0",  "%.4f",  "uneditable" }
    };
    
	public ParamListCollectionItem() {
		populateInfoItemList();
	}

	/*
	 * implement InfoItemCollection#populateInfoItemList()
	 */
	@Override
	protected void populateInfoItemList() {    
		infoItemList = new ArrayList<InfoItem>();
		ParamListItemRecip reciprocal = new ParamListItemRecip("reciprocal", false, null, this);
		infoItemList.add(reciprocal);
		
		for(String[] def : PARAM_DEF) {
		    infoItemList.add(new ParamListItem(def[0], def[1], def[2], 10, def[3].equals("editable"), this));
		}
		reciprocal.setActionLabel(((ParamListItem)infoItemList.get(1)).getLabel());
		
		updateUI();
	}

	/*
	 * implement InfoItemGuiCallBack interface, get user input from gui
	 */
	@Override
	public void guiUpdated(String name, String newValue) {
	       
		Pattern cp = getCurrentPattern();
		if (cp == null) {
			return;
		}

		if (name.equalsIgnoreCase("reciprocal")) {
			cp.setRecip(Boolean.parseBoolean(newValue));
			return;
		}

		try {
		    String s = name.toLowerCase();
		    double v = Double.parseDouble(newValue);

		    if(s.startsWith("pixel")) {
		        cp.setPixelSize(v);
		    }
		    else if(s.startsWith("center x")) {
		        cp.setCenterX(v);
		    }
		    else if(s.startsWith("center y")) {
		        cp.setCenterY(v);
		    }
		    else if(s.startsWith("tilt")) {
		        cp.setTilt(v);
		    }
		    else if(s.startsWith("twist")) {
		        cp.setTwist(v);
		    }
		    else if(s.startsWith("distance")) {
		        cp.setSdd(v);
		    }
		    else if(s.startsWith("wavelength")) {
		        cp.setWavelen(v);
		    }
		    else if(s.startsWith("oy")) {
		        cp.setBetaD(v);
		    }
		    else if(s.startsWith("oz")) {
		        cp.setGammaD(v);
		    }
		    else if(s.startsWith("offset")) {
		        cp.setOffset(v);
		    }
		    else if(s.startsWith("calibrant")) {
		        cp.setdCalibrant(v);
		    }
		    else if(s.startsWith("repeat")) {
		        cp.setRepeat(v);
		    }
		} 
		catch (NumberFormatException e) {
		    // not reachable since we already validated newvalue before guiUpdated()
		}
	}
	

	/**
	 * update parameter gui with values from current Pattern
	 */
	public void updateUI() {
	    
		Pattern cp = getCurrentPattern();
		if (cp == null) {
			return;
		}

		// display updated value
		int i = -1;
		((ParamListItemRecip) infoItemList.get(++i)).setGuiValue(cp.isRecip());
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format(PARAM_DEF[i-1][2], cp.getPixelSize()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format(PARAM_DEF[i-1][2], cp.getCenterX()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format(PARAM_DEF[i-1][2], cp.getCenterY()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format(PARAM_DEF[i-1][2], cp.getTilt()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format(PARAM_DEF[i-1][2], cp.getTwist()));
	    ((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format(PARAM_DEF[i-1][2], cp.getRepeat()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format(PARAM_DEF[i-1][2], cp.getSdd()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format(PARAM_DEF[i-1][2], cp.getWavelen()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format(PARAM_DEF[i-1][2], cp.getBetaD()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format(PARAM_DEF[i-1][2], cp.getGammaD()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format(PARAM_DEF[i-1][2], cp.getOffset()));
		((ParamListItem) infoItemList.get(++i)).setGuiValueNoCheck(String.format(PARAM_DEF[i-1][2], cp.getdCalibrant()));
	}


    /*
     * @return the current Pattern object
     */
    private Pattern getCurrentPattern() {
        return PatternProcessor.getCurrentPattern();
    }
    
} // class ParamListCollectionItem
