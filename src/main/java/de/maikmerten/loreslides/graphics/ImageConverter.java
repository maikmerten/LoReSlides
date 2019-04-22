package de.maikmerten.loreslides.graphics;

import de.maikmerten.loreslides.EGAColor;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author maik
 */
public class ImageConverter {

	private final int palette[];
	private double ditherStrength = 1.0;

	public ImageConverter() {
		palette = new int[16];
		for (int idx = 0; idx < palette.length; ++idx) {
			palette[idx] = EGAColor.getRGB(idx);
		}
	}

	private int findBestPaletteMatch(int rgb) {
		int idx = 0;
		int error = Integer.MAX_VALUE;
		for (int i = 0; i < palette.length; ++i) {
			int e = Color.rgbDistance(rgb, palette[i]);
			if (e < error) {
				idx = i;
				error = e;
			}
		}
		return idx;
	}

	private int[] imgToPalette(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();

		int[] result = new int[width * height];
		int[] delta = new int[3];

		int i = 0;
		for (int y = 0; y < img.getHeight(); ++y) {
			for (int x = 0; x < img.getWidth(); ++x) {
				int rgb = img.getRGB(x, y);
				int idx = findBestPaletteMatch(rgb);
				result[i++] = idx;
				int rgb2 = palette[idx];

				Color.rgbDelta(rgb, rgb2, delta);
				Color.floydSteinberg(img, delta, x, y, ditherStrength);
			}
		}

		return result;
	}

	public BufferedImage decode(int[] indexed, int width, int height) {
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int idx = (y * width) + x;
				int rgb = palette[indexed[idx]];
				result.setRGB(x, y, rgb);
			}
		}

		return result;
	}

	private byte[] packToBytes(int[] indexed) {
		byte[] packed = new byte[indexed.length / 2];

		for (int i = 0; i < packed.length; ++i) {
			int idx1 = indexed[2 * i];
			int idx2 = indexed[2 * i + 1];
			packed[i] = (byte) (((idx1 & 0x0F) << 4) | (idx2 & 0x0F));
		}

		return packed;
	}
	
	private int[] unpackBytes(byte[] input) {
		int[] result = new int[input.length * 2];
		for(int i = 0; i < input.length; ++i) {
			byte b = input[i];
			result[i * 2] = (b >> 4) & 0xF;
			result[i * 2 + 1] = b & 0xF;
		}
		return result;
	}
	
	

	public byte[] convertImage(BufferedImage input, int width, int height) {
		// create scaled copy
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.drawImage(input, 0, 0, width, height, null);

		return packToBytes(imgToPalette(img));
	}
	
	public BufferedImage decodeImage(byte[] input, int width, int height) {
		int[] unpacked = unpackBytes(input);
		return decode(unpacked, width, height);
	}

}
