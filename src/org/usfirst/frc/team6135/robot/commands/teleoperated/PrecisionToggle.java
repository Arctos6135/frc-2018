package org.usfirst.frc.team6135.robot.commands.teleoperated;

import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class PrecisionToggle extends InstantCommand {

    public PrecisionToggle() {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called once when the command executes
    protected void initialize() {
    	RobotMap.Speeds.DRIVE_SPEED = (RobotMap.Speeds.DRIVE_SPEED == 1.0)?0.5:1.0;
    }

}
