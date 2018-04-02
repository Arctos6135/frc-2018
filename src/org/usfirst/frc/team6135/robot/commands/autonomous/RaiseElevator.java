package org.usfirst.frc.team6135.robot.commands.autonomous;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.subsystems.ElevatorSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Raises the elevator all the way to the top until it hits the limit switch.
 */
public class RaiseElevator extends Command {

	double speed;
	
    public RaiseElevator(double speed) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.elevatorSubsystem);
    	this.speed *= ElevatorSubsystem.DIRECTION_UP;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.elevatorSubsystem.setSpeed(speed);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return !Robot.elevatorSubsystem.notAtTop();
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.elevatorSubsystem.setSpeed(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.elevatorSubsystem.setSpeed(0);
    }
}
