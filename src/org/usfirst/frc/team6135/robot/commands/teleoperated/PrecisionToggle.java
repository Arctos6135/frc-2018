package org.usfirst.frc.team6135.robot.commands.teleoperated;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class PrecisionToggle extends InstantCommand {

    public PrecisionToggle() {
        super();
        requires(Robot.drive);
    }

    // Called once when the command executes
    protected void initialize() {
    	Robot.drive.setSpeedMultiplier(Robot.drive.getSpeedMultiplier() == 1.0 ? 0.3 : 1.0);
    }

}
