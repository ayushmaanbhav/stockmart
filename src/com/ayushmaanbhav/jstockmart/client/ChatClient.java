package com.ayushmaanbhav.jstockmart.client;

import javax.swing.*;
public class ChatClient extends Thread {
	JTextArea jta;
	public ChatClient(JTextArea ja) {
		jta = ja;
	}
	public void run(String hh) {
		try {
			// StringBuffer fin=new StringBuffer("          ");
			final String str = hh.trim();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					String s[] = str.split(";");
					if (s.length < 2)
						return;
					else {
						if (!s[1].equals("-1"))
							jta.setText(jta.getText() + "\n" + s[1]);
						if (!s[0].equals("-1")) {
							String ff[] = s[0].split(":");
							String jj = "";
							for (int i = 0; i < ff.length; i++) {
								jj += ff[i] + "                    ";
							}
							NewsPane.temp.delete(0, NewsPane.temp.length());
							NewsPane.temp.append(jj);
							while (NewsPane.temp.length() <= 200) {
								NewsPane.temp.append(" ");
							}
						} else {
							NewsPane.temp.delete(0, NewsPane.temp.length());
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
