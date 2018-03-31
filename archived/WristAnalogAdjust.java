package org.usfirst.frc.team6135.robot.commands.teleopcommands;

import org.usfirst.frc.team6135.robot.OI;
import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class WristAnalogAdjust extends Command {

	double stationaryAngle;
	
	static final double errorMultiplier = 0.05;
	static final double DEADZONE = 0.15;
	
	static final double ANGLE_MAX = 1.0;
	static final double ANGLE_MIN = -90.0;
	
	boolean adjustOn = true;
	
	/*
	 * Drift -> Everything to shift up or down
	 * Keep an angle constant, or some difference between the current angle and the "minimum angle"
	 * Both of which are drifting
	 */
	
    public WristAnalogAdjust() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.wristSubsystem);
    }
    
    //Getter and setter for whether to adjust
    public boolean getAdjustOn() {
    	return adjustOn;
    }
    public void setAdjustOn(boolean b) {
    	adjustOn = b;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	stationaryAngle = Math.max(Math.min(Robot.wristSubsystem.getGyro(), ANGLE_MAX), ANGLE_MIN);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double joystickVal = OI.attachmentsController.getRawAxis(OI.Controls.WRIST);
    	if(Math.abs(joystickVal) > DEADZONE) {
    		Robot.wristSubsystem.setSpeed(joystickVal * RobotMap.Speeds.WRIST_SPEED);
    		stationaryAngle = Math.max(Math.min(Robot.wristSubsystem.getGyro(), ANGLE_MAX), ANGLE_MIN);
    	}
    	else if(adjustOn) {
	    	adjustForAngle();
    	}
    	else {
    		Robot.wristSubsystem.setSpeed(0);
    	}
    }

    public void adjustForAngle(){
    	double angle = Robot.wristSubsystem.getGyro();
    	double error = angle - stationaryAngle;
    	
    	double adjustmentSpeed = error * -errorMultiplier;
    	Robot.wristSubsystem.setSpeed(adjustmentSpeed);
    }
    
    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.wristSubsystem.setSpeed(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.wristSubsystem.setSpeed(0);
    }
}
