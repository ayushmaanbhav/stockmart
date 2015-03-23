package com.ayushmaanbhav.jstockmart.server;
import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.text.*;

import com.ayushmaanbhav.jstockmart.user.Commands;

public class BroadcastServer extends Thread {
	@SuppressWarnings("unused")
	private long SECONDS = 30000;
	List<Company> com;
	int close;
	int id;
	BlockingQueue<Integer> queue;
	Server server;
	static boolean sendFullDetails;

	public BroadcastServer(String name, List<Company> c, Server s) throws IOException {
		super(name);
		setPriority(9);
		com = c;
		server = s;
		queue = new LinkedBlockingQueue<Integer>();
	}

	static DecimalFormat twoDForm = new DecimalFormat("#.##");
	public void run() {
		sendFullDetails = true;
		close = 0;
		while (true) {
			if (close == 1)
				break;
			try {
				id = queue.take();
				int temp = (int) (StockMart.totTime * 60 - (new Date().getTime() - StockMart.startDate.getTime()) / 1000);
				int hrs = temp / 3600;
				int mins = ((temp / 60) % 60);
				StringBuffer dString = new StringBuffer();
				dString.append(Commands.BROADCAST);
				dString.append("::");
				dString.append(String.valueOf(id++));
				dString.append("#");
				dString.append(hrs);
				dString.append(":");
				dString.append(mins / 10 == 0 ? "0" : "");
				dString.append(mins);
				dString.append(";");
				dString.append(twoDForm.format(ShareMarket.sensex));
				dString.append("=");
				for (int i = 0; i < com.size(); i++) {
					Company c = com.get(i);
					// if (!c.bankrupt) {
					dString.append(String.valueOf(i));
					dString.append(":");
					dString.append(c.name);
					dString.append(":");
					dString.append(String.valueOf(c.sharevalue.get(c.sharevalue.size() - 1)));
					dString.append(":");
					dString.append(String.valueOf(c.sharevalue.get(0)));
					dString.append(":");
					dString.append(String.valueOf(c.getHighest()));
					dString.append(":");
					dString.append(String.valueOf(c.getLowest()));
					dString.append(":");
					dString.append(String.valueOf(c.price_precision));
					dString.append(";");
					// }
				}
				sendFullDetails = false;
				server.sendMulti(dString.toString(), 5);
				server.sendPeriodicStats(5);
				/*
				 * try { sleep(SECONDS); } catch (InterruptedException e) { }
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
