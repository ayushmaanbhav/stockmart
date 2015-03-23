package com.ayushmaanbhav.jstockmart.client;
import java.io.*;
import java.net.*;

import com.ayushmaanbhav.jstockmart.user.*;
import com.ayushmaanbhav.jstockmart.utils.TrippleArrayList;

public class Client {
	boolean connected = false;
	Socket socket = null;
	PrintWriter out = null;
	// BufferedReader in = null;
	ObjectInputStream in;
	String domain;
	static boolean testing = true;
	volatile int commID = 0;
	Client ccl;
	int usrD;
	Main m;
	TestMain maain;
	BroadcastClient bc = null;
	RankingClient rc = null;
	ChatClient cc = null;
	Receiver rec;

	public Client(Main mm, TestMain maaii) {
		ccl = this;
		m = mm;
		maain = maaii;
		commID = 0;
	}

	public void connect(String user, String pass) {
		if (!connected) {
			try {
				socket = new Socket(domain, 4446);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new ObjectInputStream(socket.getInputStream());
			} catch (java.net.UnknownHostException e) {
				System.err.println("Don't know about host");
				return;
			} catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to");
				return;
			}
			this.connected = true;
			rec = new Receiver(ccl);
			rec.start();
		}
	}

	String login(String domain, String user, String pass) {
		int cmdID = commID++;
		this.domain = domain;
		connect(user, pass);
		try {
			if (testing)
				System.out.println("Command: " + cmdID + ";" + Commands.LOG_IN + ":" + user + ":" + pass);
			out.println(cmdID + ";" + Commands.LOG_IN + ":" + user + ":" + pass);
			out.flush();
			String rep = (String) receiveReply(cmdID);
			return rep.split(":")[0];
		} catch (Exception r) {
			r.printStackTrace();
		}
		return null;
	}

	String register(String domain, String regno, String user, String pass) {
		int cmdID = commID++;
		this.domain = domain;
		connect(user, pass);
		try {
			if (testing)
				System.out.println("Command: " + cmdID + ";" + Commands.REGISTER + ":" + regno + ":" + user + ":" + pass);
			out.println(cmdID + ";" + Commands.REGISTER + ":" + regno + ":" + user + ":" + pass);
			out.flush();
			String rep = (String) receiveReply(cmdID);
			disconnect();
			return rep.split(":")[0];
		} catch (Exception r) {
			r.printStackTrace();
		}
		return null;
	}

	public void disconnect() {
		int cmdID = commID++;
		this.connected = false;
		try {
			if (testing)
				System.out.println("Command: " + cmdID + ";" + Commands.LOG_OUT);
			out.println(cmdID + ";" + Commands.LOG_OUT);
			out.flush();
			in.close();
			out.close();
			socket.close();
		} catch (IOException ex) {
			System.err.println("Server stop failed.");
		}
	}

	User getUserDetails(String user, String pass) {
		int cmdID = commID++;
		connect(user, pass);
		try {
			if (testing)
				System.out.println("Command: " + cmdID + ";" + Commands.GET_USER_DETAILS);
			usrD = 1;
			out.println(cmdID + ";" + Commands.GET_USER_DETAILS);
			out.flush();
			User vv = (User) receiveReply(cmdID);
			usrD = 0;
			return vv;
		} catch (Exception r) {
			r.printStackTrace();
		}
		return null;
	}

	String sendChat(String s, String user, String pass) {
		int cmdID = commID++;
		connect(user, pass);
		try {
			if (testing)
				System.out.println("Command: " + cmdID + ";" + Commands.SEND_CHAT_MESSAGE + ":" + s.trim());
			out.println(cmdID + ";" + Commands.SEND_CHAT_MESSAGE + ":" + s.trim());
			out.flush();
			return (String) receiveReply(cmdID);
		} catch (Exception r) {
			r.printStackTrace();
		}
		return null;
	}

	String getChatHistory(String user, String pass) {
		int cmdID = commID++;
		connect(user, pass);
		try {
			if (testing)
				System.out.println("Command: " + cmdID + ";" + Commands.GET_CHAT_HISTORY);
			out.println(cmdID + ";" + Commands.GET_CHAT_HISTORY);
			out.flush();
			return (String) receiveReply(cmdID);
		} catch (Exception r) {
			r.printStackTrace();
		}
		return null;
	}

	void placeOrder(final User user, String cmd, Company comp, double cost_limit, int qty_limit, int id) {
		int cmdID = commID++;
		connect(user.getName(), user.getPassword());
		try {
			if (testing)
				System.out.println("Command: " + cmdID + ";" + cmd + ":" + comp.name + ":" + Integer.toString(qty_limit) + ":" + Double.toString(cost_limit) + ":" + id);
			out.println(cmdID + ";" + cmd + ":" + comp.name + ":" + Integer.toString(qty_limit) + ":" + Double.toString(cost_limit) + ":" + id);
			out.flush();
			Shares pen = (Shares) receiveReply(cmdID);
			user.getPendingShares().add(pen);
			user.dataChanged();
		} catch (Exception r) {
			r.printStackTrace();
		}
	}

	String cancelShares(String user, String pass, int id, int sellid) {
		int cmdID = commID++;
		connect(user, pass);
		try {
			if (testing)
				System.out.println("Command: " + cmdID + ";" + Commands.CANCEL_ORDER + ":" + id + ":" + sellid);
			out.println(cmdID + ";" + Commands.CANCEL_ORDER + ":" + id + ":" + sellid);
			out.flush();
			return (String) receiveReply(cmdID);
		} catch (Exception r) {
			r.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	java.util.List<Double> getHistory(String comp, int count) {
		int cmdID = commID++;
		// connect(user.getName(),user.getPassword());
		try {
			if (testing)
				System.out.println("Command: " + cmdID + ";" + Commands.GET_COMPANY_HISTORY + ":" + comp + ":" + Integer.toString(count));
			out.println(cmdID + ";" + Commands.GET_COMPANY_HISTORY + ":" + comp + ":" + Integer.toString(count));
			out.flush();
			return (java.util.List<Double>) receiveReply(cmdID);
		} catch (Exception r) {
			r.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	TrippleArrayList<Integer, Double, Integer> getCompanyStats(String comp) {
		int cmdID = commID++;
		// connect(user.getName(),user.getPassword());
		try {
			if (testing)
				System.out.println("Command: " + cmdID + ";" + Commands.GET_COMPANY_STATS + ":" + comp);
			out.println(cmdID + ";" + Commands.GET_COMPANY_STATS + ":" + comp);
			out.flush();
			return (TrippleArrayList<Integer, Double, Integer>) receiveReply(cmdID);
		} catch (Exception r) {
			r.printStackTrace();
		}
		return null;
	}

	String getSendCompanyStatsPeriodically(String comp) {
		int cmdID = commID++;
		// connect(user.getName(),user.getPassword());
		try {
			if (comp == null)
				comp = "-1";
			if (testing)
				System.out.println("Command: " + cmdID + ";" + Commands.SEND_PERIODIC_STATS + ":" + comp);
			out.println(cmdID + ";" + Commands.SEND_PERIODIC_STATS + ":" + comp);
			out.flush();
			return (String) receiveReply(cmdID);
		} catch (Exception r) {
			r.printStackTrace();
		}
		return null;
	}

	Object receiveReply(int cmdID) {
		Object obj = null;
		while (obj == null) {
			try {
				obj = rec.rev.take(cmdID);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (testing)
			System.out.println("Got: " + cmdID + "   :   " + obj.toString() + "   :   " + Thread.currentThread());
		return obj;
	}
}