package org.usfirst.frc.team6135.robot.commands.defaultcommands;

import org.usfirst.frc.team6135.robot.OI;
import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class WristAnalogPID extends Command {
	
	static final double DEADZONE = 0.25;

    public WristAnalogPID() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.wristSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if(!OI.isInDemoMode) {
	    	double joystickVal = OI.attachmentsController.getRawAxis(OI.Controls.WRIST);
	    	//Check if joystick is pushed
	    	
	    	if(Math.abs(joystickVal) > DEADZONE) {
	    		//Disable the PID so that we can freely move the motor
	    		if(Robot.wristSubsystem.isEnabled())
	    			Robot.wristSubsystem.disable();
	    		//Set the speed
	    		Robot.wristSubsystem.setRaw(joystickVal);
	    	}
	    	else {
	    		//IMPORTANT: Since if the joystick was previously pushed, the PID would be enabled,
	    		//this would only run if the joystick went from having input to not having input.
	    		if(!Robot.wristSubsystem.isEnabled()) {
	    			//At this point we can set a new set point for the PID to hold the wrist in place
	    			Robot.wristSubsystem.setSetpoint(Robot.wristSubsystem.getAngle());
	    			//Enable and let the PID take over
	    			//Reset to make sure no integral windup happens
	    			Robot.wristSubsystem.getPIDController().reset();
	    			Robot.wristSubsystem.enable();
	    		}
	    		Robot.wristSubsystem.enable();
	    	}
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	//Don't disable the PID, but change its setpoint to stop it from adjusting
    	Robot.wristSubsystem.setSetpoint(Robot.wristSubsystem.getAngle());
    	Robot.wristSubsystem.getPIDController().reset();
    	Robot.wristSubsystem.enable();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	//Don't disable the PID, but change its setpoint to stop it from adjusting
    	Robot.wristSubsystem.setSetpoint(Robot.wristSubsystem.getAngle());
    	Robot.wristSubsystem.getPIDController().reset();
    	Robot.wristSubsystem.enable();
    }
}
