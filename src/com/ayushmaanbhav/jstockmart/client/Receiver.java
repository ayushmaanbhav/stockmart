package com.ayushmaanbhav.jstockmart.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.thavam.util.concurrent.BlockingHashMap;
import org.thavam.util.concurrent.BlockingMap;

import com.ayushmaanbhav.jstockmart.user.Commands;
import com.ayushmaanbhav.jstockmart.user.User;
import com.ayushmaanbhav.jstockmart.utils.TrippleArrayList;

public class Receiver extends Thread {
	@SuppressWarnings("rawtypes")
	BlockingMap rev;
	Client client;

	@SuppressWarnings("rawtypes")
	public Receiver(Client client) {
		this.client = client;
		rev = new BlockingHashMap();
		setPriority(MAX_PRIORITY);
	}

	@SuppressWarnings("unchecked")
	public void run() {
		while (client.connected) {
			try {
				Object obj = client.in.readObject();
				if (Client.testing)
					System.out.println("Got Obj: " + obj.toString());
				if (obj instanceof String) {
					String recString = (String) obj;
					if (recString.equals(Commands.UNKNOWN_COMMAND)) {
						client.connected = false;
						client.in.close();
						client.out.close();
						client.socket.close();
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								Cashier.closee = true;
								JOptionPane.showMessageDialog(null, "Disconnected from server. Please Restart", "Error:", JOptionPane.PLAIN_MESSAGE);
								try {
									client.m.stop();
								} catch (Exception w) {
								}
							}
						});
					} else if (recString.equals(Commands.SERVER_NOT_RUNNING)) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								Cashier.closee = true;
								JOptionPane.showMessageDialog(null, "Server Not Running.", "Error:", JOptionPane.PLAIN_MESSAGE);
							}
						});
					} else {
						String command[] = recString.split("::");
						if (command[0].equals(Commands.BROADCAST)) {
							if (client.bc != null)
								client.bc.run(command[1]);
						} else if (command[0].equals(Commands.CHAT_AND_NEWS)) {
							if (client.cc != null)
								client.cc.run(command[1]);
						} else if (command[0].equals(Commands.FINAL_RANKINGS)) {
							String hhh = command[1];
							final JDialog jd = new JDialog();
							jd.setUndecorated(false);
							JPanel pan = new JPanel(new BorderLayout());
							JLabel ppp = new JLabel();
							ppp.setFont(new Font("Calibri", Font.BOLD, 20));
							ppp.setText("<html><pre>Thanks for playing !!!<br/>1st: " + hhh.split(":")[0] + "<br/>2nd: " + hhh.split(":")[1] + "<br/>3rd: " + hhh.split(":")[2] + "</pre></html>");
							pan.add(ppp, BorderLayout.CENTER);
							JButton ok = new JButton("Ok");
							ok.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									jd.setVisible(false);
									try {
										client.m.stop();
									} catch (Exception w) {
									}
								}
							});
							pan.add(ok, BorderLayout.SOUTH);
							jd.setContentPane(pan);
							jd.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
							jd.pack();
							jd.setLocationRelativeTo(null);
							jd.setVisible(true);
						} else if (command[0].equals(Commands.RANKS)) {
							if (client.rc != null)
								client.rc.run(command[1]);
						} else {
							try {
								int cmdID = Integer.parseInt(recString);
								obj = client.in.readObject();
								rev.offer(cmdID, obj);
							} catch (Exception ppp) {
								ppp.printStackTrace();
								if (Client.testing)
									System.out.println("Shit: " + recString + "   :   " + Thread.currentThread());
							}
						}
					}
				} else if (obj instanceof User) {
					try {
						Main.ur.changeData((User) obj);
					} catch (Exception w) {
						try {
							client.maain.ur.changeData((User) obj);
						} catch (Exception ppp) {
							ppp.printStackTrace();
						}
					}
				} else if (obj instanceof TrippleArrayList<?, ?, ?>) {
					HistogramPanel.mainPanel.setScores((TrippleArrayList<Integer, Double, Integer>) obj);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							HistogramPanel.mainPanel.repaint();
						}
					});
				}
			} catch (Exception m) {
				m.printStackTrace();
				client.disconnect();
				break;
			}
		}
	}
}