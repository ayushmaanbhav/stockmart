package com.ayushmaanbhav.jstockmart.server;
import java.util.*;
import java.io.*;
class Company {
	String name;
	volatile ArrayList<Double> sharevalue, futurevalues;
	volatile int sharessold, nooflimitbidsmatched, totalshares, tsharessold, sharessoldpast;
	volatile Date d;
	volatile double perchange, W, rate, vol, highest, lowest, phighest, plowest, price_precision, pprice_precision;
	boolean bankrupt;
	BufferedWriter bw;

	@SuppressWarnings("serial")
	public Company(String n, List<Double> l, int sd, int tot, double rate1, double vol1) {
		rate = rate1;
		vol = vol1;
		name = n;
		highest = 0;
		phighest = plowest = -1;
		lowest = Integer.MAX_VALUE;
		sharevalue = new ArrayList<Double>() {
			@Override
			public boolean add(Double value) {
				if (lowest > value) {
					plowest = lowest;
					lowest = value;
				}
				if (highest < value) {
					phighest = highest;
					highest = value;
				}
				return super.add(value);
			}
		};
		sharevalue.add(l.get(0));
		futurevalues = new ArrayList<Double>();
		sharessold = sd;
		nooflimitbidsmatched = 0;
		totalshares = tot;
		tsharessold = 0;
		bankrupt = false;
		W = 0;
		pprice_precision = 0;
		price_precision = MatchingEngine.getHighestPrecision(this);
		try {
			bw = new BufferedWriter(new FileWriter("appdata/companydata/" + name + ".txt"));
		} catch (Exception mm) {
		}
	}
	/*
	 * public Company(String n) { name=n; sharevalue=new ArrayList<Double>();
	 * sharessold=0; }
	 */
	public void updateFile() {
		try {
			bw.write(d.toString() + ":" + name + ":" + sharevalue.get(sharevalue.size() - 1) + ":" + perchange + ":" + sharessoldpast + ":" + tsharessold + ":" + totalshares + "\n");
			bw.flush();
		} catch (Exception mm) {
			mm.printStackTrace();
		}
	}

	public double getHighest() {
		return highest;
	}

	public double getLowest() {
		return lowest;
	}
	
	public void setPricePrecision(double price_precision) {
		this.pprice_precision = this.price_precision;
		this.price_precision = price_precision;
	}
}