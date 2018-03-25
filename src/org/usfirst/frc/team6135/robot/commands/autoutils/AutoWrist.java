package org.usfirst.frc.team6135.robot.commands.autoutils;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 *
 */
public class AutoWrist extends TimedCommand {
	
	public static final double SPEED = 1.0;

    public AutoWrist(double timeout) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	super(0);
    	requires(Robot.wristSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//Robot.wristSubsystem.setSpeed(SPEED);
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
