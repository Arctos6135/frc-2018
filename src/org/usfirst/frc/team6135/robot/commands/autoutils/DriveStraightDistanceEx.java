package org.usfirst.frc.team6135.robot.commands.autoutils;

import static org.usfirst.frc.team6135.robot.RobotMap.leftEncoder;
import static org.usfirst.frc.team6135.robot.RobotMap.rightEncoder;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Drives a straight distance forward. The speed of both motors are tuned constantly to ensure precision.
 *	Unlike DriveStraightDistance, this version changes the amount it adjusts by according to the error,
 *	and also checks if the average speed of the motors is about equal to what they were initially supposed
 *	to run on, and adjusts them if necessary. This class also checks if the values are out of bounds; in which
 *	case it will constrain them.
 *	Since this command uses encoders instead of time, the readings will be checked periodically to ensure that
 *	the robot is not stuck forever.
 *
 *	It is recommended to use this command instead of DriveStraightDistance.
 */
public class DriveStraightDistanceEx extends Command {

	protected static final double errorMultiplier = 0.015;
	static final double TOLERANCE = 0.10;
	static final double DIST_TOLERANCE = 1.5;
	
	protected double distance;
	protected double leftSpeed;
	protected double rightSpeed;
	protected double speed;
	
	//To prevent the Robot hitting a wall and getting stuck forever, a check system is implemented
	//The encoder readings are checked periodically. If between two checks the distance traveled is less than
	//a constant, the Robot is considered to be stuck and the command is aborted.
	
	//The time of the last check
	protected double lastCheckTime = 0;
	//The averaged encoder readings from the last check
	protected double lastCheckDist = 0;
	//The interval for checking in seconds
	private static final double checkInterval = 2;
	//The minimum distance difference between checks in inches
	private static final double minDist = 1.5;
	
    public DriveStraightDistanceEx(double distance, double speed) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drive);
    	this.distance = distance;
    	this.speed = speed;
    	this.leftSpeed = speed;
    	this.rightSpeed = speed;
    }
    
    static double constrain(double val, double upper, double lower) {
    	return Math.max(lower, Math.min(upper, val));
    }
    
    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
    	lastCheckTime = 0;
    	lastCheckDist = 0;
    	leftEncoder.reset();
    	rightEncoder.reset();
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
    	double left = RobotMap.leftEncoder.getDistance();
    	double right = RobotMap.rightEncoder.getDistance();
    	
    	if(Math.abs(left - right) > DIST_TOLERANCE) {
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
    

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
    	//If checkInterval seconds passed between the last check and the calling of this method,
    	if(this.timeSinceInitialized() - lastCheckTime >= checkInterval) {
    		//Average the left and right readings
    		double avg = (leftEncoder.getDistance() + rightEncoder.getDistance()) / 2;
    		//If the absolute difference between the new reading and the old reading is less than minDist, abort
    		if(Math.abs(avg - lastCheckDist) < minDist)
    			return true;
    		//Reset vars
    		lastCheckTime = this.timeSinceInitialized();
    		lastCheckDist = avg;
    		
    	}
    	//If the above check passed, check to see if any of the sides passed the limit
        return (Math.abs(leftEncoder.getDistance()) >= Math.abs(distance) 
        		|| Math.abs(rightEncoder.getDistance()) >= Math.abs(distance));
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
    	Robot.drive.setMotorsVBus(0, 0);
    	leftEncoder.reset();
    	rightEncoder.reset();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    	Robot.drive.setMotorsVBus(0, 0);
    	leftEncoder.reset();
    	rightEncoder.reset();
    }
}
