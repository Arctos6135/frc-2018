package org.usfirst.frc.team6135.robot.commands.autoutils;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 *	A command that runs the elevator for a set period of time.
 */
public class AutoElevator extends TimedCommand {

	final double speed;
	/**
	 * A negative speed indicates downwards movement
	 * @param timeout - Command timeout in seconds
	 * @param speed - Desired speed
	 */
    public AutoElevator(double timeout, double speed) {
        super(timeout);
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.elevatorSubsystem);
        this.speed = speed;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.elevatorSubsystem.setSpeed(speed);
    }

    // Called once after timeout
    protected void end() {
    	Robot.elevatorSubsystem.setSpeed(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.elevatorSubsystem.setSpeed(0);
    }
}
