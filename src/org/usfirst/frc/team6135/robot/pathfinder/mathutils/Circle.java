package org.usfirst.frc.team6135.robot.pathfinder.mathutils;

public class Circle {
	
	Vec2D center;
	double radius;
	
	public Circle(Vec2D center, double radius) {
		this.center = center;
		this.radius = radius;
	}
	
	public Vec2D getCenter() {
		return center;
	}
	public double getRadius() {
		return radius;
	}
}
