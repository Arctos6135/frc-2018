package org.usfirst.frc.team6135.robot.vision;

import org.opencv.core.Mat;

/**
 * A class that stores an image as a byte array, with methods to access pixels
 * One byte is one pixel
 * This is a very abstract class used for convenience in processing, so data such as colour space are not stored
 */
public class ByteArrayImg {
	byte[] data;
	public final int width;
	public final int height;
	
	public ByteArrayImg(byte[] data, final int width, final int height) {
		this.data = data;
		this.width = width;
		this.height = height;
	}
	public byte getPixelByte(int x, int y) {
		return data[y * width + x];
	}
	//Does nothing if x and y are out of range
	public void setPixelByte(int x, int y, int colour) {
		if(x < width && y < height && x >= 0 && y >= 0)
			data[y * width + x] = (byte) colour;
	}
	public byte[] getBytes() {
		return data;
	}
	public Mat toMat(int type) {
		Mat output = new Mat(this.height, this.width, type);
		output.put(0, 0, this.data);
		return output;
	}
}