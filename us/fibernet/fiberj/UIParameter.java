/**
 * 
 */
package us.fibernet.fiberj;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.border.Border;

/**
 * @ClassName:UIParameter
 * @Description:TODO
 * @author kate.b.wu@gmail.com
 * 
 */
public final class UIParameter {

	private static final String PARAM_T = "Reflection-Parameter";

	private static JFrame paramFrame;
	private static UIMenubar uiMenubar;
	private static UIParamList uiParamList;
	private static UIReflxn uiReflxn;

	private static ParamListCollectionItem paramListCollectionItems;

	private static int wParamList = 50;//width of uiParamList 
	private static int hParamList = 100;//height of uiParamList
	private static int wReflxn = 50;// width of uiReflxn

	private UIParameter() {
	};

	/**
	 * create Parameter UI and make sure it happens only once.
	 * 
	 * @param x x-coordinate of Parameter window
	 * @param y y-coordinate of Parameter window
	 * @param wParam if >0 width of Parameter list pane
	 * @param hParam if > 0height of Parameter list pane
	 * @param wRef  width of Reflection pane
	 */
	public static synchronized void init(int x, int y, int wParam, int hParam,
			int wRef) {

		if (paramFrame != null) {

			return;

		}
		if (wParam > 0) {
			wParamList = wParam;
		}
		if (hParam > 0) {
			hParamList = hParam;
		}
		if (wRef > 0) {
			wReflxn = wRef;
		}

		paramFrame = new JFrame(PARAM_T);
		paramFrame.setLocation(x, y);
		paramFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		paramListCollectionItems = new ParamListCollectionItem();

		uiMenubar = new UIMenubar(paramFrame, new MenuDataParam());

		uiParamList = new UIParamList(paramFrame, wParam, hParam,
				paramListCollectionItems);
		
		//UIReflxn class is under development
		uiReflxn = new UIReflxn(paramFrame, wReflxn, hParam);

		Border lineBorder = BorderFactory.createLineBorder(Color.gray);
		uiParamList.setBorder(lineBorder);

		paramFrame.setLayout(new BoxLayout(paramFrame.getContentPane(),
				BoxLayout.LINE_AXIS));
		paramFrame.add(uiReflxn);
		paramFrame.add(uiParamList);
		
		uiReflxn.setVisible(false); //hide the Reflxn pane

		paramFrame.pack();
		paramFrame.setVisible(true);

	}

	public static UIMenubar getUIMenubar() {
		return uiMenubar;
	}

	public static UIReflxn getUIReflxn() {
		return uiReflxn;
	}

	public static UIParamList getUIParamList() {
		return uiParamList;
	}

	public static String getTitle() {
		return PARAM_T;
	}

	public static void setTitle(String title) {

		paramFrame.setTitle(title);

	}

}
