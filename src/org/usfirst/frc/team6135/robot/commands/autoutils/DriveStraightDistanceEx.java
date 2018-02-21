package org.usfirst.frc.team6135.robot.commands.autoutils;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

/**
 *	Drives a straight distance forward. The speed of both motors are tuned constantly to ensure precision.
 *	Unlike DriveStraightDistance, this version changes the amount it adjusts by according to the error,
 *	and also checks if the average speed of the motors is about equal to what they were initially supposed
 *	to run on, and adjusts them if necessary. This class also checks if the values are out of bounds; in which
 *	case it will constrain them.
 *	This class is a child of DriveStraightDistance. All behavior stays the same except for execute().
 *
 *	It is recommended to use this command instead of DriveStraightDistance.
 */
@SuppressWarnings("deprecation")
public class DriveStraightDistanceEx extends DriveStraightDistance {

	protected static final double errorMultiplier = 0.015;
	static final double TOLERANCE = 0.05;
	
	protected double speed;
	
    public DriveStraightDistanceEx(double distance, double speed) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	super(distance, speed);
    	this.speed = speed;
    }
    
    static double constrain(double val, double upper, double lower) {
    	return Math.max(lower, Math.min(upper, val));
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
    	double left = RobotMap.leftEncoder.getDistance();
    	double right = RobotMap.rightEncoder.getDistance();
    	double error = Math.abs(left - right) / 2;
    	double adjustment = error * errorMultiplier;
    	if(left > right) {
    		leftSpeed -= adjustment;
    		rightSpeed += adjustment;
    	}
    	else {
    		leftSpeed += adjustment;
    		rightSpeed -= adjustment;
    	}
    	
    	double avg = (leftSpeed + rightSpeed) / 2;
    	if(Math.abs(this.speed - avg) > TOLERANCE) {
    		double avgError = this.speed - avg;
    		leftSpeed += avgError;
    		rightSpeed += avgError;
    	}
    	
    	leftSpeed = constrain(leftSpeed, RobotMap.Speeds.DRIVE_SPEED, -RobotMap.Speeds.DRIVE_SPEED);
    	rightSpeed = constrain(rightSpeed, RobotMap.Speeds.DRIVE_SPEED, -RobotMap.Speeds.DRIVE_SPEED);
    	
    	Robot.drive.setMotorsVBus(leftSpeed, rightSpeed);
    }
}
