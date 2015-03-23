package com.ayushmaanbhav.jstockmart.server;

import java.util.ArrayList;
import java.util.Date;

import com.ayushmaanbhav.jstockmart.user.Shares;
import com.ayushmaanbhav.jstockmart.user.User;

public class MatchingEngine {

	private static double LTP;

	/*
	 * The rule is basically to match market orders first because they have to
	 * be executed immediately. I first get all the market orders and execute
	 * them at the LTP (Last Traded Price) which is also the CMP (Current Market
	 * Price). If any market orders are remaining then we match them with limit
	 * orders. The concept to understand in the limit order matching is that we
	 * are always biased towards the buyers. We try to profit them. Why ? This
	 * is because we consider sellers to be clever. You will see this in real
	 * markets also. They put the price much higher than the actual price and
	 * then they lower it on bargaining. They know the psychology of their
	 * customers. To sum up, most of the time sellers profit on the prices they
	 * set. So we always trade the limit orders on seller's price even if the
	 * buyer's limit price is higher. One case is special though in which the
	 * buyer's limit price is higher than the market price or LTP and the seller
	 * wishes to sell his shares on the market price so he places a market
	 * order. Now in this case we trade the orders on the market price or LTP
	 * for the same reasons explained above.
	 */
	static void matchOrders() {
		double avg = 0;
		for (int i = 0; i < ShareMarket.companies.size(); i++) {
			Company com = ShareMarket.companies.get(i);
			double LTP = com.sharevalue.get(com.sharevalue.size() - 1);
			MatchingEngine.LTP = LTP;

			// get market orders of this company
			ArrayList<Shares> buyOrders = Orders.getOrders(Orders.MARKET_BUY_ORDER, com.name);
			ArrayList<Shares> sellOrders = Orders.getOrders(Orders.MARKET_SELL_ORDER, com.name);

			// match market orders first
			while (buyOrders.size() > 0 && sellOrders.size() > 0) {
				Shares s1 = buyOrders.get(0);
				Shares s2 = sellOrders.get(0);
				Shares s3 = MatchingEngine.matchMarketMarketOrders(s1, s2, com, LTP);
				if (s3 == null) {
					Orders.remove(s1);
					buyOrders.remove(s1);
					Orders.remove(s2);
					sellOrders.remove(s2);
				} else if (s3 == s1) {
					Orders.remove(s1);
					buyOrders.remove(s1);
				} else if (s3 == s2) {
					Orders.remove(s2);
					sellOrders.remove(s2);
				}
			}

			// if any market orders are remaining then match them with limit
			// orders
			if (buyOrders.size() > 0) {
				sellOrders = Orders.getOrders(Orders.LIMIT_SELL_ORDER, com.name);
				while (buyOrders.size() > 0 && sellOrders.size() > 0) {
					Shares s1 = buyOrders.get(0);
					double min_diff = Double.MAX_VALUE;
					Shares s2 = null;
					for (Shares temp : sellOrders) {
						double diff = Math.abs(temp.cost_limit - LTP);
						if (diff < min_diff) {
							min_diff = diff;
							s2 = temp;
						}
					}
					Shares s3 = MatchingEngine.matchMarketLimitOrders(s1, s2, com);
					if (s3 == null) {
						Orders.remove(s1);
						buyOrders.remove(s1);
						Orders.remove(s2);
						sellOrders.remove(s2);
					} else if (s3 == s1) {
						Orders.remove(s1);
						buyOrders.remove(s1);
					} else if (s3 == s2) {
						Orders.remove(s2);
						sellOrders.remove(s2);
					}
				}
				buyOrders = Orders.getOrders(Orders.LIMIT_BUY_ORDER, com.name);
			} else if (sellOrders.size() > 0) {
				buyOrders = Orders.getOrders(Orders.LIMIT_BUY_ORDER, com.name);
				while (buyOrders.size() > 0 && sellOrders.size() > 0) {
					Shares s2 = sellOrders.get(0);
					Shares s1 = null;
					double tradingPrice = LTP;
					for (int loop = 0; loop < buyOrders.size(); loop++) {
						if (buyOrders.get(loop).cost_limit >= LTP) {
							s1 = buyOrders.get(loop);
							break;
						}
					}
					if (s1 == null) {
						double min_diff = Double.MAX_VALUE;
						for (Shares temp : buyOrders) {
							double diff = Math.abs(temp.cost_limit - LTP);
							if (diff < min_diff) {
								min_diff = diff;
								s1 = temp;
								tradingPrice = s1.cost_limit;
							}
						}
					}
					Shares s3 = MatchingEngine.matchLimitMarketOrders(s1, s2, com, tradingPrice);
					if (s3 == null) {
						Orders.remove(s1);
						buyOrders.remove(s1);
						Orders.remove(s2);
						sellOrders.remove(s2);
					} else if (s3 == s1) {
						Orders.remove(s1);
						buyOrders.remove(s1);
					} else if (s3 == s2) {
						Orders.remove(s2);
						sellOrders.remove(s2);
					}
				}
				sellOrders = Orders.getOrders(Orders.LIMIT_SELL_ORDER, com.name);
			} else {
				buyOrders = Orders.getOrders(Orders.LIMIT_BUY_ORDER, com.name);
				sellOrders = Orders.getOrders(Orders.LIMIT_SELL_ORDER, com.name);
			}

			// match limit orders
			int loop1 = 0, loop2 = 0;
			while (loop1 < buyOrders.size() && sellOrders.size() > 0) {
				Shares s1 = buyOrders.get(loop1);
				Shares s2 = null;
				for (loop2 = 0; loop2 < sellOrders.size(); loop2++) {
					if (sellOrders.get(loop2).cost_limit <= s1.cost_limit) {
						s2 = sellOrders.get(loop2);
						break;
					}
				}
				if (s2 == null) {
					loop1++;
					continue;
				}
				Shares s3 = MatchingEngine.matchLimitLimitOrders(s1, s2, com);
				if (s3 == null) {
					Orders.remove(s1);
					loop1++;
					Orders.remove(s2);
					sellOrders.remove(s2);
				} else if (s3 == s1) {
					Orders.remove(s1);
					loop1++;
				} else if (s3 == s2) {
					Orders.remove(s2);
					sellOrders.remove(s2);
				}
			}

			com.sharevalue.add(MatchingEngine.LTP);
			com.d = new Date();

			try {
				com.perchange = Double.valueOf(ShareMarket.twoDForm.format((com.sharevalue.get(com.sharevalue.size() - 1) - com.sharevalue.get(com.sharevalue.size() - 2)) * 100 / com.sharevalue.get(com.sharevalue.size() - 2)));
			} catch (Exception mm) {
				com.perchange = 0;
			}
			if (com.sharevalue.get(com.sharevalue.size() - 1) <= 0.0) {
				com.bankrupt = true;
			}
			avg += com.sharevalue.get(com.sharevalue.size() - 1);
			com.tsharessold += com.sharessold;
			com.sharessoldpast = com.sharessold;
			com.sharessold = 0;
			com.setPricePrecision(MatchingEngine.calculatePrecision(com));
		}
		ShareMarket.sensex = Double.valueOf(ShareMarket.twoDForm.format(avg / ShareMarket.companies.size()));
	}
	private static Shares matchMarketMarketOrders(Shares buyOrder, Shares sellOrder, Company comp, double LTP) {
		return matchOrders(buyOrder, sellOrder, comp, LTP);
	}

	private static Shares matchMarketLimitOrders(Shares buyOrder, Shares sellOrder, Company comp) {
		return matchOrders(buyOrder, sellOrder, comp, sellOrder.cost_limit);
	}

	private static Shares matchLimitMarketOrders(Shares buyOrder, Shares sellOrder, Company comp, double tradingPrice) {
		return matchOrders(buyOrder, sellOrder, comp, tradingPrice);
	}

	private static Shares matchLimitLimitOrders(Shares buyOrder, Shares sellOrder, Company comp) {
		return matchOrders(buyOrder, sellOrder, comp, sellOrder.cost_limit);
	}

	private static Shares matchOrders(Shares buyOrder, Shares sellOrder, Company comp, double tradingPrice) {
		User buyer = buyOrder.user;
		User seller = sellOrder.user;
		synchronized (buyer) {
			synchronized (seller) {
				if (!buyOrder.notCanceled || buyer.isBanned() || !buyer.getPendingShares().contains(buyOrder)) {
					buyOrder.status = "Canceled";
					return buyOrder;
				}

				if (!sellOrder.notCanceled || seller.isBanned() || !seller.getPendingShares().contains(sellOrder)) {
					sellOrder.status = "Canceled";
					return sellOrder;
				}

				try {
					assert tradingPrice > 0.0;
				} catch (AssertionError e) {
					e.printStackTrace();
					return new Shares();
				}

				double tot_price = Double.parseDouble(StockMart.twoDForm.format(tradingPrice * 1.02));

				int min_shares = Math.min(buyOrder.qty_limit, sellOrder.qty_limit);

				int buyersAukaad = (int) (buyer.getCurrentMoney() / tot_price);
				min_shares = Math.min(min_shares, buyersAukaad);
				if (min_shares == 0) {
					buyOrder.status = "Failed (Not Enough Money)";
					return buyOrder;
				}

				Shares sellersCurrentShares = null;
				for (int z = 0; z < seller.getCurrentShares().size(); z++) {
					if (seller.getCurrentShares().get(z).company.equalsIgnoreCase(comp.name)) {
						sellersCurrentShares = seller.getCurrentShares().get(z);
						break;
					}
				}

				if (sellersCurrentShares == null || sellersCurrentShares.qty <= 0) {
					sellOrder.status = "Failed (Not Enough Shares)";
					return sellOrder;
				}

				min_shares = Math.min(min_shares, sellersCurrentShares.qty);

				boolean remove_buyer = false, remove_seller = false;

				// buyer gets shares
				buyer.setCurrentMoney(buyer.getCurrentMoney() - Double.parseDouble(StockMart.twoDForm.format(tot_price * min_shares)));
				comp.sharessold += min_shares;
				if (min_shares == buyOrder.qty_limit) {
					buyOrder.status = "Completed";
					remove_buyer = true;
				} else if (min_shares == buyersAukaad) {
					buyOrder.status = "Completed Partially (Not Enough Money)";
					remove_buyer = true;
				} else {
					buyOrder.status = "Completed Partially, Rest Pending";
					buyOrder.qty_limit -= min_shares;
				}
				int y = 0;
				for (int z = 0; z < buyer.getCurrentShares().size(); z++) {
					if (buyer.getCurrentShares().get(z).company.equalsIgnoreCase(comp.name)) {
						buyer.getCurrentShares().get(z).cost = Double.parseDouble(StockMart.twoDForm.format((buyer.getCurrentShares().get(z).cost * buyer.getCurrentShares().get(z).qty + tradingPrice * min_shares) / (min_shares + buyer.getCurrentShares().get(z).qty)));
						buyer.getCurrentShares().get(z).qty += min_shares;
						buyer.getCurrentShares().get(z).buyed = new Date();
						y++;
						break;
					}
				}
				if (y == 0) {
					Shares sh = new Shares(buyOrder);
					sh.buyed = new Date();
					sh.cost = tradingPrice;
					sh.qty = min_shares;
					buyer.getCurrentShares().add(sh);
				}

				// seller gets money
				seller.setCurrentMoney(seller.getCurrentMoney() + Double.parseDouble(StockMart.twoDForm.format(tradingPrice * min_shares)));
				if (min_shares == sellOrder.qty_limit) {
					sellOrder.status = "Completed";
					remove_seller = true;
				} else if (sellersCurrentShares.qty == min_shares) {
					sellOrder.status = "Completed Partially (Not Enough Shares)";
					remove_seller = true;
				} else {
					sellOrder.status = "Completed Partially, Rest Pending";
					sellOrder.qty_limit -= min_shares;
				}
				if (sellersCurrentShares.qty - min_shares > 0) {
					sellersCurrentShares.cost = Double.parseDouble(StockMart.twoDForm.format((sellersCurrentShares.cost * sellersCurrentShares.qty - tradingPrice * min_shares) / (sellersCurrentShares.qty - min_shares)));
					sellersCurrentShares.qty -= min_shares;
				} else {
					seller.getCurrentShares().remove(sellersCurrentShares);
				}

				MatchingEngine.LTP = tradingPrice;

				if (buyOrder.type == Orders.LIMIT_BUY_ORDER && sellOrder.type == Orders.LIMIT_SELL_ORDER) {
					comp.nooflimitbidsmatched++;
				}

				try {
					ShareMarket.server.sendMessage(buyer, 5);
					ShareMarket.server.sendMessage(seller, 5);
				} catch (Exception hh) {
					hh.printStackTrace();
				}

				if (remove_buyer && remove_seller) {
					return null;
				} else if (remove_buyer) {
					return buyOrder;
				} else if (remove_seller) {
					return sellOrder;
				} else {
					System.out.println("Warning: neither buyer nor seller removed.");
					return new Shares();
				}
			}
		}
	}

	static double calculatePrecision(Company com) {
		double mktvalue = com.sharevalue.get(com.sharevalue.size() - 1);
		int noofdigits = noOfDigits((int) mktvalue);
		int nooflimitorders = Orders.getNoOfLimitOrders(com.name);
		if (com.nooflimitbidsmatched > 10 && nooflimitorders < 10) {
			return Double.parseDouble(ShareMarket.twoDForm.format(Math.pow(10, noofdigits - 2) / 8));
		} else if (com.nooflimitbidsmatched > 5 && nooflimitorders < 10) {
			return Double.parseDouble(ShareMarket.twoDForm.format(Math.pow(10, noofdigits - 2) / 4));
		} else if (com.nooflimitbidsmatched > 0 && nooflimitorders < 50) {
			return Double.parseDouble(ShareMarket.twoDForm.format(Math.pow(10, noofdigits - 2) / 2));
		} else {
			return Double.parseDouble(ShareMarket.twoDForm.format(Math.pow(10, noofdigits - 2)));
		}
	}

	static double getHighestPrecision(Company com) {
		double mktvalue = com.sharevalue.get(com.sharevalue.size() - 1);
		int noofdigits = noOfDigits((int) mktvalue);
		return Double.parseDouble(ShareMarket.twoDForm.format(Math.pow(10, noofdigits - 2) / 8));
	}

	static int noOfDigits(int n) {
		int i = 0;
		while ((n = n / 10) > 0) {
			i++;
		}
		return i;
	}
}
