package com.ayushmaanbhav.jstockmart.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ayushmaanbhav.jstockmart.utils.Histogram;
import com.ayushmaanbhav.jstockmart.utils.TrippleArrayList;

public class HistogramPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private TrippleArrayList<Integer, Double, Integer> scores;

	public HistogramPanel(TrippleArrayList<Integer, Double, Integer> scores) {
		this.scores = scores;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Histogram.paintHistogram(g2, getHeight(), getWidth(), scores);
	}

	public void setScores(TrippleArrayList<Integer, Double, Integer> scores) {
		this.scores = scores;
	}

	public TrippleArrayList<Integer, Double, Integer> getScores() {
		return scores;
	}

	void updatePanel() {
		comp.setEnabled(false);
		new Thread() {
			public void run() {
				TrippleArrayList<Integer, Double, Integer> arr = new TrippleArrayList<Integer, Double, Integer>(Orders.getLimitOrderStatsOfCompany((String) comp.getSelectedItem()));
				if (arr != null) {
					if (arr.size() > 0) {
						Collections.sort(arr);
						arr.add(0, 0, 0.0, 0);
						arr.add(0, 0.0, 0);
					}
					setScores(arr);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							mainPanel.repaint();
							comp.setEnabled(true);
							// comp.requestFocus();
						}
					});
				}
			}
		}.start();
	}

	@SuppressWarnings("rawtypes")
	static JComboBox comp;
	static JFrame frame;
	static HistogramPanel mainPanel;
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void main() {
		comp = new JComboBox();
		for (int i = 0; i < ShareMarket.companies.size(); i++)
			comp.addItem(ShareMarket.companies.get(i).name);
		comp.setSelectedIndex(0);
		comp.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					mainPanel.updatePanel();
				}
			}
		});
		comp.setLightWeightPopupEnabled(false);
		mainPanel = new HistogramPanel(new TrippleArrayList<Integer, Double, Integer>());
		mainPanel.setPreferredSize(new Dimension(800, 500));
		frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(comp, BorderLayout.NORTH);
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
