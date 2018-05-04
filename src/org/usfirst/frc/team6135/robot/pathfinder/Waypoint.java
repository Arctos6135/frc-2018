package org.usfirst.frc.team6135.robot.pathfinder;

import org.usfirst.frc.team6135.robot.pathfinder.math.Vec2D;

/**
 * A waypoint in a path.
 * @author tyler
 *
 */
public class Waypoint {
	double x, y, heading;
	
	/**
	 * Constructs a new waypoint with the specified X and Y value and no heading.
	 * @param x - The X value of this waypoint
	 * @param y - The Y value of this waypoint
	 */
	public Waypoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * Constructs a new waypoint with the specified X and Y value and heading.
	 * @param x - The X value of this waypoint
	 * @param y - The Y value of this waypoint
	 * @param heading - The heading of the robot, in radians.
	 */
	public Waypoint(double x, double y, double heading) {
		this.x = x;
		this.y = y;
		this.heading = heading;
	}
	
	/**
	 * Retrieves the X value of this waypoint.
	 * @return The X value of this waypoint
	 */
	public double getX() {
		return x;
	}
	/**
	 * Retrieves the Y value of this waypoint.
	 * @return The Y value of this waypoint
	 */
	public double getY() {
		return y;
	}
	/**
	 * Retrieves the heading of the robot, in radians.
	 * @return The heading in radians
	 */
	public double getHeading() {
		return heading;
	}
	/**
	 * Retrieves the position of this waypoint represented as a {@code Vec2D}
	 * @return The position of this waypoint
	 */
	public Vec2D getPosVec() {
		return new Vec2D(x, y);
	}
}
