package org.usfirst.frc.team6135.robot.commands.teleopcommands;

import org.usfirst.frc.team6135.robot.OI;
import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Handles the controlling of the intake motors (controlled by LT and RT on the attachments
 * controller). The speed is based on the analog reading of the triggers; RT is to intake,
 * LT is to reverse the intake. If both LT and RT are pressed, the intake will stop.
 * 
 * Called as a default command by IntakeSubsystem.
 */
public class IntakeAnalog extends Command {

	public static final double DEADZONE = 0.15;
	
	double constrain(final double val) {
		if(val >= -1.0 && val <= 1.0) {
			return val;
		}
		else if(val < -1.0) {
			return -1.0;
		}
		else {
			return 1.0;
		}
	}
    public IntakeAnalog() {
        requires(Robot.intakeSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	final double ltVal = Math.abs(OI.attachmentsController.getRawAxis(OI.Controls.INTAKE_OUT));
    	final double rtVal = Math.abs(OI.attachmentsController.getRawAxis(OI.Controls.INTAKE_IN));
    	if(ltVal <= DEADZONE && rtVal > DEADZONE) {
    		Robot.intakeSubsystem.setSpeed(constrain(rtVal));
    	}
    	else if(ltVal > DEADZONE && rtVal <= DEADZONE) {
    		//Set to negative of LT since here we want to reverse the intakeSubsystem
    		Robot.intakeSubsystem.setSpeed(-constrain(ltVal));
    	}
    	else {
    		//Driver messed up - Both LT and RT are pressed
    		Robot.intakeSubsystem.setSpeed(0);
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.intakeSubsystem.setSpeed(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.intakeSubsystem.setSpeed(0);
    }
}
