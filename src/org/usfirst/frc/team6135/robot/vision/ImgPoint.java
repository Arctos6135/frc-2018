package org.usfirst.frc.team6135.robot.vision;

/**
 * Represents a point in an image
 */
public class ImgPoint {
	public int x, y;
	public ImgPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	@Override
	public boolean equals(Object anotherObj) {
		if(!(anotherObj instanceof ImgPoint))
			return false;
		ImgPoint otherPoint = (ImgPoint) anotherObj;
		boolean result = this.x == otherPoint.x && this.y == otherPoint.y;
		return result;
	}
}