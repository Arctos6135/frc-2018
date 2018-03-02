package org.usfirst.frc.team6135.robot.commands.teleopcommands;

import org.usfirst.frc.team6135.robot.OI;
import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Handles the teleop control of the wrist.
 *  DO NOT USE UNLESS ElevatorAnalogBackup IS USED.
 */
public class WristAnalog extends Command {
	
	protected static final double DEADZONE = 0.25;
	//Whether the wrist has already been set to stationary. This is to stop the code from setting the output of the motor over and over again when there's no input,
	//which messes up the auto rope releasing and/or tightening of ElevatorAnalogBackup
	protected boolean stopped = false;

    public WristAnalog() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.wristSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	final double joystickVal = OI.attachmentsController.getRawAxis(OI.Controls.WRIST);
    	if(Math.abs(joystickVal) > DEADZONE) {
    		RobotMap.wristVictor.set(RobotMap.Speeds.WRIST_SPEED * joystickVal);
			stopped = false;
    	}
    	else if(!stopped) {
    		RobotMap.wristVictor.set(0);
			stopped = true;
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
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
