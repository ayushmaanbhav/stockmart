package com.ayushmaanbhav.jstockmart.client;
import javax.swing.*;

import java.awt.*;

import javax.swing.text.*;

@SuppressWarnings("serial")
class RankingWindow extends JPanel {
	JTextPane jta;
	JScrollPane jsp;
	public RankingWindow(Client cc) {
		setLayout(new BorderLayout());
		JLabel jlab = new JLabel("<<RANKINGS>>");
		jta = new JTextPane();
		StyledDocument doc = jta.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_LEFT);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		jta.setFont(new Font("Calibri", Font.PLAIN, 13));
		jta.setEditable(false);
		jta.setForeground(Color.red.darker());
		jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(jlab, BorderLayout.NORTH);
		add(jsp, BorderLayout.CENTER);
		RankingClient cser = new RankingClient(jta, jsp);
		cc.rc = cser;
	}
}