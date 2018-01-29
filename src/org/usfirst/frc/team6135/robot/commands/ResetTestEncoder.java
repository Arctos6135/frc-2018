package org.usfirst.frc.team6135.robot.commands;

import org.usfirst.frc.team6135.robot.RobotMap;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class ResetTestEncoder extends InstantCommand {

    public ResetTestEncoder() {
        super();
    }

    // Called once when the command executes
    protected void initialize() {
    	RobotMap.testEncoder.reset();
    }

}
