package com.ayushmaanbhav.jstockmart.server;
import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.text.*;

import javax.swing.*;

import com.ayushmaanbhav.jstockmart.user.*;

public class RankingServer extends Thread {
	private long SECONDS = 30000;
	List<Company> com;
	Server server;
	int close;
	BufferedWriter bw;
	BlockingQueue<Integer> queue;
	static boolean startWaiting = false;

	public RankingServer(String name, List<Company> c, Server s) throws IOException {
		super(name);
		setPriority(8);
		com = c;
		startWaiting = false;
		server = s;
		queue = new LinkedBlockingQueue<Integer>();
		try {
			bw = new BufferedWriter(new FileWriter("appdata/rank.txt"));
		} catch (Exception mm) {
		}
	}

	DecimalFormat twoDForm = new DecimalFormat("#.##");
	public void run() {
		close = 0;
		while (true) {
			if (close == 1)
				break;
			try {
				StringBuffer dString = new StringBuffer();
				dString.append(Commands.RANKS);
				dString.append("::");
				rankUsers();
				for (int i = UserDatabase.userList.size() - 1; i >= 0; i--) {
					User user = UserDatabase.userList.get(i);
					if (!user.isBanned()) {
						dString.append(user.getName());
						dString.append("-");
						dString.append(twoDForm.format(user.getScore()));
						dString.append(":");
					}
				}
				// final String sss=dString;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						StringBuffer jj = new StringBuffer();
						for (int i = UserDatabase.userList.size() - 1; i >= 0; i--) {
							User user = UserDatabase.userList.get(i);
							jj.append(UserDatabase.userList.size() - i);
							jj.append(" )  ");
							jj.append(user.getName());
							jj.append("->");
							jj.append(twoDForm.format(user.getScore()));
							jj.append("\n");
						}
						StockMart.ranks.setText(jj.toString());
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								StockMart.jkps.getVerticalScrollBar().setValue(0);
							}
						});
					}
				});
				server.sendMulti(dString.toString(), Thread.MIN_PRIORITY);
				try {
					bw.write(new Date().toString() + ":" + dString + "\n");
					bw.flush();
				} catch (Exception mm) {
					mm.printStackTrace();
				}
				if (startWaiting) {
					queue.take();
				} else {
					try {
						sleep(SECONDS);
					} catch (InterruptedException e) {
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	synchronized void rankUsers() {
		calculateScores();
		sortUsers();
	}

	synchronized private void calculateScores() {
		synchronized (UserDatabase.userList) {
			synchronized (com) {
				for (int i = 0; i < UserDatabase.userList.size(); i++) {
					User key = UserDatabase.userList.get(i);
					key.setScore(calculateUserScore(key));
				}
			}
		}
	}

	synchronized private void sortUsers() {
		synchronized (UserDatabase.userList) {
			for (int i = 0; i < UserDatabase.userList.size(); i++) {
				User key = UserDatabase.userList.get(i);
				int j;
				for (j = i - 1; j >= 0; j--) {
					if ((UserDatabase.userList.get(j).getScore() > key.getScore() && !UserDatabase.userList.get(j).isBanned()) || key.isBanned())
						UserDatabase.userList.set(j + 1, UserDatabase.userList.get(j));
					else
						break;
				}
				UserDatabase.userList.set(j + 1, key);
			}

		}
	}

	synchronized private double calculateUserScore(User u) {
		synchronized (u) {
			double mon = Double.valueOf(twoDForm.format(u.getCurrentMoney()));
			double score = mon;
			List<Shares> list = u.getCurrentShares();
			for (int i = 0; i < list.size(); i++) {
				String name = list.get(i).company;
				for (int j = 0; j < com.size(); j++) {
					if (com.get(j).name.equalsIgnoreCase(name)) {
						score = score + Double.valueOf(twoDForm.format(com.get(j).sharevalue.get(com.get(j).sharevalue.size() - 1) * list.get(i).qty));
						break;
					}
				}
			}
			return Double.valueOf(twoDForm.format(score));
		}
	}
}