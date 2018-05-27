package org.usfirst.frc.team6135.robot.misc;

public class Setpoint {
	public double position, velocity, acceleration;
	
	public Setpoint(double pos, double vel, double acl) {
		position = pos;
		velocity = vel;
		acceleration = acl;
	}
	
	public double getPos() {
		return position;
	}
	public double getVel() {
		return velocity;
	}
	public double getAcl() {
		return acceleration;
	}
}
