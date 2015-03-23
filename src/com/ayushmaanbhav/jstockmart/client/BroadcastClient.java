package com.ayushmaanbhav.jstockmart.client;

import java.util.*;
import javax.swing.*;
import java.text.*;
public class BroadcastClient extends Thread {
	StringBuffer sb;
	DecimalFormat twoDForm;
	int hj = 0;
	ShareValuesChangeListener svcl;
	int id;
	
	public BroadcastClient(StringBuffer s) {
		id = 0;
		sb = s;
		svcl = null;
		Companies.comp = new ArrayList<Company>();
		twoDForm = new DecimalFormat("#.##");
	}
	
	public void run(String poo) {
		try {
			StringBuffer fin = new StringBuffer();
			final String rec[] = poo.split("=");
			final String ids[] = rec[0].split("#");
			int recid = Integer.parseInt(ids[0]);
			if(recid <= id) {
				return;
			} else {
				id = recid;
			}
			final String mtr[] = ids[1].split(";");
			final String str[] = rec[1].split(";");
			for (int i = 0; i < str.length; i++) {
				String str2[] = str[i].split(":");
				
				int company_id = Integer.parseInt(new String(str2[0]));
				String company_name = new String(str2[1]);
				double mktvalue = Double.parseDouble(str2[2]);
				double initial_value = Double.parseDouble(str2[3]);
				double highest_value = Double.parseDouble(str2[4]);
				double lowest_value = Double.parseDouble(str2[5]);
				double price_precision = Double.parseDouble(str2[6]);
				
				Companies.updateValues(company_id, company_name, mktvalue, initial_value, highest_value, lowest_value, price_precision);
				
				fin.append(str2[1]);
				fin.append(" : ");
				fin.append(twoDForm.format(mktvalue));
				if (mktvalue > initial_value)
					fin.append("\u25b2"); // <font size="3" color="red">This
											// is some text!</font>
				else if (mktvalue < initial_value)
					fin.append("\u25bc");
				double c = mktvalue - initial_value;
				if (c > 0.0)
					fin.append(twoDForm.format(c));
				else if (c < 0.0)
					fin.append(twoDForm.format(-c));
				fin.append("        ");
			}
			sb.delete(0, sb.length());
			sb.append(fin.toString());
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						if (Main.imglabel2 != null)
							Main.imglabel2.setText("<html><pre><font color=\"white\">Sensex: " + mtr[1] + "<br/>Time Left: " + mtr[0] + "</font></pre></html>");
					} catch (Exception bb) {
						bb.printStackTrace();
					}
					try {
						if (hj == 0) {
							GraphPanel.mainPanel.setScores();
							Main.jtp.setEnabledAt(3, true);
							HistogramPanel.mainPanel.updateComboBox(Companies.comp);
							Main.jtp.setEnabledAt(4, true);
							hj = 1;
						} else {
							GraphPanel.mainPanel.repaint();
						}
					} catch (Exception e) {
					}
					try {
						svcl.valuesChanged();
					} catch (Exception e) {
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
