package org.usfirst.frc.team6135.robot.commands.teleoperated;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.subsystems.ElevatorSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.WristSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Lowers the elevator and wrist to the bottom, into the position for intaking cubes.
 */
public class IntakingPosition extends Command {

    public IntakingPosition() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.elevatorSubsystem);
    	requires(Robot.wristSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	if(!Robot.wristSubsystem.isEnabled())
    		Robot.wristSubsystem.enable();
    	Robot.wristSubsystem.setSetpoint(WristSubsystem.ANGLE_BOTTOM);
    	//Note: The speed may have to be adjusted since the wrist may be slower
    	Robot.elevatorSubsystem.setSpeed(RobotMap.Speeds.AUTO_ELEVATOR_SPEED * ElevatorSubsystem.DIRECTION_DOWN);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if(!Robot.elevatorSubsystem.notAtBottom())
    		Robot.elevatorSubsystem.setSpeed(0);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	//Finish when elevator is at bottom and wrist is "on target" with our setpoint of bottom
        return !Robot.elevatorSubsystem.notAtBottom() && Robot.wristSubsystem.onTarget();
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.elevatorSubsystem.setSpeed(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.elevatorSubsystem.setSpeed(0);
    	//Don't disable the PID, but change its setpoint to stop it from adjusting
    	Robot.wristSubsystem.setSetpoint(Robot.wristSubsystem.getAngle());
    	Robot.wristSubsystem.getPIDController().reset();
    	Robot.wristSubsystem.enable();
    }
}
