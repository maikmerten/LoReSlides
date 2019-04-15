package de.maikmerten.loreslides;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 * @author maik
 */
public class Slide {
	
	private SlideHolder sh;

	private int fontId;
	private final int cols;
	private final int rows;
	private int flags = 0;
	private int flagdata = 0;
	private byte[] text;
	private byte[] color;
	
	private final Charset CP850 = Charset.forName("cp850");
	
	public Slide(SlideHolder sh, int fontId, int textcols, int textrows) {
		this.sh = sh;
		this.fontId = fontId;
		this.cols = textcols;
		this.rows = textrows;
		
		this.text = new byte[textcols * textrows];
		this.color = new byte[textcols * textrows];
		initColor();
	}
	
	
	
	private void initColor() {
		for(int i = 0; i < color.length; ++i) {
			color[i] = (byte)(0x70); // white foreground, black background
		}
	}
	
	public void fillWithTestData() {
		int i = 0;
		for(int y = 0; (y < 16) && (y < rows); ++y) {
			for(int x = 0; (x < 16) && (x < cols); ++x) {
				text[(y*cols) + x] = (byte)(i++ & 0xFF);
			}
		}
	}
	
	
	
	public byte[] getTextBytes() {
		return text;
	}
	
	public byte[] getColorBytes() {
		return color;
	}
	
	public int getCols() {
		return cols;
	}
	
	public int getRows() {
		return rows;
	}
	
	public void setFontId(int fontId) {
		this.fontId = fontId;
	}
	
	public Font getFont() {
		return sh.getFontById(fontId);
	}
	
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		baos.write(ByteUtil.writeInt(fontId));
		baos.write(ByteUtil.writeInt(cols));
		baos.write(ByteUtil.writeInt(rows));
		baos.write(ByteUtil.writeInt(flags));
		baos.write(ByteUtil.writeInt(flagdata));
                
                for(int i = 0; i < text.length; ++i) {
                    baos.write(text[i]);
                    baos.write(color[i]);
                }
		
		return baos.toByteArray();
	}
	
	public static Slide fromBytes(byte[] data, int off, SlideHolder sh) {
		
		int fontId = ByteUtil.readInt(data, off);
		off += 4;
		
		int cols = ByteUtil.readInt(data, off);
		off += 4;
		
		int rows = ByteUtil.readInt(data, off);
		off += 4;
		
		int flags = ByteUtil.readInt(data, off);
		off += 4;
		
		int flagdata = ByteUtil.readInt(data, off);
		off += 4;
		
		int len = cols * rows;
                byte[] slidedata = ByteUtil.readByteArray(data, off, len * 2);
                
		byte[] text = new byte[len];
		byte[] color = new byte[len];
                
                for(int i = 0; i < len; ++i) {
                    text[i] = slidedata[i * 2];
                    color[i] = slidedata[(i * 2) + 1];
                }
		
		Slide s = new Slide(sh, fontId, cols, rows);
		s.flags = flags;
		s.flagdata = flagdata;
		s.text = text;
		s.color = color;
		
		return s;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		byte[] buf = new byte[1];
		
		int count = 0;
		for(int i = 0; i < text.length; ++i) {
			byte b = text[i];
			
			int charnum = b & 0xFF;
			
			if(b == 0 && count >= 3) {
				break;
			}
			
			if((b != 0 && charnum >= 0x20 && charnum <= 0xAF) || charnum == 0xE1) {
				buf[0] = b;
				String tmpstring = new String(buf, CP850);
				sb.append(tmpstring);
				count++;
			}
		}
		
		String s = sb.toString();
		s = s.trim();
		return (s.length() > 0 ? s : "untitled slide");
	}
	
}
