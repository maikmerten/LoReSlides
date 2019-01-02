package de.maikmerten.loreslides.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author maik
 */
public class ColorListCellRenderer extends JButton implements ListCellRenderer<Object> {

	boolean overrideBackground = true;
	String text;
	
	
	public ColorListCellRenderer(String text) {
		super();
		this.text = text;
	}
	
	
	@Override
	public void setBackground(Color bg) {
		if (!overrideBackground) {
			return;
		}

		super.setBackground(bg);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		if (value instanceof NibbleColor) {
			NibbleColor c = (NibbleColor) value;
			overrideBackground = true;
			setBackground(c);
			setText(text);
			overrideBackground = false;
		}

		return this;
	}

}
