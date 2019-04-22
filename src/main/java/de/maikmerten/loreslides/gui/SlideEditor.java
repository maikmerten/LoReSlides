package de.maikmerten.loreslides.gui;

import de.maikmerten.loreslides.Font;
import de.maikmerten.loreslides.Slide;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.Charset;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author maik
 */
public class SlideEditor extends JTextArea implements KeyListener {

	private Slide slide;
	private int zoom = 2;
	private double vertical_stretch = 1.0; // 1.0 for 640x480, 1.2 for 640x400
	private boolean showGrid = true;
	private boolean showCursor = true;
	private int cursor_col = 0;
	private int cursor_row = 0;
	private byte fgColor = (byte) 0xF;
	private byte bgColor = (byte) 0x0;
	
	private Font fallbackFont = new Font();

	private final LinkedList<Byte> undoBuffer = new LinkedList<>();

	public SlideEditor() {
		super();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				reactToMousePressed(evt);
			}
		});

		setPreferredSize(new Dimension(700, 450));

		this.addKeyListener(this);
		this.setFocusable(true);
	}

	private void reactToMousePressed(MouseEvent e) {
		if (slide.isGraphics()) {
			return;
		}

		int x = e.getX();
		int y = e.getY();

		int col = x / getCharWidth();
		int row = y / (int) (getCharHeight() * vertical_stretch);

		setCursorPos(col, row);
	}

	private void setCursorPos(int col, int row) {
		col = (col >= getTextCols()) ? getTextCols() - 1 : col;
		row = (row >= getTextRows()) ? getTextRows() - 1 : row;

		cursor_col = col < 0 ? 0 : col;
		cursor_row = row < 0 ? 0 : row;
		repaint();
	}

	public void setFgColor(byte b) {
		fgColor = (byte) (b & 0xF);
	}

	public void setBgColor(byte b) {
		bgColor = (byte) (b & 0xF);
	}

	private byte assembleColor() {
		int c = ((fgColor << 4) | bgColor) & 0xFF;
		return (byte) c;
	}

	@Override
	public void paintComponent(Graphics g) {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("paintComponent executed outside of event dispatch thread!");
		}

		this.setText("");

		super.paintComponent(g);

		if (slide != null) {
			if (slide.isGraphics()) {
				BufferedImage bimg = slide.getGraphicsImage();
				g.drawImage(bimg, 0, 0, this);
			} else {
				Font font = slide.getFont();
				int charwidth = getCharWidth();
				int charheight = (int) (getCharHeight() * vertical_stretch);

				int rows = getTextRows();
				int cols = getTextCols();

				for (int row = 0; row < rows; ++row) {
					for (int col = 0; col < cols; ++col) {
						byte b = slide.getTextBytes()[(row * cols) + col];
						byte c = slide.getColorBytes()[(row * cols) + col];
						BufferedImage cimg = font.getCharImg(b, c);

						g.drawImage(cimg, (col * charwidth), (row * charheight), charwidth, charheight, this);

						if (showGrid) {
							g.setColor(Color.DARK_GRAY);
							g.drawRect((col * charwidth), (row * charheight), charwidth, charheight);
						}

					}
				}

				if (showCursor) {
					g.setColor(Color.red);
					g.drawRect((cursor_col * charwidth), (cursor_row * charheight), charwidth, charheight);
				}
			}
		}

		Toolkit.getDefaultToolkit().sync();
	}

	public BufferedImage getFontImage(byte character) {
		if (slide != null && slide.getFont() != null) {
			return slide.getFont().getCharImg(character, (byte) 0xF0);
		}
		return fallbackFont.getCharImg(character, (byte) 0xF0);
	}

	private int getCharWidth() {
		Font f = slide.getFont();
		if (f == null) {
			return 0;
		}
		return f.getCharWidth() * zoom;
	}

	private int getCharHeight() {
		Font f = slide.getFont();
		if (f == null) {
			return 0;
		}
		return f.getCharHeight() * zoom;
	}

	private int getTextCols() {
		if (slide == null) {
			return 0;
		}
		return slide.getCols();
	}

	private int getTextRows() {
		if (slide == null) {
			return 0;
		}
		return slide.getRows();
	}

	public void setSlide(Slide s) {
		slide = s;
		repaint();
	}
	
	public void setSlideGraphics(File f) throws Exception {
		BufferedImage bimg = ImageIO.read(f);
		slide.setGraphics(bimg, 640, 480);
		repaint();
	}

	public void drawFrame() {
		if (slide == null) {
			return;
		}

		byte[] text = slide.getTextBytes();
		int cols = getTextCols();
		int rows = getTextRows();

		for (int i = 0; i < cols; ++i) {
			text[i] = (byte) 0xc4;
		}

		for (int i = text.length - cols; i < text.length; ++i) {
			text[i] = (byte) 0xc4;
		}

		for (int i = 0; i < text.length; i += cols) {
			text[i] = (byte) 0xb3;
		}

		for (int i = cols - 1; i < text.length; i += cols) {
			text[i] = (byte) 0xb3;
		}

		text[0] = (byte) 0xda;
		text[cols - 1] = (byte) 0xbf;
		text[text.length - cols] = (byte) 0xc0;
		text[text.length - 1] = (byte) 0xd9;

		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (slide.isGraphics()) {
			return;
		}

		String charstring = "" + e.getKeyChar();
		byte[] charbytes = charstring.getBytes(Charset.forName("cp850"));

		keyPressed(charbytes[0], e.getKeyCode());

		repaint();
	}

	private void keyPressed(byte charbyte, int keycode) {

		int cursor_idx = (cursor_row * getTextCols()) + cursor_col;
		byte[] textdata = slide.getTextBytes();
		byte[] colordata = slide.getColorBytes();

		int charnum = charbyte & 0xFF;

		System.out.println(keycode + " " + charnum);

		boolean left = keycode == 37;
		boolean up = keycode == 38;
		boolean right = keycode == 39;
		boolean down = keycode == 40;
		boolean insert = keycode == 155;
		boolean nummul = keycode == 106;
		boolean numplus = keycode == 107;
		boolean numminus = keycode == 109;
		boolean numdiv = keycode == 111;
		boolean f1 = keycode == 112;

		final int MODIFIER = 63;
		final int ENTER = 10;
		final int DELETE = 8;

		if (f1) {
			showGrid = !showGrid;
			return;
		}

		if (numplus) {
			addToTextByte(cursor_idx, 1);
			return;
		}

		if (numminus) {
			addToTextByte(cursor_idx, -1);
			return;
		}

		if (charnum != MODIFIER && charnum != ENTER && charnum != DELETE) {
			writeText(cursor_idx, charbyte, assembleColor());
			setCursorPos(cursor_col + 1, cursor_row);
		}

		if (charnum == ENTER) {
			setCursorPos(0, cursor_row + 1);
		}

		if (charnum == DELETE) {
			undoBuffer.addFirst(textdata[cursor_idx]);
			while (undoBuffer.size() > (100 * 1000)) {
				undoBuffer.removeLast();
			}

			writeText(cursor_idx, (byte) 0, assembleColor());

			setCursorPos(cursor_col - 1, cursor_row);
		}

		if (up) {
			setCursorPos(cursor_col, cursor_row - 1);
		}

		if (down) {
			setCursorPos(cursor_col, cursor_row + 1);
		}

		if (left) {
			setCursorPos(cursor_col - 1, cursor_row);
		}

		if (right) {
			setCursorPos(cursor_col + 1, cursor_row);
		}

		if (insert) {
			if (!undoBuffer.isEmpty()) {
				byte b = undoBuffer.removeFirst();
				writeText(cursor_idx, b, assembleColor());
				setCursorPos(cursor_col + 1, cursor_row);
			}
		}

	}

	private void addToTextByte(int idx, int off) {
		byte[] textdata = slide.getTextBytes();
		byte[] colordata = slide.getColorBytes();
		byte b = textdata[idx];
		writeText(idx, (byte) (b + off), assembleColor());
	}

	private void writeText(int idx, byte character, byte color) {
		byte[] textdata = slide.getTextBytes();
		byte[] colordata = slide.getColorBytes();
		textdata[idx] = character;
		colordata[idx] = color;
	}

	public void insertCharacter(byte character) {
		writeText((cursor_row * getTextCols()) + cursor_col, character, assembleColor());
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
