package org.usfirst.frc.team6135.robot.commands.teleoperated;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.autonomous.RaiseElevator;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Raises both the elevator and the wrist to their maximum for shooting cubes into the scale.
 *	Requires wrist to have PID and limit switch.
 */
public class ScalingPosition extends Command {
	
	RaiseElevator raiseElevator;

    public ScalingPosition() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.wristSubsystem);
    	raiseElevator = new RaiseElevator(RobotMap.Speeds.AUTO_ELEVATOR_SPEED);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	raiseElevator.start();
    	Robot.wristSubsystem.setRaw(-1.0);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//Limit switch handling is in the subsystem
    	if(Robot.wristSubsystem.notAtTop())
    		RobotMap.wristVictor.set(-1.0);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	//End this command only if elevator and wrist have activated their limit switches
        return (raiseElevator.isCompleted() && !Robot.wristSubsystem.notAtTop());
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.elevatorSubsystem.setSpeed(0);
    	Robot.wristSubsystem.setRaw(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
