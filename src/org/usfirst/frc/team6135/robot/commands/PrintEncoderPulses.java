package org.usfirst.frc.team6135.robot.commands;


import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *	Prints out the raw encoder reading to the SmartDashboard. Unused at the moment.
 */
public class PrintEncoderPulses extends Command {

    public PrintEncoderPulses() {
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//Note: This Value is unscaled! Depending on the counting mode of the encoder, 
    	//it may have to be divided by 2 or 4.
    	SmartDashboard.putNumber("Encoder Pulses", RobotMap.testEncoder.getRaw());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
