package com.ayushmaanbhav.jstockmart.client;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
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

	public void setScores(TrippleArrayList<Integer, Double, Integer> arr) {
		if (arr != null) {
			if (arr.size() > 0) {
				Collections.sort(arr);
				arr.add(0, 0, 0.0, 0);
				arr.add(0, 0.0, 0);
			}
		}
		this.scores = arr;
	}

	public TrippleArrayList<Integer, Double, Integer> getScores() {
		return scores;
	}

	@SuppressWarnings("unchecked")
	public void updateComboBox(List<Company> score) {
		comp.removeItemListener(item);
		comp.removeAllItems();
		for (int i = 0; i < score.size(); i++)
			comp.addItem(score.get(i).name);
		try {
			comp.setSelectedIndex(0);
			updatePanel();
		} catch (Exception o) {
		}
		comp.addItemListener(item);
	}

	void updatePanel() {
		comp.setEnabled(false);
		new Thread() {
			public void run() {
				TrippleArrayList<Integer, Double, Integer> arr = client.getCompanyStats((String) comp.getSelectedItem());
				if (arr != null) {
					setScores(arr);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							mainPanel.repaint();
							comp.setEnabled(true);
							comp.requestFocus();
						}
					});
				}
			}
		}.start();
	}

	@SuppressWarnings("rawtypes")
	static JComboBox comp;
	static ItemListener item;
	static JPanel frame;
	static HistogramPanel mainPanel;
	static Client client;
	@SuppressWarnings("rawtypes")
	public static JPanel main(Client cl) {
		client = cl;
		comp = new JComboBox();
		item = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					mainPanel.updatePanel();
				}
			}
		};
		comp.addItemListener(item);
		comp.setLightWeightPopupEnabled(false);
		mainPanel = new HistogramPanel(new TrippleArrayList<Integer, Double, Integer>());
		// mainPanel.setScores(Companies.comp);
		// mainPanel.setPreferredSize(new Dimension(800, 500));
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame = new JPanel();
		frame.setLayout(new BorderLayout());
		frame.add(comp, BorderLayout.NORTH);
		frame.add(mainPanel, BorderLayout.CENTER);
		return frame;
	}
}
