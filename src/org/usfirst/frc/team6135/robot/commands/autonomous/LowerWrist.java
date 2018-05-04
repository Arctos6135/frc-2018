package org.usfirst.frc.team6135.robot.commands.autonomous;

import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 *
 */
public class LowerWrist extends TimedCommand {

	double speed = 1.0;
	
    public LowerWrist(double timeout) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	super(timeout);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	RobotMap.wristVictor.set(speed);
    }

    // Called once after isFinished returns true
    protected void end() {
    	RobotMap.wristVictor.set(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	RobotMap.wristVictor.set(0);
    }
}
