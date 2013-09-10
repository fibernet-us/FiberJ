package us.fibernet.fiberj;

import java.awt.image.BufferedImage;

import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.lang.Math;

import javax.imageio.ImageIO;

//http://opencv-srf.blogspot.com/2010/09/rotating-images.html

//http://eab.abime.net/coders-general/29492-c-image-rotation-algorithm-problem.html

/**
 * 
 * @author Juan Ren Image rotation function
 * 
 */
public class ImageMatrixRotation {

	// Image 2D array rotation and scale
	public static double[][] MatrixRotation(double[][] ImageMatrix, double degree,
			double defaultcolor) {

		return MatrixRotation(ImageMatrix, degree, defaultcolor, 2, 2, 0);
	}

	public static double[][] MatrixRotation(double[][] ImageMatrix, double degree,
			double defaultcolor, int method) {

		return MatrixRotation(ImageMatrix, degree, defaultcolor, 2, 2, method);
	}

	public static double[][] MatrixRotation(double[][] ImageMatrix, double degree,
			double defaultcolor, int centerRow, int centerCol) {

		return MatrixRotation(ImageMatrix, degree, defaultcolor, centerRow, centerCol, 0);
	}

	// use int for test image, because the output of ImageReader.readTif is int
	public static int[][] MatrixRotation(int[][] ImageMatrix, double degree, int defaultcolor,
			int method) {

		return MatrixRotation(ImageMatrix, degree, defaultcolor, 2, 2, method);
	}

	public static int[][] MatrixRotation(int[][] ImageMatrix, double degree, int defaultcolor,
			int centerRow, int centerCol, int method) {
		int width = ImageMatrix.length;
		int heigth = ImageMatrix[0].length;

		double[][] matrix = new double[width][heigth];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < heigth; y++) {
				matrix[x][y] = ImageMatrix[x][y];
			}
		}

		double dcolor = defaultcolor;
		double[][] newmatrix = MatrixRotation(matrix, degree, dcolor, centerRow, centerCol, method);

		int[][] newImageMatrix = new int[newmatrix.length][newmatrix[0].length];
		for (int x = 0; x < newmatrix.length; x++) {
			for (int y = 0; y < newmatrix[0].length; y++) {
				newImageMatrix[x][y] = (int) Math.round(newmatrix[x][y]);
			}
		}

		return newImageMatrix;
	}

	// two methods: nearest interpolation and bilinear interpolation
	public static double[][] MatrixRotation(double[][] ImageMatrix, double degree,
			double defaultcolor, int centerRow, int centerCol, String method) {

		int methodway = 0;
		if (method.equals("bilinear")) {
			methodway = 1;
		}
		return MatrixRotation(ImageMatrix, degree, defaultcolor, centerRow, centerCol, methodway);
	}

	// two methods: nearest interpolation 0 (default) and bilinear interpolation 1; maybe add more
	// methods;
	public static double[][] MatrixRotation(double[][] ImageMatrix, double degree,
			double defaultcolor, int centerRow, int centerCol, int method) {

		double angle = degree * Math.PI / 180;
		double sinma = Math.sin(-angle);
		double cosma = Math.cos(-angle);

		int width = ImageMatrix.length;
		int heigth = ImageMatrix[0].length;

		double halfwidth = centerRow;
		double halfheigth = centerCol;

		// half row and half column
		if (centerRow == 2 && centerCol == 2) {
			halfwidth = width / 2;
			halfheigth = heigth / 2;
		}

		// calculate new image's dimension, from four corner point
		double x1 = cosma * (0 - halfwidth) - sinma * (0 - halfheigth) + halfwidth;

		double y1 = sinma * (0 - halfwidth) + cosma * (0 - halfheigth) + halfheigth;

		double x2 = cosma * (0 - halfwidth) - sinma * (heigth - halfheigth - 1) + halfwidth;
		double y2 = sinma * (0 - halfwidth) + cosma * (heigth - halfheigth - 1) + halfheigth;

		double x3 = cosma * (width - halfwidth - 1) - sinma * (0 - halfheigth) + halfwidth;
		double y3 = sinma * (width - halfwidth - 1) + cosma * (0 - halfheigth) + halfheigth;

		double x4 = cosma * (width - halfwidth - 1) - sinma * (heigth - halfheigth - 1) + halfwidth;
		double y4 = sinma * (width - halfwidth - 1) + cosma * (heigth - halfheigth - 1) + halfheigth;

		double MaxX = Math.max(Math.max(x1, x2), Math.max(x3, x4));
		double MinX = Math.min(Math.min(x1, x2), Math.min(x3, x4));

		double MaxY = Math.max(Math.max(y1, y2), Math.max(y3, y4));
		double MinY = Math.min(Math.min(y1, y2), Math.min(y3, y4));

		// System.out.println(x1 + " " + y1);
		// System.out.println(x2 + " " + y2);
		// System.out.println(x3 + " " + y3);
		// System.out.println(x4 + " " + y4);

		// System.out.println(MaxX + " " + MinX + ", " + MaxY + " " + MinY);

		int newWidth = (int) Math.round(MaxX - MinX + 1);
		int newHeigth = (int) Math.round(MaxY - MinY + 1);

		/*
		 * // calculate scale double scale = 1; int maxOldlength = Math.max(width, heigth); int
		 * maxNewlength = Math.max(newWidth, newHeigth);
		 * 
		 * if (maxNewlength > maxOldlength) { scale = Math.max((double) newWidth / width, (double)
		 * newHeigth / heigth); // System.out.println(scale); // System.out.println(newWidth + " " +
		 * width + ", " + newHeigth + // " " + heigth); scale = 1; newWidth = (int)
		 * Math.round(newWidth / scale + 1); newHeigth = (int) Math.round(newHeigth / scale + 1);
		 * 
		 * }
		 */

		// image matrix after rotation
		double[][] newImageMatrix = new double[newWidth][newHeigth];

		for (int x = 0; x < newWidth; x++) {
			for (int y = 0; y < newHeigth; y++) {
				newImageMatrix[x][y] = defaultcolor;
			}
		}

		for (int x = 0; x < newWidth; x++) {
			for (int y = 0; y < newHeigth; y++) {

				double xt = x - halfwidth + MinX;
				double yt = y - halfheigth + MinY;

				double xs = (((cosma * xt + sinma * yt) + halfwidth));
				double ys = (((-sinma * xt + cosma * yt) + halfheigth));

				int xx = (int) Math.round(xs);
				int yy = (int) Math.round(ys);

				if (method == 0) {// nearest interpolation
					if (xx >= 0 && yy >= 0 && xx < width && yy < heigth) {
						newImageMatrix[x][y] = ImageMatrix[xx][yy];
					}
				} else if (method == 1) {// bilinear interpolation begin
					int xx1 = xx - 1;
					int yy1 = yy - 1;
					int xx2 = xx;
					int yy2 = yy;

					if (xs > xx) {
						xx1 = xx;
						xx2 = xx + 1;
					}

					if (ys > yy) {
						yy1 = yy;
						yy2 = yy + 1;
					}

					if (xx1 >= 0 && yy1 >= 0 && xx2 < width && yy2 < heigth) {
						double Q11 = ImageMatrix[xx1][yy1];
						double Q12 = ImageMatrix[xx1][yy2];
						double Q21 = ImageMatrix[xx2][yy1];
						double Q22 = ImageMatrix[xx2][yy2];

						double newvalue = bilinearinterpolation(xx1, xx2, xs, yy1, yy2, ys, Q11,
								Q12, Q21, Q22);
						newImageMatrix[x][y] = newvalue;
					} else if (xx >= 0 && yy >= 0 && xx < width && yy < heigth) {
						newImageMatrix[x][y] = ImageMatrix[xx][yy];
					}
				}// bilinear interpolation end

			}
		}

		return newImageMatrix;
	}

	// bilinear interpolation
	// http://supercomputingblog.com/graphics/coding-bilinear-interpolation/
	private static double bilinearinterpolation(int x1, int x2, double x, int y1, int y2, double y,
			double Q11, double Q12, double Q21, double Q22) {
		double R1 = ((x2 - x) / (x2 - x1)) * Q11 + ((x - x1) / (x2 - x1)) * Q21;
		double R2 = ((x2 - x) / (x2 - x1)) * Q12 + ((x - x1) / (x2 - x1)) * Q22;

		double P = ((y2 - y) / (y2 - y1)) * R1 + ((y - y1) / (y2 - y1)) * R2;

		// System.out.println(R1 + " ,"+R2+" ,"+P);
		return P;
	}

	// default pixel is 0
	public static double[][] MatrixRotation(double[][] ImageMatrix, double degree) {
		return MatrixRotation(ImageMatrix, degree, 0);
	}

	// save image for test
	public static void displayImage(int[][] imageArray) throws IOException {

		// Dimensions of the image
		int heigth = imageArray.length;

		int width = imageArray[0].length;
		System.out.println(heigth + " " + width);

		// Let's create a BufferedImage for a gray level image.
		BufferedImage im = new BufferedImage(width, heigth, BufferedImage.TYPE_BYTE_GRAY);
		// We need its raster to set the pixels' values.
		WritableRaster raster = im.getRaster();
		// Put the pixels on the raster, using values between 0 and 255.
		for (int h = 0; h < heigth; h++)
			for (int w = 0; w < width; w++) {
				// int value = 127 + (int) (128 * Math.sin(w / 32.) * Math
				// .sin(h / 32.)); // Weird sin pattern.

				int value = imageArray[h][w];
				raster.setSample(w, h, 0, value);
			}
		// Store the image using the JPG format.
		ImageIO.write(im, "JPG", new File("/home/Downloads/sss3.jpg"));

	}

	// test
	public static void main(String[] arg0) {
		double dd = 160;
		String[] testfile = { "/home/Downloads/MARBIBM.tif" };
		// System.out.println(testfile);

		int[][] inputimage = ImageReader.readPattern(testfile);

		int[][] rotatimage = MatrixRotation(inputimage, dd, 0, 100, 100, 0);
		// int[][] rotatimage = MatrixRotation(inputimage,dd,0);
		try {
			displayImage(rotatimage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
