package de.maikmerten.loreslides;

import de.maikmerten.loreslides.graphics.ImageConverter;
import de.maikmerten.loreslides.graphics.RLECoder;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 * @author maik
 */
public class Slide {

	private SlideHolder sh;

	private int fontId = 0;
	private int cols = 0;
	private int rows = 0;
	private int flags = 0;
	private int flagdata = 0;
	private byte[] text = null;
	private byte[] color = null;

	private byte[] graphicsdata = null;
	private int graphicswidth = 0;
	private int graphicsheight = 0;
	BufferedImage bimg = null;

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

	public Slide(SlideHolder sh, int graphicswidth, int graphicsheight, byte[] graphicsdata) {
		this.sh = sh;

		this.graphicswidth = graphicswidth;
		this.graphicsheight = graphicsheight;
		this.graphicsdata = graphicsdata;
	}

	private void initColor() {
		for (int i = 0; i < color.length; ++i) {
			color[i] = (byte) (0x70); // white foreground, black background
		}
	}

	public boolean isGraphics() {
		return graphicsdata != null;
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
		if (isGraphics()) {
			return null;
		}

		return sh.getFontById(fontId);
	}

	public int getGraphicsWidth() {
		return graphicswidth;
	}

	public int getGraphicsHeight() {
		return graphicsheight;
	}

	public void setGraphics(BufferedImage bimg, int width, int height) {
		ImageConverter conv = new ImageConverter();
		this.graphicsdata = conv.convertImage(bimg, width, height);
		this.graphicswidth = width;
		this.graphicsheight = height;
		this.bimg = null;
	}

	public BufferedImage getGraphicsImage() {
		if(this.bimg == null) {
			ImageConverter conv = new ImageConverter();
			this.bimg = conv.decodeImage(graphicsdata, graphicswidth, graphicsheight);
		}
		return this.bimg;
	}

	public byte[] toBytes() throws IOException {
		if (isGraphics()) {
			return graphicsToBytes();
		}
		return slideToBytes();
	}

	private byte[] slideToBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		baos.write(ByteUtil.writeInt(fontId));
		baos.write(ByteUtil.writeInt(cols));
		baos.write(ByteUtil.writeInt(rows));
		baos.write(ByteUtil.writeInt(flags));
		baos.write(ByteUtil.writeInt(flagdata));
                
                byte[] compressedText = RLECoder.encode(text);
                byte[] compressedColor = RLECoder.encode(color);
                baos.write(ByteUtil.writeInt(compressedText.length));
                baos.write(ByteUtil.writeInt(compressedColor.length));
                baos.write(compressedText);
                baos.write(compressedColor);

		return baos.toByteArray();
	}

	private byte[] graphicsToBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] compressed = RLECoder.encode(graphicsdata);
		
		baos.write(ByteUtil.writeInt(graphicswidth));
		baos.write(ByteUtil.writeInt(graphicsheight));
		baos.write(ByteUtil.writeInt(compressed.length));
		baos.write(compressed);

		return baos.toByteArray();
	}

	public static Slide fromBytes(byte[] data, int off, SlideHolder sh, int type) {
		if (type == DirEntry.TYPE_SLIDE) {
			return slideFromBytes(data, off, sh);
		}
		return graphicsFromBytes(data, off, sh);
	}

	private static Slide graphicsFromBytes(byte[] data, int off, SlideHolder sh) {
		int width = ByteUtil.readInt(data, off);
		off += 4;

		int height = ByteUtil.readInt(data, off);
		off += 4;
		
		int compressedlen = ByteUtil.readInt(data, off);
		off += 4;

		byte[] graphicsdata = ByteUtil.readByteArray(data, off, compressedlen);
		graphicsdata = RLECoder.decode(new ByteArrayInputStream(graphicsdata));

		return new Slide(sh, width, height, graphicsdata);
	}

	private static Slide slideFromBytes(byte[] data, int off, SlideHolder sh) {

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
                
                int textLen = ByteUtil.readInt(data, off);
                off += 4;
                
                int colorLen = ByteUtil.readInt(data, off);
                off += 4;

		byte[] text = ByteUtil.readByteArray(data, off, textLen);
                off += textLen;
                
                byte[] color = ByteUtil.readByteArray(data, off, colorLen);
                
                text = RLECoder.decode(text);
                color = RLECoder.decode(color);
                

		Slide s = new Slide(sh, fontId, cols, rows);
		s.flags = flags;
		s.flagdata = flagdata;
		s.text = text;
		s.color = color;

		return s;
	}

	@Override
	public String toString() {
		if (text == null) {
			return "Graphics";
		}

		StringBuilder sb = new StringBuilder();
		byte[] buf = new byte[1];

		int count = 0;
		for (int i = 0; i < text.length; ++i) {
			byte b = text[i];

			int charnum = b & 0xFF;

			if (b == 0 && count >= 3) {
				break;
			}

			if ((b != 0 && charnum >= 0x20 && charnum <= 0xAF) || charnum == 0xE1) {
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
