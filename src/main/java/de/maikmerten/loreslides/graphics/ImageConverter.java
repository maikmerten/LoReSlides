package de.maikmerten.loreslides.graphics;

import de.maikmerten.loreslides.EGAColor;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author maik
 */
public class ImageConverter {

    private final int palette[] = new int[16];
    private double ditherStrength = 1.0;

    public ImageConverter() {
        loadDefaultPalette();
    }

    public final void loadDefaultPalette() {
        for (int idx = 0; idx < palette.length; ++idx) {
            palette[idx] = EGAColor.getRGB(idx);
        }
    }

    public void setPaletteEntry(int index, int rgb) {
        if (index < 0 || index >= palette.length) {
            return;
        }
        palette[index] = rgb;
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

    private BufferedImage decode(int[] indexed, int width, int height) {
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
        byte[] packed = new byte[(indexed.length / 2) + 48];

        int packed_idx = 0;

        // write 48 bytes of palette data
        for (int i = 0; i < 16; i++) {
            int rgb = palette[i];
            packed[packed_idx++] = (byte) ((rgb >> 16) & 0xFF);
            packed[packed_idx++] = (byte) ((rgb >> 8) & 0xFF);
            packed[packed_idx++] = (byte) (rgb & 0xFF);
        }

        // write pixel data
        for (int i = 0; i < indexed.length;) {
            int idx1 = indexed[i++];
            int idx2 = indexed[i++];
            packed[packed_idx++] = (byte) (((idx1 & 0x0F) << 4) | (idx2 & 0x0F));
        }

        return packed;
    }

    private int[] unpackBytes(byte[] input) {

        // first 48 bytes: 16 color palette
        for (int i = 0; i < 16; ++i) {
            byte r = input[(i * 3)];
            byte g = input[(i * 3) + 1];
            byte b = input[(i * 3) + 2];

            int rgb = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
            setPaletteEntry(i, rgb);
        }

        // rest: pixel data
        int pixels = (input.length - 48) * 2;

        int[] result = new int[pixels];
        int result_idx = 0;
        for (int i = 48; i < input.length; ++i) {
            byte b = input[i];
            result[result_idx++] = (b >> 4) & 0xF;
            result[result_idx++] = b & 0xF;
        }
        return result;
    }

    public byte[] convertImage(BufferedImage input, int width, int height) {
        loadDefaultPalette();

        // check if image already has a color palette of up to 16 colors
        if (input.getColorModel() instanceof IndexColorModel) {
            // count used palette colors
            Set<Integer> colors = new HashSet<>();
            for (int x = 0; x < input.getWidth(); ++x) {
                for (int y = 0; y < input.getHeight(); ++y) {
                    int rgb = input.getRGB(x, y);
                    colors.add(rgb & 0xFFFFFF);
                }
            }

            // this is an image with up to 16 colors, we can use this palette
            if (colors.size() <= 16) {
                int i = 0;
                for (int rgb : colors) {
                    setPaletteEntry(i++, rgb);
                }
            }
        }

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
