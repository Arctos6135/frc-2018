package org.usfirst.frc.team6135.robot.misc;

/**
 * A trapezoidal motion profile trajectory in 1D.
 * This class assumes starting and ending velocities are both 0, 
 * and the max velocity remains constant.
 * @author Tyler
 *
 */
public class TrapezoidalMotionProfile {
	/*
	 * a - Accel
	 * v - Velocity
	 * d - Distance
	 */
	double aMax, vMax, aAccel, aDecel, vCruise, dCruise, dAccel;
	
	//Acceleration, Velocity and Position
	double acl, vel, pos;
	//Time
	double t;
	
	public enum State {
		ACCEL, DECEL, CRUISE, END
	}
	
	State state;
	
	public TrapezoidalMotionProfile(double dist, double maxAccel, double maxVel) {
		aMax = maxAccel;
		vMax = maxVel;
		//https://youtu.be/8319J1BEHwM
		//Velocity could be negative
		vCruise = Math.max(-vMax, Math.min(vMax, dist * maxAccel));
		//But time is absolute
		double tAccel = Math.abs(vCruise / aMax);
		//Assuming both max velocity and acceleration are positive, these distances should be positive
		dAccel = aMax * tAccel * tAccel / 2;
		dCruise = Math.abs(dist) - dAccel * 2;
		
		if(dist < 0) {
			aAccel = -maxAccel;
			aDecel = maxAccel;
		}
		else {
			aAccel = maxAccel;
			aDecel = -maxAccel;
		}
		
		acl = vel = pos = 0;
		t = 0;
		
		state = State.ACCEL;
	}
	
	public double getAcceleration() {
		return acl;
	}
	public double getVelocity() {
		return vel;
	}
	public double getPosition() {
		return pos;
	}
	public double getTime() {
		return t;
	}
	
	public Setpoint getSetpoint() {
		return new Setpoint(pos, vel, acl);
	}
	
	/**
	 * Advances the time by {@code t_delta}.
	 * This will cause the acceleration to be updated if necessary and the velocity and position to be integrated.
	 * Because of the nature of Riemann sums, the smaller {@code t_delta} is, the more accurate the outcome.<br>
	 * <br>
	 * Calling this method will have no effect if this motion profile has ended.
	 * @param t_delta - The time increment
	 */
	public void advanceTime(double t_delta) {
		if(state == State.END)
			return;
		
		t += t_delta;
		
		if(state == State.ACCEL) {
			acl = aAccel;
			if(Math.abs(pos) >= dAccel)
				state = State.CRUISE;
		}
		else if(state == State.CRUISE) {
			acl = 0;
			if(Math.abs(pos) >= dAccel + dCruise)
				state = State.DECEL;
		}
		else {
			acl = aDecel;
			if(Math.abs(pos) >= dAccel + dCruise + dAccel) {
				state = State.END;
			}
		}
		
		vel += acl * t_delta;
		pos += vel * t_delta;
	}
	
	public boolean ended() {
		return state == State.END;
	}
}
