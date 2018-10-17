package org.usfirst.frc.team6135.robot.commands.autonomous;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 *	Runs the intake for a set period of time
 */
public class AutoIntake extends TimedCommand {

	final double speed;
	
	public enum Direction {
		IN,
		OUT
	}
	
    public AutoIntake(double timeout, double speed) {
        super(timeout);
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.intakeSubsystem);
        this.speed = speed;
    }
    public AutoIntake(Direction direction) {
    	this(RobotMap.AUTO_INTAKE_TIME, direction == Direction.IN ? RobotMap.Speeds.AUTO_INTAKE_SPEED : -RobotMap.Speeds.AUTO_INTAKE_SPEED);
    }
    public AutoIntake(Direction direction, double time) {
    	this(time, direction == Direction.IN ? RobotMap.Speeds.AUTO_INTAKE_SPEED : -RobotMap.Speeds.AUTO_INTAKE_SPEED);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.intakeSubsystem.setSpeed(speed);
    }

    // Called once after timeout
    protected void end() {
    	Robot.intakeSubsystem.setSpeed(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.intakeSubsystem.setSpeed(0);
    }
}
