package com.ayushmaanbhav.jstockmart.client;
import javax.swing.*;

import com.ayushmaanbhav.jstockmart.utils.HintPasswordField;
import com.ayushmaanbhav.jstockmart.utils.HintTextField;

import java.awt.event.*;
import java.util.regex.Pattern;

class RegisterAction implements ActionListener {
	HintTextField reg, user;
	HintPasswordField pass;
	String domain;
	Client client;
	public RegisterAction(HintTextField r, HintTextField u, HintPasswordField p, String d, Client c) {
		reg = r;
		user = u;
		pass = p;
		domain = d;
		client = c;
	}
	public void actionPerformed(ActionEvent e) {
		Pattern p1 = Pattern.compile("[a-z0-9_]{3,16}");
		Pattern p2 = Pattern.compile("[a-z0-9_]{6,18}");
		if (!p1.matcher(reg.getText()).matches()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, "Incorect registration no.\nThe registration no. should be of length {3-16} and can contain\nletters(a-z), numbers and underscores.", "Error:", JOptionPane.PLAIN_MESSAGE);
					user.setText("");
				}
			});
			return;
		}
		if (!p1.matcher(user.getText()).matches()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, "Incorrect username.\nA username should be of length {3-16} and can contain\nletters(a-z), numbers and underscores.", "Error:", JOptionPane.PLAIN_MESSAGE);
					user.setText("");
				}
			});
			return;
		}
		if (!p2.matcher(pass.getText()).matches()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, "Incorrect password.\nA password should be of length {6-18} and can contain\nletters(a-z), numbers and underscores", "Error:", JOptionPane.PLAIN_MESSAGE);
					pass.setText("");
				}
			});
			return;
		}
		if (domain.equals("domain")) {
			domain = JOptionPane.showInputDialog("Enter domain.");
		}
		if (domain == null || domain.equals(""))
			return;
		new Thread() {
			public void run() {
				String rep = client.register(domain, reg.getText(), user.getText(), pass.getText());
				if (rep == null)
					return;
				if (rep.equals("1")) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(null, "Server Replies: Reg Successful", "Registration", JOptionPane.PLAIN_MESSAGE);
						}
					});
					// regestered
				} else if (rep.equals("3")) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(null, "Server Replies: Username already exists.\nA username should be of length {3-16} and can contain\nletters(a-z), numbers and underscores.", "Error:", JOptionPane.PLAIN_MESSAGE);
							user.setText("");
						}
					});
					return;
				} else if (rep.equals("2")) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(null, "Server Replies: Incorect registration no.\nThe registration no. should be of length {3-16} and can contain\nletters(a-z), numbers and underscores.", "Error:", JOptionPane.PLAIN_MESSAGE);
							reg.setText("");
						}
					});
					return;
				} else if (rep.equals("4")) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(null, "Server Replies: Already registered.", "Error:", JOptionPane.PLAIN_MESSAGE);
						}
					});
					return;
				}
			}
		}.start();
	}
}