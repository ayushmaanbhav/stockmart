package com.ayushmaanbhav.jstockmart.user;

import java.io.*;
import java.util.*;

public class Shares implements Serializable {
	private static final long serialVersionUID = 1L;
	volatile public String company, status;
	volatile public int type, qty_limit;
	volatile public double cost_limit;
	volatile public int qty, id, buyid, sellid;
	volatile public double cost;
	public Date ordered, buyed;
	public User user;
	volatile public boolean notCanceled;

	public Shares() {
		company = null;
		status = null;
		qty = 0;
		type = -1;
		cost_limit = 0;
		qty_limit = 0;
		id = -1;
		user = null;
		sellid = -1;
		buyid = -1;
		cost = 0.0;
		ordered = null;
		buyed = null;
		notCanceled = true;
	}

	public Shares(Shares s) {
		try {
			company = new String(s.company);
		} catch (Exception k) {
			company = null;
		}
		try {
			status = new String(s.status);
		} catch (Exception k) {
			status = null;
		}
		qty = s.qty;
		user = s.user;
		id = s.id;
		cost_limit = s.cost_limit;
		type = s.type;
		qty_limit = s.qty_limit;
		buyid = s.buyid;
		sellid = s.sellid;
		cost = s.cost;
		try {
			ordered = (Date) s.ordered.clone();
		} catch (Exception k) {
			ordered = null;
		}
		try {
			buyed = (Date) s.buyed.clone();
		} catch (Exception k) {
			buyed = null;
		}
		notCanceled = s.notCanceled;
	}
}
