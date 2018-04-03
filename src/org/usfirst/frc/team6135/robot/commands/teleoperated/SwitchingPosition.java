package org.usfirst.frc.team6135.robot.commands.teleoperated;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.subsystems.ElevatorSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.WristPIDSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Raises the elevator to its max height and lowers the wrist for a place cubes into switch position.
 */
public class SwitchingPosition extends Command {

    public SwitchingPosition() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.wristSubsystem);
    	requires(Robot.elevatorSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	if(!Robot.wristSubsystem.isEnabled())
    		Robot.wristSubsystem.enable();
    	Robot.wristSubsystem.setSetpoint(WristPIDSubsystem.ANGLE_BOTTOM);
    	Robot.elevatorSubsystem.setSpeed(RobotMap.Speeds.AUTO_ELEVATOR_SPEED * ElevatorSubsystem.DIRECTION_UP);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//Stop the elevator if it reached the top
    	if(!Robot.elevatorSubsystem.notAtTop())
    		Robot.elevatorSubsystem.setSpeed(0);
    	//No need to stop the wrist since the subsystem code takes care of that
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	//Finish when elevator is at bottom and wrist is "on target" with our setpoint of bottom
        return !Robot.elevatorSubsystem.notAtTop() && Robot.wristSubsystem.onTarget();
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
    }
}
