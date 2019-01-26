package de.maikmerten.loreslides.gui;

import de.maikmerten.loreslides.EGAColor;
import java.awt.Color;

/**
 *
 * @author maik
 */
public class NibbleColor extends Color {
	
	private final byte colorval;
	
	public final static NibbleColor COLOR0 = new NibbleColor((byte)0, EGAColor.getRGB(0));
	public final static NibbleColor COLOR1 = new NibbleColor((byte)1, EGAColor.getRGB(1));
	public final static NibbleColor COLOR2 = new NibbleColor((byte)2, EGAColor.getRGB(2));
	public final static NibbleColor COLOR3 = new NibbleColor((byte)3, EGAColor.getRGB(3));
	public final static NibbleColor COLOR4 = new NibbleColor((byte)4, EGAColor.getRGB(4));
	public final static NibbleColor COLOR5 = new NibbleColor((byte)5, EGAColor.getRGB(5));
	public final static NibbleColor COLOR6 = new NibbleColor((byte)6, EGAColor.getRGB(6));
	public final static NibbleColor COLOR7 = new NibbleColor((byte)7, EGAColor.getRGB(7));
	public final static NibbleColor COLOR8 = new NibbleColor((byte)8, EGAColor.getRGB(8));
	public final static NibbleColor COLOR9 = new NibbleColor((byte)9, EGAColor.getRGB(9));
	public final static NibbleColor COLOR10 = new NibbleColor((byte)10, EGAColor.getRGB(10));
	public final static NibbleColor COLOR11 = new NibbleColor((byte)11, EGAColor.getRGB(11));
	public final static NibbleColor COLOR12 = new NibbleColor((byte)12, EGAColor.getRGB(12));
	public final static NibbleColor COLOR13 = new NibbleColor((byte)13, EGAColor.getRGB(13));
	public final static NibbleColor COLOR14 = new NibbleColor((byte)14, EGAColor.getRGB(14));
	public final static NibbleColor COLOR15 = new NibbleColor((byte)15, EGAColor.getRGB(15));
	

	
	public NibbleColor(byte idx, int rgb) {
		super(rgb);
		this.colorval = idx;
	}
	
	
	public byte getColorValue() {
		return colorval;
	}
}
