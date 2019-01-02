package de.maikmerten.loreslides;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * @author maik
 */
public class DirEntry {
	
	public static byte TYPE_FONT = (byte)(0x00);
	public static byte TYPE_SLIDE = (byte)(0x01);
	
	byte type;
	int id;
	int off;
	
	public DirEntry(byte type, int id, int off) {
		this.type = type;
		this.id = id;
		this.off = off;
	}
	
	public void setOffset(int off) {
		this.off = off;
	}
	
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		baos.write(ByteUtil.writeInt(id));
		baos.write(ByteUtil.writeInt(off));
		baos.write(type);
		
		// write another 3 bytes for alignment
		baos.write((byte)0);
		baos.write((byte)0);
		baos.write((byte)0);
		
		return baos.toByteArray();
	}
	
	public static DirEntry fromBytes(byte[] data, int off) {
		
		int id = ByteUtil.readInt(data, off);
		off += 4;
		
		int offset = ByteUtil.readInt(data, off);
		off += 4;

		byte type = data[off++];
		
		return new DirEntry(type, id, offset);
	}
	
	public static int entryLength() {
		return 12;
	}
	
}
