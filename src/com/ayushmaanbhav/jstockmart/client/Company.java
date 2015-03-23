package com.ayushmaanbhav.jstockmart.client;
import java.util.*;

class Company {
	String name;
	int id, yy;
	Double inivalue, mktvalue, low, high, price_precision;
	List<Double> sharevalue;

	public Company(int ii, String n, double m, double i, double hi, double lo, double p_precision) {
		name = new String(n);
		sharevalue = new ArrayList<Double>();
		mktvalue = m;
		sharevalue.add(m);
		inivalue = i;
		low = lo;
		high = hi;
		id = ii;
		price_precision = p_precision;
		yy = 0;
	}

	public void updateData(double m, double hi, double lo, double p_precision) {
		mktvalue = m;
		price_precision = p_precision;
		try {
			// if (m != sharevalue.get(sharevalue.size() - 1))
			sharevalue.add(m);
		} catch (Exception ml) {
		}
		low = lo;
		high = hi;
	}

	public void updateData(double m, double p_precision) {
		mktvalue = m;
		price_precision = p_precision;
		try {
			// if (m != sharevalue.get(sharevalue.size() - 1))
			sharevalue.add(m);
		} catch (Exception ml) {
		}
	}

	public void updateData(double m) {
		mktvalue = m;
		try {
			// if (m != sharevalue.get(sharevalue.size() - 1))
			sharevalue.add(m);
		} catch (Exception ml) {
		}
	}
}