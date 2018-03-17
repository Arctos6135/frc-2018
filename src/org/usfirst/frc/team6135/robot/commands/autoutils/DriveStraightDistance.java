package org.usfirst.frc.team6135.robot.commands.autoutils;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import static org.usfirst.frc.team6135.robot.RobotMap.*;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Drives a straight distance forward. The speed of the motors are constantly adjusted to ensure precision.
 *	@deprecated Use DriveStraightDistanceEx if possible, for it is more accurate and less error-prone.
 */
@Deprecated
public class DriveStraightDistance extends Command {

	protected double distance;
	protected double leftSpeed;
	protected double rightSpeed;
	
	//To prevent the Robot hitting a wall and getting stuck forever, a check system is implemented
	//The encoder readings are checked periodically. If between two checks the distance traveled is less than
	//a constant, the Robot is considered to be stuck and the command is aborted.
	
	//The time of the last check
	protected double lastCheckTime = 0;
	//The averaged encoder readings from the last check
	protected double lastCheckDist = 0;
	//The interval for checking in seconds
	private final double checkInterval = 2;
	//The minimum distance difference between checks in inches
	private final double minDist = 1.5;
	
	private final double adjustValue = 0.05;
	
    public DriveStraightDistance(double distance, double speed) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drive);
    	this.distance = distance;
    	leftSpeed = speed;
    	rightSpeed = speed;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	lastCheckTime = 0;
    	lastCheckDist = 0;
    	leftEncoder.reset();
    	rightEncoder.reset();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if(leftEncoder.getDistance() > rightEncoder.getDistance()) {
    		leftSpeed -= adjustValue;
    		rightSpeed += adjustValue;
    	} else if(leftEncoder.getDistance() < rightEncoder.getDistance()) {
    		leftSpeed += adjustValue;
    		rightSpeed -= adjustValue;
    	}
    	leftSpeed = Math.max(Math.min(RobotMap.Speeds.DRIVE_SPEED, leftSpeed), -RobotMap.Speeds.DRIVE_SPEED);
    	rightSpeed = Math.max(Math.min(RobotMap.Speeds.DRIVE_SPEED, rightSpeed), -RobotMap.Speeds.DRIVE_SPEED);
    	Robot.drive.setMotorsVBus(leftSpeed, rightSpeed);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	//If checkInterval seconds passed between the last check and the calling of this method,
    	if(this.timeSinceInitialized() - lastCheckTime >= checkInterval) {
    		//Average the left and right readings
    		double avg = leftEncoder.getDistance() + rightEncoder.getDistance();
    		avg /= 2.0;
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
    protected void end() {
    	Robot.drive.setMotorsVBus(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.drive.setMotorsVBus(0, 0);
    }
}
