package com.ayushmaanbhav.jstockmart.server;
import java.util.*;
import java.io.*;

import javax.swing.*;

import com.ayushmaanbhav.jstockmart.user.*;
class UserDatabase {
	volatile static protected List<User> userList;
	static protected List<String> regno;
	private static double money;

	static void loadList() {
		userList = new ArrayList<User>();
		regno = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("reg.txt"));
			String str;
			while ((str = br.readLine()) != null) {
				str = str.trim().toLowerCase();
				regno.add(str);
			}
			br.close();
		} catch (Exception m) {
			m.printStackTrace();
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader("money.txt"));
			money = Double.parseDouble(ShareMarket.twoDForm.format(Double.parseDouble(br.readLine())));
			br.close();
		} catch (Exception e) {
		}
	}

	static void deleteUser(final User ur) {
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).checkName(ur.getName())) {
				userList.remove(i);
				break;
			}
		}
	}

	static int registerUser(final String regno, final String name, String pass) {
		boolean b = true;
		synchronized (userList) {
			if (StockMart.checkRegNo) {
				b = false;
				if (containsRegNo(regno))
					b = true;
			}
			if (b) {
				for (int i = 0; i < userList.size(); i++) {
					if (userList.get(i).checkName(name))
						if (userList.get(i).checkRegNo(regno))
							return 4;
						else
							return 3;
				}
				User temp = new User(regno, name, pass, 1000, money);
				UserSharesManager.allotInitialShares(temp);
				userList.add(temp);
				temp = null;
				SwingUtilities.invokeLater(new Runnable() {
					@SuppressWarnings("unchecked")
					public void run() {
						StockMart.users.addItem(name);
					}
				});
				return 1;
			}
			return 2;
		}
	}

	static boolean containsRegNo(String reg) {
		for (int i = 0; i < regno.size(); i++) {
			if (regno.get(i).equals(reg))
				return true;
		}
		return false;
	}

	static User getUserWithRegNo(String r) {
		synchronized (userList) {
			for (int i = 0; i < userList.size(); i++) {
				if (userList.get(i).checkRegNo(r)) {
					return userList.get(i);
				}
			}
		}
		return null;
	}

	static User getUserWithName(String n) {
		synchronized (userList) {
			for (int i = 0; i < userList.size(); i++) {
				if (userList.get(i).checkName(n)) {
					return userList.get(i);
				}
			}
		}
		return null;
	}

	static int validate(String n, String p) {
		synchronized (userList) {
			User user;
			int value = 1;
			try {
				for (int i = 0; i < userList.size(); i++) {
					user = userList.get(i);
					if (user.isBanned())
						return 11;
					if (user.checkName(n)) {
						value = 2;
						if (user.checkPassword(p)) {
							return 0;
						}
					}
				}
			} catch (Exception e) {
			}
			return value;
		}
	}

	static User getUser(String n, String p) {
		synchronized (userList) {
			User user;
			try {
				for (int i = 0; i < userList.size(); i++) {
					user = userList.get(i);
					if (user.checkName(n)) {
						if (user.checkPassword(p)) {
							return user;
						}
					}
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	static User deleteUserWithName(String n) {
		synchronized (userList) {
			for (int i = 0; i < userList.size(); i++) {
				if (userList.get(i).checkName(n)) {
					User uu = userList.get(i);
					userList.remove(i);
					return uu;
				}
			}
		}
		return null;
	}
}