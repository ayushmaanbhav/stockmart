package com.ayushmaanbhav.jstockmart.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.ayushmaanbhav.jstockmart.user.Shares;
import com.ayushmaanbhav.jstockmart.utils.ButtonColumn;

public class OrdersPanel {
	static JFrame frame;
	static DefaultTableModel dtm;
	static Action delete;
	static JTable orders;
	static String[] colNames = {"ID", "Sell ID", "User", "Curr Money", "No. of shares", "Ordered", "Type", "Limit Price", "Qty Limit", "Status", "Cancel"};
	static JScrollPane jspt;
	static JComboBox<String> combo;
	static JButton refresh;
	static JPanel panel;

	@SuppressWarnings("serial")
	public static void main() {
		dtm = new DefaultTableModel(colNames, 0);
		orders = new JTable(dtm) {
			public boolean isCellEditable(int row, int col) {
				if (col == 10)
					return true;
				return false;
			}
			public boolean isCellSelected(int row, int col) {
				return false;
			}
		};
		orders.setDragEnabled(false);
		orders.setRowSelectionAllowed(false);
		orders.setColumnSelectionAllowed(false);
		// table.setShowHorizontalLines(true);
		// table.setShowVerticalLines(true);
		orders.setRowHeight(30);
		orders.getTableHeader().setReorderingAllowed(false);
		orders.setFillsViewportHeight(true);
		jspt = new JScrollPane(orders);
		delete = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				final int row = Integer.valueOf(e.getActionCommand());
				int res = JOptionPane.showConfirmDialog(null, "Are you sure ?", "Confirm:", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (res == JOptionPane.YES_OPTION) {
					Shares ss = Orders.remove((Integer) orders.getValueAt(row, 0), (Integer) orders.getValueAt(row, 1), "Canceled by Admin");
					if (ss != null)
						ShareMarket.server.sendMessage(ss.user, 5);
				}
			}
		};
		combo = new JComboBox<String>();
		for (int i = 0; i < ShareMarket.companies.size(); i++)
			combo.addItem(ShareMarket.companies.get(i).name);
		combo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					update((String) combo.getSelectedItem());
				}
			}
		});
		refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				update((String) combo.getSelectedItem());
			}
		});
		frame = new JFrame("Orders");
		panel = (JPanel) frame.getContentPane();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(800, 500));
		panel.add(combo, BorderLayout.NORTH);
		panel.add(jspt, BorderLayout.CENTER);
		panel.add(refresh, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				combo.setSelectedIndex(0);
				combo.requestFocus();
			}
		});
	}

	public static void update(final String company) {
		new Thread() {
			public void run() {
				Company comp = null;
				for (Company temp : ShareMarket.companies) {
					if (temp.name.equals(company)) {
						comp = temp;
						break;
					}
				}
				ArrayList<Object[]> arr = new ArrayList<Object[]>();
				arr.add(new Object[]{"LIMIT_BUY_ORDERS:", null, null, null, null, null, null, null, null, null, null});
				ArrayList<Shares> shares = Orders.getOrders(Orders.LIMIT_BUY_ORDER, comp.name);
				for (Shares ss : shares) {
					int noofshares = 0;
					for (Shares pp : ss.user.getCurrentShares())
						if (pp.company.equals(comp.name))
							noofshares += pp.qty;
					arr.add(new Object[]{ss.id, ss.sellid, ss.user.getName(), ss.user.getCurrentMoney(), noofshares, String.valueOf((ss.ordered.getTime() - StockMart.startDate.getTime()) / 60000) + " mins", "LIMIT_BUY_ORDER", ss.cost_limit, ss.qty_limit, ss.status, "Cancel"});
				}
				arr.add(new Object[]{"LIMIT_SELL_ORDERS:", null, null, null, null, null, null, null, null, null, null});
				shares = Orders.getOrders(Orders.LIMIT_SELL_ORDER, comp.name);
				for (Shares ss : shares) {
					int noofshares = 0;
					for (Shares pp : ss.user.getCurrentShares())
						if (pp.company.equals(comp.name))
							noofshares += pp.qty;
					arr.add(new Object[]{ss.id, ss.sellid, ss.user.getName(), ss.user.getCurrentMoney(), noofshares, String.valueOf((ss.ordered.getTime() - StockMart.startDate.getTime()) / 60000) + " mins", "LIMIT_SELL_ORDER", ss.cost_limit, ss.qty_limit, ss.status, "Cancel"});
				}
				arr.add(new Object[]{"MARKET_BUY_ORDERS:", null, null, null, null, null, null, null, null, null, null});
				shares = Orders.getOrders(Orders.MARKET_BUY_ORDER, comp.name);
				for (Shares ss : shares) {
					int noofshares = 0;
					for (Shares pp : ss.user.getCurrentShares())
						if (pp.company.equals(comp.name))
							noofshares += pp.qty;
					arr.add(new Object[]{ss.id, ss.sellid, ss.user.getName(), ss.user.getCurrentMoney(), noofshares, String.valueOf((ss.ordered.getTime() - StockMart.startDate.getTime()) / 60000) + " mins", "MARKET_BUY_ORDER", "N.A.", ss.qty_limit, ss.status, "Cancel"});
				}
				arr.add(new Object[]{"MARKET_SELL_ORDERS:", null, null, null, null, null, null, null, null, null, null});
				shares = Orders.getOrders(Orders.MARKET_SELL_ORDER, comp.name);
				for (Shares ss : shares) {
					int noofshares = 0;
					for (Shares pp : ss.user.getCurrentShares())
						if (pp.company.equals(comp.name))
							noofshares += pp.qty;
					arr.add(new Object[]{ss.id, ss.sellid, ss.user.getName(), ss.user.getCurrentMoney(), noofshares, String.valueOf((ss.ordered.getTime() - StockMart.startDate.getTime()) / 60000) + " mins", "MARKET_SELL_ORDER", "N.A.", ss.qty_limit, ss.status, "Cancel"});
				}
				final Object obj[][] = new Object[arr.size()][];
				for (int i = 0; i < arr.size(); i++) {
					obj[i] = arr.get(i);
				}
				arr.clear();
				arr = null;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						dtm.setDataVector(obj, colNames);
						new ButtonColumn(orders, delete, 10);
						combo.requestFocus();
					}
				});
			}
		}.start();
	}
}
