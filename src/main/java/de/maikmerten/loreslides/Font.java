package de.maikmerten.loreslides;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author maik
 */
public class Font {

	int id = 0;
	int width = 8;
	int height = 8;
	byte[] fontbytes;

	private Map<Integer, BufferedImage> charCache = new HashMap<>();
	
	public Font() {
		InputStream is = Font.class.getResourceAsStream("/fonts/cp850.f08");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			byte[] buf = new byte[1024];
			int read = is.read(buf);
			while (read > 0) {
				baos.write(buf, 0, read);
				read = is.read(buf);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		setFontBytes(baos.toByteArray());
	}
	
	public Font(byte[] fontbytes) {
		setFontBytes(fontbytes);
	}
	
	private void setFontBytes(byte[] fontbytes) {
		if(fontbytes.length != 2048) {
			throw new IllegalArgumentException("Font data is not 2048 bytes long");
		}
		charCache.clear();
		this.fontbytes = fontbytes;
	}
	
	private BufferedImage generateCharImg(byte c, byte renderColor) {
		BufferedImage cimg = new BufferedImage(getCharWidth(), getCharHeight(), BufferedImage.TYPE_INT_RGB);
		
		int fgred = (renderColor & 0x40) != 0 ? 255 : 0;
		int fggre = (renderColor & 0x20) != 0 ? 255 : 0;
		int fgblu = (renderColor & 0x10) != 0 ? 255 : 0;
		
		int bgred = (renderColor & 0x04) != 0 ? 255 : 0;
		int bggre = (renderColor & 0x02) != 0 ? 255 : 0;
		int bgblu = (renderColor & 0x01) != 0 ? 255 : 0;
		
		Color fgcolor = new Color(fgred, fggre, fgblu);
		Color bgcolor = new Color(bgred, bggre, bgblu);
			
		int off = (c & 0xFF) * getCharHeight();
		for(int y = 0; y < getCharHeight(); ++y) {
			byte b = fontbytes[off + y];
			int mask = 0x80;
			for(int x = 0; x < getCharWidth(); ++x) {
				Color color = ((b & mask) != 0) ? fgcolor : bgcolor;
				Graphics g = cimg.getGraphics();
				g.setColor(color);
				g.drawRect(x, y, 1, 1);
				mask >>= 1;
			}
		}
		return cimg;
	}
	
	
	public BufferedImage getCharImg(byte character, byte color) {
		
		int cachekey = (character << 8) | (color & 0xFF);
		
		BufferedImage cimg = charCache.get(cachekey);
		if(cimg == null) {
			cimg = generateCharImg(character, color);
			charCache.put(cachekey, cimg);
		}
		
		return cimg;
	}
	
	
	public int getCharWidth() {
		return width;
	}
	
	public void setCharWidth(int width) {
		this.width = width;
	}
	
	public int getCharHeight() {
		return height;
	}
	
	public void setCharHeight(int height) {
		this.height = height;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		baos.write(ByteUtil.writeInt(id));
		
		baos.write((byte)(width & 0xFF));
		baos.write((byte)(height & 0xFF));
		
		baos.write(fontbytes);
		
		
		return baos.toByteArray();
	}
	
	public static Font fromBytes(byte[] data, int off) {
		Font f = new Font();
		
		int id = ByteUtil.readInt(data, off);
		off += 4;
		
		int width = data[off++] & 0xFF;
		int height = data[off++] & 0xFF;
		
		int fontlen = (width / 8) * height * 256;
		byte[] fontbytes = ByteUtil.readByteArray(data, off, fontlen);
		
		f.setId(id);
		f.setCharWidth(width);
		f.setCharHeight(height);
		f.setFontBytes(fontbytes);
		
		return f;
	}
	
}
