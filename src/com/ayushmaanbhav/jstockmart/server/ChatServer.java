package com.ayushmaanbhav.jstockmart.server;
import java.io.*;
import java.util.*;

import com.ayushmaanbhav.jstockmart.user.Commands;

public class ChatServer extends Thread {
	Server server;
	StringBuffer chaat;
	BufferedWriter bw;
	public ChatServer(Server s) throws IOException {
		setPriority(Thread.MIN_PRIORITY);
		server = s;
		chaat = new StringBuffer();
		try {
			bw = new BufferedWriter(new FileWriter("appdata/chat.txt"));
		} catch (Exception mm) {
		}
	}

	public void run() {
		while (true) {
			try {
				StringBuffer toSend = new StringBuffer();
				toSend.append(Commands.CHAT_AND_NEWS);
				toSend.append("::");
				if (StockMart.newsfeed.equals(""))
					toSend.append("-1");
				else
					toSend.append(StockMart.newsfeed);
				toSend.append(";");
				if (chaat.toString().equals(""))
					toSend.append("-1");
				else
					toSend.append(chaat);
				chaat.setLength(0);
				server.sendMulti(toSend.toString(), Thread.MIN_PRIORITY);
				try {
					bw.write(new Date().toString() + ":" + toSend + "\n");
					bw.flush();
				} catch (Exception mm) {
					mm.printStackTrace();
				}
				try {
					sleep(30000);
				} catch (InterruptedException e1) {
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
