package com.ayushmaanbhav.jstockmart.utils;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UIManager;
public class LookAndFeel {
	public static void set() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName()); // com.sun.java.swing.plaf.windows.WindowsLookAndFeel
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look
			// and feel.
		}
	}
}