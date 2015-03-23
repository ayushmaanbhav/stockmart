package com.ayushmaanbhav.jstockmart.client;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import com.ayushmaanbhav.jstockmart.user.*;
import com.ayushmaanbhav.jstockmart.utils.ButtonColumn;
import com.ayushmaanbhav.jstockmart.utils.HintPasswordField;
import com.ayushmaanbhav.jstockmart.utils.HintTextField;
import com.ayushmaanbhav.jstockmart.utils.LookAndFeel;

import java.text.*;

@SuppressWarnings("serial")
public class Main extends JFrame {
	String dir, domain;
	Image img, img2;
	int maxWidth;
	JPanel mpane;
	Client client = null;
	JInternalFrame internalFrame;
	JPanel imgpane;
	Graphics gd;
	Main main = this;
	HintTextField user;
	HintPasswordField pass;
	JPanel jp1, jp, jp2;
	GridBagLayout gbll;
	JLabel username;
	GridBagConstraints gbc;
	@SuppressWarnings("rawtypes")
	JComboBox jcb;
	ChatWindow chat;
	RankingWindow rank;
	UserDataChangedListener udcl;
	DefaultTableModel dtm[];
	static JLabel mon[];
	ShareValuesChangeListener svcl;
	static User ur = null;
	final static String location = "com/ayushmaanbhav/jstockmart/data/";

	public static void main(String args[]) {
		Main m = new Main();
		m.init(args);
	}

	int getWidth1() {
		return getWidth() - 20;
	}

	int getHeight1() {
		return getHeight() - 20;
	}

	protected void loadAppletParameters(String args[]) {
		domain = args[0];
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void createGUI() {
		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		setSize(screenSize.width, screenSize.height);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(main, "Are you sure you want to quit?", "Confirm exit.", JOptionPane.OK_OPTION, 0, new ImageIcon("")) != 0) {
					return;
				}
				stop();
			}
		});
		LookAndFeel.set();
		gbll = new GridBagLayout();
		mpane = new JPanel(gbll);
		mpane.setOpaque(false);
		mpane.setBackground(Color.WHITE);
		mpane.setBorder(new EtchedBorder());
		setGlassPane(mpane);

		jp = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics grphcs) {
				Graphics2D g2d = (Graphics2D) grphcs;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint gp = new GradientPaint(0, 0, getBackground().brighter(), 0, getHeight(), getBackground().darker());
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(grphcs);
			}
		};
		jp.setOpaque(false);
		user = new HintTextField("Username");
		user.setColumns(18);
		pass = new HintPasswordField("Password");
		pass.setColumns(18);
		JButton login = new JButton("Login");
		login.setMnemonic(KeyEvent.VK_ENTER);
		login.addActionListener(new LoginAction(main, user, pass, domain, client));
		username = new JLabel("Username");
		jcb = new JComboBox(new String[]{"Home", "Rules", "Logout"}); // help
																		// setting
		jcb.setLightWeightPopupEnabled(false);
		jcb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sel = jcb.getSelectedIndex();
				if (sel == 1) {
					try {
						String url = "http://" + domain + "/rules/index.html";
						String os = System.getProperty("os.name").toLowerCase();
						Runtime rt = Runtime.getRuntime();

						if (os.indexOf("win") >= 0) {

							// this doesn't support showing urls in the form of
							// "page.html#nameLink"
							rt.exec("rundll32 url.dll,FileProtocolHandler " + url);

						} else if (os.indexOf("mac") >= 0) {

							rt.exec("open " + url);

						} else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {

							// Do a best guess on unix until we get a platform
							// independent way
							// Build a list of browsers to try, in this order.
							String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror", "netscape", "opera", "links", "lynx"};

							// Build a command string which looks like
							// "browser1 "url" || browser2 "url" ||..."
							StringBuffer cmd = new StringBuffer();
							for (int i = 0; i < browsers.length; i++)
								cmd.append((i == 0 ? "" : " || ") + browsers[i] + " \"" + url + "\" ");

							rt.exec(new String[]{"sh", "-c", cmd.toString()});
						}
						// main.getAppletContext().showDocument(new URL(),
						// "_blank");
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				} else if (sel == 2) {
					main.stop();
					// main.getAppletContext().showDocument(main.getDocumentBase(),
					// "_self");
				}
			}
		});
		JLabel space = new JLabel("      ");
		space.setOpaque(false);
		JLabel space2 = new JLabel("      ");
		space2.setOpaque(false);

		jp2 = new JPanel(new FlowLayout());
		jp2.setOpaque(false);
		jp2.add(username);
		jp2.add(jcb);
		jp2.add(space2);

		jp1 = new JPanel(new FlowLayout());
		jp1.setOpaque(false);
		jp1.add(user);
		jp1.add(pass);
		jp1.add(login);
		jp1.add(space);

		JPanel developer = new JPanel(new GridBagLayout());
		JLabel tit = new JLabel(" StockMart ");
		tit.setOpaque(false);
		try {
			tit.setFont(titleFont);
			tit.setForeground(Color.gray.darker().darker().darker());
		} catch (Exception bb) {
			bb.printStackTrace();
		}
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.gridwidth = 1;
		gbc1.gridheight = 1;
		developer.add(tit, gbc1);
		JLabel subtit = new JLabel("   Developed by: Ayush Jain (ayushmaanbhav1008@gmail.com), Contribute here: https://github.com/ayushmaanbhav/stockmart");
		subtit.setOpaque(false);
		try {
			subtit.setFont(calibri);
			subtit.setForeground(Color.black);
		} catch (Exception bb) {
			bb.printStackTrace();
		}
		gbc1.gridx = 0;
		gbc1.gridy = 1;
		gbc1.gridwidth = 2;
		gbc1.gridheight = 1;
		developer.add(subtit, gbc1);
		developer.setOpaque(false);
		jp.add(developer, BorderLayout.WEST);
		jp.add(jp1, BorderLayout.EAST);
		jp.setBorder(new BevelBorder(BevelBorder.RAISED));
		jp.setPreferredSize(new Dimension(mpane.getWidth(), 45));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 8;
		gbc.gridheight = 1;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		mpane.add(jp, gbc);

		HintTextField regno = new HintTextField("Registration No.");
		regno.setColumns(10);
		// textField.setMaximumSize( .getPreferredSize() );
		HintTextField ruser = new HintTextField("Username");
		ruser.setColumns(10);
		HintPasswordField rpass = new HintPasswordField("Password");
		rpass.setColumns(10);
		JButton register = new JButton("Register");
		register.addActionListener(new RegisterAction(regno, ruser, rpass, domain, client));

		JPanel regpane = new JPanel() {
			@Override
			protected void paintComponent(Graphics grphcs) {
				Graphics2D g2d = (Graphics2D) grphcs;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint gp = new GradientPaint(0, 0, getBackground().brighter(), 0, getHeight(), getBackground().darker());
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(grphcs);
			}
		};
		regpane.setOpaque(false);
		regpane.setLayout(new BoxLayout(regpane, BoxLayout.PAGE_AXIS));
		regpane.setBorder(new EmptyBorder(10, 20, 10, 20)); // tlbr
		regpane.add(Box.createRigidArea(new Dimension(0, 5)));
		regpane.add(regno);
		regpane.add(Box.createRigidArea(new Dimension(0, 5)));
		regpane.add(ruser);
		regpane.add(Box.createRigidArea(new Dimension(0, 5)));
		regpane.add(rpass);
		regpane.add(Box.createRigidArea(new Dimension(0, 5)));
		regpane.add(register);
		regpane.add(Box.createRigidArea(new Dimension(0, 5)));

		internalFrame = new JInternalFrame("Register:", false, false, false, false);
		internalFrame.setContentPane(regpane);
		internalFrame.pack();

		unmovable(internalFrame);

		imgpane = new JPanel();
		imgpane.setOpaque(false);
		JLabel imglabel = new JLabel();
		imglabel.setSize((int) (.384 * (double) getWidth1()), (int) (.79 * (double) getHeight1()));
		imglabel.setOpaque(false);
		imglabel.setIcon(new ImageIcon(img.getScaledInstance(imglabel.getWidth(), imglabel.getHeight(), Image.SCALE_SMOOTH)));
		imgpane.add(imglabel);

		gbc.gridx = 6;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		gbc.gridheight = 3;
		gbc.ipadx = 200;
		gbc.ipady = 55;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 100, 0);
		mpane.add(internalFrame, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		gbc.gridheight = 7;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = new Insets(0, 10, 30, 0);
		mpane.add(imgpane, gbc);

		tester = new BackgroundAnimation();
		JPanel anipane = new JPanel();
		anipane.setLayout(new BoxLayout(anipane, BoxLayout.Y_AXIS));
		anipane.add(Box.createRigidArea(new Dimension(0, 45)));
		anipane.add(tester);

		rb = new RibbonPane(140, client);
		rb.setPreferredSize(new Dimension(mpane.getWidth(), 28));
		anipane.add(rb);
		setContentPane(anipane);
		mpane.setVisible(true);
		internalFrame.setVisible(true);
		try {
			internalFrame.setSelected(true);
		} catch (Exception v) {
		};
		rb.startAnimation();
	}
	RibbonPane rb;
	BackgroundAnimation tester;

	void unmovable(JInternalFrame jif) {
		BasicInternalFrameUI ui = (BasicInternalFrameUI) jif.getUI();
		Component north = ui.getNorthPane();
		MouseMotionListener[] actions = (MouseMotionListener[]) north.getListeners(MouseMotionListener.class);
		for (int i = 0; i < actions.length; i++)
			north.removeMouseMotionListener(actions[i]);
	}

	Font titleFont = null;
	Font tempus = null;
	Font arial = null;
	Font calibri = null;
	public void init(String args[]) {
		loadAppletParameters(args);
		try {
			calibri = new Font("Calibri", Font.PLAIN, 11);
			arial = new Font("Arial", Font.BOLD, 18);
			tempus = new Font("Tempus Sans ITC", Font.BOLD, 20);
			titleFont = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getClassLoader().getResourceAsStream(location + "burnstown-dam.regular.ttf")).deriveFont(Font.PLAIN, 25);
			img = new ImageIcon(this.getClass().getClassLoader().getResource(location + "logo.png")).getImage();
			img2 = new ImageIcon(this.getClass().getClassLoader().getResource(location + "logo2.png")).getImage();
		} catch (Exception m) {
			m.printStackTrace();
		}
		client = new Client(this, null);
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					createGUI();
					main.setVisible(true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("createGUI didn't successfully complete");
		}
	}

	static DecimalFormat twoDForm = new DecimalFormat("#.##");
	JTable table[];
	JPanel add;
	NewsPane news;
	JLabel sensex, spon, time;
	JPanel userpane[];
	static JLabel imglabel2 = null;
	Cashier cash;
	final String[] columnNames1 = {"SrNo.", "Company", "Quantity", "C.M.P.", "Mkt Value", "Gain/Loss", "Abs Return(%)", "Action"};
	final String[] columnNames2 = {"SrNo.", "Company", "C.M.P.", "% Change", "Low", "High", "Action"};
	final String[] columnNames3 = {"SrNo.", "Company", "Type", "Price Limit", "Quantity (Limit)", "Status", "Action"};
	Action delete;
	static JTabbedPane jtp;
	void proceedLogin(User user) {
		tester.stopAnimation();
		ur = user;
		username.setFont(tempus);
		username.setText(user.getName() + "     ");
		mpane.remove(internalFrame);
		jp.remove(jp1);
		mpane.remove(imgpane);
		mpane.repaint();

		news = new NewsPane();
		news.startAnimation();
		JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new RankingWindow(client), new ChatWindow(user, client));
		jsp.setPreferredSize(new Dimension(getWidth1() / 4, getHeight1() - 120));
		gbc.gridx = 6;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.VERTICAL; // VERTICAL
		gbc.gridwidth = 2;
		gbc.gridheight = 9;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = new Insets(0, 0, 40, 20);
		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		mpane.add(jsp, gbc);
		jp.add(jp2, BorderLayout.EAST);
		jsp.setDividerLocation((getHeight1() - 120) / 2);
		JPanel add = new JPanel();
		// add.setOpaque(false);
		add.setPreferredSize(new Dimension((getWidth1() * 5) / 8, (getHeight1() * 17) / 100));
		add.setMaximumSize(new Dimension((getWidth1() * 5) / 8, (getHeight1() * 17) / 100));
		add.setLayout(new BorderLayout());
		add.add(news, BorderLayout.SOUTH);
		add.setBorder(new BevelBorder(BevelBorder.RAISED));
		imglabel2 = new JLabel();
		imglabel2.setSize((int) add.getPreferredSize().getWidth() + 50, (int) add.getPreferredSize().getHeight() - 25);
		imglabel2.setOpaque(false);
		imglabel2.setIcon(new ImageIcon(img2.getScaledInstance(imglabel2.getWidth(), imglabel2.getHeight(), Image.SCALE_SMOOTH)));
		imglabel2.setFont(arial);
		imglabel2.setText("<html><pre><font color=\"white\">Sensex: <br/>Time Left: </font></pre></html>");
		imglabel2.setHorizontalTextPosition(JLabel.LEFT);
		imglabel2.setVerticalTextPosition(JLabel.BOTTOM);
		add.add(imglabel2, BorderLayout.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 5;
		gbc.gridheight = 2;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = new Insets(20, 40, 0, 0);
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		mpane.add(add, gbc);

		jtp = new JTabbedPane();
		jtp.setPreferredSize(new Dimension((getWidth1() * 5) / 8, (getHeight1() * 6) / 10));
		userpane = new JPanel[5];

		dtm = new DefaultTableModel[3];
		dtm[0] = new DefaultTableModel(columnNames1, 0);
		dtm[1] = new DefaultTableModel(columnNames2, 0);
		dtm[2] = new DefaultTableModel(columnNames3, 0);

		table = new JTable[3];
		JScrollPane jspt[] = new JScrollPane[3];
		mon = new JLabel[3];

		cash = new Cashier(client, ur);
		table[0] = new JTable(dtm[0]) {
			public boolean isCellEditable(int row, int col) {
				if (col == 7)
					return true;
				return false;
			}
			public boolean isCellSelected(int row, int col) {
				return false;
			}
		};
		table[1] = new JTable(dtm[1]) {
			public boolean isCellEditable(int row, int col) {
				if (col == 6)
					return true;
				return false;
			}
			public boolean isCellSelected(int row, int col) {
				return false;
			}
		};
		table[2] = new JTable(dtm[2]) {
			public boolean isCellEditable(int row, int col) {
				if (col == 6)
					return true;
				return false;
			}
			public boolean isCellSelected(int row, int col) {
				return false;
			}
		};

		for (int i = 0; i < 3; i++) {
			table[i].setDragEnabled(false);
			table[i].setRowSelectionAllowed(false);
			table[i].setColumnSelectionAllowed(false);
			// table.setShowHorizontalLines(true);
			// table.setShowVerticalLines(true);
			table[i].setRowHeight(30);
			table[i].getTableHeader().setReorderingAllowed(false);
			table[i].setFillsViewportHeight(true);
			jspt[i] = new JScrollPane(table[i]);
			userpane[i] = new JPanel(new BorderLayout());
			userpane[i].add(jspt[i], BorderLayout.CENTER);
			mon[i] = new JLabel("Avail. Cash : " + twoDForm.format(user.getCurrentMoney()));
			mon[i].setFont(tempus);
			userpane[i].add(mon[i], BorderLayout.SOUTH);
		}

		userpane[3] = GraphPanel.main(client);
		userpane[4] = HistogramPanel.main(client);

		delete = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				JTable table = (JTable) e.getSource();
				final int row = Integer.valueOf(e.getActionCommand());
				if (table == main.table[0]) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							cash.showDialog((String) main.table[0].getValueAt(row, 1), 1, ur.getCurrentShares().get(row));
						}
					});
				} else if (table == main.table[1]) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							cash.showDialog((String) main.table[1].getValueAt(row, 1), 0, null);
						}
					});
				} else if (table == main.table[2]) {
					int res = JOptionPane.showConfirmDialog(null, "Are you sure ?", "Confirm:", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (res == JOptionPane.YES_OPTION) {
						new Thread() {
							public void run() {
								final String res = client.cancelShares(ur.getName(), ur.getPassword(), ur.getPendingShares().get(row).id, ur.getPendingShares().get(row).sellid);
								if (res.equals("0")) {
									SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											JOptionPane.showMessageDialog(null, "Sorry your cancellation period is over.", "Error:", JOptionPane.PLAIN_MESSAGE);
										}
									});
								} else if (res.equals("1")) {
									ur.getPendingShares().get(row).status = "Cancelled";
									SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											updateTables();
										}
									});
								}
							}
						}.start();
					}
				}
			}
		};
		/*
		 * ButtonColumn b=new ButtonColumn(table[0], delete, 7); ButtonColumn
		 * b2=new ButtonColumn(table[1], delete, 6); ButtonColumn b3=new
		 * ButtonColumn(table[2], delete, 4);
		 */
		jtp.add("Your Holdings", userpane[0]);
		jtp.add("Watchlist", userpane[1]);
		jtp.add("Your Orders", userpane[2]);
		jtp.add("Company Overview", userpane[3]);

		jtp.add("Order Stats", userpane[4]);
		jtp.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				String temp = null;
				if (jtp.getSelectedIndex() == 4) {
					temp = (String) HistogramPanel.comp.getSelectedItem();
				}
				final String company = temp;
				new Thread() {
					public void run() {
						client.getSendCompanyStatsPeriodically(company);
					}
				}.start();
			}
		});

		jtp.setEnabledAt(3, false);
		jtp.setEnabledAt(4, false);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 5;
		gbc.gridheight = 5;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = new Insets(0, 40, 60, 0);
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		mpane.add(jtp, gbc);
		jtp.repaint();
		udcl = new UserDataChangedListener() {
			public void userDataChanged(User user) {
				updateTables();
			}
		};
		user.addUserDataChangeListener(udcl);
		svcl = new ShareValuesChangeListener() {
			public void valuesChanged() {
				updateTables();
			}
		};
		rb.addShareValuesChangeListener(svcl);
	}
	DefaultTableCellRenderer dtr = new DefaultTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (value.toString().indexOf("-") != -1) {
				value = value.toString() + " \u25bc";
			} else {
				value = value.toString() + " \u25b2";
			}
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value.toString().indexOf("-") != -1) {
				c.setForeground(Color.red);
			} else {
				c.setForeground(Color.green);
			}
			return c;
		}
	};
	void updateTables() {
		try {
			for (int i = 0; i < 3; i++) {
				mon[i].setText("Avail. Cash : " + twoDForm.format(ur.getCurrentMoney()) + "     Rank: " + RankingClient.rank);
				mon[i].repaint();
			}
		} catch (Exception mm) {
			mm.printStackTrace();
		}
		try {
			java.util.List<Shares> ss = ur.getCurrentShares();
			Object data[][] = new Object[ss.size()][8];
			for (int i = 0; i < ss.size(); i++) {
				data[i] = new Object[]{Integer.toString(i + 1), ss.get(i).company, ss.get(i).qty, twoDForm.format(Companies.getCompanyWithName(ss.get(i).company).mktvalue), twoDForm.format(ss.get(i).qty * Companies.getCompanyWithName(ss.get(i).company).mktvalue), twoDForm.format(ss.get(i).qty * Companies.getCompanyWithName(ss.get(i).company).mktvalue - ss.get(i).qty * ss.get(i).cost), twoDForm.format(((Companies.getCompanyWithName(ss.get(i).company).mktvalue - ss.get(i).cost) * 100) / ss.get(i).cost), "Sell"};
			}
			dtm[0].setDataVector(data, columnNames1);
			new ButtonColumn(table[0], delete, 7);
		} catch (Exception mm) {
			mm.printStackTrace();
		}
		try {
			Object data[][] = new Object[Companies.comp.size()][7];
			for (int i = 0; i < Companies.comp.size(); i++) {
				data[i] = new Object[]{Integer.toString(i + 1), Companies.comp.get(i).name, twoDForm.format(Companies.comp.get(i).mktvalue), twoDForm.format((Companies.comp.get(i).mktvalue - Companies.comp.get(i).inivalue) * 100 / Companies.comp.get(i).inivalue), twoDForm.format(Companies.comp.get(i).low), twoDForm.format(Companies.comp.get(i).high), "Buy"};
			}
			dtm[1].setDataVector(data, columnNames2);
			new ButtonColumn(table[1], delete, 6);
			table[1].getColumnModel().getColumn(3).setCellRenderer(dtr);
			table[1].repaint();
		} catch (Exception mm) {
			mm.printStackTrace();
		}
		try {
			java.util.List<Shares> ss = ur.getPendingShares();
			Object data[][] = new Object[ss.size()][7];
			for (int i = 0; i < ss.size(); i++) {
				String type = "", costLimit = "Not Applicable";
				switch (ss.get(i).type) {
					case 0 :
						type = "Market Buy Order";
						break;
					case 1 :
						type = "Market Sell Order";
						break;
					case 2 :
						type = "Limit Buy Order";
						costLimit = String.valueOf(ss.get(i).cost_limit);
						break;
					case 3 :
						type = "Limit Sell Order";
						costLimit = String.valueOf(ss.get(i).cost_limit);
						break;
				}
				data[i] = new Object[]{Integer.toString(i + 1), ss.get(i).company, type, costLimit, ss.get(i).qty_limit, ss.get(i).status, "Cancel"};
			}
			dtm[2].setDataVector(data, columnNames3);
			new ButtonColumn(table[2], delete, 6);
		} catch (Exception mm) {
			mm.printStackTrace();
		}
	}
	public void stop() {
		try {
			if (client != null)
				client.disconnect();
		} finally {
			System.exit(0);
		}
	}
}