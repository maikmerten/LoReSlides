package de.maikmerten.loreslides;

/**
 *
 * @author maik
 */
public class EGAColor {
	
	public static int getRGB(int idx) {
		idx &= 0xF;
		switch(idx) {
			case 0: return 0x000000;
			case 1: return 0x0000AA;
			case 2: return 0x00AA00;
			case 3: return 0x00AAAA;
			case 4: return 0xAA0000;
			case 5: return 0xAA00AA;
			case 6: return 0xAA5500;
			case 7: return 0xAAAAAA;
			case 8: return 0x555555;
			case 9: return 0x5555FF;
			case 10: return 0x55FF55;
			case 11: return 0x55FFFF;
			case 12: return 0xFF5555;
			case 13: return 0xFF55FF;
			case 14: return 0xFFFF55;
			case 15: return 0xFFFFFF;
		}
		
		return 0;
	}
	
}
