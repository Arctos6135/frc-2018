package org.usfirst.frc.team6135.robot.commands.teleopcommands;

import org.usfirst.frc.team6135.robot.OI;
import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TiltAnalog extends Command {
	
	static final double DEADZONE = 0.15;

    public TiltAnalog() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.tiltSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	final double joystickVal = OI.attachmentsController.getRawAxis(RobotMap.ControllerMap.RSTICK_Y_AXIS);
    	if(Math.abs(joystickVal) > DEADZONE) {
    		RobotMap.tiltVictor.set(RobotMap.Speeds.TILT_SPEED * joystickVal);
    	}
    	else {
    		RobotMap.tiltVictor.set(0);
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	RobotMap.tiltVictor.set(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	RobotMap.tiltVictor.set(0);
    }
}
