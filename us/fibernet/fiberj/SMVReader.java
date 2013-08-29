package us.fibernet.fiberj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName:SMVReader
 * @Description:utility class to read SMV image file
 * @author kate.b.wu@gmail.com
 * 
 */
public final class SMVReader {

	private static String header;
	private static int[][] image;

	private SMVReader() {}

	/**
	 * read SMV file and return int[][]
	 * 
	 */
	public static int[][] readSMV(String fileName)
			throws IllegalArgumentException {

		Map<String, String> parameters = readSMVHeader(fileName);
		String headSize = parameters.get("HEADER_BYTES");
		String size1 = parameters.get("SIZE1");
		String size2 = parameters.get("SIZE2");
		String type = parameters.get("TYPE");
		int n = Integer.parseInt(headSize);
		int x = Integer.parseInt(size1);
		int y = Integer.parseInt(size2);

		// check file TYPE, throw exception if not unsigned short
		if (type.equalsIgnoreCase("unsigned_short")) {
			image = readSMVData(fileName, n, x, y);
			return image;
		} else {
			throw new IllegalArgumentException("TYPE is not unsigned_short");
		}

	}

	/**
	 * private helper method to read the binary data in SMV file with given
	 * header size, image width&height
	 * 
	 */
	private static int[][] readSMVData(String fileName, int n, int x, int y) {

		EndianInputStream input = null;
		image = null;

		try {
			image = new int[x][y];
			File file = new File(fileName);
			input = new EndianInputStream((new FileInputStream(file)), false);
			input.skipBytes(n);
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {

					image[i][j] = (int) (input.readShortCorrect());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return image;
	}

	/**
	 * return the header information as string
	 * 
	 */
	public static String printSMVHeader(String fileName) {
		Map<String, String> parameters = readSMVHeader(fileName);
		header = parameters.toString();
		return header;
	}

	/**
	 * read the parameters in the SMV image header, and set the parameters in
	 * Pattern accordingly
	 */
	public static void readSMVPattern(String fileName, Pattern p) {
		Map<String, String> parameters = readSMVHeader(fileName);
		String pSize = parameters.get("PIXEL_SIZE");
		String dist = parameters.get("DISTANCE");
		String wlength = parameters.get("WAVELENGTH");
		int[][] data = readSMV(fileName);

		p.setPixelSize(Double.parseDouble(pSize));
		p.setWavelen(Double.parseDouble(wlength));
		p.setSdd(Double.parseDouble(dist));
		p.setData(data);

	}

	/**
	 * helper method, read SMV image header and return a HashMap which store the
	 * parameter name and value
	 */
	private static Map<String, String> readSMVHeader(String fileName) {

		Map<String, String> hashMap = new HashMap<String, String>();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String s1 = reader.readLine();// read the beginning "{" and pass

			while ((s1 = reader.readLine()) != null) {

				// break out of loop when read "}"
				if (s1.contains("}")) {
					break;
				}

				String[] s = s1.split("[=;]");

				if (s.length > 1) {

					hashMap.put(s[0], s[1]);

				} else if (!s[0].isEmpty()) {
					// make sure there is no empty string
					hashMap.put(s[0], "");

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return hashMap;
	}

}
