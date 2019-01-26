package de.maikmerten.loreslides.gui;

import de.maikmerten.loreslides.SlideHolder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author maik
 */
public class CharPanel extends JPanel {
	
	JComboBox fgColorBox;
	JComboBox bgColorBox;

	SlideEditor se;
	SlideHolder sh;
	
	public CharPanel(SlideEditor se, SlideHolder sh) {
		super();
		
		this.se = se;
		this.sh = sh;
		
		this.setFocusable(false);
		
		Color[] fgcolors = {
			NibbleColor.COLOR0,
			NibbleColor.COLOR1,
			NibbleColor.COLOR2,
			NibbleColor.COLOR3,
			NibbleColor.COLOR4,
			NibbleColor.COLOR5,
			NibbleColor.COLOR6,
			NibbleColor.COLOR7,
			NibbleColor.COLOR8,
			NibbleColor.COLOR9,
			NibbleColor.COLOR10,
			NibbleColor.COLOR11,
			NibbleColor.COLOR12,
			NibbleColor.COLOR13,
			NibbleColor.COLOR14,
			NibbleColor.COLOR15,
		};
		fgColorBox = new JComboBox(fgcolors);
		fgColorBox.setFocusable(false);
		fgColorBox.setRenderer(new ColorListCellRenderer("Text color"));
		fgColorBox.setSelectedIndex(15);
		
		fgColorBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object o = fgColorBox.getSelectedItem();
				if(o instanceof NibbleColor) {
					NibbleColor nc = (NibbleColor)o;
					se.setFgColor(nc.getColorValue());
				}
			}
		});
		
		bgColorBox = new JComboBox(fgcolors);
		bgColorBox.setFocusable(false);
		bgColorBox.setRenderer(new ColorListCellRenderer("Background"));
		bgColorBox.setSelectedIndex(0);
		
		bgColorBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object o = bgColorBox.getSelectedItem();
				if(o instanceof NibbleColor) {
					NibbleColor nc = (NibbleColor)o;
					se.setBgColor(nc.getColorValue());
				}
			}
		});
		
		JPanel colorPanel = new JPanel(new GridLayout(2, 1));
		colorPanel.add(fgColorBox);
		colorPanel.add(bgColorBox);
		
		
		JPanel charPanel = new JPanel(new GridLayout(32, 8));
		for(int row = 0; row < 32; ++row) {
			for(int col = 0; col < 8; ++col) {
				byte character = (byte)((row * 8) + col);
				CharButton cbutton = new CharButton(se, character);
				cbutton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						se.insertCharacter(character);
					}
				});
				charPanel.add(cbutton);
			}
		}
		
				
		setLayout(new BorderLayout());

		add(new JScrollPane(charPanel), BorderLayout.CENTER);
		add(colorPanel, BorderLayout.SOUTH);

	}
	
}
