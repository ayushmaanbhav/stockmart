package com.ayushmaanbhav.jstockmart.utils;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class HintTextField extends JTextField implements FocusListener {

	private final String hint;

	public HintTextField(final String hint) {
		super(hint);
		this.hint = hint;
		super.addFocusListener(this);
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText("");
		}
	}
	@Override
	public void focusLost(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText(hint);
		}
	}

	@Override
	public String getText() {
		String typed = super.getText();
		return typed.equals(hint) ? "" : typed;
	}
}