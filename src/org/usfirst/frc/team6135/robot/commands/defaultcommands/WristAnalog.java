package org.usfirst.frc.team6135.robot.commands.defaultcommands;

import org.usfirst.frc.team6135.robot.OI;
import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class WristAnalog extends Command {
	
	static final double DEADZONE = 0.25;

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
    	double joystickVal = OI.attachmentsController.getRawAxis(OI.Controls.WRIST);
    	//Check if joystick is pushed
    	boolean override = OI.attachmentsController.getRawButtonPressed(OI.Controls.WRIST_OVERRIDE);
    	if(Math.abs(joystickVal) > DEADZONE) {
    		if(override) {
    			Robot.wristSubsystem.setRawOverride(joystickVal);
    		}
    		else {
    			Robot.wristSubsystem.setRaw(joystickVal);
    		}
    	}
    	else {
    		Robot.wristSubsystem.setRaw(0);
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
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
