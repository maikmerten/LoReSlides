package de.maikmerten.loreslides.graphics;

import java.awt.image.BufferedImage;

/**
 *
 * @author maik
 */
public class Color {

    public static int rgbDistance(int rgb1, int rgb2) {
        int sum = 0;

        int r1 = (rgb1 & 0xFF0000) >> 16;
        int r2 = (rgb2 & 0xFF0000) >> 16;
        int g1 = (rgb1 & 0xFF00) >> 8;
        int g2 = (rgb2 & 0xFF00) >> 8;
        int b1 = (rgb1 & 0xFF);
        int b2 = (rgb2 & 0xFF);

        int rdiff = r1 - r2;
        int gdiff = g1 - g2;
        int bdiff = b1 - b2;

        sum += (rdiff * rdiff);
        sum += (gdiff * gdiff);
        sum += (bdiff * bdiff);

        return sum;
    }

    public static int assembleRGB(int r, int g, int b) {
        r = Math.max(0, r);
        r = Math.min(255, r);
        g = Math.max(0, g);
        g = Math.min(255, g);
        b = Math.max(0, b);
        b = Math.min(255, b);
        return (r << 16) | (g << 8) | b;
    }

    public static void rgbDelta(int rgb1, int rgb2, int[] delta) {
        int r1 = (rgb1 & 0xFF0000) >> 16;
        int r2 = (rgb2 & 0xFF0000) >> 16;
        int g1 = (rgb1 & 0xFF00) >> 8;
        int g2 = (rgb2 & 0xFF00) >> 8;
        int b1 = (rgb1 & 0xFF);
        int b2 = (rgb2 & 0xFF);

        int dr = r1 - r2;
        int dg = g1 - g2;
        int db = b1 - b2;

        delta[0] = dr;
        delta[1] = dg;
        delta[2] = db;
    }

    private static int rgbAddDelta(int rgb, int[] delta, double f) {
        int r1 = (rgb & 0xFF0000) >> 16;
        int g1 = (rgb & 0xFF00) >> 8;
        int b1 = (rgb & 0xFF);

        int r2 = (int) (delta[0] * f);
        int g2 = (int) (delta[1] * f);
        int b2 = (int) (delta[2] * f);

        r1 = Math.max(0, Math.min(255, r1 + r2));
        g1 = Math.max(0, Math.min(255, g1 + g2));
        b1 = Math.max(0, Math.min(255, b1 + b2));

        return assembleRGB(r1, g1, b1);
    }

    private static void addDelta(BufferedImage img, int[] delta, int x, int y, double weight) {
        int rgb = img.getRGB(x, y);
        rgb = rgbAddDelta(rgb, delta, weight);
        img.setRGB(x, y, rgb);
    }

    public static void floydSteinberg(BufferedImage img, int[] delta, int x, int y, double strength) {
        int width = img.getWidth();
        int height = img.getHeight();

        if (x + 1 < width) {
            addDelta(img, delta, x + 1, y, (7.0 / 16.0) * strength);
        }

        if (y + 1 < height) {
            if (x - 1 >= 0) {
                addDelta(img, delta, x - 1, y + 1, (3.0 / 16.0) * strength);
            }
            addDelta(img, delta, x, y + 1, (5.0 / 16.0) * strength);
            if (x + 1 < width) {
                addDelta(img, delta, x + 1, y + 1, (1.0 / 16.0) * strength);
            }
        }
    }

}
