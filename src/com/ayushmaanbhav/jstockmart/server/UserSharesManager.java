package com.ayushmaanbhav.jstockmart.server;

import java.util.Date;

import com.ayushmaanbhav.jstockmart.user.Shares;
import com.ayushmaanbhav.jstockmart.user.User;
import com.ayushmaanbhav.jstockmart.utils.RandomGenerator;

public class UserSharesManager {
	static void allotInitialShares(User user) {
		int size = ShareMarket.companies.size();
		if (size > 1) {
			double limit = user.getCurrentMoney();
			RandomGenerator rg = new RandomGenerator(size);
			double limitpercomp = (limit * 2) / size;
			for (int i = 0; i < size / 2; i++) {
				int index = rg.generateRandom();
				Company comp = ShareMarket.companies.get(index);
				double mktvalue = comp.sharevalue.get(comp.sharevalue.size() - 1);
				int noofshares = (int) (limitpercomp / mktvalue);
				Shares share = new Shares();
				share.buyed = new Date();
				share.id = Server.id++;
				share.type = Orders.MARKET_BUY_ORDER;
				share.company = comp.name;
				share.cost = mktvalue;
				share.qty = noofshares;
				share.status = "Initial Shares";
				user.getCurrentShares().add(share);
				limit -= share.cost * share.qty;
				comp.totalshares += share.qty;
			}
			user.setCurrentMoney(user.getCurrentMoney() + limit);
		} else {
			System.err.println("Companies not loaded !!");
		}
	}
}
