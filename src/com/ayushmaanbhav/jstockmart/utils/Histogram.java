package com.ayushmaanbhav.jstockmart.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

public class Histogram {
	private static int padding = 25;
	private static int labelPadding = 25;
	private static Color rectColor1 = new Color(255, 0, 0, 100);
	private static Color rectColor2 = new Color(0, 255, 0, 100);
	private static Color gridColor = new Color(200, 200, 200, 200);
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	private static int pointWidth = 4;
	private static int numberYDivisions = 10;

	public static void paintHistogram(Graphics2D g2, int height, int width, TrippleArrayList<Integer, Double, Integer> scores) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		double xScale = ((double) width - (2 * padding) - labelPadding) / (scores.size() - 1);
		double yScale = ((double) height - 2 * padding - labelPadding) / (getMaxScore(scores) - getMinScore(scores));

		List<Point> graphPoints1 = new ArrayList<>();
		for (int i = 1; i < scores.size() - 1; i++) {
			int x1 = (int) (i * xScale + padding + labelPadding);
			int y1 = (int) ((getMaxScore(scores) - scores.getFirstElement(i)) * yScale + padding);
			graphPoints1.add(new Point(x1, y1));
		}

		List<Point> graphPoints2 = new ArrayList<>();
		for (int i = 1; i < scores.size() - 1; i++) {
			int x1 = (int) (i * xScale + padding + labelPadding);
			int y1 = (int) ((getMaxScore(scores) - scores.getThirdElement(i)) * yScale + padding);
			graphPoints2.add(new Point(x1, y1));
		}

		// draw white background
		g2.setColor(Color.WHITE);
		g2.fillRect(padding + labelPadding, padding, width - (2 * padding) - labelPadding, height - 2 * padding - labelPadding);
		g2.setColor(Color.BLACK);

		// create hatch marks and grid lines for y axis.
		for (int i = 0; i < numberYDivisions + 1; i++) {
			int x0 = padding + labelPadding;
			int x1 = pointWidth + padding + labelPadding;
			int y0 = height - ((i * (height - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
			int y1 = y0;
			if (scores.size() > 0) {
				g2.setColor(gridColor);
				g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, width - padding, y1);
				g2.setColor(Color.BLACK);
				String yLabel = ((int) ((getMinScore(scores) + (getMaxScore(scores) - getMinScore(scores)) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
				FontMetrics metrics = g2.getFontMetrics();
				int labelWidth = metrics.stringWidth(yLabel);
				g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
			}
			g2.drawLine(x0, y0, x1, y1);
		}

		// and for x axis
		for (int i = 0; i < scores.size(); i++) {
			if (scores.size() > 1) {
				int x0 = i * (width - padding * 2 - labelPadding) / (scores.size() - 1) + padding + labelPadding;
				int x1 = x0;
				int y0 = height - padding - labelPadding;
				int y1 = y0 - pointWidth;
				if ((i % ((int) ((scores.size() / 20.0)) + 1)) == 0) {
					g2.setColor(gridColor);
					g2.drawLine(x0, height - padding - labelPadding - 1 - pointWidth, x1, padding);
					g2.setColor(Color.BLACK);
					String xLabel = Double.toString(scores.getSecondElement(i));
					FontMetrics metrics = g2.getFontMetrics();
					int labelWidth = metrics.stringWidth(xLabel);
					g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
				}
				g2.drawLine(x0, y0, x1, y1);
			}
		}

		String legend1 = "Red Bars   : Buy Orders ";
		String legend2 = "Green Bars: Sell Orders";
		FontMetrics metrics = g2.getFontMetrics();
		int labelWidth = metrics.stringWidth(legend1);
		g2.drawString(legend1, width - padding - labelWidth - 1, padding + metrics.getHeight() + 1);
		labelWidth = metrics.stringWidth(legend2);
		g2.drawString(legend2, width - padding - labelWidth - 1, padding + (metrics.getHeight() + 1) * 2);

		String legend4 = "Bid/Ask Price";
		String legend3 = "No. of orders";
		labelWidth = metrics.stringWidth(legend3);
		g2.drawString(legend3, padding - labelWidth / 4, padding - metrics.getHeight() / 2);
		labelWidth = metrics.stringWidth(legend4);
		g2.drawString(legend4, width / 2 - labelWidth, height - padding + metrics.getHeight() - 1);

		// create x and y axes
		g2.drawLine(padding + labelPadding, height - padding - labelPadding, padding + labelPadding, padding);
		g2.drawLine(padding + labelPadding, height - padding - labelPadding, width - padding, height - padding - labelPadding);

		Stroke oldStroke = g2.getStroke();
		g2.setColor(rectColor1);
		g2.setStroke(GRAPH_STROKE);
		int rec_width = (width - padding * 2 - labelPadding) / (2 * (scores.size() - 1)) - 1;
		for (int i = 0; i < graphPoints1.size(); i++) {
			int x1 = graphPoints1.get(i).x;
			int y1 = graphPoints1.get(i).y;
			int rec_height = height - padding - labelPadding - y1;
			// int x2 = graphPoints.get(i + 1).x;
			// int y2 = graphPoints.get(i + 1).y;
			g2.fillRect(x1 - rec_width / 2, y1, rec_width, rec_height);
			// g2.drawLine(x1, y1, x2, y2);
		}
		g2.setColor(rectColor2);
		for (int i = 0; i < graphPoints2.size(); i++) {
			int x1 = graphPoints2.get(i).x;
			int y1 = graphPoints2.get(i).y;
			int rec_height = height - padding - labelPadding - y1;
			// int x2 = graphPoints.get(i + 1).x;
			// int y2 = graphPoints.get(i + 1).y;
			g2.fillRect(x1 - rec_width / 2, y1, rec_width, rec_height);
			// g2.drawLine(x1, y1, x2, y2);
		}
		g2.setStroke(oldStroke);
	}

	private static double getMinScore(TrippleArrayList<Integer, Double, Integer> scores) {
		int minScore = Integer.MAX_VALUE;
		for (int i = 1; i < scores.size() - 1; i++) {
			minScore = Math.min(minScore, scores.getThirdElement(i));
			minScore = Math.min(minScore, scores.getFirstElement(i));
		}
		return minScore;
	}

	private static double getMaxScore(TrippleArrayList<Integer, Double, Integer> scores) {
		int maxScore = Integer.MIN_VALUE;
		for (int i = 1; i < scores.size() - 1; i++) {
			maxScore = Math.max(maxScore, scores.getThirdElement(i));
			maxScore = Math.max(maxScore, scores.getFirstElement(i));
		}
		return maxScore + 2;
	}
}
