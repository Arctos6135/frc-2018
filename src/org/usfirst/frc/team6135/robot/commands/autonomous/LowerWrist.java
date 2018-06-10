package org.usfirst.frc.team6135.robot.commands.autonomous;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.subsystems.WristSubsystem;

import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 *
 */
public class LowerWrist extends TimedCommand {

	double speed = 1.0 * WristSubsystem.DIRECTION_DOWN;
	
    public LowerWrist() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	super(3.0);
    	requires(Robot.wristSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.wristSubsystem.setRaw(speed);
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.wristSubsystem.setRaw(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
