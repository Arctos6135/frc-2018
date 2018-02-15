package org.usfirst.frc.team6135.robot.commands.autoutils;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import static org.usfirst.frc.team6135.robot.RobotMap.*;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DriveStraightDistance extends Command {

	private double distance;
	//private double speed;
	private double leftSpeed;
	private double rightSpeed;
	private double adjustValue = 0.05;
	
    public DriveStraightDistance(double distance, double speed) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drive);
    	this.distance = distance;
    	//this.speed = speed;
    	leftSpeed = speed;
    	rightSpeed = speed;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
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
