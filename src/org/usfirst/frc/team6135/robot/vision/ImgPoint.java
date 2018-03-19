package org.usfirst.frc.team6135.robot.vision;

import java.util.List;

/**
 * Represents a point in an image
 */
public class ImgPoint {
	public int x, y;
	public ImgPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public static ImgPoint average(List<ImgPoint> points) {
		ImgPoint avg = new ImgPoint(0, 0);
		for(ImgPoint i : points) {
			avg.x += i.x;
			avg.y += i.y;
		}
		avg.x /= points.size();
		avg.y /= points.size();
		return avg;
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