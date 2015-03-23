package com.ayushmaanbhav.jstockmart.utils;

import java.util.ArrayList;

public class RandomGenerator {
	ArrayList<Integer> arr;

	public RandomGenerator(int max) {
		arr = new ArrayList<Integer>(max);
		for (int i = 0; i < max; i++) {
			arr.add(i);
		}
	}

	public int generateRandom() {
		return arr.remove((int) (Math.random() * arr.size()));
	}
}