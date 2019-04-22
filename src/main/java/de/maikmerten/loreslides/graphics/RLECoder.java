package de.maikmerten.loreslides.graphics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 *
 * @author maik
 */
public class RLECoder {

	private static byte findEscape(byte[] input) {
		int[] freq = new int[256];

		for (byte b : input) {
			freq[b & 0xFF]++;
		}

		int min = Integer.MAX_VALUE;
		byte result = 0;
		for (int i = 0; i < freq.length; ++i) {
			if (freq[i] < min) {
				min = freq[i];
				result = (byte) (i & 0xFF);
			}
		}

		System.out.println("found escape " + result + " with frequency " + min);

		return result;
	}

	private static void assertArrays(byte[] b1, byte[] b2) {
		if (b1.length != b2.length) {
			throw new RuntimeException("arrays have mismatching sizes: " + b1.length + " and " + b2.length);
		}

		for (int i = 0; i < b1.length; ++i) {
			if (b1[i] != b2[i]) {
				throw new RuntimeException("array contents don't match");
			}
		}

	}

	private static void writeRun(int len, byte escape, byte b, ByteArrayOutputStream baos) {
		if (len > 2 || b == escape) {
			baos.write(escape);
			baos.write((byte) (len & 0xFF));
			baos.write(b);
		} else {
			for (int i = 0; i <= len; ++i) {
				baos.write(b);
			}
		}
	}

	public static byte[] encode(byte[] input) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte escape = findEscape(input);
		baos.write(escape);

		int len = 0; // repeat counter
		byte curr = input[0];

		for (int i = 1; i < input.length; ++i) {
			if (input[i] == curr && len < 255) {
				len++;
			} else {
				writeRun(len, escape, curr, baos);
				len = 0;
				curr = input[i];
			}
		}
		// write out last run
		writeRun(len, escape, curr, baos);

		byte[] result = null;
		try {
			baos.close();
			result = baos.toByteArray();
			System.out.println("compressed " + input.length + " bytes to " + result.length + " bytes");

			byte[] decompressed = decode(new ByteArrayInputStream(result));
			assertArrays(input, decompressed);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	public static byte[] decode(InputStream is) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			byte[] buf = new byte[1];

			is.read(buf);
			byte escape = buf[0];

			while (is.available() > 0) {
				is.read(buf);
				if (buf[0] == escape) {
					is.read(buf); // read len
					int len = buf[0] & 0xFF;
					is.read(buf); // read byte
					byte b = buf[0];

					while (len >= 0) {
						baos.write(b);
						len--;
					}
				} else {
					baos.write(buf[0]);
				}
			}
			baos.close();

			byte[] result = baos.toByteArray();
			System.out.println("decompressed " + result.length + " bytes");
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		byte[] input = new byte[]{
			0, 1, 1, 1, 1, 1, 1, 2, 3, 4, 5, 5, 5
		};

		byte[] out = encode(input);
	}

}
