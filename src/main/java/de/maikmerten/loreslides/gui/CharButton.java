package de.maikmerten.loreslides.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JButton;

/**
 *
 * @author maik
 */
public class CharButton extends JButton {
	
	private final byte character;
	private final SlideEditor se;
	
	public CharButton(SlideEditor se, byte character) {
		super();
		this.se = se;
		this.character = character;
		this.setFocusable(false);
		setPreferredSize(new Dimension(18, 18));
		setText(" ");
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		BufferedImage cimg = se.getFontImage(character);
		if(cimg != null) {
			g.drawImage(cimg, 1, 1, cimg.getWidth() * 2, cimg.getHeight() * 2, null);
		}
	}
	
}
