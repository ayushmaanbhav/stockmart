package com.ayushmaanbhav.jstockmart.server;

import java.util.*;
import java.io.*;
import java.text.*;

import javax.swing.*;

class ShareMarket {
	volatile static List<Company> companies;
	static Server server;
	BroadcastServer bserver;
	RankingServer rserver;
	volatile static double sensex;
	boolean testing = false;
	Orders orderbook;
	Thread calculations;
	final int SLEEP = 30000;
	volatile int loopp = 1;
	static DecimalFormat twoDForm = new DecimalFormat("#.##");
	BufferedWriter bw;
	double T, N;

	void initialise() {
		companies = new ArrayList<Company>();
		loadCompanies();
		UserDatabase.loadList();
		try {
			bw = new BufferedWriter(new FileWriter("appdata/sharemarket.txt"));
		} catch (Exception mm) {
		}
	}

	void startApp() {
		orderbook = new Orders();
		try {
			bw.write(new Date().toString() + ":app started\n");
			bw.flush();
		} catch (Exception mm) {
			mm.printStackTrace();
		}
		try {
			server = new Server(companies);
		} catch (Exception m) {
			m.printStackTrace();
		}
		server.startServer();
		try {
			bserver = new BroadcastServer("Share Market Broadcast", companies, server);
		} catch (Exception mm) {
			mm.printStackTrace();
		}
		try {
			rserver = new RankingServer("Share Market Ranks Broadcast", companies, server);
		} catch (Exception mm) {
			mm.printStackTrace();
		}
		rserver.start();
	}

	void start() {
		try {
			bw.write(new Date().toString() + ":market started\n");
			bw.flush();
		} catch (Exception mm) {
			mm.printStackTrace();
		}
		loopp = 0;
		bserver.start();
		bserver.queue.clear();
		bserver.queue.offer(loopp);
		RankingServer.startWaiting = true;
		calculations = new Thread() {
			public void run() {
				T = StockMart.totTime;
				N = (int) (T * 2);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							StockMart.updateCompanyTable();
							/*
							 * if(GraphPanel.frame.isVisible())
							 * GraphPanel.mainPanel.repaint();
							 */
						} catch (Exception r) {
							r.printStackTrace();
						}
					}
				});
				while (StockMart.started) {
					try {
						Thread.sleep(SLEEP);
					} catch (Exception mm) {
					}
					Orders.calculateLimitOrderStats();
					if (testing)
						Orders.printQueues();
					synchronized (companies) {
						MatchingEngine.matchOrders();
					}
					bserver.queue.offer(++loopp);
					rserver.queue.offer(loopp);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							try {
								StockMart.updateCompanyTable();
								if (GraphPanel.frame.isVisible())
									GraphPanel.mainPanel.repaint();
								if (HistogramPanel.frame.isVisible())
									HistogramPanel.mainPanel.updatePanel();
							} catch (Exception r) {
								r.printStackTrace();
							}
						}
					});
					for (int i = 0; i < companies.size(); i++) {
						companies.get(i).updateFile();
					}
				}
			}
		};
		calculations.setPriority(Thread.MAX_PRIORITY);
		calculations.start();
	}

	void loadCompanies() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("companies.txt"));
			String str;
			while ((str = br.readLine()) != null) {
				String s[] = str.trim().split(":");
				List<Double> l = new ArrayList<Double>();
				l.add(Double.parseDouble(s[1]));
				// companies.add(new
				// Company(s[0],l,0,Integer.parseInt(s[2]),Double.parseDouble(s[3]),Double.parseDouble(s[4])));
				companies.add(new Company(s[0], l, 0, 0, 0.0005, 0.03162277660168379331998893544433));
			}
			br.close();
		} catch (Exception m) {
			m.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	void stop() {
		try {
			bw.write(new Date().toString() + ":market stopped\n");
			bw.flush();
		} catch (Exception mm) {
			mm.printStackTrace();
		}
		try {
			server.stopServer();
		} catch (Exception m) {
		}
		try {
			bserver.close = 1;
			bserver.queue.offer(loopp);
		} catch (Exception m) {
		}
		try {
			RankingServer.startWaiting = false;
			rserver.close = 1;
			rserver.queue.offer(1);
		} catch (Exception m) {
		}
		try {
			calculations.stop();
		} catch (Exception m) {
		}
	}
}
