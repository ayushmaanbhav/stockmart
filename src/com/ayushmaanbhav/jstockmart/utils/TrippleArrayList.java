package com.ayushmaanbhav.jstockmart.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class TrippleArrayList<A extends Comparable<A>, B extends Comparable<B>, C extends Comparable<C>> extends ArrayList<TrippleArrayList<A, B, C>.Tupple> implements Serializable {
	private static final long serialVersionUID = 1L;

	public TrippleArrayList() {
	}

	public TrippleArrayList(TrippleArrayList<A, B, C> list) {
		for (Tupple t : list) {
			add(new Tupple(t));
		}
	}

	public class Tupple implements Comparable<Tupple>, Serializable {
		private static final long serialVersionUID = 1L;
		A a;
		B b;
		C c;

		public Tupple(Tupple t) {
			this.a = t.a;
			this.b = t.b;
			this.c = t.c;
		}

		public Tupple(A a, B b, C c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		public A getA() {
			return a;
		}

		public B getB() {
			return b;
		}

		public C getC() {
			return c;
		}

		@Override
		public int compareTo(Tupple t) {
			return b.compareTo(t.b);
		}
	}

	public boolean add(A a, B b, C c) {
		return add(new Tupple(a, b, c));
	}

	public void add(int index, A a, B b, C c) {
		add(index, new Tupple(a, b, c));
	}

	public int containsFirstElement(A a) {
		for (int i = 0; i < size(); i++) {
			if (get(i).a.compareTo(a) == 0) {
				return i;
			}
		}
		return -1;
	}

	public int containsSecondElement(B b) {
		for (int i = 0; i < size(); i++) {
			if (get(i).b.compareTo(b) == 0) {
				return i;
			}
		}
		return -1;
	}

	public int containsThirdElement(C c) {
		for (int i = 0; i < size(); i++) {
			if (get(i).c.compareTo(c) == 0) {
				return i;
			}
		}
		return -1;
	}

	public A getFirstElement(int index) {
		Tupple t = get(index);
		return t.getA();
	}

	public B getSecondElement(int index) {
		Tupple t = get(index);
		return t.getB();
	}

	public C getThirdElement(int index) {
		Tupple t = get(index);
		return t.getC();
	}

	public void setFirstElement(int index, A a) {
		get(index).a = a;
	}

	public void setSecondElement(int index, B b) {
		get(index).b = b;
	}

	public void setThirdElement(int index, C c) {
		get(index).c = c;
	}
}
