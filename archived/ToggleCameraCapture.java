package org.usfirst.frc.team6135.robot.misc;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class ToggleCameraCapture extends InstantCommand {

    public ToggleCameraCapture() {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called once when the command executes
    protected void initialize() {
    	if(Robot.captureTask.getPaused())
    		Robot.captureTask.resume();
    	else
    		Robot.captureTask.pause();
    }

}
