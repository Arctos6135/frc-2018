package org.usfirst.frc.team6135.robot.misc;

public class TrapezoidalMotionProfile {
	
	double distance;
	double maxAcl, maxVel;
	double cruiseVel;
	
	double tAccel, tCruise, tTotal;
	double accelDist, cruiseDist;
	
	boolean reverse = false;
	
	static double absMin(double a, double b) {
		return Math.abs(a) < Math.abs(b) ? a : b;
	}
	
	public TrapezoidalMotionProfile(double dist, double maxVelocity, double maxAcceleration) {
		this(dist, maxVelocity, maxAcceleration, false);
	}
	public TrapezoidalMotionProfile(double dist, double maxVelocity, double maxAcceleration, boolean reverse) {
		this.reverse = reverse;
		distance = dist;
		maxAcl = maxAcceleration;
		maxVel = maxVelocity;
		
		//Calculate the max reachable velocity
		//Formula can be derived from 4th kinematic equation
		double vc = Math.sqrt(2 * maxAcl * dist / 2);
		cruiseVel = Math.min(vc, maxVel);
		
		tAccel = cruiseVel / maxAcl;
		
		accelDist = Math.pow(tAccel, 2) * maxAcl * 1/2;
		//Acceleration and deceleration should cover the same distance
		cruiseDist = dist - accelDist * 2;
		tCruise = cruiseDist / cruiseVel;
		
		tTotal = tAccel + tCruise + tAccel;
	}
	
	public double totalTime() {
		return tTotal;
	}
	public double distAt(double time) {
		double result = 0;
		//When accelerating
		if(time <= tAccel) {
			result = Math.pow(time, 2) * maxAcl * 1/2;
		}
		//When cruising
		else if(time <= tAccel + tCruise) {
			//The distance is the distance covered during acceleration and rest of the time multiplied by the cruise velocity
			result = accelDist + (time - tAccel) * cruiseVel;
		}
		//When decelerating
		else if(time <= totalTime()) {
			//The distance is the distance covered during acceleration and cruising, minus the distance covered during deceleration,
			//which can be solved using the third kinematic formula
			result = accelDist + cruiseDist - ((time - tAccel - tCruise) * cruiseVel - 
					Math.pow(time - tAccel - tCruise, 2) * maxAcl * 1/2);
		}
		else {
			throw new RuntimeException("Time out of range");
		}
		return reverse ? -result : result;
	}
	public double veloAt(double time) {
		double result = 0;
		//When accelerating
		if(time <= tAccel) {
			//The velocity is just the time multiplied by the acceleration
			result = time * maxAcl;
		}
		//When cruising
		else if(time <= tAccel + tCruise) {
			//The velocity is the cruise velocity
			result = cruiseVel;
		}
		//When decelerating
		else if(time <= totalTime()) {
			//The velocity is the cruise velocity minus the acceleration times the time decelerating
			result = cruiseVel - (time - tAccel - tCruise) * maxAcl;
		}
		else {
			throw new RuntimeException("Time out of range");
		}
		return reverse ? -result : result;
	}
	public double accelAt(double time) {
		double result = 0;
		//When accelerating
		if(time <= tAccel) {
			result = maxAcl;
		}
		//When cruising
		else if(time <= tAccel + tCruise) {
			result = 0;
		}
		//When decelerating
		else if(time <= totalTime()) {
			result = -maxAcl;
		}
		else {
			throw new RuntimeException("Time out of range");
		}
		return reverse ? -result : result;
	}
}
