package org.usfirst.frc.team6135.robot.vision;

import org.opencv.core.Scalar;

/**
 * A class that represents a colour filtering threshold, composed of an upper value, and a lower value.
 */
public class ColorRange {
	final Scalar upper, lower;
	/**
	 * Constructs a ColorRange object using two Scalars (in this case they represent HSV colour values).
	 * 
	 * @param l - The upper limit
	 * @param u - The lower limit
	 */
	public ColorRange(Scalar l, Scalar u) {
		upper = u;
		lower = l;
	}
	public Scalar getUpper() {
		return upper;
	}
	public Scalar getLower() {
		return lower;
	}
}
