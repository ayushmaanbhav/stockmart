package com.ayushmaanbhav.jstockmart.server;

import java.util.*;

import com.ayushmaanbhav.jstockmart.user.Shares;
import com.ayushmaanbhav.jstockmart.utils.TrippleArrayList;

class Orders {
	public final static int MARKET_BUY_ORDER = 0;
	public final static int MARKET_SELL_ORDER = 1;
	public final static int LIMIT_BUY_ORDER = 2;
	public final static int LIMIT_SELL_ORDER = 3;

	public static ArrayList<Shares> limitBuyOrders, limitSellOrders;
	public static ArrayList<Shares> marketBuyOrders, marketSellOrders;
	public static HashMap<String, TrippleArrayList<Integer, Double, Integer>> stats;

	public Orders() {
		limitBuyOrders = new ArrayList<Shares>();
		limitSellOrders = new ArrayList<Shares>();
		marketBuyOrders = new ArrayList<Shares>();
		marketSellOrders = new ArrayList<Shares>();
		stats = new HashMap<String, TrippleArrayList<Integer, Double, Integer>>(ShareMarket.companies.size());
		for (Company comp : ShareMarket.companies) {
			stats.put(comp.name, new TrippleArrayList<Integer, Double, Integer>());
		}
	}

	public static boolean remove(Shares s) {
		return limitBuyOrders.remove(s) || limitSellOrders.remove(s) || marketBuyOrders.remove(s) || marketSellOrders.remove(s);
	}

	public static Shares remove(Integer id, Integer sellid, String status) {
		for (Shares share : marketBuyOrders) {
			if (share.id == id && share.sellid == sellid) {
				marketBuyOrders.remove(share);
				share.status = status;
				return share;
			}
		}
		for (Shares share : marketSellOrders) {
			if (share.id == id && share.sellid == sellid) {
				marketSellOrders.remove(share);
				share.status = status;
				return share;
			}
		}
		for (Shares share : limitBuyOrders) {
			if (share.id == id && share.sellid == sellid) {
				limitBuyOrders.remove(share);
				share.status = status;
				return share;
			}
		}
		for (Shares share : limitSellOrders) {
			if (share.id == id && share.sellid == sellid) {
				limitSellOrders.remove(share);
				share.status = status;
				return share;
			}
		}
		return null;
	}

	public static boolean contain(Shares s) {
		return limitBuyOrders.contains(s) || limitSellOrders.contains(s) || marketBuyOrders.contains(s) || marketSellOrders.contains(s);
	}

	public static ArrayList<Shares> getOrders(int type, String comName) {
		ArrayList<Shares> list = new ArrayList<Shares>();
		if (type == MARKET_BUY_ORDER) {
			for (Shares share : marketBuyOrders) {
				if (share.company.equals(comName))
					list.add(share);
			}
		} else if (type == MARKET_SELL_ORDER) {
			for (Shares share : marketSellOrders) {
				if (share.company.equals(comName))
					list.add(share);
			}
		} else if (type == LIMIT_BUY_ORDER) {
			for (Shares share : limitBuyOrders) {
				if (share.company.equals(comName))
					list.add(share);
			}
		} else if (type == LIMIT_SELL_ORDER) {
			for (Shares share : limitSellOrders) {
				if (share.company.equals(comName))
					list.add(share);
			}
		}
		return list;
	}

	public static void calculateLimitOrderStats() {
		for (Company com : ShareMarket.companies) {
			TrippleArrayList<Integer, Double, Integer> arr = stats.get(com.name);
			arr.clear();
			for (Shares share : limitBuyOrders) {
				if (share.company.equals(com.name)) {
					int index = arr.containsSecondElement(share.cost_limit);
					if (index != -1)
						arr.setFirstElement(index, arr.getFirstElement(index) + share.qty_limit);
					else
						arr.add(share.qty_limit, share.cost_limit, 0);
				}
			}
			for (Shares share : limitSellOrders) {
				if (share.company.equals(com.name)) {
					int index = arr.containsSecondElement(share.cost_limit);
					if (index != -1)
						arr.setThirdElement(index, arr.getThirdElement(index) + share.qty_limit);
					else
						arr.add(0, share.cost_limit, share.qty_limit);
				}
			}
		}
	}

	public static TrippleArrayList<Integer, Double, Integer> getLimitOrderStatsOfCompany(String comName) {
		for (String key : stats.keySet()) {
			if (key.equalsIgnoreCase(comName))
				return stats.get(key);
		}
		return new TrippleArrayList<Integer, Double, Integer>();
	}

	public static int getNoOfLimitOrders(String comName) {
		int n = 0;
		for (Shares share : limitBuyOrders) {
			if (share.company.equals(comName))
				n++;
		}
		for (Shares share : limitSellOrders) {
			if (share.company.equals(comName))
				n++;
		}
		return n;
	}

	public static void printQueues() {
		System.out.println("\nStatus of all the queues:");
		System.out.print("Limit Buy Orders: ");
		System.out.println(limitBuyOrders.toString());
		System.out.print("Limit Sell Orders: ");
		System.out.println(limitSellOrders.toString());
		System.out.print("Market Buy Orders: ");
		System.out.println(marketBuyOrders.toString());
		System.out.print("Market Sell Orders: ");
		System.out.println(marketSellOrders.toString());
		System.out.println("Stats:");
		for (Company c : ShareMarket.companies) {
			System.out.print(c.name + ": ");
			System.out.println(getLimitOrderStatsOfCompany(c.name).toString());
		}
	}
}