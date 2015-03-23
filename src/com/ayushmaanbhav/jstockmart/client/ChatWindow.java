package com.ayushmaanbhav.jstockmart.client;
import javax.swing.*;

import com.ayushmaanbhav.jstockmart.user.*;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings("serial")
class ChatWindow extends JPanel {
	JTextField jtf;
	JTextArea jta;
	JLabel jlab;
	Client cc;
	User user;
	String remain;
	BlockingQueue<String> queue;

	public ChatWindow(final User u, Client client) {
		user = u;
		cc = client;
		setLayout(new BorderLayout());
		jlab = new JLabel("<<DISCUSSION>> (" + u.getChat() + " characters remainimg)");
		jtf = new JTextField();
		jta = new JTextArea();
		jta.setFont(new Font("Calibri", Font.PLAIN, 13));
		jta.setLineWrap(true);
		jta.setEditable(false);
		jta.setForeground(Color.green.darker());
		// jtf.setEnabled(false);
		jtf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = jtf.getText().trim();
				if (!s.equals("")) {
					jtf.setText("");
					queue.offer(s);
				}
			}
		});
		queue = new LinkedBlockingQueue<String>();
		new Thread() {
			public void run() {
				while (true) {
					try {
						String message = queue.take();
						remain = sendString(message);
						u.setChat(Integer.parseInt(remain));
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								jlab.setText("<<DISCUSSION>> (" + u.getChat() + " characters remainimg)");
							}
						});
					} catch (Exception mm) {
						mm.printStackTrace();
					}
					if (u.getChat() <= 0)
						JOptionPane.showMessageDialog(null, "Sorry, You have finished your chat limit.", "Error:", JOptionPane.PLAIN_MESSAGE);
				}
			}
		}.start();
		JScrollPane jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(jlab, BorderLayout.NORTH);
		add(jsp, BorderLayout.CENTER);
		add(jtf, BorderLayout.SOUTH);
		try {
			jta.setText(cc.getChatHistory(user.getName(), user.getPassword()));
		} catch (Exception mmm) {
		}
		ChatClient cser = new ChatClient(jta);
		// cser.start();
		client.cc = cser;
	}

	synchronized String sendString(String s) {
		return cc.sendChat(s, user.getName(), user.getPassword());
	}
}