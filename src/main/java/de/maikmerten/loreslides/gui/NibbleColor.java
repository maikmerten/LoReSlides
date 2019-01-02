package de.maikmerten.loreslides.gui;

import java.awt.Color;

/**
 *
 * @author maik
 */
public class NibbleColor extends Color {
	
	private final byte colorval;
	
	public final static NibbleColor COLOR0 = new NibbleColor((byte)0);
	public final static NibbleColor COLOR1 = new NibbleColor((byte)1);
	public final static NibbleColor COLOR2 = new NibbleColor((byte)2);
	public final static NibbleColor COLOR3 = new NibbleColor((byte)3);
	public final static NibbleColor COLOR4 = new NibbleColor((byte)4);
	public final static NibbleColor COLOR5 = new NibbleColor((byte)5);
	public final static NibbleColor COLOR6 = new NibbleColor((byte)6);
	public final static NibbleColor COLOR7 = new NibbleColor((byte)7);

	
	public NibbleColor(byte value) {
		super(((value & 0x4) != 0 ? 255 : 0), ((value & 0x2) != 0 ? 255 : 0), ((value & 0x1) != 0 ? 255 : 0));
		this.colorval = value;
	}
	
	
	public byte getColorValue() {
		return colorval;
	}
}
