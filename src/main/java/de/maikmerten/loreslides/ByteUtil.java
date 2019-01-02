package de.maikmerten.loreslides;

/**
 *
 * @author maik
 */
public class ByteUtil {
	
	public static byte[] writeInt(int i) {
		byte[] b = new byte[4];
		
		b[0] = (byte)(i & 0xFF);
		b[1] = (byte)((i >> 8) & 0xFF);
		b[2] = (byte)((i >> 16) & 0xFF);
		b[3] = (byte)((i >> 24) & 0xFF);
		
		return b;
	}
	
	
	public static int readInt(byte[] data, int off) {
		int i0 = data[off++] & 0xFF;
		int i1 = (data[off++] & 0xFF) << 8;
		int i2 = (data[off++] & 0xFF) << 16;
		int i3 = (data[off++] & 0xFF) << 24;
		
		return (i3 | i2 | i1 | i0);
	}
	
	
	public static byte[] readByteArray(byte[] data, int off, int len) {
		byte[] b = new byte[len];
		
		for(int i = 0; i < len; ++i) {
			b[i] = data[off++];
		}
		
		return b;
	}
	
}
